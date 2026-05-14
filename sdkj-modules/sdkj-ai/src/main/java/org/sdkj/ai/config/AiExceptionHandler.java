package org.sdkj.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.exception.AiDisabledException;
import org.sdkj.ai.kb.KbIngestException;
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
     * KB 摄取失败 → 500,把 docId 也带回去让前端可以重试或删除。
     * 审查 I5。
     */
    @ExceptionHandler(KbIngestException.class)
    public ResponseEntity<R<Long>> handleKbIngestFailure(KbIngestException e) {
        log.error("KB ingest failed for docId={}: {}", e.getDocId(), e.getMessage());
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(R.fail(e.getMessage(), e.getDocId()));
    }
}
