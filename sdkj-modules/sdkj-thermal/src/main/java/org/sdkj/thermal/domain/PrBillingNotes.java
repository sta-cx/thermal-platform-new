package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

/**
 * 票据备注
 * 迁移自旧系统 PrBillingNotes
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_billing_notes")
public class PrBillingNotes extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 流水号 */
    private String serialNum;

    /** 备注 */
    private String notes;
}
