package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrFamily;
import org.sdkj.thermal.domain.bo.PrFamilyBo;
import org.sdkj.thermal.domain.vo.PrFamilyVo;
import org.sdkj.thermal.service.IPrFamilyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 家庭成员信息管理
 * 迁移自旧系统 PrFamilyController
 * 旧端点: /property/family/* -> 新端点: /thermal/property/family/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/family")
public class PrFamilyController extends BaseController {

    private final IPrFamilyService familyService;

    /**
     * 分页查询家庭成员列表
     * 旧端点: GET /property/family/pageList
     * 新端点: GET /thermal/property/family/list
     */
    @SaCheckPermission("thermal:property:family:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrFamilyVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String houseId,
            PageQuery pageQuery) {
        return familyService.selectPageList(search, houseId, pageQuery);
    }

    /**
     * 查询家庭成员详情
     * 旧端点: GET /property/family/queryPrFamily/{id}
     * 新端点: GET /thermal/property/family/{id}
     */
    @SaCheckPermission("thermal:property:family:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrFamilyVo> getById(@PathVariable String id) {
        return R.ok(familyService.selectVoById(id));
    }

    /**
     * 新增家庭成员
     * 旧端点: POST /property/family/insertData
     * 新端点: POST /thermal/property/family
     */
    @SaCheckPermission("thermal:property:family:add")
    @SaCheckLogin
    @Log(title = "家庭成员", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrFamilyBo bo) {
        PrFamily family = MapstructUtils.convert(bo, PrFamily.class);
        return toAjax(familyService.save(family));
    }

    /**
     * 修改家庭成员
     * 旧端点: POST /property/family/updateData
     * 新端点: PUT /thermal/property/family
     */
    @SaCheckPermission("thermal:property:family:edit")
    @SaCheckLogin
    @Log(title = "家庭成员", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrFamilyBo bo) {
        PrFamily family = MapstructUtils.convert(bo, PrFamily.class);
        return toAjax(familyService.updateById(family));
    }

    /**
     * 删除家庭成员
     * 旧端点: POST /property/family/deleteData/{id}
     * 新端点: DELETE /thermal/property/family/{id}
     */
    @SaCheckPermission("thermal:property:family:remove")
    @SaCheckLogin
    @Log(title = "家庭成员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(familyService.removeById(id));
    }

}
