package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 运行监控 - 5参数设置 Bo
 */
@Data
public class MonitorSetValveGroupBo {

    /** 阀门ID列表 */
    @NotEmpty(message = "ids 不能为空")
    private List<String> ids;

    /** 小区ID */
    @NotBlank(message = "orgId 不能为空")
    private String orgId;

    /** 模式: channel=信道, setting=设置 */
    @NotBlank(message = "mode 不能为空")
    private String mode;

    /** 信道 */
    private Integer channel;

    /** 目标温度 */
    private Integer targetTemp;

    /** 开度 */
    private Integer opening;

    /** 周期 */
    private Integer period;

    /** 流量 */
    private Integer flowRate;
}
