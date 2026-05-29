package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 运行监控 - 第三方编码批量读取 Bo
 */
@Data
public class MonitorOtherCodeReadBo {

    /** 阀门号列表 */
    @NotEmpty(message = "meterNums 不能为空")
    private List<String> meterNums;

    /** 小区ID */
    @NotBlank(message = "orgId 不能为空")
    private String orgId;
}
