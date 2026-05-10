package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrOptions;

/**
 * 系统选项 VO
 */
@Data
@AutoMapper(target = PrOptions.class)
public class PrOptionsVo {
    private Long id;
    private String orgId;
    private String level;
    private Boolean forbiddenBuyElectric;
    private Boolean forbiddenBuyWater;
}
