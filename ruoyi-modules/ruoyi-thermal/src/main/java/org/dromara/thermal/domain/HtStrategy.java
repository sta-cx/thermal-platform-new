package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtStrategyVo;

import java.util.List;

/**
 * 控制策略主表
 * 迁移自旧系统 HtStrategy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_strategy")
@AutoMapper(target = HtStrategyVo.class)
public class HtStrategy extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 策略名称 */
    private String name;

    /** 策略类型 */
    private Integer type;

    /** 公司ID */
    private String companyId;

    /** 备注 */
    private String remark;

    /** 子表记录列表（非数据库字段） */
    @TableField(exist = false)
    private List<HtStrategySub> subList;

}
