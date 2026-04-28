package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.HtScopeVo;
import org.sdkj.thermal.service.IHtScopeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 控制范围管理
 * 迁移自旧系统 HtScopeController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/scope")
public class HtScopeController extends BaseController {

    private final IHtScopeService scopeService;

    /**
     * 根据任务ID获取控制范围内的房屋列表
     */
    @SaCheckPermission("thermal:ht:scope:list")
    @SaCheckLogin
    @GetMapping("/houseList")
    public R<List<HtScopeVo>> getHouseListByTaskId(
            @RequestParam String orgId,
            @RequestParam String taskId,
            @RequestParam(required = false) Integer scopeType) {
        return R.ok(scopeService.getHouseListByTaskId(orgId, taskId, scopeType));
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(scopeService.list());
    }
}
