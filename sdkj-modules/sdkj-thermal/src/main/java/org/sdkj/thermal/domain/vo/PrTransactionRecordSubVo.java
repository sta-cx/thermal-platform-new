package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrTransactionRecordSub;

import java.math.BigDecimal;

/**
 * 交易记录子表视图对象
 */
@Data
@AutoMapper(target = PrTransactionRecordSub.class)
public class PrTransactionRecordSubVo {

    private Long id;
    private Long mainId;
    private Long expenseId;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String itemName;
    private String notes;
}
