package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 运行监控 - 巡测 Bo
 */
@Data
public class MonitorXunceBo {

    /** 阀门ID列表 */
    @NotEmpty(message = "ids 不能为空")
    private List<String> ids;

    /** 小区ID */
    @NotBlank(message = "orgId 不能为空")
    private String orgId;

    /** 采集间隔(分钟) */
    @NotNull(message = "collectInterval 不能为空")
    private Integer collectInterval;

    /** 类型: heat=热表, valve=阀门 */
    private String type;
}
