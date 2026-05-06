package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtStrategyVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 控制策略主表
 * 迁移自旧系统 HtStrategy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_strategy")
@AutoMapper(target = HtStrategyVo.class)
public class HtStrategy extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 策略名称 */
    private String name;

    /** 策略类型 */
    private Integer type;

    /** 公司ID */
    private String companyId;

    /** 备注 */
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

    /** 阀门最小角度(0-99) */
    private Integer valveMin;

    /** 阀门最大角度(1-100) */
    private Integer valveMax;

    /** 设定进水温度 */
    private BigDecimal inTemp;

    /** 进水温度偏差值 */
    private Integer inTempDeviation;

    /** 设定回水温度 */
    private BigDecimal outTemp;

    /** 回水温度偏差值 */
    private Integer outTempDeviation;

    /** 设定瞬时流量 */
    private BigDecimal curFlow;

    /** 瞬时流量偏差值 */
    private Integer curFlowDeviation;

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

    /** 是否数据未上报报警(0否1是) */
    private Integer isReportPolice;

    /** 数据未上报次数 */
    private Integer reportPoliceNumber;

    /** 是否控制未处理报警(0否1是) */
    private Integer isManagePolice;

    /** 控制未处理次数 */
    private Integer managePoliceNumber;

    /** 策略系数 */
    private BigDecimal coefficient;

    /** 边户供热系数 */
    private BigDecimal bianhxs;

    /** 顶户供热系数 */
    private BigDecimal dinghxs;

    /** 底户供热系数 */
    private BigDecimal dihxs;

    /** 中间户供热系数 */
    private BigDecimal zhonghxs;

    /** 不利于环路户供热系数 */
    private BigDecimal bulixs;

    /** 边顶户供热系数 */
    private BigDecimal biandinghxs;

    /** 边底户供热系数 */
    private BigDecimal biandihxs;

    /** 孤岛户供热系数 */
    private BigDecimal gdhxs;

    /** 上不供户供热系数 */
    private BigDecimal sbgnhxs;

    /** 下不供户供热系数 */
    private BigDecimal xbghxs;

    /** 正常户供热系数 */
    private BigDecimal zchxs;

    /** 是否启用系数 */
    private Integer isXishu;

    /** 是否启用分组供热系数 */
    private Integer isFzxishu;

    /** 楼房设计供热指标 */
    private Integer heatSupplyIndex;

    /** 供回水设计温差 */
    private Integer temperatureDifference;

    /** 热差异 */
    private Integer heatDifference;

    /** 子表记录列表（非数据库字段） */
    @TableField(exist = false)
    private List<HtStrategySub> subList;

}
