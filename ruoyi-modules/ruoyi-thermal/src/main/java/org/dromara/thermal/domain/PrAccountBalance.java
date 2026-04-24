package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;

/**
 * 个人账户余额实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_account_balance")
@AutoMapper(target = PrAccountBalanceVo.class)
public class PrAccountBalance extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;

    private String houseId;

    private String itemGroup;

    private String itemCode;

    private BigDecimal balance;

    private String orgId;

    private String companyId;
}
