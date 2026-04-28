package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.domain.bo.HtAlertBo;
import org.sdkj.thermal.domain.vo.HtAlertVo;
import org.sdkj.thermal.service.IHtAlertService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 报警记录管理
 * 迁移自旧系统 HtAlertController
 * 旧端点: /htAlert/* -> 新端点: /thermal/ht/alert/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/alert")
public class HtAlertController extends BaseController {

    private final IHtAlertService htAlertService;

    /**
     * 分页查询报警记录列表
     * 旧端点: GET /htAlert/pageList
     * 新端点: GET /thermal/ht/alert/list
     */
    @SaCheckPermission("thermal:ht:alert:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtAlertVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) Integer alertType,
            @RequestParam(required = false) String alertStatus,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtAlert> lqw = new LambdaQueryWrapper<>();
        lqw.eq(companyId != null && !companyId.isEmpty(), HtAlert::getCompanyId, companyId)
           .eq(orgId != null && !orgId.isEmpty(), HtAlert::getOrgId, orgId)
           .eq(buildingId != null && !buildingId.isEmpty(), HtAlert::getBuildingId, buildingId)
           .eq(alertType != null, HtAlert::getAlertType, alertType)
           .eq(alertStatus != null && !alertStatus.isEmpty(), HtAlert::getAlertStatus, alertStatus);
        lqw.orderByDesc(HtAlert::getAlertTime);
        return htAlertService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增报警记录
     * 旧端点: POST /htAlert/insertData
     * 新端点: POST /thermal/ht/alert
     */
    @SaCheckPermission("thermal:ht:alert:add")
    @SaCheckLogin
    @Log(title = "报警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody HtAlertBo bo) {
        HtAlert alert = MapstructUtils.convert(bo, HtAlert.class);
        return toAjax(htAlertService.save(alert));
    }

    /**
     * 修改报警记录
     * 旧端点: POST /htAlert/updateData
     * 新端点: PUT /thermal/ht/alert
     */
    @SaCheckPermission("thermal:ht:alert:edit")
    @SaCheckLogin
    @Log(title = "报警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtAlertBo bo) {
        HtAlert alert = MapstructUtils.convert(bo, HtAlert.class);
        return toAjax(htAlertService.updateById(alert));
    }

    /**
     * 根据ID查询报警详情
     * 新端点: GET /thermal/ht/alert/{id}
     */
    @SaCheckPermission("thermal:ht:alert:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtAlert> getInfo(@PathVariable String id) {
        return R.ok(htAlertService.getById(id));
    }

    /**
     * 查询仪表的异常报警列表
     * 旧端点: GET /htAlert/queryAbnormalAlarmList
     * 新端点: GET /thermal/ht/alert/abnormal
     */
    @SaCheckPermission("thermal:ht:alert:query")
    @SaCheckLogin
    @GetMapping("/abnormal")
    public R<List<HtAlertVo>> abnormalAlarmList(@RequestParam String meterId) {
        return R.ok(htAlertService.selectAbnormalAlarmList(meterId));
    }

    /**
     * 按报警类型统计数量
     * 旧端点: GET /htAlert/queryTypeCount
     * 新端点: GET /thermal/ht/alert/typeCount
     */
    @SaCheckPermission("thermal:ht:alert:query")
    @SaCheckLogin
    @GetMapping("/typeCount")
    public R<List<Map<String, Object>>> typeCount(@RequestParam String companyId) {
        return R.ok(htAlertService.selectTypeCount(companyId));
    }

    /**
     * 批量新增报警记录
     * 新端点: POST /thermal/ht/alert/batch
     */
    @SaCheckPermission("thermal:ht:alert:add")
    @SaCheckLogin
    @Log(title = "报警记录", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public R<Void> batchInsert(@Validated @RequestBody List<HtAlertBo> boList) {
        List<HtAlert> list = MapstructUtils.convert(boList, HtAlert.class);
        return toAjax(htAlertService.batchInsertAlerts(list));
    }

    /**
     * 按报警类型和DTU维度统计数量
     * 新端点: GET /thermal/ht/alert/typeCountDtu
     */
    @SaCheckPermission("thermal:ht:alert:query")
    @SaCheckLogin
    @GetMapping("/typeCountDtu")
    public R<List<Map<String, Object>>> typeCountDtu(@RequestParam String companyId) {
        return R.ok(htAlertService.selectTypeCountDtu(companyId));
    }

}
