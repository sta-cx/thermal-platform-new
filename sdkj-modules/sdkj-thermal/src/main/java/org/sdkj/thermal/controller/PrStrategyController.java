package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrStrategy;
import org.sdkj.thermal.domain.bo.PrStrategyBo;
import org.sdkj.thermal.domain.vo.PrStrategyVo;
import org.sdkj.thermal.service.IPrStrategyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/strategy")
public class PrStrategyController extends BaseController {

    private final IPrStrategyService strategyService;

    @SaCheckPermission("thermal:property:strategy:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<Page<PrStrategyVo>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String orgId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PrStrategy> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrStrategy> lqw = new LambdaQueryWrapper<>();
        lqw.eq(type != null && !type.isEmpty(), PrStrategy::getType, type);
        lqw.eq(orgId != null && !orgId.isEmpty(), PrStrategy::getOrgId, orgId);
        lqw.orderByDesc(PrStrategy::getCreateTime);
        return R.ok(strategyService.page(page, lqw).convert(e -> MapstructUtils.convert(e, PrStrategyVo.class)));
    }

    @SaCheckPermission("thermal:property:strategy:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrStrategyVo> getById(@PathVariable Long id) {
        return R.ok(strategyService.selectVoById(id));
    }

    @SaCheckPermission("thermal:property:strategy:add")
    @SaCheckLogin
    @Log(title = "物业策略", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrStrategyBo bo) {
        return toAjax(strategyService.saveStrategy(bo));
    }

    @SaCheckPermission("thermal:property:strategy:edit")
    @SaCheckLogin
    @Log(title = "物业策略", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrStrategyBo bo) {
        return toAjax(strategyService.updateStrategy(bo));
    }

    @SaCheckPermission("thermal:property:strategy:remove")
    @SaCheckLogin
    @Log(title = "物业策略", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        return toAjax(strategyService.removeById(id));
    }
}
