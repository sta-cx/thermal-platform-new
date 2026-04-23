package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtAlert;
import org.dromara.thermal.domain.vo.HtAlertVo;
import org.dromara.thermal.service.IHtAlertService;
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
    @SaCheckLogin
    @Log(title = "报警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody HtAlert alert) {
        return toAjax(htAlertService.save(alert));
    }

    /**
     * 修改报警记录
     * 旧端点: POST /htAlert/updateData
     * 新端点: PUT /thermal/ht/alert
     */
    @SaCheckLogin
    @Log(title = "报警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtAlert alert) {
        return toAjax(htAlertService.updateById(alert));
    }

    /**
     * 查询仪表的异常报警列表
     * 旧端点: GET /htAlert/queryAbnormalAlarmList
     * 新端点: GET /thermal/ht/alert/abnormal
     */
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
    @SaCheckLogin
    @GetMapping("/typeCount")
    public R<List<Map<String, Object>>> typeCount(@RequestParam String companyId) {
        return R.ok(htAlertService.selectTypeCount(companyId));
    }

}
