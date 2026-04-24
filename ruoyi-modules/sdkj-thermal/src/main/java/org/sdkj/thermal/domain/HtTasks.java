package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtTasksVo;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 调控任务
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_tasks")
@AutoMapper(target = HtTasksVo.class)
public class HtTasks extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 当前任务组ID */
    private String cuGroupId;

    /** 任务名称 */
    private String name;

    /** 执行方式 */
    private Integer type;

    /** Cron表达式 */
    private String cronExpression;

    /** 策略ID */
    private String strategyId;

    /** 优先级 */
    private Integer priority;

    /** 状态 0=停止 1=启动 */
    private Integer status;

    /** 执行次数 */
    private Integer number;

    /** 上次执行时间 */
    private Date lastTime;

    /** 指令总条数 */
    private Integer total;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 调控依据 */
    private Integer adjustBasis;

    /** 控制范围类型 1=户阀 2=单元阀 */
    private Integer scopeType;

    /** 任务描述 */
    private String description;

    /** 任务类 */
    private String beanClass;

    /** 分组 */
    private String jobGroup;

    /** 调控天数 */
    private Integer days;

    /** 调控次数限制 */
    private Integer nums;

    /** 平衡率达标值 */
    private Integer standard;

    /** 停止时间 */
    private Date endTime;

    /** 执行时长(秒) */
    private Integer executionTime;

    /** 平均回水温度 */
    private BigDecimal outTempPj;

    /** 是否使用回报率检查 */
    private Integer isUseReportRate;

    /** 回报率 */
    private Integer reportRate;

    /** 是否使用首调 */
    private Integer isUseFirstControl;

    /** 关联策略（非数据库字段） */
    @TableField(exist = false)
    private HtStrategy htStrategy;
}
