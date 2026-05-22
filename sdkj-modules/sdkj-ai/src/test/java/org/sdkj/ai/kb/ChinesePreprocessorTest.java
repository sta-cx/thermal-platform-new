package org.sdkj.ai.kb;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class ChinesePreprocessorTest {
    private final ChinesePreprocessor preprocessor = new ChinesePreprocessor();

    @Test
    void shouldInsertNewlineAfterChinesePeriod() {
        String input = "阀门已开启。下一步检查压力。压力正常。";
        String out = preprocessor.process(input);
        long count = out.chars().filter(c -> c == '\n').count();
        assertEquals(3, count, "应在每个句号后换行");
    }

    @Test
    void shouldHandleMixedPunctuation() {
        String input = "需要确认:阀门开度?是的!继续操作。";
        String out = preprocessor.process(input);
        assertTrue(out.contains(":\n") && out.contains("?\n")
                && out.contains("!\n") && out.contains("。\n"));
    }

    @Test
    void shouldCollapseConsecutiveBlankLines() {
        String input = "段一。\n\n\n\n段二。";
        String out = preprocessor.process(input);
        assertFalse(out.contains("\n\n\n"));
    }

    @Test
    void shouldKeepEnglishPeriodsUnchanged() {
        String input = "OK. Done.";
        String out = preprocessor.process(input);
        assertEquals("OK. Done.", out);
    }
}
