package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.vo.HtTasksVo;

import java.util.List;
import java.util.Map;

/**
 * 调控任务Mapper
 */
public interface HtTasksMapper extends BaseMapperPlus<HtTasks, HtTasksVo> {

    /**
     * 查询所有任务（按组织）
     */
    List<HtTasks> selectAllByOrgId(@Param("orgId") String orgId);

    /**
     * 删除任务关联的范围记录
     */
    int deleteScopeByTaskId(@Param("taskId") Integer taskId);

    /**
     * 更新任务状态
     */
    int updateTaskStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 更新最后执行时间
     */
    int updateLastTime(@Param("id") Integer id);

    /**
     * 标记特殊户/单元
     */
    int markScopeAsSpecial(@Param("scopeIds") List<String> scopeIds, @Param("val") String val, @Param("remark") String remark);

    /**
     * 标记特殊单元
     */
    int markUnitAsSpecial(@Param("scopeIds") List<String> scopeIds, @Param("val") String val, @Param("remark") String remark);

    /**
     * 设置缴费状态
     */
    int markPayStatus(@Param("scopeIds") List<String> scopeIds, @Param("val") String val);

    /**
     * 查询平衡率
     */
    double queryBalanceRate(@Param("taskId") String taskId);

    /**
     * 查询平均回水温度
     */
    double queryAvgReturnTemp(@Param("taskId") String taskId);

    /**
     * 保存设定开度（户）
     */
    int updateValveAngleH(@Param("taskId") String taskId);

    /**
     * 保存设定开度（单元）
     */
    int updateValveAngleD(@Param("taskId") String taskId);

    /**
     * 更新控制范围状态
     */
    int updateScopeStatus(@Param("taskId") String taskId, @Param("status") Integer status);

    /**
     * 查询汇总统计
     */
    Map<String, Object> querySummary(
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode
    );

    /**
     * 查询户阀开度数据（关联策略子表）
     */
    List<Map<String, Object>> selectScopeForAngleH(@Param("taskId") String taskId);

    /**
     * 查询单元阀开度数据（关联策略子表）
     */
    List<Map<String, Object>> selectScopeForAngleD(@Param("taskId") String taskId);

    /**
     * 批量插入指令执行记录
     */
    int insertTasksPerformBatch(@Param("records") List<HtTasksPerform> records);

    /**
     * 重新设定开度日志 - 插入主表（户阀）
     */
    int insertSettingLogMainH(@Param("logId") String logId, @Param("newMainId") String newMainId, @Param("createBy") String createBy);

    /**
     * 重新设定开度日志 - 插入主表（单元阀）
     */
    int insertSettingLogMainD(@Param("logId") String logId, @Param("newMainId") String newMainId, @Param("createBy") String createBy);

    /**
     * 重新设定开度日志 - 插入子表（户阀）
     */
    int insertSettingLogItemH(@Param("oldMainId") String oldMainId, @Param("newMainId") String newMainId);

    /**
     * 重新设定开度日志 - 插入子表（单元阀）
     */
    int insertSettingLogItemD(@Param("oldMainId") String oldMainId, @Param("newMainId") String newMainId);

    /**
     * 重新设定开度日志 - 更新户阀设定状态
     */
    int updateValveSettingStatusH(@Param("newMainId") String newMainId);

    /**
     * 重新设定开度日志 - 更新单元阀设定状态
     */
    int updateValveSettingStatusD(@Param("newMainId") String newMainId);

    /**
     * 查询DTU平均回水温度（调控依据=3时使用）
     */
    List<org.sdkj.thermal.domain.vo.PJHSVo> queryHtTasksPJHSDTU(@Param("taskId") String taskId);

    /**
     * 更新任务执行次数
     */
    int updateHtasks(@Param("taskId") String taskId);

    /**
     * 插入指令表每个阀门信息（广播方式，不下发，仅作为界面查询展示用）
     */
    int insertHtTasksPerformByRadio(@Param("taskId") String taskId, @Param("commandIndex") Integer commandIndex);

    // ==================== Regulation Engine Methods ====================
    // Ported from old ControlJob flow - single valve 5-step regulation

    /**
     * 获取户阀平均回水温度（不包括特殊户，缴费状态=1）
     */
    double queryHtTasksPJHS(@Param("tasksId") String tasksId);

    /**
     * 获取单元阀平均回水温度（不包括特殊户）
     */
    double queryHtTasksPJHSD(@Param("tasksId") String tasksId);

    /**
     * 更新任务执行时长和平均回水温度
     */
    void updateExecutionTime(@Param("tasksId") String tasksId,
                             @Param("executionTime") Integer executionTime,
                             @Param("average") double average);

    /**
     * 查询当前平衡率（户阀/单元阀，非特殊户中status=9的比例）
     */
    int queryStandard(@Param("tasksId") String tasksId);

    /**
     * 查询DTU平衡率
     */
    int queryDtuStandard(@Param("tasksId") String tasksId);

    /**
     * 更新DTU调控范围状态（根据组平均回水温度判断是否达标）
     */
    void updateDtuScopeStatus(@Param("tasksId") String tasksId);

    /**
     * 清空调控临时表 ht_tasks_perform_ls
     */
    int deleteHtTasksPerformLs(@Param("tasksId") String tasksId);

