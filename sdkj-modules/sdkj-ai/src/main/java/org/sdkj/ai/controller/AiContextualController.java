package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.cache.AiViewCache;
import org.sdkj.ai.cache.CacheKeyBuilder;
import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualPromptRegistry;
import org.sdkj.ai.core.ContextualRequest;
import org.sdkj.ai.core.ContextualView;
import org.sdkj.ai.core.GenericContextualView;
import org.sdkj.ai.core.PromptPayload;
import org.sdkj.ai.exception.AiUnavailableException;
import org.sdkj.ai.safety.AiCircuitBreaker;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.ratelimiter.annotation.RateLimiter;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiContextualController {

    @Resource(name = "contextualChatClient")
    private ChatClient contextualChatClient;

    @Resource
    private MeterRegistry meterRegistry;

    private final ContextualPromptRegistry registry;
    private final AiViewCache cache;
    private final CacheKeyBuilder keyBuilder;
    private final AiCircuitBreaker circuitBreaker;

    @RateLimiter(time = 60, count = 30)
    @SaCheckLogin
    @PostMapping("/contextual-view")
    public R<ContextualView> getContextualView(@RequestBody @Valid ContextualRequest req) {

        String tenantId = LoginHelper.getTenantId();
        String feature = "contextual";

        // 1. 熔断检查
        circuitBreaker.checkAllowed(feature, tenantId);

        // 2. 路由匹配
        ContextualPrompt prompt = registry.match(req.getRoute());
        if (prompt == null) {
            return R.ok(GenericContextualView.fallbackFor(req));
        }

        // 3. 权限校验(可选)
        if (prompt.requiredPermission() != null) {
            cn.dev33.satoken.stp.StpUtil.checkPermission(prompt.requiredPermission());
        }

        // 4. 缓存查
        String cacheKey = keyBuilder.build(tenantId, req, prompt);
        ContextualView cached = cache.get(cacheKey);
        meterRegistry.counter("ai.contextual.cache",
            Tags.of("result", cached != null ? "hit" : "miss"))
            .increment();
        if (cached != null) {
            log.debug("AI view cache HIT: {}", cacheKey);
            return R.ok(cached);
        }

        // 5. 业务模块拉数据 + 构 prompt
        PromptPayload payload;
        try {
            payload = prompt.buildPrompt(req);
        } catch (Exception e) {
            log.error("ContextualPrompt buildPrompt failed: {}", prompt.getClass().getSimpleName(), e);
            circuitBreaker.recordFailure(feature, tenantId);
            throw new AiUnavailableException("AI 旁注准备数据失败", e);
        }

        // 6. 调用 LLM(Advisor Chain 自动跑)
        Map<String, Object> vars = payload.getTemplateVars() == null
            ? Map.of()
            : payload.getTemplateVars();

        ContextualView view;
        try {
            view = contextualChatClient.prompt()
                .system(payload.getSystemPrompt())
                .user(spec -> {
                    spec.text(payload.getUserPromptTemplate());
                    vars.forEach(spec::param);
                })
                .advisors(a -> {
                    a.param("ai.route", req.getRoute());
                    a.param("ai.promptName", prompt.getClass().getSimpleName());
                })
                .call()
                .entity(prompt.viewSchema());

            circuitBreaker.recordSuccess(feature, tenantId);
        } catch (RuntimeException e) {
            circuitBreaker.recordFailure(feature, tenantId);
            log.error("LLM call failed for prompt {}", prompt.getClass().getSimpleName(), e);
            throw new AiUnavailableException("AI 助手暂时不可用,稍后重试", e);
        }

        // 7. 缓存 + 返回
        if (payload.getCacheKey() != null) {
            cache.put(cacheKey, view, prompt.cacheTtl());
        }
        return R.ok(view);
    }

    @ExceptionHandler(AiUnavailableException.class)
    public ResponseEntity<R<Void>> handleUnavailable(AiUnavailableException e) {
        log.warn("AI unavailable: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(R.fail(503, e.getMessage()));
    }
}
