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
@RequestMapping("/thermal/property/import/basic-data")
public class PrImportBasicDataController extends BaseController {

    @SaCheckPermission("thermal:property:import:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<Void> pageList() {
        // TODO: Phase 5c - 查询待提交的基础数据导入记录
        return R.ok();
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "基础数据导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importData(@RequestParam("file") MultipartFile file) {
        // TODO: Phase 5c - EasyExcel 解析 + 数据校验 + 入临时表
        return R.fail("导入功能待实现");
    }

    @SaCheckPermission("thermal:property:import:export")
    @SaCheckLogin
    @GetMapping("/template")
    public R<Void> downloadTemplate() {
        // TODO: Phase 5c - 下载基础数据导入模板
        return R.fail("模板下载待实现");
    }

    @SaCheckPermission("thermal:property:import:remove")
    @SaCheckLogin
    @Log(title = "基础数据导入", businessType = BusinessType.DELETE)
    @DeleteMapping
    public R<Void> deleteData() {
        // TODO: Phase 5c - 删除当前用户的待提交数据
        return R.ok();
    }

    @SaCheckPermission("thermal:property:import:import")
    @SaCheckLogin
    @Log(title = "基础数据提交", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public R<Void> submitData() {
        // TODO: Phase 5c - 从临时表提交到正式表
        return R.fail("提交功能待实现");
    }
}
