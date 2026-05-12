package org.sdkj.ai.safety;

import lombok.RequiredArgsConstructor;
import org.sdkj.ai.config.AiProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PiiMasker {

    private final AiProperties properties;
    private volatile List<Pattern> compiledPatterns;

    private List<Pattern> getPatterns() {
        List<Pattern> p = compiledPatterns;
        if (p == null) {
            synchronized (this) {
                p = compiledPatterns;
                if (p == null) {
                    p = new ArrayList<>();
                    for (String regex : properties.getAudit().getPiiMaskPatterns()) {
                        p.add(Pattern.compile(regex));
                    }
                    compiledPatterns = p;
                }
            }
        }
        return p;
    }

    public String mask(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String result = input;
        for (Pattern p : getPatterns()) {
            result = p.matcher(result).replaceAll(match -> {
                String s = match.group();
                if (s.length() <= 4) return "****";
                return s.substring(0, 3) + "****" + s.substring(s.length() - 2);
            });
        }
        return result;
    }
}
