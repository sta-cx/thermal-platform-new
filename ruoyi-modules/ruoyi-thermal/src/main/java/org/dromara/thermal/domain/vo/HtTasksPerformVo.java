package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.HtTasksPerform;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AutoMapper(target = HtTasksPerform.class)
public class HtTasksPerformVo {

    private String id;
    private String instructionId;
    private String groupId;
    private String strategyId;
    private Integer commandIndex;
    private Integer orderr;
    private Integer instructionType;
    private Integer instruction;
    private Integer number;
    private Integer intervall;
    private Integer foreStart;
    private Integer unit;
    private Integer duration;
    private String orgId;
    private String companyId;
    private String concentratorCode;
    private String deviceId;
    private String meterNum;
    private String meterId;
    private String meterArcCode;
    private Integer status;
    private Integer instructionStatus;
    private Date sendTime;
    private String tasksId;
    private BigDecimal inTemp;
    private BigDecimal outTemp;
    private BigDecimal roomTemp;
    private Integer valveOpen;
    private String imei;
    private String dtuNum;
    private String chanNum;
    private BigDecimal outTempPj;
    private BigDecimal refHeat;
    private String createBy;
    private Date createTime;
}
