package org.sdkj.ai.tools.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.assistant.AssistantChunk;
import org.sdkj.ai.assistant.AssistantResumeService;
import org.sdkj.ai.domain.AiToolInvocation;
import org.sdkj.ai.domain.vo.AiToolInvocationVo;
import org.sdkj.ai.mapper.AiToolInvocationMapper;
import org.sdkj.ai.safety.AiTenantGate;
import org.sdkj.ai.tools.dispatcher.ToolExecutor;
import org.sdkj.ai.AiConstants;
import org.sdkj.ai.tools.invocation.DefaultToolInvocationRecorder;
import org.sdkj.ai.tools.invocation.ToolInvocationRecorder;
import org.sdkj.ai.tools.store.ConfirmationStore;
import org.sdkj.ai.tools.store.PendingToolCall;
import org.sdkj.ai.tools.store.PendingToolCallStatus;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.ratelimiter.annotation.RateLimiter;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/ai/tool-calls")
@RequiredArgsConstructor
public class AiToolCallController {

    private final ConfirmationStore store;
    private final ToolExecutor executor;
    private final ToolInvocationRecorder recorder;
    private final AssistantResumeService resumeService;
    private final AiTenantGate tenantGate;
    private final ObjectMapper objectMapper;
    private final AiToolInvocationMapper invocationMapper;
    private final org.sdkj.ai.context.ContextStore contextStore;
    private final org.sdkj.ai.context.ConversationContextService conversationContextService;
    private final org.sdkj.ai.context.TaskExecutor taskExecutor;
    private final org.sdkj.ai.context.BranchEvaluator branchEvaluator;
    private final org.sdkj.ai.config.AiProperties aiProperties;
    private final org.sdkj.ai.assistant.SessionService sessionService;

    /** 按 messageId 查 Tool 调用详情 — 前端 TOOL 角色消息点击展开 */
    @SaCheckLogin
    @GetMapping("/by-message/{messageId}")
    public R<AiToolInvocationVo> getByMessageId(@PathVariable Long messageId) {
        tenantGate.requireEnabled(LoginHelper.getTenantId());
        var vo = invocationMapper.selectVoOne(
            new LambdaQueryWrapper<AiToolInvocation>()
                .eq(AiToolInvocation::getMessageId, messageId)
        );
        return vo != null ? R.ok(vo) : R.fail("invocation not found");
    }

    /** 查待确认状态 — 前端轮询(SSE 已关流后) */
    @SaCheckLogin
    @GetMapping("/{callId}")
    public R<PendingToolCall> get(@PathVariable @NotBlank String callId) {
        tenantGate.requireEnabled(LoginHelper.getTenantId());
        return store.findByCallId(callId)
            .filter(c -> requireOwner(c))
            .map(R::ok)
            .orElseGet(() -> R.fail("call expired or not found"));
    }

