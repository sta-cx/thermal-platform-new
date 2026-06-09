package org.sdkj.ai.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 参数补全：仅补"目标 Tool 声明了、LLM 漏填、且属于 entityKeys"的 ID 类参数。
 * 来源优先级：focus.attrs（明确焦点，无歧义）> facts 中该 key 的唯一值。
 * 多值冲突时不补（避免张冠李戴）。只补 ID 类，绝不补语义参数（action/note/amount…）。
 */
@Slf4j
@Component
public class ContextArgFiller {

    public Map<String, Object> fill(Set<String> paramNames, Map<String, Object> args,
                                    ConversationContext ctx, List<String> entityKeys) {
        Map<String, Object> out = new HashMap<>(args == null ? Map.of() : args);
        if (ctx == null || paramNames == null) return out;

        for (String p : paramNames) {
            if (!entityKeys.contains(p)) continue;                  // 只补业务主键类
            if (out.get(p) != null) continue;                       // LLM 已给，不覆盖
            Object val = resolve(p, ctx);
            if (val != null) {
                out.put(p, val);
                log.info("[CtxFill] auto-filled {}={} from context (session={})", p, val, ctx.getSessionId());
            }
        }
        return out;
    }

    private Object resolve(String key, ConversationContext ctx) {
        // 1) focus.attrs 优先
        if (ctx.getFocus() != null && ctx.getFocus().attrs() != null) {
            Object v = ctx.getFocus().attrs().get(key);
            if (v != null) return v;
        }
        // 2) facts 中该 key 的 distinct 非 null 值；唯一才补
        Set<Object> distinct = new LinkedHashSet<>();
        if (ctx.getFacts() != null) {
            for (EntityFact f : ctx.getFacts()) {
                Object v = f.keys().get(key);
                if (v != null) distinct.add(v);
            }
        }
        return distinct.size() == 1 ? distinct.iterator().next() : null;
    }
}
