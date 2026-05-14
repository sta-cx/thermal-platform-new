package org.sdkj.ai.kb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkingService {

    @Value("${thermal.ai.kb.chunk-size-tokens:500}")
    private int chunkSizeTokens;

    @Value("${thermal.ai.kb.chunk-overlap-tokens:50}")
    private int chunkOverlapTokens;

    public int estimateTokens(String text) {
        if (text == null || text.isEmpty()) return 0;
        return (int) Math.ceil(text.length() / 1.5);
    }

    public List<ChunkResult> chunk(String text, DocFormat format) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<String> segments = splitByParagraph(text);
        List<String> sentences = new ArrayList<>();
        for (String seg : segments) {
            if (estimateTokens(seg) > chunkSizeTokens) {
                sentences.addAll(splitBySentence(seg));
            } else {
                sentences.add(seg);
            }
        }
        return assemble(sentences);
    }

    private List<String> splitByParagraph(String text) {
        String[] parts = text.split("\\n\\s*\\n");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) out.add(trimmed);
        }
        return out;
    }

    private List<String> splitBySentence(String segment) {
        String[] parts = segment.split("(?<=[。!?\\.\\!\\?;;])");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) out.add(trimmed);
        }
        return out;
    }

    private List<ChunkResult> assemble(List<String> sentences) {
        List<ChunkResult> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int currentTokens = 0;
        int index = 0;

        for (String sentence : sentences) {
            int sentTokens = estimateTokens(sentence);
            if (currentTokens + sentTokens > chunkSizeTokens && current.length() > 0) {
                chunks.add(new ChunkResult(index++, current.toString(), currentTokens));
                String tail = takeTail(current.toString(), chunkOverlapTokens);
                current = new StringBuilder(tail);
                currentTokens = estimateTokens(tail);
            }
            current.append(sentence);
            currentTokens += sentTokens;
        }
        if (current.length() > 0) {
            chunks.add(new ChunkResult(index, current.toString(), currentTokens));
        }
        return chunks;
    }

    private String takeTail(String s, int overlapTokens) {
        int targetChars = (int) (overlapTokens * 1.5);
        if (s.length() <= targetChars) return s;
        return s.substring(s.length() - targetChars);
    }
}
