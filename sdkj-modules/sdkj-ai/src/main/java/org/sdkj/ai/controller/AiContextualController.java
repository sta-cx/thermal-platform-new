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
import org.sdkj.ai.core.GenericPromptBuilder;
import org.sdkj.ai.core.PromptPayload;
import org.sdkj.ai.core.RouteWhitelistProvider;
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

import java.time.Duration;
import java.util.List;
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
    private final RouteWhitelistProvider routeWhitelistProvider;

    @RateLimiter(
        time = 60,
        count = 30,
        key = "#{T(org.sdkj.common.satoken.utils.LoginHelper).getUserId()}"
    )
    @SaCheckLogin
    @PostMapping("/contextual-view")
    public R<ContextualView> getContextualView(@RequestBody @Valid ContextualRequest req) {

        String tenantId = LoginHelper.getTenantId();
        String feature = "contextual";

        // 1. 熔断检查
        circuitBreaker.checkAllowed(feature, tenantId);

        // 2. 获取可用路由列表（每次查 DB，轻量不缓存）
        List<String> availableRoutes = routeWhitelistProvider.getAvailableRoutes();

        // 3. 路由匹配
        ContextualPrompt prompt = registry.match(req.getRoute());
        if (prompt == null) {
            return handleGenericMode(req, tenantId, feature, availableRoutes);
        }

        // 4. 权限校验(可选)
        if (prompt.requiredPermission() != null) {
            cn.dev33.satoken.stp.StpUtil.checkPermission(prompt.requiredPermission());
        }

        // 5. 缓存查
        String cacheKey = keyBuilder.build(tenantId, req, prompt);
        ContextualView cached = cache.get(cacheKey);
        meterRegistry.counter("ai.contextual.cache",
            Tags.of("result", cached != null ? "hit" : "miss"))
            .increment();
        if (cached != null) {
            log.debug("AI view cache HIT: {}", cacheKey);
            return R.ok(cached);
        }

        // 6. 业务模块拉数据 + 构 prompt
        PromptPayload payload;
        try {
            payload = prompt.buildPrompt(req);
        } catch (Exception e) {
            log.error("ContextualPrompt buildPrompt failed: {}", prompt.getClass().getSimpleName(), e);
            circuitBreaker.recordFailure(feature, tenantId);
            throw new AiUnavailableException("AI 旁注准备数据失败", e);
        }

        // 7. 调用 LLM(Advisor Chain 自动跑)
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

        // 8. 缓存 + 返回
        if (payload.getCacheKey() != null) {
            cache.put(cacheKey, view, prompt.cacheTtl());
        }
        return R.ok(view);
    }

    /**
     * 通用模式：无专用 Prompt 的页面走 LLM 生成通用旁注
     */
    private R<ContextualView> handleGenericMode(ContextualRequest req, String tenantId,
                                                 String feature, List<String> availableRoutes) {
        // 缓存查
        String cacheKey = keyBuilder.buildGenericKey(tenantId, req.getRoute());
        ContextualView cached = cache.get(cacheKey);
        meterRegistry.counter("ai.contextual.cache",
            Tags.of("result", cached != null ? "hit" : "miss"))
            .increment();
        if (cached != null) {
            log.debug("AI generic view cache HIT: {}", cacheKey);
            return R.ok(cached);
        }

        // 构建通用 prompt
        PromptPayload payload = GenericPromptBuilder.build(
            req.getRoute(), req.getRouteName(), availableRoutes, req.getExtraContext()
        );

        // 调用 LLM
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
                    a.param("ai.promptName", "GenericPrompt");
                })
                .call()
                .entity(ContextualView.class);

            circuitBreaker.recordSuccess(feature, tenantId);
        } catch (RuntimeException e) {
            circuitBreaker.recordFailure(feature, tenantId);
            log.error("LLM call failed for generic prompt on route {}", req.getRoute(), e);
            throw new AiUnavailableException("AI 助手暂时不可用,稍后重试", e);
        }

        // 缓存 30 分钟 + 返回
        cache.put(cacheKey, view, Duration.ofMinutes(30));
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
