package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.dto.MarkedPaymentResult;
import org.sdkj.thermal.service.IPrExpenseService;
import org.sdkj.common.satoken.utils.LoginHelper;

/**
 * 标记费用为已缴费（CRUD 类 WriteTool）。
 * 薄层：仅收集参数 + 委托 Service，不注入 Mapper、不查 DB。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarkPaidTool {

    private final IPrExpenseService expenseService;

    @Tool(description = """
        将指定费用条目标记为已交费(用于现金等非线上渠道收款的后录入)。
        典型用途:客服收到用户现金后,在线下窗口标记缴费完成。
        如果用户只说了房号或户主姓名,先调用 QueryArrearsTool 查到费用条目 ID 再调用本 Tool。
        """)
    @WriteTool(
        risk = RiskLevel.MEDIUM,
        confirm = true,
        permission = "thermal:property:expense:edit"
    )
    public MarkedPaymentResult markPaid(
        @ToolParam(description = "费用条目 ID,必填。如果用户不知道 ID,请先调用 QueryArrearsTool 查询")
        Long expenseId,

        @ToolParam(description = "备注,可选。仅用于本次操作说明,不会持久化到费用记录中",
                   required = false)
        String note
    ) {
        Long operatorId = LoginHelper.getUserId();
        log.info("[Tool] markPaidTool.markPaid expenseId={} operator={}", expenseId, operatorId);
        return expenseService.markPaidFromAi(expenseId, note, operatorId);
    }
}
