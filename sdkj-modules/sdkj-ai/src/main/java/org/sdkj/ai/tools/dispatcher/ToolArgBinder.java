package org.sdkj.ai.tools.dispatcher;

import org.sdkj.ai.tools.registry.ToolMetadata;

import java.math.BigDecimal;
import java.util.Map;

/**
 * LLM JSON 反序列化后 Number 类型可能不匹配(Long 参数收到 Integer)。
 * 此工具类统一处理参数绑定与类型转换，供 Dispatcher 和 Executor 共用。
 */
final class ToolArgBinder {

    private ToolArgBinder() {}

    static Object[] bind(ToolMetadata md, Map<String, Object> args) {
        var params = md.method().getParameters();
        Object[] out = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            out[i] = coerce(args.get(params[i].getName()), params[i].getType());
        }
        return out;
    }

    static Object coerce(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isInstance(value)) return value;
        if (targetType == Long.class || targetType == long.class) {
            if (value instanceof Number n) return n.longValue();
            try { return Long.parseLong(value.toString()); } catch (NumberFormatException ignored) {}
        }
        if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Number n) return n.intValue();
        }
        if (targetType == Double.class || targetType == double.class) {
            if (value instanceof Number n) return n.doubleValue();
        }
        if (targetType == BigDecimal.class && value instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof String s) return Boolean.parseBoolean(s);
        }
        return value;
    }
}
