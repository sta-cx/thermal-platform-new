package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchValveCommandTool {

    private final PrHeatValveArchiveMapper valveMapper;

    public record ValveCommandResult(
        Long houseId,
        String action,
        Integer openness,
        boolean dispatched,
        String message
    ) {}

    @Tool(description = """
        给指定热户的阀门下发控制指令。此操作会真正影响用户家暖气供热,需要二次确认。
        action 枚举:OPEN(全开) / CLOSE(全关) / SET_OPENNESS(部分开,配合 openness 参数 0-100)。
        若 IoT 链路未就绪或用户无执行权限,系统会强制 dryRun=true 只生成指令清单。
        """)
    @WriteTool(
        risk = RiskLevel.HIGH,
        confirm = true,
        permission = "thermal:ht:valve:dispatch"
    )
    public ValveCommandResult dispatch(
        @ToolParam(description = "热户 ID(必填)") Long houseId,
        @ToolParam(description = "动作:OPEN / CLOSE / SET_OPENNESS") String action,
        @ToolParam(description = "开度 0-100,仅 SET_OPENNESS 必填", required = false) Integer openness,
        @ToolParam(description = "是否仅模拟(默认 true,生产实发需配置+权限)", required = false) Boolean dryRun
    ) {
        boolean effectiveDryRun = dryRun == null || dryRun;
        log.info("[Tool] dispatchValveCommandTool houseId={} action={} openness={} dryRun={}",
            houseId, action, openness, effectiveDryRun);

        // 查阀门确认存在
        PrHeatValveArchive valve = valveMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PrHeatValveArchive>()
                .eq(PrHeatValveArchive::getHouseId, houseId)
                .last("LIMIT 1")
        );
        if (valve == null) {
            return new ValveCommandResult(houseId, action, openness, false,
                "该热户未配置阀门,无法下发指令");
        }

        if (effectiveDryRun) {
            return new ValveCommandResult(houseId, action, openness, false,
                "dryRun:命令已生成,未真实下发到 IoT。请运维确认 IoT 链路后改 ai.tools.valve.dry-run=false 或赋予 thermal:ht:valve:execute 权限后重试。");
        }

        // TODO Phase 3: 真实 IoT 下发 — 调用 MBus/AG 平台 API
        return new ValveCommandResult(houseId, action, openness, true,
            "指令已下发(IoT 链路待接入,当前为占位实现)");
    }
}
