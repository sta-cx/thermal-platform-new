package org.sdkj.thermal.ai.tools.write;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;
import org.sdkj.thermal.domain.dto.CreatedRepairResult;
import org.sdkj.thermal.service.IHtRepairService;
import org.sdkj.common.satoken.utils.LoginHelper;

/**
 * 创建报修工单（CRUD 类 WriteTool）。
 * 薄层：仅收集参数 + 委托 Service，不注入 Mapper、不查 DB。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateRepairTool {

    private final IHtRepairService repairService;

    @Tool(description = """
        给指定热户创建一条报修工单。
        典型用途:客服在与用户对话时,用户口述报修原因后调用本 Tool 录入。
        如果用户只给了门牌号(如"3号楼201"),先调用 queryHouseByAddress 查到 houseId 再调用本 Tool。
        """)
    @WriteTool(
        risk = RiskLevel.MEDIUM,
        confirm = true,
        permission = "thermal:ht:repair:add"
    )
    public CreatedRepairResult create(
        @ToolParam(description = "房屋 ID,必填。如果用户只提供地址,请先调用 queryHouseByAddress 查询")
        Long houseId,

        @ToolParam(description = "报修描述,用户原话或概括")
        String repairInfo,

        @ToolParam(description = "联系人姓名,可选。不传时该字段为空,请尽量向用户收集",
                   required = false)
        String userName,

        @ToolParam(description = "联系电话,可选。格式 1[3-9]xxxxxxxxx;不传时该字段为空,请尽量向用户收集",
                   required = false)
        String userPhone
    ) {
        Long operatorId = LoginHelper.getUserId();
        log.info("[Tool] createRepairTool.create houseId={} operator={}", houseId, operatorId);
        return repairService.createFromAi(houseId, repairInfo, userName, userPhone, operatorId);
    }
}
