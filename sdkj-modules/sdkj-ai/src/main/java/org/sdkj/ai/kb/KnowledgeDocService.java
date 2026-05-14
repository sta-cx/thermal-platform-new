package org.sdkj.ai.kb;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.springframework.stereotype.Service;

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
}
