package org.sdkj.thermal.constant;

/**
 * 阀门状态码 / 告警类型 / 设备类型 常量
 * <p>
 * 迁移自旧系统 ControlReturnApi (硬编码) 与 ValveData.java 注释。
 * 新系统统一来源，禁止散落 magic number。
 */
public final class ValveStatusConstants {

    private ValveStatusConstants() {}

    // ==================== 阀门状态码 (valveStatus) ====================
    /** 开 */
    public static final int VALVE_OPEN = 1;
    /** 关 / 通用失败 */
    public static final int VALVE_CLOSED = 2;
    /** 异常(error) */
    public static final int VALVE_ERROR = 3;
    /** 采集平台无阀门信息 */
    public static final int VALVE_NOT_FOUND = 14;
    /** DTU 掉线 */
    public static final int VALVE_DTU_OFFLINE = 15;
    /** 查询完成 */
    public static final int QUERY_COMPLETED = 9;

    // ==================== 告警类型 (alert_type) ====================
    /** 阀门控制失败(通用) — 复用 valveStatus 2 */
    public static final int ALERT_CONTROL_FAILED = 2;
    /** 阀门控制异常(error) — 复用 valveStatus 3 */
    public static final int ALERT_CONTROL_ERROR = 3;
    /** 回水温度超出正常范围 */
    public static final int ALERT_TEMP_OUT_OF_RANGE = 5;
    /** 未缴费回水温度异常 */
    public static final int ALERT_UNPAID_TEMP_ABNORMAL = 7;
    /** 已缴费未开阀 */
    public static final int ALERT_PAID_BUT_CLOSED = 11;
    /** 未缴费开阀(偷热) */
    public static final int ALERT_UNPAID_BUT_OPEN = 12;

    // ==================== 设备类型 (returnControl type) ====================
    /** 阀门 */
    public static final int DEVICE_VALVE = 1;
    /** 热表 */
    public static final int DEVICE_HEAT_METER = 2;
    /** 温控器 */
    public static final int DEVICE_THERMOSTAT = 3;
    /** 无线跳转阀 */
    public static final int DEVICE_RADIO_BYPASS = 4;
    /** 采集器(DTU) */
    public static final int DEVICE_COLLECTOR = 5;

    // ==================== 错误消息字面量 (adjustValveStatusByMessage) ====================
    public static final String MSG_DTU_OFFLINE = "DTU掉线";
    public static final String MSG_VALVE_NOT_FOUND = "采集平台无此阀门信息";
    public static final String MSG_ERROR = "error";
    public static final String MSG_TIMEOUT = "timeout";

    // ==================== 控制命令码 (command) ====================
    /** 开阀 */
    public static final int CMD_OPEN_VALVE = 27;
    /** 关阀 */
    public static final int CMD_CLOSE_VALVE = 28;
    /** 开度调整 */
    public static final int CMD_ADJUST_OPENING = 18;
    /** 状态查询 */
    public static final int CMD_QUERY = 4;
}
