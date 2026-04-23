package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtHouseStrategy;
import org.dromara.thermal.domain.vo.HtHouseStrategyVo;
import org.dromara.thermal.service.IHtHouseStrategyService;
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
@RequestMapping("/thermal/ht/houseStrategy")
public class HtHouseStrategyController extends BaseController {

    private final IHtHouseStrategyService houseStrategyService;

    /**
     * 分页查询房屋策略绑定
     */
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
     * 批量保存房屋策略绑定
     */
    @SaCheckLogin
    @Log(title = "房屋策略绑定", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public R<Void> insertBatch(@RequestBody List<HtHouseStrategy> list,
                               @RequestParam String orgId,
                               @RequestParam String companyId) {
        return toAjax(houseStrategyService.insertBatch(list, orgId, companyId));
    }

    /**
     * 批量修改房屋策略绑定
     */
    @SaCheckLogin
    @Log(title = "房屋策略绑定", businessType = BusinessType.UPDATE)
    @PutMapping("/batch")
    public R<Void> updateBatch(@RequestBody List<HtHouseStrategy> list) {
        return toAjax(houseStrategyService.updateBatch(list));
    }

    /**
     * 批量删除房屋策略绑定
     */
    @SaCheckLogin
    @Log(title = "房屋策略绑定", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<HtHouseStrategy> list) {
        return toAjax(houseStrategyService.deleteBatch(list));
    }
}