    /**
     * 用户拒绝 — 更新状态 + 启动第二阶段 SSE 让 LLM 告知用户操作已取消。
     */
    @SaCheckLogin
    @RateLimiter(time = 60, count = 30, key = "#{T(org.sdkj.common.satoken.utils.LoginHelper).getUserId()}")
    @PostMapping(value = "/{callId}/reject", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter reject(@PathVariable @NotBlank String callId) {
        tenantGate.requireEnabled(LoginHelper.getTenantId());
        var transitioned = store.transition(callId,
            PendingToolCallStatus.PENDING, PendingToolCallStatus.REJECTED,
            c -> c.setDecidedAt(Instant.now())
        );
        if (transitioned.isEmpty()) {
            throw new IllegalStateException("call state mismatch or expired: " + callId);
        }
        PendingToolCall call = transitioned.get();
        if (!requireOwner(call)) {
            throw new IllegalStateException("not your tool call: " + callId);
        }

        SseEmitter emitter = new SseEmitter(AiConstants.SSE_TIMEOUT_MS);
        Disposable disp = resumeService.resumeAfterRejection(
            call.getTenantId(), call.getUserId(), call.getSessionId(), call.getToolName()
        ).doOnNext(chunk -> {
            try {
                emitter.send(SseEmitter.event()
                    .data(objectMapper.writeValueAsString(chunk), MediaType.APPLICATION_JSON));
            } catch (Exception e) { emitter.completeWithError(e); }
        }).doOnComplete(emitter::complete)
          .doOnError(emitter::completeWithError)
          .subscribe();
        emitter.onCompletion(disp::dispose);
        emitter.onTimeout(disp::dispose);
        emitter.onError(e -> disp.dispose());
        return emitter;
    }

    /**
     * 用户确认 — 同步执行 Tool,SSE 流式返回 LLM 后续生成。
     */
    @SaCheckLogin
    @RateLimiter(time = 60, count = 10, key = "#{T(org.sdkj.common.satoken.utils.LoginHelper).getUserId()}")
    @PostMapping(value = "/{callId}/confirm", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter confirm(@PathVariable @NotBlank String callId) {
        tenantGate.requireEnabled(LoginHelper.getTenantId());

        var transitioned = store.transition(callId,
            PendingToolCallStatus.PENDING, PendingToolCallStatus.APPROVED,
            c -> c.setDecidedAt(Instant.now())
        );
        if (transitioned.isEmpty()) {
            throw new IllegalStateException("call state mismatch or expired: " + callId);
        }
        PendingToolCall call = transitioned.get();
        if (!requireOwner(call)) {
            throw new IllegalStateException("not your tool call: " + callId);
        }

        SseEmitter emitter = new SseEmitter(AiConstants.SSE_TIMEOUT_MS);
        long t0 = System.currentTimeMillis();
        try {
            var outcome = executor.execute(call);
            int latency = (int) (System.currentTimeMillis() - t0);
            String execStatus = outcome.dryRun()
                ? AiConstants.ToolExecStatus.DRY_RUN.name()
                : AiConstants.ToolExecStatus.SUCCESS.name();

            store.transition(callId,
                PendingToolCallStatus.APPROVED, PendingToolCallStatus.EXECUTED,
                c -> {
                    c.setResult(outcome.resultJson());
                    c.setExecutedAt(Instant.now());
                }
            );
            Long toolMessageId = recorder.record(call, outcome.resultJson(), execStatus, latency, null);

            // 立即通知前端 Tool 执行结果（TOOL 角色消息卡片）
            String summary = DefaultToolInvocationRecorder.buildSummary(call.getToolName(), execStatus, null);
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                AssistantChunk.builder().toolCallResult(
                    AssistantChunk.ToolResultView.builder()
                        .messageId(toolMessageId)
                        .toolName(call.getToolName())
                        .status(execStatus)
                        .summary(summary)
                        .build()
                ).build()
            ), MediaType.APPLICATION_JSON));

            // 能力 C：若该确认属于运行中的编排任务 → 评估分支并续跑后续步骤
            var ctx = contextStore.load(call.getSessionId());
            var ts = ctx.getTaskState();
            if (ts != null && "AWAITING_CONFIRM".equals(ts.getStatus())
                && callId.equals(ts.getPendingCallId())) {
                // 写步结果入会话记忆，供后续分支判断
                conversationContextService.recordResults(call.getSessionId(), java.util.List.of(
                    org.sdkj.ai.tools.dispatcher.ToolCallResult.ToolExecResult.builder()
                        .toolName(call.getToolName()).resultJson(outcome.resultJson()).build()));
                var c2 = contextStore.load(call.getSessionId());
                var step = c2.getTaskState().stepById(c2.getTaskState().getCurrentStepId());
                if (step != null) step.setStepStatus("COMPLETED");   // 写步已确认执行 → 计入收尾复述
                String next = branchEvaluator.next(step, c2.getFacts(),
                    aiProperties.getContext().getClosedValues(), aiProperties.getContext().getErrorValues());
                c2.getTaskState().setCurrentStepId(next);
                c2.getTaskState().setStatus("RUNNING");
                c2.getTaskState().setPendingCallId(null);
                contextStore.save(c2);
                taskExecutor.run(call.getSessionId(), call.getTenantId(), call.getUserId(), emitter);
            } else {
                // 普通写 Tool 确认：拉起 LLM 继续生成（原 Phase 2B 行为）
                Disposable disp = resumeService.resumeAfterToolCall(
                    call.getTenantId(), call.getUserId(), call.getSessionId(), outcome.resultJson()
                ).doOnNext(chunk -> {
                    try {
                        emitter.send(SseEmitter.event()
                            .data(objectMapper.writeValueAsString(chunk), MediaType.APPLICATION_JSON));
                    } catch (Exception e) { emitter.completeWithError(e); }
                }).doOnComplete(emitter::complete)
                  .doOnError(emitter::completeWithError)
                  .subscribe();
                emitter.onCompletion(disp::dispose);
                emitter.onTimeout(disp::dispose);
                emitter.onError(e -> disp.dispose());
            }

        } catch (SecurityException se) {
            int latency = (int) (System.currentTimeMillis() - t0);
            log.warn("[AiToolCall] permission denied for {}: {}", callId, se.getMessage());
            store.transition(callId,
                PendingToolCallStatus.APPROVED, PendingToolCallStatus.REJECTED,
                c -> { c.setResult("permission denied"); c.setDecidedAt(Instant.now()); }
            );
            Long failMsgId = recorder.record(call, null, AiConstants.ToolExecStatus.FAILED.name(), latency, "permission denied: " + se.getMessage());
            AbortClosing abort = abortOrchestrationOnFailure(callId, call, "权限不足:" + se.getMessage());
            sendFailureChunk(emitter, call, failMsgId, "权限不足:" + se.getMessage(), abort);

        } catch (Exception e) {
            // ToolExecutor 已解包 InvocationTargetException，此处 e 是原始业务异常
            String errMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            int latency = (int) (System.currentTimeMillis() - t0);
            log.error("[AiToolCall] confirm execution failed for {}: {}", callId, errMsg, e);
            store.transition(callId,
                PendingToolCallStatus.APPROVED, PendingToolCallStatus.FAILED,
                c -> { c.setResult(errMsg); c.setExecutedAt(Instant.now()); }
            );
            Long failMsgId = recorder.record(call, null, AiConstants.ToolExecStatus.FAILED.name(), latency, errMsg);
            AbortClosing abort = abortOrchestrationOnFailure(callId, call, "执行出错：" + errMsg);
            sendFailureChunk(emitter, call, failMsgId, "tool execution failed: " + errMsg, abort);
        }
        return emitter;
    }

    private boolean requireOwner(PendingToolCall call) {
        return call.getUserId() != null && call.getUserId().equals(LoginHelper.getUserId());
    }

    /**
     * 写步在编排中执行失败 → 把任务干净中止、落库一句收尾，并把收尾信息返回给调用方以便即时流式下发，
     * 避免 taskState 滞留 AWAITING_CONFIRM 僵尸态、且历史只剩原始报错。仅当该 callId 正是当前编排
     * 等待确认的写步时才动；非编排写步返回 null。
     */
    private AbortClosing abortOrchestrationOnFailure(String callId, PendingToolCall call, String reason) {
        try {
            var ctx = contextStore.load(call.getSessionId());
            var ts = ctx.getTaskState();
            if (ts != null && "AWAITING_CONFIRM".equals(ts.getStatus())
                && callId.equals(ts.getPendingCallId())) {
                ts.setStatus("ABORTED");
                ts.setPendingCallId(null);
                contextStore.save(ctx);
                String summary = org.sdkj.ai.context.OrchestrationSummary.build(ts, null, "ABORTED", reason,
                    aiProperties.getContext().getClosedValues(), aiProperties.getContext().getErrorValues());
                Long mid = sessionService.appendAssistantMessage(call.getSessionId(), summary);
                int total = ts.getSteps() == null ? 0 : ts.getSteps().size();
                return new AbortClosing(mid, summary, ts.getTaskId(), total);
            }
        } catch (Exception ex) {
            log.warn("[AiToolCall] abort orchestration on failure failed (call={}): {}", callId, ex.getMessage());
        }
        return null;
    }

    /** 编排写步失败时的中止收尾信息（用于即时流式下发，与 TaskExecutor.finishWithSummary 帧序列一致）。 */
    private record AbortClosing(Long messageId, String summary, String taskId, int totalSteps) {}

    private void sendFailureChunk(SseEmitter emitter, PendingToolCall call,
                                   Long messageId, String error, AbortClosing abort) {
        try {
            String summary = DefaultToolInvocationRecorder.buildSummary(call.getToolName(), AiConstants.ToolExecStatus.FAILED, error);
            AssistantChunk.ToolResultView toolView = AssistantChunk.ToolResultView.builder()
                .messageId(messageId)
                .toolName(call.getToolName())
                .status(AiConstants.ToolExecStatus.FAILED.name())
                .summary(summary)
                .build();

            if (abort == null) {
                // 普通写 Tool 失败(非编排)：带 error 的终止帧 → 前端 onError 提示(原 Phase 2B 行为)
                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                    AssistantChunk.builder().toolCallResult(toolView).error(error).finish(true).build()
                ), MediaType.APPLICATION_JSON));
            } else {
                // 编排写步失败：前端见 chunk.error 会 onError+return 丢弃后续帧，故全程不带 error，
                // 复刻 TaskExecutor.finishWithSummary 帧序列(toolCallResult → taskProgress → delta → messageId)，
                // 即时显示「计划已中止」收尾、与成功路径体验对齐，messageId 为真实库 id 刷新不丢。
                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                    AssistantChunk.builder().toolCallResult(toolView).finish(false).build()
                ), MediaType.APPLICATION_JSON));
                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                    AssistantChunk.builder()
                        .taskProgress(AssistantChunk.TaskProgressView.builder()
                            .taskId(abort.taskId()).status("ABORTED")
                            .currentStep(abort.totalSteps()).totalSteps(abort.totalSteps())
                            .message("ABORTED").build())
                        .finish(false).build()
                ), MediaType.APPLICATION_JSON));
                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                    AssistantChunk.builder().delta(abort.summary())
                        .sessionId(call.getSessionId()).finish(false).build()
                ), MediaType.APPLICATION_JSON));
                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                    AssistantChunk.builder().sessionId(call.getSessionId())
                        .messageId(abort.messageId()).finish(true).build()
                ), MediaType.APPLICATION_JSON));
            }
            emitter.complete();
        } catch (Exception ignored) {
            log.debug("[AiToolCall] sendFailureChunk failed: {}", ignored.getMessage());
            emitter.completeWithError(new RuntimeException(error));
        }
    }
}
