package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 授权码导入管理
 * 迁移自旧系统 PrImportAuthorizationCodeController
 * 旧端点: /property/authorizationCode/* -> 新端点: /thermal/property/auth-code/*
 *
 * 注意：依赖 Jackcess 库读取 Access (.mdb) 文件
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/auth-code")
public class PrImportAuthorizationCodeController extends BaseController {

    /**
     * 从 .mdb 文件导入授权码
     * 旧端点: POST /property/authorizationCode/importData
     * 新端点: POST /thermal/property/auth-code/import
     */
    @SaCheckLogin
    @Log(title = "授权码导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importData(
            @RequestParam("file") Object file,
            @RequestParam String companyId,
            @RequestParam String orgId) {
        // TODO: Phase 5e 完整实现 - Jackcess 读取 .mdb 文件
        return R.fail("此功能需要 Jackcess 依赖集成");
    }
}
