package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.AiConstants;
import org.sdkj.ai.assistant.SessionService;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.context.ContextStore;
import org.sdkj.ai.context.ConversationContext;
import org.sdkj.ai.context.OrchestrationSummary;
import org.sdkj.ai.context.TaskExecutor;
import org.sdkj.ai.context.TaskState;
import org.sdkj.ai.safety.AiTenantGate;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.ratelimiter.annotation.RateLimiter;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/** 能力 C：任务计划批准/中止。 */
@Slf4j
@RestController
@RequestMapping("/ai/tasks")
@RequiredArgsConstructor
public class AiTaskController {

    private final ContextStore contextStore;
    private final TaskExecutor taskExecutor;
    private final AiTenantGate tenantGate;
    private final SessionService sessionService;
    private final AiProperties aiProperties;

    /** 批准计划 → 启动编排，SSE 流式返回进度/暂停帧。 */
    @SaCheckLogin
    @RateLimiter(time = 60, count = 10, key = "#{T(org.sdkj.common.satoken.utils.LoginHelper).getUserId()}")
    @PostMapping(value = "/{sessionId}/approve", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter approve(@PathVariable @NotBlank String sessionId) {
        String tenantId = LoginHelper.getTenantId();
        Long userId = LoginHelper.getUserId();
        tenantGate.requireEnabled(tenantId);
        Long sid = Long.valueOf(sessionId);

        ConversationContext ctx = contextStore.load(sid);
        TaskState ts = ctx.getTaskState();
        SseEmitter emitter = new SseEmitter(AiConstants.SSE_LONG_TIMEOUT_MS);
        if (ts == null || !"AWAITING_APPROVAL".equals(ts.getStatus())) {
            try { emitter.send("{\"error\":\"无待批准任务\",\"finish\":true}"); } catch (Exception ignored) {}
            emitter.complete();
            return emitter;
        }
        ts.setStatus("RUNNING");
        contextStore.save(ctx);
        // 在 SSE 请求线程同步驱动（dispatcher 内部自管数据源）
        taskExecutor.run(sid, tenantId, userId, emitter);
        return emitter;
    }

    /** 中止当前任务。 */
    @SaCheckLogin
    @PostMapping("/{sessionId}/abort")
    public R<Void> abort(@PathVariable @NotBlank String sessionId) {
        tenantGate.requireEnabled(LoginHelper.getTenantId());
        Long sid = Long.valueOf(sessionId);
        ConversationContext ctx = contextStore.load(sid);
        TaskState ts = ctx.getTaskState();
        if (ts != null) {
            ts.setStatus("ABORTED");
            contextStore.save(ctx);
            // 落库一条取消说明，保证历史「有来有回」（刷新不丢）
            String summary = OrchestrationSummary.build(ts, null, "ABORTED", null,
                aiProperties.getContext().getClosedValues(), aiProperties.getContext().getErrorValues());
            try {
                sessionService.appendAssistantMessage(sid, summary);
            } catch (Exception e) {
                log.warn("[AiTask] persist abort summary failed (session={}): {}", sid, e.getMessage());
            }
        }
        return R.ok();
    }
}
