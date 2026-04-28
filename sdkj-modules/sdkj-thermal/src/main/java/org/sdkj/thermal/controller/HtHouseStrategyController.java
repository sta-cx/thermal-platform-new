package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtHouseStrategy;
import org.sdkj.thermal.domain.bo.HtHouseStrategyBo;
import org.sdkj.thermal.domain.vo.HtHouseStrategyVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.service.IHtHouseStrategyService;
import org.sdkj.thermal.service.IPrHouseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * 房屋-策略绑定管理
 * 迁移自旧系统 HtHouseStrategyController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping({"/thermal/ht/houseStrategy", "/thermal/ht/house-strategy"})
public class HtHouseStrategyController extends BaseController {

    private final IHtHouseStrategyService houseStrategyService;
    private final IPrHouseService houseService;

    /**
     * 分页查询房屋策略绑定
     */
    @SaCheckPermission("thermal:ht:houseStrategy:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtHouseStrategyVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtHouseStrategy> lqw = new LambdaQueryWrapper<>();
        lqw.eq(companyId != null && !companyId.isEmpty(), HtHouseStrategy::getCompanyId, companyId);
        lqw.eq(orgId != null && !orgId.isEmpty(), HtHouseStrategy::getOrgId, orgId);
        lqw.eq(buildingId != null && !buildingId.isEmpty(), HtHouseStrategy::getTasksId, buildingId);
        String keyword = search != null ? search.trim() : null;
        lqw.like(keyword != null && !keyword.isEmpty(), HtHouseStrategy::getName, keyword);
        lqw.orderByDesc(HtHouseStrategy::getCreateTime);
        return houseStrategyService.selectPageList(lqw, pageQuery);
    }

    /**
     * 查询可用房屋（用于策略绑定）
     * 根据小区/楼宇查询可绑定到热力调控策略的房屋列表
     */
    @SaCheckPermission("thermal:ht:houseStrategy:list")
    @SaCheckLogin
    @GetMapping("/houses")
    public R<List<PrHouseVo>> queryPrHouse(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String search) {
        List<PrHouseVo> list = houseService.selectForStrategyBinding(companyId, orgId, buildingId, search);
        return R.ok(list);
    }

    /**
     * 批量保存房屋策略绑定
     */
    @SaCheckPermission("thermal:ht:houseStrategy:add")
    @SaCheckLogin
    @Log(title = "房屋策略绑定", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public R<Void> insertBatch(@RequestBody List<HtHouseStrategyBo> boList,
                               @RequestParam String orgId,
                               @RequestParam String companyId) {
        List<HtHouseStrategy> list = MapstructUtils.convert(boList, HtHouseStrategy.class);
        return toAjax(houseStrategyService.insertBatch(list, orgId, companyId));
    }

    /**
     * 批量修改房屋策略绑定
     */
    @SaCheckPermission("thermal:ht:houseStrategy:edit")
    @SaCheckLogin
    @Log(title = "房屋策略绑定", businessType = BusinessType.UPDATE)
    @PutMapping("/batch")
    public R<Void> updateBatch(@RequestBody List<HtHouseStrategyBo> boList) {
        List<HtHouseStrategy> list = MapstructUtils.convert(boList, HtHouseStrategy.class);
        return toAjax(houseStrategyService.updateBatch(list));
    }

    /**
     * 批量删除房屋策略绑定
     */
    @SaCheckPermission("thermal:ht:houseStrategy:remove")
    @SaCheckLogin
    @Log(title = "房屋策略绑定", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<HtHouseStrategyBo> boList) {
        List<HtHouseStrategy> list = MapstructUtils.convert(boList, HtHouseStrategy.class);
        return toAjax(houseStrategyService.deleteBatch(list));
    }
}
