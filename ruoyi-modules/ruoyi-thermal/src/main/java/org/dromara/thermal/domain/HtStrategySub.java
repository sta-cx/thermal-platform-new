package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtStrategySubVo;

/**
 * 控制策略子表
 * 迁移自旧系统 HtStrategySub
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_strategy_sub")
@AutoMapper(target = HtStrategySubVo.class)
public class HtStrategySub extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 策略ID */
    private String strategyId;

    /** 指令ID */
    private String instructionId;

    /** 排序 */
    private Integer sort;

    /** 阀门开度 */
    private String valveAngle;

}
