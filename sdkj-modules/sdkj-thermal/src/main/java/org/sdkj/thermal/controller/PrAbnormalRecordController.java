package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrAbnormalRecord;
import org.sdkj.thermal.service.IPrAbnormalRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/abnormal")
public class PrAbnormalRecordController extends BaseController {

    private final IPrAbnormalRecordService abnormalService;

    @SaCheckPermission("thermal:property:abnormal:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<Page<PrAbnormalRecord>> list(
            @RequestParam(required = false) String abnormalType,
            @RequestParam(required = false) String handleStatus,
            @RequestParam(required = false) String orgId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PrAbnormalRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrAbnormalRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(abnormalType != null && !abnormalType.isEmpty(), PrAbnormalRecord::getAbnormalType, abnormalType);
        lqw.eq(handleStatus != null && !handleStatus.isEmpty(), PrAbnormalRecord::getHandleStatus, handleStatus);
        lqw.eq(orgId != null && !orgId.isEmpty(), PrAbnormalRecord::getOrgId, orgId);
        lqw.orderByDesc(PrAbnormalRecord::getCreateTime);
        return R.ok(abnormalService.page(page, lqw));
    }

    @SaCheckPermission("thermal:property:abnormal:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrAbnormalRecord> getById(@PathVariable String id) {
        return R.ok(abnormalService.getById(id));
    }

    @SaCheckPermission("thermal:property:abnormal:add")
    @SaCheckLogin
    @Log(title = "异常记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrAbnormalRecord record) {
        return toAjax(abnormalService.save(record));
    }

    @SaCheckPermission("thermal:property:abnormal:edit")
    @SaCheckLogin
    @Log(title = "异常记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrAbnormalRecord record) {
        return toAjax(abnormalService.updateById(record));
    }

    @SaCheckPermission("thermal:property:abnormal:remove")
    @SaCheckLogin
    @Log(title = "异常记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(abnormalService.removeById(id));
    }
}
