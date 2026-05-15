package org.sdkj.ai.tools.annotation;

public enum RiskLevel {
    /** 只读 / 幂等查询,无需确认,直接执行 */
    LOW,
    /** 轻写(创建报修、添加备注),需确认,默认聚焦"确认"按钮 */
    MEDIUM,
    /** 重写(阀门控制、单笔退费),需确认 + 3 秒倒计时 */
    HIGH,
    /** 极端写(批量阀控、删除房屋),本期一律拒绝,即使 LLM 决定调用 */
    CRITICAL
}
