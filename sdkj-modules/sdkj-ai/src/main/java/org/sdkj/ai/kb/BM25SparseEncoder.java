package org.sdkj.ai.kb;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BM25SparseEncoder {

    public record SparseVec(List<Long> indices, List<Float> values) {}

    public SparseVec encode(String text) {
        if (text == null || text.isBlank()) {
            return new SparseVec(List.of(), List.of());
        }
        Map<String, Integer> tf = tokenize(text);
        List<Long> indices = new ArrayList<>(tf.size());
        List<Float> values = new ArrayList<>(tf.size());
        for (Map.Entry<String, Integer> e : tf.entrySet()) {
            long idx = stableHash(e.getKey());
            float val = (float) Math.log1p(e.getValue());
            indices.add(idx);
            values.add(val);
        }
        return new SparseVec(indices, values);
    }

    public List<SparseVec> encodeBatch(List<String> texts) {
        return texts.stream().map(this::encode).toList();
    }

    private Map<String, Integer> tokenize(String text) {
        Map<String, Integer> tf = new HashMap<>();
        try (SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
             TokenStream stream = analyzer.tokenStream("text", new StringReader(text))) {
            CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                String token = term.toString();
                if (token.length() > 1 || isCjkChar(token.charAt(0))) {
                    tf.merge(token, 1, Integer::sum);
                }
            }
            stream.end();
        } catch (Exception e) {
            log.warn("Tokenize failed for text length={}", text.length(), e);
        }
        return tf;
    }

    private boolean isCjkChar(char c) {
        return c >= 0x4E00 && c <= 0x9FFF;
    }

    private long stableHash(String term) {
        return term.hashCode() & 0x7FFFFFFFL;
    }
}
