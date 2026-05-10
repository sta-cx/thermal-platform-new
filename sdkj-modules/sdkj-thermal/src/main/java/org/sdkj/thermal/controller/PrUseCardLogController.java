package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.sdkj.common.core.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrUseCardLog;
import org.sdkj.thermal.domain.bo.PrUseCardLogBo;
import org.sdkj.thermal.service.IPrUseCardLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 写卡日志管理
 * 迁移自旧系统 PrUseCardLogController
 * 旧端点: /property/prUseCardLog/* -> 新端点: /thermal/property/use-card-log/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/use-card-log")
public class PrUseCardLogController extends BaseController {

    private final IPrUseCardLogService useCardLogService;

    @SaCheckPermission("thermal:property:useCardLog:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<PrUseCardLog>> list(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String meterNum) {
        LambdaQueryWrapper<PrUseCardLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrUseCardLog::getOrgId, orgId)
                .eq(StringUtils.isNotBlank(meterNum), PrUseCardLog::getMeterNum, meterNum)
                .orderByDesc(PrUseCardLog::getOperationTime);
        return R.ok(useCardLogService.list(lqw));
    }

    @SaCheckPermission("thermal:property:useCardLog:add")
    @SaCheckLogin
    @Log(title = "写卡日志", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insertWriteUseCardLog(@RequestBody PrUseCardLogBo bo) {
        PrUseCardLog log = MapstructUtils.convert(bo, PrUseCardLog.class);
        return toAjax(useCardLogService.save(log));
    }

    @SaCheckPermission("thermal:property:useCardLog:list")
    @SaCheckLogin
    @GetMapping("/valve-status")
    public R<List<PrUseCardLog>> pageListValveOCStatus(@RequestParam(required = false) String orgId) {
        LambdaQueryWrapper<PrUseCardLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrUseCardLog::getOrgId, orgId)
                .isNotNull(PrUseCardLog::getValveStatus)
                .orderByDesc(PrUseCardLog::getOperationTime);
        return R.ok(useCardLogService.list(lqw));
    }

    @SaCheckPermission("thermal:property:useCardLog:add")
    @SaCheckLogin
    @Log(title = "阀门状态日志", businessType = BusinessType.INSERT)
    @PostMapping("/valve-status")
    public R<Void> insertValveOCStatusLog(@RequestBody PrUseCardLogBo bo) {
        PrUseCardLog log = MapstructUtils.convert(bo, PrUseCardLog.class);
        return toAjax(useCardLogService.changeValveStatus(log.getMeterId(), log.getValveStatus()));
    }
}
