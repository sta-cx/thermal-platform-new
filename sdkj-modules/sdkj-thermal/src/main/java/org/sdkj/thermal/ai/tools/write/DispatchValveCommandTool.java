package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.dto.ValveCommandResultDto;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.common.satoken.utils.LoginHelper;

/**
 * 阀门指令下发（Command 类 WriteTool）。
 * 薄层：仅收集参数 + 委托 Service，不注入 Mapper、不查 DB、不做 dryRun 分支。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchValveCommandTool {

    private final IPrHeatValveArchiveService valveArchiveService;

    @Tool(description = """
        给指定热户的阀门下发控制指令。此操作会真正影响用户家暖气供热,需要二次确认。
        典型用途:客服根据用户请求调节阀门开度。
        action 枚举:OPEN(全开) / CLOSE(全关) / SET_OPENNESS(部分开,需配合 openness 参数 0-100)。
        如果用户只说了门牌号,先调用 queryHouseByAddress 查到 houseId,再调用 getValveStatus 确认阀门存在后再调用本 Tool。
        若 IoT 链路未就绪或用户无执行权限,系统会强制 dryRun=true 只生成指令清单。
        """)
    @WriteTool(
        risk = RiskLevel.HIGH,
        confirm = true,
        permission = "thermal:ht:valve:dispatch"
    )
    public ValveCommandResultDto dispatch(
        @ToolParam(description = "热户 ID,必填。如果用户只提供地址,请先调用 queryHouseByAddress 查询")
        Long houseId,

        @ToolParam(description = "动作:OPEN / CLOSE / SET_OPENNESS,必填")
        String action,

        @ToolParam(description = "开度 0-100,仅 action=SET_OPENNESS 时必填,其他 action 忽略",
                   required = false)
        Integer openness,

        @ToolParam(description = "是否仅模拟,默认 true;生产实发需配置 dryRun=false 并具备 thermal:ht:valve:execute 权限",
                   required = false)
        Boolean dryRun
    ) {
        Long operatorId = LoginHelper.getUserId();
        log.info("[Tool] dispatchValveCommandTool houseId={} action={} openness={} operator={}",
            houseId, action, openness, operatorId);
        return valveArchiveService.dispatchFromAi(houseId, action, openness, dryRun, operatorId);
    }
}
