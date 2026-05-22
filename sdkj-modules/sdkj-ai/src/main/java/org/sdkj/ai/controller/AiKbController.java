package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.kb.*;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.sdkj.ai.safety.AiTenantGate;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ai/admin/kb")
@RequiredArgsConstructor
public class AiKbController {

    private final KbIngestPipeline pipeline;
    private final KnowledgeDocService docService;
    private final AiKnowledgeDocMapper docMapper;
    private final AiKnowledgeChunkMapper chunkMapper;
    private final EmbeddingService embeddingService;
    private final AiTenantGate aiTenantGate;
    private final AiProperties aiProperties;

    @SaCheckPermission("ai:kb:manage")
    @PostMapping("/upload")
    public R<List<KbUploadResult>> upload(@RequestParam("files") MultipartFile[] files) {
        String tenantId = LoginHelper.getTenantId();
        aiTenantGate.requireEnabled(tenantId);

        var upload = aiProperties.getRag().getUpload();
        if (files == null || files.length == 0) {
            return R.fail("未选择文件");
        }
        if (files.length > upload.getMaxFilesPerBatch()) {
            return R.fail("单次最多上传 " + upload.getMaxFilesPerBatch() + " 个文件");
        }

        List<KbUploadResult> results = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            try {
                // Size check
                long maxBytes = (long) upload.getMaxSizeMb() * 1024 * 1024;
                if (file.getSize() > maxBytes) {
                    results.add(KbUploadResult.failed(filename,
                        "超过 " + upload.getMaxSizeMb() + "MB 上限"));
                    continue;
                }
                // Extension check (backend guard)
                String ext = extractExt(filename);
                if (!upload.getAllowedExtensions().isEmpty()
                        && !upload.getAllowedExtensions().contains(ext)) {
                    results.add(KbUploadResult.failed(filename,
                        "不支持的格式: " + ext));
                    continue;
                }
                IngestResult ir = pipeline.ingestFile(tenantId, filename, detectFormat(ext), file);
                if (ir.duplicate()) {
                    results.add(KbUploadResult.duplicate(filename, ir.docId()));
                } else {
                    results.add(KbUploadResult.success(filename, ir.docId(), ir.chunkCount()));
                }
            } catch (KbIngestException e) {
                log.error("KB ingest failed for {}", filename, e);
                results.add(KbUploadResult.failed(filename, e.getMessage()));
            } catch (Exception e) {
                log.error("KB ingest failed for {}", filename, e);
                results.add(KbUploadResult.failed(filename, e.getMessage()));
            }
        }
        return R.ok(results);
    }

    private String extractExt(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot).toLowerCase();
    }

    private DocFormat detectFormat(String ext) {
        return switch (ext) {
            case ".md", ".markdown" -> DocFormat.MARKDOWN;
            case ".pdf" -> DocFormat.PDF;
            case ".txt", ".text" -> DocFormat.PLAIN_TEXT;
            default -> DocFormat.AUTO;
        };
    }

    @SaCheckPermission("ai:kb:manage")
    @GetMapping("/docs")
    public R<List<AiKnowledgeDoc>> list() {
        String tenantId = LoginHelper.getTenantId();
        return R.ok(docService.listByTenant(tenantId));
    }

    @SaCheckPermission("ai:kb:manage")
    @GetMapping("/docs/{id}/chunks")
    public R<List<AiKnowledgeChunk>> chunks(@PathVariable Long id) {
        return R.ok(docService.listChunks(id));
    }

    @SaCheckPermission("ai:kb:manage")
    @PostMapping("/docs/{id}/reindex")
    public R<Void> reindex(@PathVariable Long id) {
        AiKnowledgeDoc doc = docService.findById(id);
        if (doc == null) return R.fail("文档不存在");

        List<AiKnowledgeChunk> oldChunks = docService.listChunks(id);
        List<String> oldPointIds = oldChunks.stream()
            .map(AiKnowledgeChunk::getQdrantPointId)
            .filter(s -> s != null && !s.isEmpty())
            .toList();
        embeddingService.deletePoints(doc.getTenantId(), oldPointIds);

        chunkMapper.delete(new LambdaQueryWrapper<AiKnowledgeChunk>()
            .eq(AiKnowledgeChunk::getDocId, id));

        doc.setStatus("UPLOADED");
        docMapper.updateById(doc);

        return R.ok();
    }

    @SaCheckPermission("ai:kb:manage")
    @DeleteMapping("/docs/{id}")
    public R<Void> delete(@PathVariable Long id) {
        AiKnowledgeDoc doc = docService.findById(id);
        if (doc == null) return R.ok();
        List<AiKnowledgeChunk> chunks = docService.listChunks(id);
        List<String> pointIds = chunks.stream()
            .map(AiKnowledgeChunk::getQdrantPointId)
            .filter(s -> s != null && !s.isEmpty())
            .toList();
        embeddingService.deletePoints(doc.getTenantId(), pointIds);
        chunkMapper.delete(new LambdaQueryWrapper<AiKnowledgeChunk>()
            .eq(AiKnowledgeChunk::getDocId, id));
        docMapper.deleteById(id);
        return R.ok();
    }
}
