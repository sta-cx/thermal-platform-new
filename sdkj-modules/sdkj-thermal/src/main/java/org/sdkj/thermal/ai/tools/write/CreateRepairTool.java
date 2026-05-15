package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.service.IHtRepairService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRepairTool {

    private final IHtRepairService repairService;

    public record CreatedRepair(
        Long repairId,
        Long houseId,
        String repairInfo,
        String status
    ) {}

    @Tool(description = """
        给指定热户创建一条报修工单。
        典型用途:客服在与用户对话时,用户口述报修原因后调用本 Tool 录入。
        """)
    @WriteTool(
        risk = RiskLevel.MEDIUM,
        confirm = true,
        permission = "thermal:ht:repair:add"
    )
    public CreatedRepair create(
        @ToolParam(description = "房屋 ID") Long houseId,
        @ToolParam(description = "报修描述") String repairInfo,
        @ToolParam(description = "联系人姓名,可选", required = false) String userName,
        @ToolParam(description = "联系电话,可选", required = false) String userPhone
    ) {
        log.info("[Tool] createRepairTool.create houseId={} info={}", houseId, repairInfo);
        HtRepair repair = new HtRepair();
        repair.setHouseId(houseId);
        repair.setRepairInfo(repairInfo);
        repair.setUserName(userName);
        repair.setUserPhone(userPhone);
        repair.setRepairStatus(0); // 待处理
        repairService.save(repair);
        return new CreatedRepair(repair.getId(), houseId, repairInfo, "PENDING");
    }
}
