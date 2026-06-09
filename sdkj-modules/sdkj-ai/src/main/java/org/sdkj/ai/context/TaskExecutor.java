package org.sdkj.ai.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.assistant.AssistantChunk;
import org.sdkj.ai.assistant.SessionService;
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
    private final SessionService sessionService;

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
                    finishWithSummary(emitter, sessionId, ts, ctx.getFacts(), "DONE", null);
                    return;
                }
                TaskStep step = ts.stepById(curId);
                if (step == null) {
                    ts.setStatus("DONE");
                    contextStore.save(ctx);
                    meterRegistry.counter("ai.ctx.task.completed").increment();
                    finishWithSummary(emitter, sessionId, ts, ctx.getFacts(), "DONE", null);
                    return;
                }

                ToolMetadata md = registry.resolve(step.getToolName());
                if (md == null || md.risk() == RiskLevel.CRITICAL) {     // CRITICAL 不入编排（双保险）
                    ts.setStatus("ABORTED");
                    contextStore.save(ctx);
                    meterRegistry.counter("ai.ctx.task.aborted").increment();
                    finishWithSummary(emitter, sessionId, ts, ctx.getFacts(), "ABORTED", "步骤不可执行：" + step.getDesc());
                    return;
                }

                Map<String, Object> args = contextService.enrichArgs(sessionId, step.getToolName(),
                    new HashMap<>(step.getPresetArgs() == null ? Map.of() : step.getPresetArgs()));
                var req = new ToolCallDispatcher.ToolCallRequest(step.getToolName(), args, tenantId, userId, sessionId, null);

                try {
                    ToolCallResult tcr = dispatcher.dispatch(List.of(req));      // LOW → 直接执行
                    contextService.recordResults(sessionId, tcr.getToolResults());
                    // 评估分支 → 推进（在 reload 后的 c2 上标记完成，避免被覆盖）
                    ConversationContext c2 = contextStore.load(sessionId);
                    TaskStep curStep = c2.getTaskState().stepById(curId);
                    if (curStep != null) curStep.setStepStatus("COMPLETED");
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
                    finishWithSummary(emitter, sessionId, c2.getTaskState(), c2.getFacts(), "ABORTED", "包含禁止操作，已中止");
                    return;
                }
            }
            // 超步数保护
            ConversationContext cOver = contextStore.load(sessionId);
            if (cOver.getTaskState() != null) {
                cOver.getTaskState().setStatus("ABORTED");
                contextStore.save(cOver);
            }
            meterRegistry.counter("ai.ctx.task.aborted").increment();
            finishWithSummary(emitter, sessionId, cOver.getTaskState(), cOver.getFacts(), "ABORTED", "任务步数超限");
        } catch (Exception e) {
            log.error("[TaskExecutor] run failed (session={}): {}", sessionId, e.getMessage(), e);
            try {
                ConversationContext cErr = contextStore.load(sessionId);
                if (cErr.getTaskState() != null) {
                    cErr.getTaskState().setStatus("ABORTED");
                    contextStore.save(cErr);
                }
                finishWithSummary(emitter, sessionId, cErr.getTaskState(), cErr.getFacts(), "ABORTED", "执行出错：" + safeMsg(e));
            } catch (Exception inner) {
                emitter.completeWithError(e);
            }
        }
    }

    /**
     * 终态收尾：落库一句话结论 + 流式下发（taskProgress 终态帧 + delta + messageId），并关流。
     * 前端复用现有"流式 delta → onDone 落地真消息"范式，刷新/切会话不丢（spec §6.5「一句收尾」）。
     */
    private void finishWithSummary(SseEmitter emitter, Long sessionId, TaskState ts,
                                   List<EntityFact> facts, String status, String reason) {
        AiProperties.Context cfg = aiProperties.getContext();
        EntityFact vf = latestFactWithKey(facts, "valveStatus");
        String summary = OrchestrationSummary.build(ts, vf, status, reason,
            cfg.getClosedValues(), cfg.getErrorValues());
        Long mid = null;
        try {
            mid = sessionService.appendAssistantMessage(sessionId, summary);
        } catch (Exception e) {
            log.warn("[TaskExecutor] persist closing summary failed (session={}): {}", sessionId, e.getMessage());
        }
        int total = ts == null || ts.getSteps() == null ? 0 : ts.getSteps().size();
        // 1) taskProgress 终态帧（spec：DONE/ABORTED 仍发 taskProgress）
        send(emitter, AssistantChunk.builder()
            .taskProgress(AssistantChunk.TaskProgressView.builder()
                .taskId(ts == null ? null : ts.getTaskId()).status(status)
                .currentStep(total).totalSteps(total).message(status).build())
            .finish(false).build());
        // 2) 收尾语作为正常 assistant delta
        send(emitter, AssistantChunk.builder().delta(summary).sessionId(sessionId).finish(false).build());
        // 3) 终止帧带 messageId（前端 onDone 用真实 id 落地消息）
        send(emitter, AssistantChunk.builder().sessionId(sessionId).messageId(mid).finish(true).build());
        emitter.complete();
    }

    private EntityFact latestFactWithKey(List<EntityFact> facts, String key) {
        if (facts == null) return null;
        for (int i = facts.size() - 1; i >= 0; i--) {
            if (facts.get(i).keys() != null && facts.get(i).keys().containsKey(key)) return facts.get(i);
        }
        return null;
    }

    private String safeMsg(Exception e) {
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
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
