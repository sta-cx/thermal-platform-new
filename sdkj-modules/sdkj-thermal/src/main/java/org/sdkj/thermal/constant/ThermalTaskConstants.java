package org.sdkj.thermal.constant;

/**
 * 调控任务相关常量
 * <p>
 * 统一 task status / adjustBasis / scopeType / perform status 等魔法值的单一来源。
 * 禁止在业务代码中直接使用数字字面量。
 */
public final class ThermalTaskConstants {

    private ThermalTaskConstants() {}

    // ==================== 任务状态 (ht_tasks.status) ====================
    /** 停止 */
    public static final int TASK_STOPPED = 0;
    /** 运行中 */
    public static final int TASK_RUNNING = 1;

    // ==================== 调控依据 (adjustBasis) ====================
    /** 单策略 */
    public static final int ADJUST_SINGLE_STRATEGY = 0;
    /** 回水温度 */
    public static final int ADJUST_RETURN_WATER = 1;
    /** 室温 */
    public static final int ADJUST_ROOM_TEMP = 2;
    /** 广播平均回水温度 */
    public static final int ADJUST_AVG_RETURN_WATER = 3;
    /** 回水温度+供热系数 */
    public static final int ADJUST_RETURN_WATER_COEFF = 4;

    // ==================== 控制范围类型 (scopeType) ====================
    /** 户阀 */
    public static final int SCOPE_HOUSE_VALVE = 1;
    /** 单元阀 */
    public static final int SCOPE_UNIT_VALVE = 2;
    /** DTU 广播 */
    public static final int SCOPE_DTU_BROADCAST = 3;
    /** 混合 */
    public static final int SCOPE_MIXED = 4;

    // ==================== 执行记录状态 (ht_tasks_perform.status) ====================
    /** 待执行 */
    public static final int PERFORM_PENDING = 0;
    /** 已下发/执行中 */
    public static final int PERFORM_SENT = 1;

    // ==================== 布尔标志 (isUseReportRate / isUseFirstControl) ====================
    /** 关闭 */
    public static final int FLAG_OFF = 0;
    /** 开启 */
    public static final int FLAG_ON = 1;

    // ==================== 导入类型 (PrImport*.type) ====================
    /** 导入类型: 默认/待处理 */
    public static final int IMPORT_TYPE_DEFAULT = 0;
}
