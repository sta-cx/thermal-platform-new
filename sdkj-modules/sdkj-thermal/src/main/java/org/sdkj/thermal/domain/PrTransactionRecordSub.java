package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrTransactionRecordSubVo;

import java.math.BigDecimal;

/**
 * 交易记录子表实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_transaction_record_sub")
@AutoMapper(target = PrTransactionRecordSubVo.class)
public class PrTransactionRecordSub extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 主记录ID */
    private Long mainId;

    /** 费用明细ID */
    private Long expenseId;

    /** 金额 */
    private BigDecimal amount;

    /** 交易前余额 */
    private BigDecimal balanceBefore;

    /** 交易后余额 */
    private BigDecimal balanceAfter;

    /** 费项分组 */
    private String itemGroup;

    /** 费项编码 */
    private String itemCode;

    /** 房屋ID */
    private Long houseId;

    /** 费用名称 */
    private String itemName;

    /** 备注 */
    private String notes;
}
