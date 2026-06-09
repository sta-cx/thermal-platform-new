package org.sdkj.ai.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.assistant.AssistantChunk;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.dispatcher.*;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.sdkj.ai.tools.store.ConfirmationStore;
import org.sdkj.ai.tools.store.PendingToolCall;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

/**
 * 编排执行器：从 currentStepId 起，连续执行 LOW 步（按 BranchEvaluator 推进），
 * 遇写步抛 PendingConfirmationException → 暂停等确认。复用 ToolCallDispatcher。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutor {

    private final ContextStore contextStore;
    private final ConversationContextService contextService;
    private final ToolCallDispatcher dispatcher;
    private final BranchEvaluator branchEvaluator;
    private final ToolRegistry registry;
    private final ConfirmationStore confirmationStore;
    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    /** 从当前 step 起推进。在 emitter 上 send taskProgress/toolCallPending，结束/暂停时 complete。 */
    public void run(Long sessionId, String tenantId, Long userId, SseEmitter emitter) {
        AiProperties.Context cfg = aiProperties.getContext();
        int guard = 0;
        try {
            while (guard++ < cfg.getMaxOrchestrationSteps() + 1) {
                ConversationContext ctx = contextStore.load(sessionId);
                TaskState ts = ctx.getTaskState();
                if (ts == null || !"RUNNING".equals(ts.getStatus())) { emitter.complete(); return; }

                String curId = ts.getCurrentStepId();
                if (curId == null || "END".equals(curId)) {
                    ts.setStatus("DONE");
                    contextStore.save(ctx);
                    meterRegistry.counter("ai.ctx.task.completed").increment();
                    send(emitter, progress(ts, "DONE", "任务完成"));
                    emitter.complete(); return;
                }
                TaskStep step = ts.stepById(curId);
                if (step == null) { ts.setStatus("DONE"); contextStore.save(ctx); meterRegistry.counter("ai.ctx.task.completed").increment(); send(emitter, progress(ts, "DONE", "任务完成")); emitter.complete(); return; }

                ToolMetadata md = registry.resolve(step.getToolName());
                if (md == null || md.risk() == RiskLevel.CRITICAL) {     // CRITICAL 不入编排（双保险）
                    ts.setStatus("ABORTED");
                    contextStore.save(ctx);
                    meterRegistry.counter("ai.ctx.task.aborted").increment();
                    send(emitter, progress(ts, "ABORTED", "步骤不可执行：" + step.getToolName()));
                    emitter.complete(); return;
                }

                Map<String, Object> args = contextService.enrichArgs(sessionId, step.getToolName(),
                    new HashMap<>(step.getPresetArgs() == null ? Map.of() : step.getPresetArgs()));
                var req = new ToolCallDispatcher.ToolCallRequest(step.getToolName(), args, tenantId, userId, sessionId, null);

                try {
                    ToolCallResult tcr = dispatcher.dispatch(List.of(req));      // LOW → 直接执行
                    contextService.recordResults(sessionId, tcr.getToolResults());
                    // 评估分支 → 推进
                    ConversationContext c2 = contextStore.load(sessionId);
                    String next = branchEvaluator.next(step, c2.getFacts(), cfg.getClosedValues(), cfg.getErrorValues());
                    c2.getTaskState().setCurrentStepId(next);
                    contextStore.save(c2);
                    send(emitter, progress(c2.getTaskState(), "RUNNING", step.getDesc() + " ✓"));
                    // 继续循环执行下一 LOW 步
                } catch (PendingConfirmationException pe) {
                    // 写步：暂停等确认
                    ConversationContext c2 = contextStore.load(sessionId);
                    c2.getTaskState().setStatus("AWAITING_CONFIRM");
                    c2.getTaskState().setPendingCallId(pe.getCallId());
                    contextStore.save(c2);
                    send(emitter, progress(c2.getTaskState(), "AWAITING_CONFIRM", "等待确认：" + step.getDesc()));
                    sendPending(emitter, pe.getCallId());
                    emitter.complete(); return;
                } catch (CriticalToolRejectedException ce) {
                    ConversationContext c2 = contextStore.load(sessionId);
                    c2.getTaskState().setStatus("ABORTED");
                    contextStore.save(c2);
                    meterRegistry.counter("ai.ctx.task.aborted").increment();
                    send(emitter, progress(c2.getTaskState(), "ABORTED", "包含禁止操作，已中止"));
                    emitter.complete(); return;
                }
            }
            // 超步数保护
            send(emitter, AssistantChunk.builder().error("任务步数超限").finish(true).build());
            emitter.complete();
        } catch (Exception e) {
            log.error("[TaskExecutor] run failed (session={}): {}", sessionId, e.getMessage(), e);
            emitter.completeWithError(e);
        }
    }

    private AssistantChunk progress(TaskState ts, String status, String msg) {
        int total = ts.getSteps() == null ? 0 : ts.getSteps().size();
        int cur = 1;
        if (ts.getCurrentStepId() != null && !"END".equals(ts.getCurrentStepId()) && ts.getSteps() != null) {
            for (int i = 0; i < ts.getSteps().size(); i++) {
                if (ts.getSteps().get(i).getStepId().equals(ts.getCurrentStepId())) { cur = i + 1; break; }
            }
        }
        return AssistantChunk.builder()
            .taskProgress(AssistantChunk.TaskProgressView.builder()
                .taskId(ts.getTaskId()).status(status).currentStep(cur).totalSteps(total).message(msg).build())
            .finish("DONE".equals(status) || "ABORTED".equals(status))
            .build();
    }

    private void sendPending(SseEmitter emitter, String callId) {
        PendingToolCall ptc = confirmationStore.findByCallId(callId).orElse(null);
        if (ptc == null) return;
        ToolMetadata md = registry.byName(ptc.getToolName());
        send(emitter, AssistantChunk.builder()
            .toolCallPending(AssistantChunk.PendingToolCallView.builder()
                .callId(callId)
                .toolName(ptc.getToolName())
                .risk(ptc.getRisk().name())
                .arguments(ptc.getArguments())
                .description(md == null ? ptc.getToolName() : md.description())
                .countdownSeconds(ptc.getRisk() == RiskLevel.HIGH ? 3 : 0)
                .build())
            .finish(true)
            .build());
    }

    private void send(SseEmitter emitter, AssistantChunk chunk) {
        try {
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(chunk), MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.warn("[TaskExecutor] send failed: {}", e.getMessage());
        }
    }
}
