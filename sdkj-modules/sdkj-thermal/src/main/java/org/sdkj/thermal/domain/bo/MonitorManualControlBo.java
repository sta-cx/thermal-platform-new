package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 运行监控 - 手动阀门控制 Bo
 */
@Data
public class MonitorManualControlBo {

    /** 控制类型: adjust=调节, close=关闭, open=开启 */
    @NotBlank(message = "controlType 不能为空")
    private String controlType;

    /** 阀门ID列表 */
    @NotEmpty(message = "ids 不能为空")
    private List<String> ids;

    /** 开度（controlType=adjust 时使用） */
    private Integer opening;

    /** 小区ID */
    @NotBlank(message = "orgId 不能为空")
    private String orgId;
}
