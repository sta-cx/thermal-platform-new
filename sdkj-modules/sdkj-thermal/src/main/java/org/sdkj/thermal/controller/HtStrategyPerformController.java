package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.bo.HtStrategyPerformBo;
import org.sdkj.thermal.domain.vo.HtStrategyPerformVo;
import org.sdkj.thermal.service.IHtStrategyPerformService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 策略执行记录管理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/strategy-perform")
public class HtStrategyPerformController extends BaseController {

    private final IHtStrategyPerformService htStrategyPerformService;

    /**
     * 分页查询策略执行记录
     */
    @SaCheckPermission("thermal:ht:strategy:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtStrategyPerformVo> list(
            @RequestParam(required = false) Long strategyId,
            PageQuery pageQuery) {
        return htStrategyPerformService.selectPageList(strategyId, pageQuery);
    }

    /**
     * 根据策略ID查询全部执行记录
     */
    @SaCheckPermission("thermal:ht:strategy:query")
    @SaCheckLogin
    @GetMapping("/all/{strategyId}")
    public R<List<HtStrategyPerformVo>> all(@PathVariable Long strategyId) {
        return R.ok(htStrategyPerformService.selectByStrategyId(strategyId));
    }

    /**
     * 批量保存策略执行记录
     */
    @SaCheckPermission("thermal:ht:strategy:edit")
    @SaCheckLogin
    @Log(title = "策略执行记录", businessType = BusinessType.UPDATE)
    @PostMapping
    public R<Void> insertBatch(
            @RequestParam(required = false) Long strategyId,
            @Validated @RequestBody List<HtStrategyPerformBo> list) {
        htStrategyPerformService.insertBatch(strategyId, list);
        return R.ok();
    }

    /**
     * 根据策略ID删除执行记录
     */
    @SaCheckPermission("thermal:ht:strategy:remove")
    @SaCheckLogin
    @Log(title = "策略执行记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{strategyId}")
    public R<Void> deleteByStrategyId(@PathVariable Long strategyId) {
        htStrategyPerformService.deleteByStrategyId(strategyId);
        return R.ok();
    }
}
