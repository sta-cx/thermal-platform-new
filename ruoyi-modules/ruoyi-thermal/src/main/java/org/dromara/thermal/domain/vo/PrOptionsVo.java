package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrOptions;

/**
 * 系统选项 VO
 */
@Data
@AutoMapper(target = PrOptions.class)
public class PrOptionsVo {
    private String id;
    private String companyId;
    private String orgId;
    private String level;
    private Boolean forbiddenBuyElectric;
    private Boolean forbiddenBuyWater;
}
