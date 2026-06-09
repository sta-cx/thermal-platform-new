package org.sdkj.ai.context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.tools.dispatcher.ToolCallResult.ToolExecResult;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.Tool;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.*;

/** A 能力的会话上下文编排入口。全部方法在 context.enabled=false 时为 no-op（回落 Phase 2B）。 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationContextService {

    private final ContextStore store;
    private final FactExtractor extractor;
    private final ContextArgFiller filler;
    private final ToolRegistry registry;
    private final AiProperties aiProperties;
    private final SuggestionEngine suggestionEngine;

    /** 启动时检测 -parameters 编译标志是否生效，否则 enrichArgs 会静默失效。 */
    @PostConstruct
    void checkParameterNamesAvailable() {
        for (ToolMetadata md : registry.all()) {
            Parameter[] params = md.method().getParameters();
            if (params.length > 0 && params[0].isNamePresent()) return;
        }
        log.warn("[CtxService] javac -parameters flag not detected; enrichArgs (arg-fill) will not match entity keys. "
            + "Ensure maven-compiler-plugin has <parameters>true</parameters>.");
    }

    /** A 补全：给某个 tool call 的参数 map 补全缺失的 ID 类参数。异常时返回原 args。 */
    public Map<String, Object> enrichArgs(Long sessionId, String toolName, Map<String, Object> args) {
        AiProperties.Context cfg = aiProperties.getContext();
        if (!cfg.isEnabled() || !cfg.isArgFill()) return args;
        try {
            ToolMetadata md = registry.resolve(toolName);
            if (md == null) return args;
            Set<String> paramNames = new HashSet<>();
            for (Parameter p : md.method().getParameters()) paramNames.add(p.getName());
            ConversationContext ctx = store.load(sessionId);
            return filler.fill(paramNames, args, ctx, cfg.getEntityKeys());
        } catch (Exception e) {
            log.warn("[CtxService] enrichArgs failed (session={}, tool={}): {}", sessionId, toolName, e.getMessage());
            return args;
        }
    }

    /** A 记忆：dispatch 后记录 LOW 结果，更新 facts + focus。异常静默。 */
    public void recordResults(Long sessionId, List<ToolExecResult> results) {
        AiProperties.Context cfg = aiProperties.getContext();
        if (!cfg.isEnabled() || results == null || results.isEmpty()) return;
        try {
            List<EntityFact> facts = extractor.extract(results, cfg.getFactTtl());
            if (facts.isEmpty()) return;
            ConversationContext ctx = store.load(sessionId);
            ctx.addFacts(facts, cfg.getMaxFacts());
            FocusEntity focus = extractor.deriveFocus(ctx.getFacts(), cfg.getEntityKeys(), cfg.getFocusPriority());
            if (focus != null) ctx.setFocus(focus);
            store.save(ctx);
        } catch (Exception e) {
            log.warn("[CtxService] recordResults failed (session={}): {}", sessionId, e.getMessage());
        }
    }

    /** 页面上下文播种：把选中实体写入 focus（低置信，Tool fact 会覆盖）。 */
    public void seedFromPageContext(Long sessionId, Map<String, Object> pageContext) {
        AiProperties.Context cfg = aiProperties.getContext();
        if (!cfg.isEnabled() || !cfg.isInjectPageContext()
            || pageContext == null || pageContext.isEmpty()) return;
        try {
            Object type = pageContext.get("selectedEntityType");
            Object ids = pageContext.get("selectedEntityIds");
            if (type == null || !(ids instanceof List<?> list) || list.isEmpty()) return;
            Object first = list.get(0);
            Long idVal;
            if (first instanceof Number num) {
                idVal = num.longValue();
            } else {
                // 前端雪花 ID 经 BigNumberSerializer 序列化为 string,需兼容
                try { idVal = Long.parseLong(String.valueOf(first)); }
                catch (NumberFormatException ex) { return; }
            }
            String t = type.toString();
            ConversationContext ctx = store.load(sessionId);
            // 仅当当前无 focus 时播种，避免覆盖 Tool 得到的高置信 focus
            if (ctx.getFocus() == null) {
                ctx.setFocus(new FocusEntity(t, idVal, t + "#" + idVal,
                    new HashMap<>(Map.of(t + "Id", idVal)), Instant.now()));
                store.save(ctx);
            }
        } catch (Exception e) {
            log.warn("[CtxService] seedFromPageContext failed (session={}): {}", sessionId, e.getMessage());
        }
    }

    /** B：基于会话最近 facts 产出推荐动作。空则返回 null（SSE NON_NULL 不下发）。 */
    public List<SuggestedAction> buildSuggestions(Long sessionId) {
        AiProperties.Context cfg = aiProperties.getContext();
        if (!cfg.isEnabled() || !cfg.isSuggestion()) return null;
        try {
            ConversationContext ctx = store.load(sessionId);
            var actions = suggestionEngine.evaluate(ctx.getFacts(), ctx.getFocus(), cfg.getSuggestionRules());
            return actions.isEmpty() ? null : actions;
        } catch (Exception e) {
            log.warn("[CtxService] buildSuggestions failed (session={}): {}", sessionId, e.getMessage());
            return null;
        }
    }
}
