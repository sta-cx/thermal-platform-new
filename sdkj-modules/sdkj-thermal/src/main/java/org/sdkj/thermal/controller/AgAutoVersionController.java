package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.domain.AgAutoVersion;
import org.sdkj.thermal.service.IAgAutoVersionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自助机版本管理
 * 迁移自旧系统 AgAutoVersionController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/auto-version")
public class AgAutoVersionController {

    private final IAgAutoVersionService service;

    @SaCheckPermission("thermal:agent:autoVersion:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<AgAutoVersion>> list() {
        return R.ok(service.list());
    }

    @SaCheckPermission("thermal:agent:autoVersion:edit")
    @SaCheckLogin
    @PutMapping
    public R<Void> update(@RequestBody AgAutoVersion version) {
        return service.updateById(version) ? R.ok() : R.fail("更新失败");
    }
}
