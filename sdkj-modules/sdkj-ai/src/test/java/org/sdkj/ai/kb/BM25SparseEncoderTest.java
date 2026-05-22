package org.sdkj.ai.kb;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class BM25SparseEncoderTest {
    private final BM25SparseEncoder encoder = new BM25SparseEncoder();

    @Test
    void encodeShouldReturnNonEmptySparseVectorForChinese() {
        var sv = encoder.encode("二次网阀门开度调节");
        assertNotNull(sv);
        assertTrue(sv.indices().size() > 0);
        assertEquals(sv.indices().size(), sv.values().size());
    }

    @Test
    void sameTokenShouldHashToSameIndex() {
        var a = encoder.encode("阀门 阀门 阀门");
        assertEquals(1, a.indices().stream().distinct().count(),
            "同一 term 应只占一个 index");
    }

    @Test
    void differentTextsShouldHaveSomeIndexOverlap() {
        var a = encoder.encode("阀门开度调节");
        var b = encoder.encode("阀门开度过高");
        boolean hasOverlap = a.indices().stream().anyMatch(b.indices()::contains);
        assertTrue(hasOverlap, "共享 term 应有 index 重合");
    }
}
