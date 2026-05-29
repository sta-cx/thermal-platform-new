package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 运行监控 - 停供设定 Bo
 */
@Data
public class StopSupplyBo {

    /** 房屋ID列表 */
    @NotEmpty(message = "ids 不能为空")
    private List<String> ids;

    /** 小区ID */
    @NotBlank(message = "orgId 不能为空")
    private String orgId;

    /** 标记: 1=取消停供, 2=设定停供 */
    @NotNull(message = "flag 不能为空")
    private Integer flag;
}
