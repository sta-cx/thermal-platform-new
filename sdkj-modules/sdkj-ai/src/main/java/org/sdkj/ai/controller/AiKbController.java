package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.kb.DocFormat;
import org.sdkj.ai.kb.EmbeddingService;
import org.sdkj.ai.kb.KbIngestPipeline;
import org.sdkj.ai.kb.KnowledgeDocService;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.sdkj.ai.safety.AiTenantGate;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AI 知识库管理后台
 */
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

    @SaCheckPermission("ai:kb:manage")
    @PostMapping("/upload")
    public R<Long> upload(@RequestParam("file") MultipartFile file,
                          @RequestParam("title") String title,
                          @RequestParam(value = "format", defaultValue = "PLAIN_TEXT") String format)
            throws IOException {
        String tenantId = LoginHelper.getTenantId();
        aiTenantGate.requireEnabled(tenantId);

        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        DocFormat fmt = DocFormat.valueOf(format);
        Long docId = pipeline.ingest(tenantId, title, fmt, text);
        return R.ok(docId);
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

        // Delete old Qdrant points
        List<AiKnowledgeChunk> oldChunks = docService.listChunks(id);
        List<String> oldPointIds = oldChunks.stream()
            .map(AiKnowledgeChunk::getQdrantPointId)
            .filter(s -> s != null && !s.isEmpty())
            .toList();
        embeddingService.deletePoints(doc.getTenantId(), oldPointIds);

        // Delete chunk rows (doc stays)
        chunkMapper.delete(new LambdaQueryWrapper<AiKnowledgeChunk>()
            .eq(AiKnowledgeChunk::getDocId, id));

        // Mark for re-ingest - simplified: caller re-uploads
        // For Phase 2A, reindex deletes old data; user re-uploads same file
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
