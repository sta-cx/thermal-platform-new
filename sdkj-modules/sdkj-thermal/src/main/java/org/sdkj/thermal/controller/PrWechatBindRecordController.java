package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.service.IPrWechatBindRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信绑定记录管理
 * 迁移自旧系统 PrWechatBindRecordController
 * 旧端点: /property/prWechatBindRecord/* -> 新端点: /thermal/property/wechat-bind/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/wechat-bind")
public class PrWechatBindRecordController extends BaseController {

    private final IPrWechatBindRecordService wechatBindRecordService;

    /**
     * 绑定房屋到微信
     * 旧端点: POST /property/prWechatBindRecord/insertData
     * 新端点: POST /thermal/property/wechat-bind
     */
    @SaCheckLogin
    @Log(title = "微信绑定", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insertData(@RequestBody Map<String, String> bindRecord) {
        String houseId = bindRecord.get("houseId");
        String heatPayCode = bindRecord.get("heatPayCode");
        String wxOpenId = bindRecord.get("wxOpenId");
        String orgId = bindRecord.get("orgId");
        String orgName = bindRecord.get("orgName");
        String buildingId = bindRecord.get("buildingId");
        String buildingName = bindRecord.get("buildingName");
        String unitCode = bindRecord.get("unitCode");
        String roomNum = bindRecord.get("roomNum");
        String createBy = bindRecord.get("createBy");

        if (houseId == null || houseId.isEmpty()) {
            return R.fail("房屋ID不能为空");
        }
        if (wxOpenId == null || wxOpenId.isEmpty()) {
            return R.fail("微信openId不能为空");
        }

        // TODO: 验证 wxOpenId 有效性（依赖微信 API Phase 6）

        // 检查是否已绑定
        int count = wechatBindRecordService.getCountByHouseId(houseId, createBy);
        if (count > 0) {
            return R.fail("当前地址已做过绑定，不能进行重复绑定操作");
        }

        // 执行绑定
        int result = wechatBindRecordService.insertData(houseId, heatPayCode, wxOpenId,
            orgId, orgName, buildingId, buildingName, unitCode, roomNum, createBy);
        if (result > 0) {
            return R.ok();
        }
        return R.fail("绑定数据存储失败");
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(wechatBindRecordService.list());
    }
}
