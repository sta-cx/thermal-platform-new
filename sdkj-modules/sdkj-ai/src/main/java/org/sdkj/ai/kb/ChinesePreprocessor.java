package org.sdkj.ai.kb;

import org.springframework.stereotype.Component;

@Component
public class ChinesePreprocessor {

    public String process(String text) {
        if (text == null || text.isEmpty()) return "";
        // 在中文句末标点后注入换行,给 TokenTextSplitter 提供切分边界
        String withBreaks = text.replaceAll("([。!?;:…])", "$1\n");
        // 合并 3+ 连续空行为 2 个
        return withBreaks.replaceAll("\n{3,}", "\n\n");
    }
}
