package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.assistant.AssistantRequest;
import org.sdkj.ai.assistant.AssistantResponse;
import org.sdkj.ai.assistant.AssistantService;
import org.sdkj.ai.assistant.SessionService;
import org.sdkj.ai.domain.AiChatMessage;
import org.sdkj.ai.domain.AiChatSession;
import org.sdkj.ai.exception.AiDisabledException;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.ratelimiter.annotation.RateLimiter;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/ai/assistant")
@RequiredArgsConstructor
public class AiAssistantController {

    private final AssistantService assistantService;
    private final SessionService sessionService;
    private final org.sdkj.ai.safety.AiTenantGate aiTenantGate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SaCheckLogin
    @RateLimiter(time = 60, count = 20,
        key = "#{T(org.sdkj.common.satoken.utils.LoginHelper).getUserId()}")
    @PostMapping("/chat")
    public R<AssistantResponse> chat(@RequestBody @Valid AssistantRequest req) {
        aiTenantGate.requireEnabled(LoginHelper.getTenantId());
        return R.ok(assistantService.chat(req));
    }

    @SaCheckLogin
    @RateLimiter(time = 60, count = 20,
        key = "#{T(org.sdkj.common.satoken.utils.LoginHelper).getUserId()}")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody @Valid AssistantRequest req) {
        aiTenantGate.requireEnabled(LoginHelper.getTenantId());
        SseEmitter emitter = new SseEmitter(60_000L);

        assistantService.streamChat(req)
            .doOnNext(chunk -> {
                try {
                    emitter.send(SseEmitter.event()
                        .data(objectMapper.writeValueAsString(chunk), MediaType.APPLICATION_JSON));
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            })
            .doOnComplete(emitter::complete)
            .doOnError(emitter::completeWithError)
            .subscribe();

        return emitter;
    }

    @SaCheckLogin
    @GetMapping("/sessions")
    public R<List<AiChatSession>> sessions() {
        String tenantId = LoginHelper.getTenantId();
        Long userId = LoginHelper.getUserId();
        return R.ok(sessionService.listByUser(tenantId, userId));
    }

    @SaCheckLogin
    @GetMapping("/session/{id}/messages")
    public R<List<AiChatMessage>> messages(@PathVariable Long id) {
        String tenantId = LoginHelper.getTenantId();
        Long userId = LoginHelper.getUserId();
        sessionService.requireOwned(id, tenantId, userId);
        return R.ok(sessionService.listMessages(id));
    }

    @SaCheckLogin
    @DeleteMapping("/session/{id}")
    public R<Void> deleteSession(@PathVariable Long id) {
        String tenantId = LoginHelper.getTenantId();
        Long userId = LoginHelper.getUserId();
        sessionService.requireOwned(id, tenantId, userId);
        sessionService.delete(id);
        return R.ok();
    }

    @ExceptionHandler(AiDisabledException.class)
    public ResponseEntity<R<Void>> handleDisabled(AiDisabledException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(R.fail(503, e.getMessage()));
    }
}
