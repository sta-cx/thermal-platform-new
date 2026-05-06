package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrOptionsHeat;

/**
 * 供热系统选项 VO
 */
@Data
@AutoMapper(target = PrOptionsHeat.class)
public class PrOptionsHeatVo {
    private Long id;
    private String heatStartDate;
    private String heatEndDate;
    private String chargeStandardType;
    private String penaltyRate;
    private String invoiceNotes;
    private String paymentReminder;
    private String companyId;
    private String orgId;
    private String level;
}
