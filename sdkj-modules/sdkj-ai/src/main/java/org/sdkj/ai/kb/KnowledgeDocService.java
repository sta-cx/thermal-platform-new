package org.sdkj.ai.kb;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeDocService {

    private final AiKnowledgeDocMapper docMapper;
    private final AiKnowledgeChunkMapper chunkMapper;

    public List<AiKnowledgeDoc> listByTenant(String tenantId) {
        return docMapper.selectList(
            new LambdaQueryWrapper<AiKnowledgeDoc>()
                .eq(AiKnowledgeDoc::getTenantId, tenantId)
                .orderByDesc(AiKnowledgeDoc::getCreateTime)
        );
    }

    public AiKnowledgeDoc findById(Long id) {
        return docMapper.selectById(id);
    }

    public List<AiKnowledgeChunk> listChunks(Long docId) {
        return chunkMapper.selectList(
            new LambdaQueryWrapper<AiKnowledgeChunk>()
                .eq(AiKnowledgeChunk::getDocId, docId)
                .orderByAsc(AiKnowledgeChunk::getChunkIndex)
        );
    }

    public AiKnowledgeDoc findByTenantAndHash(String tenantId, String hash) {
        return docMapper.selectOne(
            new LambdaQueryWrapper<AiKnowledgeDoc>()
                .eq(AiKnowledgeDoc::getTenantId, tenantId)
                .eq(AiKnowledgeDoc::getSourceHash, hash)
        );
    }

    /**
     * Atomically insert the doc row + all chunk rows in one transaction.
     * <p>
     * Moved out of {@link KbIngestPipeline} so that {@code @Transactional} actually applies —
     * self-invocation within the pipeline bypasses Spring AOP proxies (see Phase 2A code review B-C3).
     */
    @Transactional(rollbackFor = Exception.class)
    public Long persistDocAndChunks(String tenantId, String title, DocFormat format,
                                     int sourceSize, String hash, List<ChunkResult> chunks) {
        AiKnowledgeDoc doc = new AiKnowledgeDoc();
        doc.setTenantId(tenantId);
        doc.setTitle(title);
        doc.setSourceFormat(format.name());
        doc.setSourceSize(sourceSize);
        doc.setSourceHash(hash);
        doc.setChunkCount(chunks.size());
        doc.setStatus(DocStatus.CHUNKED.name());
        docMapper.insert(doc);

        Date now = new Date();
        List<AiKnowledgeChunk> chunkEntities = new ArrayList<>();
        for (ChunkResult c : chunks) {
            AiKnowledgeChunk e = new AiKnowledgeChunk();
            e.setDocId(doc.getId());
            e.setTenantId(tenantId);
            e.setChunkIndex(c.getIndex());
            e.setText(c.getText());
            e.setTokenCount(c.getEstimatedTokens());
            e.setQdrantPointId("");
            e.setCreateTime(now);
            chunkEntities.add(e);
        }
        chunkMapper.insertBatch(chunkEntities);
        return doc.getId();
    }
}
