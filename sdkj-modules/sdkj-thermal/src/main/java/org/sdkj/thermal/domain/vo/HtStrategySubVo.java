package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtStrategySub;

/**
 * 控制策略子表视图对象
 * 迁移自旧系统 HtStrategySub
 */
@Data
@AutoMapper(target = HtStrategySub.class)
public class HtStrategySubVo {

    private Long id;

    /** 策略ID */
    private Long strategyId;

    /** 指令ID */
    private Long instructionId;

    /** 排序 */
    private Integer sort;

    /** 阀门开度 */
    private String valveAngle;

}
