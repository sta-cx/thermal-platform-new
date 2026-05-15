package org.sdkj.thermal.ai.tools.readonly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.thermal.domain.PrExpense;
import org.sdkj.thermal.domain.vo.PrExpenseVo;
import org.sdkj.thermal.mapper.PrExpenseMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryArrearsTool {

    private final PrExpenseMapper expenseMapper;

    public record ArrearsItem(
        Long id,
        Long houseId,
        String itemName,
        BigDecimal finalMoney,
        BigDecimal paidIn,
        Integer isCharged,
        Integer overdueDay
    ) {}

    @Tool(description = """
        查询未缴费的费用明细列表。可按欠费天数下限过滤。
        典型用途:用户问"有多少户欠费 3 个月以上"或"今年有多少未缴费用"。
        返回 JSON 数组,每条包含 id/houseId/itemName/finalMoney/paidIn/isCharged/overdueDay。
        最多返回 50 条。
        """)
    public List<ArrearsItem> queryUnpaid(
        @ToolParam(description = "欠费天数下限,默认 30 天", required = false)
        Integer minOverdueDays
    ) {
        int threshold = minOverdueDays == null ? 30 : minOverdueDays;
        log.info("[Tool] queryArrearsTool.queryUnpaid minOverdueDays={}", threshold);

        LambdaQueryWrapper<PrExpense> qw = new LambdaQueryWrapper<PrExpense>()
            .eq(PrExpense::getIsCharged, 0)
            .gt(PrExpense::getOverdueDay, threshold)
            .orderByDesc(PrExpense::getOverdueDay)
            .last("LIMIT 50");

        List<PrExpenseVo> rows = expenseMapper.selectVoList(qw);
        return rows.stream()
            .map(r -> new ArrearsItem(
                r.getId(),
                r.getHouseId(),
                r.getItemName(),
                r.getFinalMoney(),
                r.getPaidIn(),
                r.getIsCharged(),
                r.getOverdueDay()
            ))
            .collect(Collectors.toList());
    }
}
