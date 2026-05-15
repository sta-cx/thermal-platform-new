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

    /** 用户拒绝 */
    @SaCheckLogin
    @RateLimiter(time = 60, count = 30, key = "#{T(org.sdkj.common.satoken.utils.LoginHelper).getUserId()}")
    @PostMapping("/{callId}/reject")
    public R<Void> reject(@PathVariable @NotBlank String callId) {
        tenantGate.requireEnabled(LoginHelper.getTenantId());
        var opt = store.findByCallId(callId);
        if (opt.isEmpty() || !requireOwner(opt.get())) {
            return R.fail("call expired or not found");
        }
        boolean ok = store.transition(callId,
            PendingToolCallStatus.PENDING, PendingToolCallStatus.REJECTED,
            c -> c.setDecidedAt(Instant.now())
        ).isPresent();
        return ok ? R.ok() : R.fail("state mismatch");
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

        SseEmitter emitter = new SseEmitter(60_000L);
        long t0 = System.currentTimeMillis();
        try {
            var outcome = executor.execute(call);
            int latency = (int) (System.currentTimeMillis() - t0);
            String execStatus = outcome.dryRun() ? "DRY_RUN" : "SUCCESS";

            store.transition(callId,
                PendingToolCallStatus.APPROVED, PendingToolCallStatus.EXECUTED,
                c -> {
                    c.setResult(outcome.resultJson());
                    c.setExecutedAt(Instant.now());
                }
            );
            recorder.record(call, outcome.resultJson(), execStatus, latency, null);

            // 拉起 LLM 继续生成（传入工具执行结果作为上下文）
            resumeService.resumeAfterToolCall(
                call.getTenantId(), call.getUserId(), call.getSessionId(), outcome.resultJson()
            ).doOnNext(chunk -> {
                try {
                    emitter.send(SseEmitter.event()
                        .data(objectMapper.writeValueAsString(chunk), MediaType.APPLICATION_JSON));
                } catch (Exception e) { emitter.completeWithError(e); }
            }).doOnComplete(emitter::complete)
              .doOnError(emitter::completeWithError)
              .subscribe();

        } catch (SecurityException se) {
            int latency = (int) (System.currentTimeMillis() - t0);
            log.warn("[AiToolCall] permission denied for {}: {}", callId, se.getMessage());
            store.transition(callId,
                PendingToolCallStatus.APPROVED, PendingToolCallStatus.REJECTED,
                c -> { c.setResult("permission denied"); c.setDecidedAt(Instant.now()); }
            );
            recorder.record(call, null, "FAILED", latency, "permission denied: " + se.getMessage());
            try {
                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                    AssistantChunk.builder().error("权限不足:" + se.getMessage()).finish(true).build()
                ), MediaType.APPLICATION_JSON));
                emitter.complete();
            } catch (Exception ignored) { emitter.completeWithError(se); }

        } catch (Exception e) {
            int latency = (int) (System.currentTimeMillis() - t0);
            log.error("[AiToolCall] confirm execution failed for {}: {}", callId, e.getMessage(), e);
            store.transition(callId,
                PendingToolCallStatus.APPROVED, PendingToolCallStatus.FAILED,
                c -> { c.setResult(e.getMessage()); c.setExecutedAt(Instant.now()); }
            );
            recorder.record(call, null, "FAILED", latency, e.getMessage());
            try {
                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                    AssistantChunk.builder()
                        .error("tool execution failed: " + e.getMessage())
                        .finish(true).build()
                ), MediaType.APPLICATION_JSON));
                emitter.complete();
            } catch (Exception ignored) {
                emitter.completeWithError(e);
            }
        }
        return emitter;
    }

    private boolean requireOwner(PendingToolCall call) {
        return call.getUserId() != null && call.getUserId().equals(LoginHelper.getUserId());
    }
}
