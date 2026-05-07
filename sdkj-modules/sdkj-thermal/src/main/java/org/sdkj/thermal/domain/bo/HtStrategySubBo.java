package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.HtStrategySub;

/**
 * 控制策略子表业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = HtStrategySub.class, reverseConvertGenerate = false)
public class HtStrategySubBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 策略ID(由 Service 在 save 时回填,前端不传) */
    private Long strategyId;

    /** 指令ID */
    @NotNull(message = "指令ID不能为空")
    private Long instructionId;

    /** 排序 */
    private Integer sort;

    /** 阀门开度 */
    private String valveAngle;
}
