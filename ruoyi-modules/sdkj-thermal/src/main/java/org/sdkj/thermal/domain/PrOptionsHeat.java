package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrOptionsHeatVo;

/**
 * 供热系统选项配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_options_heat")
@AutoMapper(target = PrOptionsHeatVo.class)
public class PrOptionsHeat extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

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
