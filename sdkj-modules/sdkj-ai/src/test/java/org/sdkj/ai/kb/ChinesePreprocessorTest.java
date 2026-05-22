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
        String input = "需要确认：阀门开度?是的!继续操作。";
        String out = preprocessor.process(input);
        // 全角 ：?! 应换行; 中文 ! ? 在 markdown/技术文档里实际多用半角, 不应被切
        assertTrue(out.contains("：\n") && out.contains("。\n"));
        assertFalse(out.contains("?\n"), "半角问号不应被切分");
        assertFalse(out.contains("!\n"), "半角感叹号不应被切分");
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

    @Test
    void shouldNotBreakUrlsAndMarkdownImageSyntax() {
        String input = "操作步骤：\n![](http://192.168.2.206:4999/server/index.php?s=/api/x)";
        String out = preprocessor.process(input);
        assertTrue(out.contains("http://192.168.2.206:4999"), "URL 中半角冒号不应被换行");
        assertTrue(out.contains("![](http://"), "Markdown 图片语法不应被破坏");
    }
}
