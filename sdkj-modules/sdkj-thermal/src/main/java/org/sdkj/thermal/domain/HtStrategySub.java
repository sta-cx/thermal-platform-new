package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtStrategySubVo;

/**
 * 控制策略子表
 * 迁移自旧系统 HtStrategySub
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_strategy_sub")
@AutoMapper(target = HtStrategySubVo.class)
public class HtStrategySub extends BaseEntity {

    @TableId(value = "id")
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
