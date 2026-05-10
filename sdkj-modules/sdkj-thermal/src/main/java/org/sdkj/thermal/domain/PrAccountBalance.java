package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;

/**
 * 个人账户余额实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_account_balance")
@AutoMapper(target = PrAccountBalanceVo.class)
public class PrAccountBalance extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private Long userId;

    private Long houseId;

    private String itemGroup;

    private String itemCode;

    private BigDecimal balance;

    private String orgId;

}
