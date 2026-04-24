package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtStrategy;
import org.sdkj.thermal.domain.vo.HtStrategyVo;
import org.sdkj.thermal.service.IHtStrategyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 控制策略管理
 * 迁移自旧系统 HtStrategyController
 * 旧端点: /htStrategy/* -> 新端点: /thermal/ht/strategy/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/strategy")
public class HtStrategyController extends BaseController {

    private final IHtStrategyService htStrategyService;

    /**
     * 分页查询控制策略列表
     * 旧端点: GET /htStrategy/pageList
     * 新端点: GET /thermal/ht/strategy/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtStrategyVo> list(
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtStrategy> lqw = new LambdaQueryWrapper<>();
        String keyword = search != null ? search.trim() : null;
        boolean hasSearch = keyword != null && !keyword.isEmpty();
        lqw.like(hasSearch, HtStrategy::getName, keyword);
        lqw.orderByDesc(HtStrategy::getCreateTime);
        return htStrategyService.selectPageList(lqw, pageQuery);
    }

    /**
     * 根据ID查询控制策略详情（包含子表记录）
     * 旧端点: GET /htStrategy/queryHtStrategy
     * 新端点: GET /thermal/ht/strategy/{id}
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtStrategyVo> getInfo(@PathVariable String id) {
        return R.ok(htStrategyService.selectDetailById(id));
    }

    /**
     * 新增控制策略
     * 旧端点: POST /htStrategy/insertData
     * 新端点: POST /thermal/ht/strategy
     */
    @SaCheckLogin
    @Log(title = "控制策略", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody HtStrategy strategy) {
        return toAjax(htStrategyService.save(strategy));
    }

    /**
     * 修改控制策略
     * 旧端点: POST /htStrategy/updateData
     * 新端点: PUT /thermal/ht/strategy
     */
    @SaCheckLogin
    @Log(title = "控制策略", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtStrategy strategy) {
        return toAjax(htStrategyService.updateById(strategy));
    }

    /**
     * 删除控制策略
     * 旧端点: POST /htStrategy/deleteData
     * 新端点: DELETE /thermal/ht/strategy/{id}
     */
    @SaCheckLogin
    @Log(title = "控制策略", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        return toAjax(htStrategyService.removeById(id));
    }

    /**
     * 查询所有控制策略
     * 旧端点: GET /htStrategy/queryHtStrategyList
     * 新端点: GET /thermal/ht/strategy/all
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<HtStrategy>> all() {
        return R.ok(htStrategyService.selectAllList());
    }

}
