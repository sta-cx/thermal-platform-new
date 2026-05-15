package org.sdkj.thermal.ai.tools.readonly;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.thermal.domain.PrHeatStation;
import org.sdkj.thermal.mapper.PrHeatStationMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetStationDailyStatTool {

    private final PrHeatStationMapper stationMapper;

    public record StationInfo(
        Long id,
        String name,
        String code,
        String address,
        String principal,
        String tel,
        String companyName
    ) {}

    @Tool(description = """
        查询指定换热站的基本信息(名称、地址、负责人、联系电话)。
        典型用途:用户问"X 站什么情况"或"查一下换热站 5 的信息"。
        日报聚合功能待 Phase 3 补充,本期返回基础信息。
        """)
    public StationInfo getById(
        @ToolParam(description = "换热站 ID(必填)") Long stationId
    ) {
        log.info("[Tool] getStationDailyStatTool.getById stationId={}", stationId);

        PrHeatStation s = stationMapper.selectById(stationId);
        if (s == null) return null;
        return new StationInfo(
            s.getId(),
            s.getName(),
            s.getCode(),
            s.getAddress(),
            s.getPrincipal(),
            s.getTel(),
            s.getCompanyName()
        );
    }
}
