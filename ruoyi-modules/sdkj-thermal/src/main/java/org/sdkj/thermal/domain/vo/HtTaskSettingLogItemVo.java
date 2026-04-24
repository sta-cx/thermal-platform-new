package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtTaskSettingLogItem;

@Data
@AutoMapper(target = HtTaskSettingLogItem.class)
public class HtTaskSettingLogItemVo {

    private String id;
    private String mainId;
    private String scopeId;
    private String meterNum;
    private Integer oldAngle;
    private Integer newAngle;
}
