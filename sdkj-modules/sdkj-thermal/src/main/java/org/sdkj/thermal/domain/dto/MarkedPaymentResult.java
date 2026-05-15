package org.sdkj.thermal.domain.dto;

import java.math.BigDecimal;

/**
 * AI Tool 标记缴费的返回结果
 */
public record MarkedPaymentResult(
    Long expenseId,
    BigDecimal amount,
    String summary,
    String status
) {}
