package org.sdkj.ai.context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.tools.dispatcher.ToolCallResult.ToolExecResult;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.springframework.stereotype.Service;

import java.lang.reflect.Parameter;
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
}
