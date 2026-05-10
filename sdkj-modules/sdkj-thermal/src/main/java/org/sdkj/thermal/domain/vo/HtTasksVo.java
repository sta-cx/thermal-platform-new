package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtTasks;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AutoMapper(target = HtTasks.class)
public class HtTasksVo {

    private Long id;
    private String cuGroupId;
    private String name;
    private Integer type;
    private String cronExpression;
    private Long strategyId;
    private Integer priority;
    private Integer status;
    private Integer number;
    private Date lastTime;
    private Integer total;
    private String orgId;
    private Integer adjustBasis;
    private Integer scopeType;
    private String description;
    private String beanClass;
    private String jobGroup;
    private Integer days;
    private Integer nums;
    private Integer standard;
    private Date endTime;
    private Integer executionTime;
    private BigDecimal outTempPj;
    private Integer isUseReportRate;
    private Integer reportRate;
    private Integer isUseFirstControl;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
