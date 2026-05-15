package org.sdkj.thermal.ai.tools.readonly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.vo.HtRepairVo;
import org.sdkj.thermal.mapper.HtRepairMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetRepairHistoryTool {

    private final HtRepairMapper repairMapper;

    public record RepairItem(
        Long id,
        Long houseId,
        String buildingName,
        String roomNum,
        BigDecimal inTemp,
        BigDecimal outTemp,
        BigDecimal roomTemp,
        String repairInfo,
        Integer repairStatus,
        Date repairTime
    ) {}

    @Tool(description = """
        查询报修历史记录。可按房屋 ID 过滤。
        典型用途:用户问"张三家最近报修过几次"或"本月还有多少个待处理的报修"。
        返回最多 30 条,按时间倒序。
        """)
    public List<RepairItem> queryRecent(
        @ToolParam(description = "房屋 ID,可选", required = false) Long houseId,
        @ToolParam(description = "最近 N 天,默认 30", required = false) Integer recentDays
    ) {
        int days = recentDays == null ? 30 : recentDays;
        log.info("[Tool] getRepairHistoryTool.queryRecent houseId={} days={}", houseId, days);

        LambdaQueryWrapper<HtRepair> qw = new LambdaQueryWrapper<HtRepair>();
        if (houseId != null) {
            qw.eq(HtRepair::getHouseId, houseId);
        }
        qw.orderByDesc(HtRepair::getCreateTime)
          .last("LIMIT 30");

        List<HtRepairVo> rows = repairMapper.selectVoList(qw);
        return rows.stream()
            .map(r -> new RepairItem(
                r.getId(),
                r.getHouseId(),
                r.getBuildingName(),
                r.getRoomNum(),
                r.getInTemp(),
                r.getOutTemp(),
                r.getRoomTemp(),
                r.getRepairInfo(),
                r.getRepairStatus(),
                r.getRepairTime()
            ))
            .collect(Collectors.toList());
    }
}
