package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtHouseStrategyVo;
import java.math.BigDecimal;

/**
 * 单元房屋策略绑定
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_house_strategy")
@AutoMapper(target = HtHouseStrategyVo.class)
public class HtHouseStrategy extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 房屋/单元ID */
    private Long tasksId;

    /** 类型 1=房屋 2=单元 */
    private Integer type;

    /** 策略ID */
    private Long strategyId;

    /** 策略名称 */
    private String name;

    /** 策略备注 */
    private String remark;

    /** 调控依据 */
    private Integer adjustBasis;

    /** 调节步长 */
    private Integer stride;

    /** 优先级 */
    private Integer priority;

    /** 调节时间间隔(分钟) */
    private Integer intervall;

    /** 调节次数 */
    private Integer number;

    /** 阀门最小角度 */
    private Integer valveMin;

    /** 阀门最大角度 */
    private Integer valveMax;

    /** 设定进水温度 */
    private BigDecimal inTemp;

    /** 进水温度偏差值 */
    private Integer inTempDeviation;

    /** 设定回水温度 */
    private BigDecimal outTemp;

    /** 回水温度偏差值 */
    private Integer outTempDeviation;

    /** 进水温度报警最小范围 */
    private BigDecimal isInTempAlertMin;

    /** 进水温度报警最大范围 */
    private BigDecimal isInTempAlertMax;

    /** 设定标准室温 */
    private BigDecimal roomTemp;

    /** 室温偏差值 */
    private Integer roomTempDeviation;

    /** 策略范围类型 */
    private Integer scopeType;

    /** 是否数据未上报报警 0不启用 1启用 */
    private Integer isReportPolice;

    /** 数据未上报次数 */
    private Integer reportPoliceNumber;

    /** 是否控制未处理报警 0不启用 1启用 */
    private Integer isManagePolice;

    /** 控制未处理次数 */
    private Integer managePoliceNumber;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 预设角度 */
    private Integer presetAngle;

    /** 预设流量 */
    private BigDecimal presetFlowRate;
}
