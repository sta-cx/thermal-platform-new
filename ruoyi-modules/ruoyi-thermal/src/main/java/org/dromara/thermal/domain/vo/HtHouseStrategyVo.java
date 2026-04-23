package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.HtHouseStrategy;
import java.math.BigDecimal;

/**
 * 单元房屋策略视图对象
 */
@Data
@AutoMapper(target = HtHouseStrategy.class)
public class HtHouseStrategyVo {

    private String id;
    private String tasksId;
    private Integer type;
    private String strategyId;
    private String name;
    private String remark;
    private Integer adjustBasis;
    private Integer stride;
    private Integer priority;
    private Integer intervall;
    private Integer number;
    private Integer valveMin;
    private Integer valveMax;
    private BigDecimal inTemp;
    private Integer inTempDeviation;
    private BigDecimal outTemp;
    private Integer outTempDeviation;
    private BigDecimal isInTempAlertMin;
    private BigDecimal isInTempAlertMax;
    private BigDecimal roomTemp;
    private Integer roomTempDeviation;
    private Integer scopeType;
    private Integer isReportPolice;
    private Integer reportPoliceNumber;
    private Integer isManagePolice;
    private Integer managePoliceNumber;
    private String orgId;
    private String companyId;
    private Integer presetAngle;
    private BigDecimal presetFlowRate;
    private String createName;
}
