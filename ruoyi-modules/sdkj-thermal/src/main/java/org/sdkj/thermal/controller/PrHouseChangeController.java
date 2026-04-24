package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.sdkj.common.core.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.service.IPrHouseChangeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋入住/迁出管理
 * 迁移自旧系统 PrHouseChangeController
 * 旧端点: /property/houseChange/* -> 新端点: /thermal/property/house-change/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/house-change")
public class PrHouseChangeController extends BaseController {

    private final IPrHouseChangeService houseChangeService;

    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHouse> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHouse::getCompanyId, companyId)
                .eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId)
                .eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId)
                .eq(StringUtils.isNotBlank(unitCode), PrHouse::getUnitCode, unitCode)
                .like(StringUtils.isNotBlank(search), PrHouse::getRoomNum, search)
                .orderByDesc(PrHouse::getCreateTime);
        return houseChangeService.selectPageList(lqw, pageQuery);
    }

    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHouse> getById(@PathVariable String id) {
        return R.ok(houseChangeService.getById(id));
    }

    @SaCheckLogin
    @Log(title = "入住登记", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insertData(@RequestBody PrHouse house) {
        return toAjax(houseChangeService.save(house));
    }

    @SaCheckLogin
    @Log(title = "入住登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateData(@RequestBody PrHouse house) {
        return toAjax(houseChangeService.updateById(house));
    }

    @SaCheckLogin
    @Log(title = "迁出", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> deleteData(@PathVariable String id) {
        return toAjax(houseChangeService.removeById(id));
    }

    @SaCheckLogin
    @Log(title = "批量迁出", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> deleteMulData(@RequestBody List<String> ids) {
        return toAjax(houseChangeService.removeByIds(ids));
    }

    @SaCheckLogin
    @Log(title = "入住审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public R<Void> changeData(@RequestBody PrHouse house) {
        return toAjax(houseChangeService.audit(house));
    }
}
