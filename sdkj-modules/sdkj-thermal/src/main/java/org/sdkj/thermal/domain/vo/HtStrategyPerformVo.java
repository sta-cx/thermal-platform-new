package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtStrategyPerform;

import java.util.Date;

/**
 * 策略执行记录视图对象
 */
@Data
@AutoMapper(target = HtStrategyPerform.class)
public class HtStrategyPerformVo {

    private String id;

    /** 任务ID */
    private Long tasksId;

    /** 指令顺序 */
    private Integer commandIndex;

    /** 策略主表ID */
    private Long strategyId;

    /** 策略子表ID */
    private Long strategySubId;

    /** 指令ID */
    private Long instructionId;

    /** 指令内容 */
    private String instruction;

    /** 指令名称 */
    private String name;

    /** 指令类型 1控制命令 2采集命令 */
    private Integer type;

    /** 执行间隔 */
    private Integer intervall;

    /** 间隔单位 */
    private Integer unit;

    /** 作用时长 */
    private Integer duration;

    /** 执行顺序 */
    private Integer orderr;

    /** 是否循环 1是0否 */
    private Integer xunhuan;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

}
