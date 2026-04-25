package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/import/history")
public class PrImportHistoryController extends BaseController {

    @SaCheckPermission("thermal:property:import:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<Void> pageList() {
        // TODO: Phase 5c - 查询待提交的历史数据导入记录
        return R.ok();
    }

    @SaCheckPermission("thermal:property:import:export")
    @SaCheckLogin
    @GetMapping("/template")
    public R<Void> downloadTemplate() {
        // TODO: Phase 5c - 下载历史数据导入模板
        return R.fail("模板下载待实现");
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "历史数据导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importData(@RequestParam("file") MultipartFile file) {
        // TODO: Phase 5c - EasyExcel 解析 + 校验
        return R.fail("导入功能待实现");
    }

    @SaCheckPermission("thermal:property:import:remove")
    @SaCheckLogin
    @DeleteMapping
    public R<Void> deleteData() {
        return R.ok();
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @PostMapping("/submit")
    public R<Void> submitData() {
        // TODO: Phase 5c - 从临时表提交到历史记录表
        return R.fail("提交功能待实现");
    }
}
