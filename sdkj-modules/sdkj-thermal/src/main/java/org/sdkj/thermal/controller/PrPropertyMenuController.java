package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.AgPropertyMenu;
import org.sdkj.thermal.service.IAgPropertyMenuService;
import org.sdkj.thermal.vo.TreeNode;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物业菜单管理
 * 复用 AgPropertyMenuService
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/menu")
public class PrPropertyMenuController extends BaseController {

    private final IAgPropertyMenuService menuService;

    @SaCheckPermission("thermal:property:menu:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<AgPropertyMenu>> list() {
        return R.ok(menuService.list());
    }

    @SaCheckPermission("thermal:property:menu:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<AgPropertyMenu> getInfo(@PathVariable String id) {
        return R.ok(menuService.getById(id));
    }

    @SaCheckPermission("thermal:property:menu:add")
    @SaCheckLogin
    @Log(title = "物业菜单", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody AgPropertyMenu menu) {
        return toAjax(menuService.save(menu));
    }

    @SaCheckPermission("thermal:property:menu:edit")
    @SaCheckLogin
    @Log(title = "物业菜单", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody AgPropertyMenu menu) {
        return toAjax(menuService.updateById(menu));
    }

    @SaCheckPermission("thermal:property:menu:remove")
    @SaCheckLogin
    @Log(title = "物业菜单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(menuService.removeById(id));
    }
}