    /**
     * Step 2: 户阀 - 填充临时表（平均回水调控方式，关联上次执行记录）
     */
    int insertHtTasksPerformLsHfPj(@Param("tasksId") String tasksId);

    /**
     * Step 2: 单元阀 - 填充临时表（平均回水调控方式，关联上次执行记录）
     */
    int insertHtTasksPerformLsDyPj(@Param("tasksId") String tasksId);

    // ---- Step 3: instructionGeneration (alert type evaluation) ----

    /** 单一策略：次数超限标记为完成(alert_type=9) */
    int setNumber(@Param("tasksId") String tasksId);

    /** 回水温度 - 不在范围且次数未超限，阀门角度报警(alert_type=4) */
    int setOutTempNumberX(@Param("tasksId") String tasksId);

    /** 回水温度 - 不在范围且次数已超限，温度报警(alert_type=5) */
    int setOutTempNumberD(@Param("tasksId") String tasksId);

    /** 回水温度 - 在范围内，标记完成(alert_type=9) */
    int setOutTempW(@Param("tasksId") String tasksId);

    /** 室温 - 不在范围且次数未超限，阀门角度报警(alert_type=4) */
    int setRoomTempNumberX(@Param("tasksId") String tasksId);

    /** 室温 - 不在范围且次数已超限，温度报警(alert_type=5) */
    int setRoomTempNumberD(@Param("tasksId") String tasksId);

    /** 室温 - 在范围内，标记完成(alert_type=9) */
    int setRoomTempW(@Param("tasksId") String tasksId);

    /** 平均回水温度 - 不在范围且次数未超限，阀门角度报警(alert_type=4) */
    int setRoomTempNumberXPJ(@Param("tasksId") String tasksId, @Param("average") double average);

    /** 平均回水温度 - 不在范围且次数已超限，温度报警(alert_type=5) */
    int setRoomTempNumberDPJ(@Param("tasksId") String tasksId, @Param("average") double average);

    /** 平均回水温度 - 在范围内，标记完成(alert_type=9) */
    int setRoomTempWPJ(@Param("tasksId") String tasksId, @Param("average") double average);

    // ---- Step 4: updateInstructionGeneration (next instruction determination) ----

    /** 更新异常记录（继续执行） */
    int updateInstructionGenerationyC(@Param("tasksId") String tasksId);

    /** 无策略 - 调大指令 */
    int updateInstructionGenerationD(@Param("tasksId") String tasksId);

    /** 无策略 - 调小指令 */
    int updateInstructionGenerationX(@Param("tasksId") String tasksId);

    /** 单一策略 - 循环执行指令（非首次） */
    int updateInstructionGeneration(@Param("tasksId") String tasksId);

    /** 单一策略 - 首次执行指令 */
    int updateInstructionGenerationOne(@Param("tasksId") String tasksId);

    /** 平均回水 - 调大指令（非首次） */
    int updateInstructionGenerationDPJ(@Param("tasksId") String tasksId, @Param("average") double average);

    /** 平均回水 - 调小指令（非首次） */
    int updateInstructionGenerationXPJ(@Param("tasksId") String tasksId, @Param("average") double average);

    /** 平均回水 - 完成查询（非首次） */
    int updateInstructionGenerationWPJ(@Param("tasksId") String tasksId, @Param("average") double average);

    /** 平均回水 - 首次执行默认指令 */
    int updateInstructionGenerationOnePJ(@Param("tasksId") String tasksId, @Param("average") double average);

    // ---- Step 5: boundary checks and perform table operations ----

    /** 判断调大最大范围 */
    int updateInstructionGenerationDMax(@Param("tasksId") String tasksId);

    /** 判断调小最小范围 */
    int updateInstructionGenerationDMin(@Param("tasksId") String tasksId);

    /** 控制指令完成均衡判断 */
    int updateInstructionGenerationHPEQUJ(@Param("tasksId") String tasksId);

    /** 判断调大最大范围（平均回水） */
    int updateInstructionGenerationDPJMax(@Param("tasksId") String tasksId, @Param("average") double average);

    /** 控制指令调大最大范围（开度） */
    int updateInstructionGenerationDKzMax(@Param("tasksId") String tasksId);

    /** 控制指令调小最小范围（开度） */
    int updateInstructionGenerationXKzMin(@Param("tasksId") String tasksId);

    /** 删除状态=0的执行记录（清除待执行） */
    int deleteHtTasksPerform0(@Param("tasksId") String tasksId);

    /** 插入执行记录（平均回水/单一策略模式） */
    int inserHtTasksPerformPj(@Param("tasksId") String tasksId);

    /** 插入执行记录（普通模式） */
    int inserHtTasksPerform(@Param("tasksId") String tasksId);

    /** 插入已完成的执行记录 */
    int inserHtTasksPerformW(@Param("tasksId") String tasksId);

    /** 插入异常报警记录 */
    int insertHtAlert(@Param("tasksId") String tasksId);

    /** 更新控制范围状态（从临时表同步alert_type） */
    int updateHtScope(@Param("tasksId") String tasksId);

    /** 删除上次执行记录备份 */
    int deleteHtTasksPerformLast(@Param("tasksId") String tasksId);

    /** 备份执行记录到last表 */
    int inserHtTasksPerformLast(@Param("tasksId") String tasksId);

    /** 清空临时表 */
    int deleteTtTasksPerformTemp(@Param("tasksId") String tasksId);
}
