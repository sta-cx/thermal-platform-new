package org.sdkj.meter.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.MtElectricArchive;

import java.math.BigDecimal;

/**
 * 电表档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MtElectricArchive.class, reverseConvertGenerate = false)
public class MtElectricArchiveBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 分类ID */
    @NotBlank(message = "分类ID不能为空")
    private String sortId;

    /** 编号 */
    @NotBlank(message = "编号不能为空")
    private String code;

    /** 名称 */
    @NotBlank(message = "名称不能为空")
    private String name;

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

}
