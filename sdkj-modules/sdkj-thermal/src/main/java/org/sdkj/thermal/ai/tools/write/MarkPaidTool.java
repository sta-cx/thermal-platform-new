package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.mapper.PrExpenseMapper;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarkPaidTool {

    private final PrExpenseMapper expenseMapper;

    public record MarkedPayment(
        Long expenseId,
        BigDecimal amount,
        String note
    ) {}

    @Tool(description = """
        将指定费用条目标记为已交费(用于现金等非线上渠道收款的后录入)。
        典型用途:客服收到用户现金后,在线下窗口标记缴费完成。
        """)
    @WriteTool(
        risk = RiskLevel.MEDIUM,
        confirm = true,
        permission = "thermal:property:expense:edit"
    )
    public MarkedPayment markPaid(
        @ToolParam(description = "费用条目 ID") Long expenseId,
        @ToolParam(description = "备注,可选", required = false) String note
    ) {
        log.info("[Tool] markPaidTool.markPaid expenseId={}", expenseId);
        PrExpense expense = expenseMapper.selectById(expenseId);
        if (expense == null) {
            throw new IllegalArgumentException("费用条目不存在: " + expenseId);
        }
        expense.setIsCharged(1);
        expenseMapper.updateById(expense);
        return new MarkedPayment(expenseId, expense.getFinalMoney(), note);
    }
}
