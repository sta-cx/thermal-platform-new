package org.sdkj.ai.tools.dispatcher;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.sdkj.ai.tools.store.PendingToolCall;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 写 Tool 的执行入口(由 /confirm 端点调用)。
 *
 * 责任:
 *  1. 权限二次校验(SaCheckPermission 表达式)
 *  2. 应用 effective args 覆盖(如阀门 dryRun 强制)
 *  3. 反射执行 Tool 方法
 *  4. 异常包装为统一返回结构
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolExecutor {

    private final ToolRegistry registry;
    private final ObjectMapper objectMapper;
    private final AiProperties aiProperties;

    public record ExecutionOutcome(String resultJson, boolean dryRun) {}

    public ExecutionOutcome execute(PendingToolCall call) throws Exception {
        ToolMetadata md = registry.byName(call.getToolName());
        if (md == null) {
            throw new IllegalStateException("tool no longer registered: " + call.getToolName());
        }

        // 权限校验
        if (!md.permission().isBlank() && !StpUtil.hasPermission(md.permission())) {
            throw new SecurityException("permission denied: " + md.permission());
        }

        // 应用 dryRun 覆盖(阀门类)
        Map<String, Object> args = new HashMap<>(call.getArguments());
        boolean effectiveDryRun = applyDryRunPolicy(md, args);

        // 反射执行
        Object[] params = bindArgs(md, args);
        Object out = md.method().invoke(md.bean(), params);
        String resultJson = out == null ? "null" : objectMapper.writeValueAsString(out);
        return new ExecutionOutcome(resultJson, effectiveDryRun);
    }

    private boolean applyDryRunPolicy(ToolMetadata md, Map<String, Object> args) {
        if (!md.fullName().toLowerCase().contains("valve")) return false;
        if (!args.containsKey("dryRun")) args.put("dryRun", true);

        boolean configDefault = aiProperties.getTools().getValve().isDryRun();
        boolean canExecuteReal = StpUtil.hasPermission("thermal:ht:valve:execute");
        if (!canExecuteReal) {
            args.put("dryRun", true);
            log.info("[ToolExecutor] valve dryRun enforced=true (no execute permission)");
            return true;
        }
        if (configDefault) {
            args.put("dryRun", true);
            log.info("[ToolExecutor] valve dryRun enforced=true (config default)");
            return true;
        }
        Object passed = args.get("dryRun");
        return Boolean.TRUE.equals(passed);
    }

    private Object[] bindArgs(ToolMetadata md, Map<String, Object> args) {
        var params = md.method().getParameters();
        Object[] out = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            out[i] = args.get(params[i].getName());
        }
        return out;
    }
}
