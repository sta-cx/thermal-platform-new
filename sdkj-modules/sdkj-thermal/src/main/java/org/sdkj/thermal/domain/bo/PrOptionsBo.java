package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrOptions;

/**
 * 系统选项业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrOptions.class, reverseConvertGenerate = false)
public class PrOptionsBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 公司ID */
    private String companyId;

    /** 组织/小区ID */
    private String orgId;

    /** 级别 */
    private String level;

    /** 是否禁止购电 */
    private Boolean forbiddenBuyElectric;

    /** 是否禁止购水 */
    private Boolean forbiddenBuyWater;
}
