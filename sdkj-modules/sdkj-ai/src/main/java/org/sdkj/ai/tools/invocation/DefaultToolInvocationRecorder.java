package org.sdkj.ai.tools.invocation;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.assistant.SessionService;
import org.sdkj.ai.domain.AiToolInvocation;
import org.sdkj.ai.mapper.AiToolInvocationMapper;
import org.sdkj.ai.tools.store.PendingToolCall;
import org.sdkj.common.json.utils.JsonUtils;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultToolInvocationRecorder implements ToolInvocationRecorder {

    private final SessionService sessionService;
    private final AiToolInvocationMapper invocationMapper;

    @Override
    public void record(PendingToolCall call, String resultJson, String status, int latencyMs, String errorMessage) {
        // TenantFilter 把 HTTP 线程切到租户库;本方法写 master 表,强制切回
        // 注意:不能用 @Transactional,否则事务在 push 之前就绑定了租户数据源
        DynamicDataSourceContextHolder.push("master");
        try {
            String summary = buildSummary(call, status, errorMessage);
            Long messageId = sessionService.appendToolMessage(call.getSessionId(), summary);

            AiToolInvocation row = new AiToolInvocation();
            row.setMessageId(messageId);
            row.setPendingCallId(call.getCallId());
            row.setTenantId(call.getTenantId());
            row.setUserId(call.getUserId());
            row.setToolName(call.getToolName());
            row.setRiskLevel(call.getRisk().name());
            row.setArguments(toJsonSafe(call.getEffectiveArgs()));
            row.setResult(resultJson);
            row.setStatus(status);
            row.setLatencyMs(latencyMs);
            row.setErrorMessage(errorMessage);
            row.setCreatedTime(new Date());
            invocationMapper.insert(row);
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }

    private String buildSummary(PendingToolCall call, String status, String err) {
        return switch (status) {
            case "SUCCESS" -> "已执行 " + call.getToolName();
            case "FAILED"  -> call.getToolName() + " 执行失败:" + (err == null ? "未知错误" : err);
            case "DRY_RUN" -> "已生成 " + call.getToolName() + " 指令清单(dryRun,未实发)";
            default        -> call.getToolName() + " → " + status;
        };
    }

    private static String toJsonSafe(Map<String, Object> args) {
        if (args == null) return "{}";
        try {
            return JsonUtils.toJsonString(args);
        } catch (Exception e) {
            return "{}";
        }
    }
}
