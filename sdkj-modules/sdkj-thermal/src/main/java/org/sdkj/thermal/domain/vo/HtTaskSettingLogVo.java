package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtTaskSettingLog;
import java.util.List;

@Data
@AutoMapper(target = HtTaskSettingLog.class)
public class HtTaskSettingLogVo {

    private Long id;
    private String taskId;
    private String scopeType;
    private String createBy;
    private String createTime;
    private List<HtTaskSettingLogItemVo> items;
}
