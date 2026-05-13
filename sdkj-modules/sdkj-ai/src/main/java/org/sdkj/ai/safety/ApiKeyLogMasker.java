package org.sdkj.ai.safety;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

public class ApiKeyLogMasker extends ClassicConverter {

    private static final Pattern SK_PATTERN = Pattern.compile("sk-[A-Za-z0-9]{30,}");
    private static final Pattern BEARER_PATTERN = Pattern.compile("(?i)Bearer\\s+[A-Za-z0-9._-]{30,}");

    @Override
    public String convert(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        if (msg == null) return "";
        msg = SK_PATTERN.matcher(msg).replaceAll("sk-***");
        msg = BEARER_PATTERN.matcher(msg).replaceAll("Bearer ***");
        return msg;
    }
}
