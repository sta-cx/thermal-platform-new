package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 调控任务上次执行记录
 *
 * @author sdkj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_tasks_perform_last")
public class HtTasksPerformLast extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 控制任务ID */
    private Long tasksId;

    /** 指令ID */
    private Long instructionId;

    /** 指令顺序 */
    private Integer orderr;

    /** 指令类型 */
    private Integer instructionType;

    /** 指令内容 */
    private String instruction;

    /** 指令执行次数 */
    private Integer number;

    /** 间隔 */
    private Integer intervall;

    /** 单位 */
    private String unit;

    /** 作用时长 */
    private Boolean duration;

    /** 小区ID */
    private String orgId;


    /** 集中器编号 */
    private String concentratorCode;

    /** 电信产品ID */
    private String teleProductId;

    /** 电信平台Master-APIkey */
    private String teleApiKey;

    /** 电信平台AppKey */
    private String teleAppKey;

    /** 设备ID */
    private String deviceId;

    /** 仪表号 */
    private String meterNum;

    /** 仪表ID */
    private Long meterId;

    /** 档案编号 */
    private String meterArcCode;

    /** 执行状态 */
    private Integer status;

    /** 执行结果 */
    private Integer instructionStatus;

    /** 指令发送时间 */
    private Date sendTime;

    /** 是否开始新的循环 */
    private Integer foreStart;

    /** 进水温度 */
    private BigDecimal inTemp;

    /** 回水温度 */
    private BigDecimal outTemp;

    /** 室温 */
    private BigDecimal roomTemp;

    /** 当前开度 */
    private Integer valveOpen;

    /** 设备IMEI */
    private String imei;

    /** DTU编号 */
    private String dtuNum;

    /** 通道号 */
    private String chanNum;

    /** 平均回水温度 */
    private BigDecimal outTempPj;

    /** 计算流量 */
    private BigDecimal curFlowCompute;

    /** 参考热量 */
    private BigDecimal refHeat;

    /** 策略ID */
    private Long groupId;

    /** 任务组ID */
    private Long strategyId;

    /** 任务指令顺序号 */
    private Integer commandIndex;
}
