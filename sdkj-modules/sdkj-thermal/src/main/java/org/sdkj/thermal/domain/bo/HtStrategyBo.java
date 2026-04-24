package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.HtStrategy;

/**
 * 控制策略业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = HtStrategy.class, reverseConvertGenerate = false)
public class HtStrategyBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 策略名称 */
    private String name;

    /** 策略类型 */
    private Integer type;

    /** 公司ID */
    private String companyId;

    /** 备注 */
    private String remark;
}
