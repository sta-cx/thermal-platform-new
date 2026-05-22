package org.sdkj.ai.kb;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiKnowledgeChunk;
import org.sdkj.ai.domain.AiKnowledgeDoc;
import org.sdkj.ai.mapper.AiKnowledgeChunkMapper;
import org.sdkj.ai.mapper.AiKnowledgeDocMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class KbIngestPipeline {

    private final AiKnowledgeDocMapper docMapper;
    private final AiKnowledgeChunkMapper chunkMapper;
    private final EmbeddingService embeddingService;
    private final KnowledgeDocService docService;
    private final TikaDocumentReaderFactory tikaReaderFactory;
    private final ChinesePreprocessor chinesePreprocessor;
    private final TokenTextSplitter tokenTextSplitter;

    @Value("${thermal.ai.kb.max-doc-bytes:5242880}")
    private int maxDocBytes;

    @Value("${thermal.ai.kb.max-chunks-per-doc:2000}")
    private int maxChunksPerDoc;

    /**
     * Ingest a file via Tika (supports 23+ formats).
     */
    public IngestResult ingestFile(String tenantId, String title, DocFormat format, MultipartFile file) throws IOException {
        // 1. Parse with Tika
        List<Document> docs = tikaReaderFactory.read(file);
        String fullText = docs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));

        if (fullText == null || fullText.isBlank()) {
            throw new KbIngestException("无法提取文本内容(可能是扫描件或加密文件)", null, null);
        }

        return ingestText(tenantId, title, format, fullText);
    }

    /**
     * Ingest raw text (legacy entry point).
     */
    public IngestResult ingest(String tenantId, String title, DocFormat format, String text) {
        return ingestText(tenantId, title, format, text);
    }

    private IngestResult ingestText(String tenantId, String title, DocFormat format, String text) {
        // 1. Validate
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxDocBytes) {
            throw new IllegalArgumentException("文档超过 " + maxDocBytes + " 字节上限");
        }
        String hash = sha256(bytes);

        // 2. Dedup
        AiKnowledgeDoc existing = docService.findByTenantAndHash(tenantId, hash);
        if (existing != null) {
            log.info("Doc with hash {} already exists for tenant {}, skip ingest", hash, tenantId);
            return new IngestResult(existing.getId(), 0);
        }

        // 3. Chinese preprocess + split
        String preprocessed = chinesePreprocessor.process(text);
        List<Document> chunkDocs = tokenTextSplitter.apply(List.of(new Document(preprocessed)));
        if (chunkDocs.isEmpty()) {
            throw new KbIngestException("文本切分后为空(内容过短)", null, null);
        }
        List<ChunkResult> chunks = IntStream.range(0, chunkDocs.size())
            .mapToObj(i -> {
                String chunkText = chunkDocs.get(i).getText();
                int estTokens = (int) Math.ceil(chunkText.length() / 1.5);
                return new ChunkResult(i, chunkText, estTokens);
            })
            .toList();

        if (chunks.size() > maxChunksPerDoc) {
            throw new IllegalArgumentException("切片数 " + chunks.size() + " 超过单文档上限 " + maxChunksPerDoc);
        }

        // 4. Persist doc metadata + chunks
        Long docId = docService.persistDocAndChunks(tenantId, title, format, bytes.length, hash, chunks);

        // 5. Embed + Qdrant upsert
        try {
            Map<Integer, String> idMap = embeddingService.embedAndStore(tenantId, docId, chunks);
            updateChunkQdrantIds(docId, idMap);
            markStatus(docId, DocStatus.EMBEDDED, null);
        } catch (Exception e) {
            log.error("Embedding/Qdrant ingest failed for doc {}", docId, e);
            markStatus(docId, DocStatus.FAILED, truncate(e.getMessage(), 500));
            throw new KbIngestException("知识库索引失败:" + e.getMessage(), docId, e);
        }

        return new IngestResult(docId, chunks.size());
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
        docMapper.updateStatusWithErrorMessage(docId, status.name(), errMsg);
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
