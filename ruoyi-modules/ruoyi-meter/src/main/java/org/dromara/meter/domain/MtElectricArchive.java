package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtElectricArchiveVo;

import java.math.BigDecimal;

/**
 * 电表档案
 * 迁移自旧系统 MtElectricArchive
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_electric_archive")
@AutoMapper(target = MtElectricArchiveVo.class)
public class MtElectricArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 分类ID */
    private String sortId;

    /** 编号 */
    private String code;

    /** 名称 */
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
