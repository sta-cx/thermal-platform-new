package org.sdkj.ai.kb;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KbIngestPipeline {

    private final AiKnowledgeDocMapper docMapper;
    private final AiKnowledgeChunkMapper chunkMapper;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final KnowledgeDocService docService;

    @Value("${thermal.ai.kb.max-doc-bytes:5242880}")
    private int maxDocBytes;

    @Value("${thermal.ai.kb.max-chunks-per-doc:2000}")
    private int maxChunksPerDoc;

    public Long ingest(String tenantId, String title, DocFormat format, String text) {
        // 1. 校验
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxDocBytes) {
            throw new IllegalArgumentException("文档超过 " + maxDocBytes + " 字节上限");
        }
        String hash = sha256(bytes);

        // 2. 去重
        AiKnowledgeDoc existing = docService.findByTenantAndHash(tenantId, hash);
        if (existing != null) {
            log.info("Doc with hash {} already exists for tenant {}, skip ingest", hash, tenantId);
            return existing.getId();
        }

        // 3. 切片
        List<ChunkResult> chunks = chunkingService.chunk(text, format);
        if (chunks.size() > maxChunksPerDoc) {
            throw new IllegalArgumentException("切片数 " + chunks.size() + " 超过单文档上限 " + maxChunksPerDoc);
        }

        // 4. 入库 doc 元数据 + chunks(独立 Bean 让 @Transactional 生效,见 Phase 2A 审查 B-C3)
        Long docId = docService.persistDocAndChunks(tenantId, title, format, bytes.length, hash, chunks);

        // 5. 调 embedding + Qdrant (事务外, 失败标 FAILED)
        try {
            Map<Integer, String> idMap = embeddingService.embedAndStore(tenantId, docId, chunks);
            updateChunkQdrantIds(docId, idMap);
            markStatus(docId, DocStatus.EMBEDDED, null);
        } catch (Exception e) {
            log.error("Embedding/Qdrant ingest failed for doc {}", docId, e);
            markStatus(docId, DocStatus.FAILED, truncate(e.getMessage(), 500));
        }

        return docId;
    }

    private void updateChunkQdrantIds(Long docId, Map<Integer, String> idMap) {
        List<AiKnowledgeChunk> chunks = chunkMapper.selectList(
            new LambdaQueryWrapper<AiKnowledgeChunk>().eq(AiKnowledgeChunk::getDocId, docId)
        );
        for (AiKnowledgeChunk c : chunks) {
            String pointId = idMap.get(c.getChunkIndex());
            if (pointId != null) {
                c.setQdrantPointId(pointId);
                chunkMapper.updateById(c);
            }
        }
    }

    private void markStatus(Long docId, DocStatus status, String errMsg) {
        AiKnowledgeDoc update = new AiKnowledgeDoc();
        update.setId(docId);
        update.setStatus(status.name());
        update.setErrorMessage(errMsg);
        docMapper.updateById(update);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
