package org.sdkj.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.meter.domain.MtElectricArchive;

import java.math.BigDecimal;

/**
 * 电表档案视图对象
 * 迁移自旧系统 MtElectricArchive
 */
@Data
@AutoMapper(target = MtElectricArchive.class)
public class MtElectricArchiveVo {

    private String id;

    /** 分类ID */
    private String sortId;

    /** 编号 */
    private String code;

    /** 名称 */
    private String name;

    /** 通讯方式 (1=卡式 2=远传 3=手工抄表) */
    private Integer msgType;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 额定电压 */
    private String ratedVoltage;

    /** 额定电流 */
    private String ratedCurrent;

    /** 电压比 */
    private String voltageRatio;

    /** 电流比 */
    private String currentRatio;

    /** 负载限制 */
    private String loadLimit;

    /** 报警值 */
    private String alarmValue;

    /** 显示值 */
    private String displayValue;

    /** 常量 */
    private String constant;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

    /** 排序 */
    private String seq;

    /** 表号是否必填 (0=否 1=是) */
    private Integer meterNumRequired;

    /** 最大金额 */
    private BigDecimal maxAmount;

    /** 创建时间 */
    private java.util.Date createTime;

}
