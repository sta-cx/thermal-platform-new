package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrOptionsHeat;

/**
 * 供热系统选项业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrOptionsHeat.class, reverseConvertGenerate = false)
public class PrOptionsHeatBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 供热周期开始日期 */
    private String heatStartDate;

    /** 供热周期结束日期 */
    private String heatEndDate;

    /** 收费标准类型 */
    private String chargeStandardType;

    /** 违约金比例 */
    private String penaltyRate;

    /** 发票备注 */
    private String invoiceNotes;

    /** 缴费提示 */
    private String paymentReminder;

    /** 公司ID */
    private String companyId;

    /** 小区ID */
    private String orgId;

    /** 配置级别 (company/org) */
    private String level;
}
