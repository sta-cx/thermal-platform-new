package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 绑定房屋到微信
     * 旧端点: POST /property/prWechatBindRecord/insertData
     * 新端点: POST /thermal/property/wechat-bind
     */
    @SaCheckLogin
    @Log(title = "微信绑定", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insertData(@RequestBody Object bindRecord) {
        // TODO: Phase 5d 完整实现（依赖微信集成 Phase 6）
        return R.fail("此功能需要微信集成（Phase 6）");
    }
}
