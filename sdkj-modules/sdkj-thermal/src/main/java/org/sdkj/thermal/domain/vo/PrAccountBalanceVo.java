package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrAccountBalance;

import java.math.BigDecimal;

/**
 * 个人账户余额 VO
 */
@Data
@AutoMapper(target = PrAccountBalance.class)
public class PrAccountBalanceVo {

    private Long id;

    private String userId;

    private Long houseId;

    private String itemGroup;

    private String itemCode;

    private BigDecimal balance;

    private String orgId;

    private String companyId;

    /** 房间号 */
    private String roomNum;

    /** 用户名称 */
    private String userName;

    /** 费用项目名称 */
    private String itemName;
}
