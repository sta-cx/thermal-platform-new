package org.sdkj.thermal.ai.tools.write;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;

import java.util.List;

/**
 * CRITICAL 风险演示 Tool — 批量阀门下发，本期一律拒绝。
 * 故意标 risk=CRITICAL，advisor 看到 risk=CRITICAL 直接抛 CriticalToolRejectedException。
 * 即使 LLM 决策调用，也不会走到本方法体。
 */
@Slf4j
@Component
public class BatchValveDispatchTool {

    @Tool(description = """
        批量给多个热户的阀门下发控制指令。这是 CRITICAL 风险操作，
        当前版本系统不允许通过 AI 触发，需要走线下审批流程。
        """)
    @WriteTool(risk = RiskLevel.CRITICAL, confirm = true,
               permission = "thermal:ht:valve:batchDispatch")
    public Object batchDispatch(
        @ToolParam(description = "热户 ID 列表") List<Long> houseIds,
        @ToolParam(description = "动作") String action
    ) {
        // 永远不会执行到这
        throw new IllegalStateException("CRITICAL tool should never execute");
    }
}
