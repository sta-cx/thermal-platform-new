package org.sdkj.ai.config;

import cn.dev33.satoken.exception.SaTokenContextException;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.exception.AiDisabledException;
import org.sdkj.common.core.domain.R;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * AI 模块异常处理器。
 * <p>
 * 优先级高于全局 GlobalExceptionHandler，确保所有 /ai/* 端点
 * （包括未来新增的）都能正确返回 503。
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "org.sdkj.ai")
public class AiExceptionHandler {

    @ExceptionHandler(AiDisabledException.class)
    public ResponseEntity<R<Void>> handleDisabled(AiDisabledException e) {
        log.warn("AI disabled: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(R.fail(503, e.getMessage()));
    }

    /**
     * SSE async dispatch 完成后 Servlet 容器做 ASYNC dispatch，
     * 此时 SaInterceptor 在无 HTTP 上下文的线程中执行导致此异常。
     * SSE 响应已在主线程完成，此处静默忽略。
     */
    @ExceptionHandler(SaTokenContextException.class)
    public void handleAsyncDispatch(SaTokenContextException e) {
        log.debug("SSE async dispatch SaToken context missing (expected): {}", e.getMessage());
    }
}
