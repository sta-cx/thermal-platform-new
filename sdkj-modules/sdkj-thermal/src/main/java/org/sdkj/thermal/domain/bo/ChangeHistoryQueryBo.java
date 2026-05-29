package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 运行监控 - 变更历史查询 Bo
 */
@Data
public class ChangeHistoryQueryBo {

    /** 房屋ID */
    private Long houseId;

    /** 范围: house=户级, unit=单元级 */
    @NotBlank(message = "scope 不能为空")
    private String scope;

    /** 变更类型: payment=缴费, special=特殊户 */
    @NotBlank(message = "changeType 不能为空")
    private String changeType;
}
