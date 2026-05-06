package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHeatDaily;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热表日记录业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHeatDaily.class, reverseConvertGenerate = false)
public class PrHeatDailyBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 配表ID */
    private Long meterId;

    /** 表号 */
    @NotBlank(message = "表号不能为空")
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 热表档案编号 */
    @NotBlank(message = "热表档案编号不能为空")
    private String meterArcCode;

    /** 上次抄表时间 */
    private Date startTime;

    /** 上次读数 */
    private BigDecimal startReading;

    /** 本次抄表时间 */
    private Date readTime;

    /** 当前读数 */
    private BigDecimal currentReading;

    /** 日用量 */
    private BigDecimal qty;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 回水温度 */
    private BigDecimal outTemperature;

    /** 供回水温差 */
    private BigDecimal diffTemperature;

    /** 阀门设定状态 */
    private String settingStatus;

    /** 阀门当前状态 */
    private String valveStatus;

    /** 电压 */
    private String voltage;

    /** 基本单价 */
    private BigDecimal standardPrice;

    /** 总金额 */
    private BigDecimal totalMoney;

    /** 日表所属日期 */
    private Date dailyDate;

    /** 是否计算用量 */
    private Integer isCalc;

    /** 费用结算时间 */
    private Date calcDate;

    /** 房屋ID */
    @NotNull(message = "房屋ID不能为空")
    private Long houseId;

    /** 小区ID */
    @NotBlank(message = "小区ID不能为空")
    private String orgId;

    /** 公司ID */
    @NotBlank(message = "公司ID不能为空")
    private String companyId;
}
