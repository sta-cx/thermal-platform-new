package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrInspectionRecord;
import org.sdkj.thermal.service.IPrInspectionRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/inspection-record")
public class PrInspectionRecordController extends BaseController {

    private final IPrInspectionRecordService inspectionRecordService;

    @SaCheckPermission("thermal:property:inspection:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrInspectionRecord> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String companyId,
            PageQuery pageQuery) {
        LambdaQueryWrapper<PrInspectionRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StrUtil.isNotBlank(companyId), PrInspectionRecord::getCompanyId, companyId);
        lqw.eq(StrUtil.isNotBlank(orgId), PrInspectionRecord::getOrgId, orgId);
        lqw.and(StrUtil.isNotBlank(search), wrapper -> wrapper
            .like(PrInspectionRecord::getPersonName, search)
            .or()
            .like(PrInspectionRecord::getEquipmentName, search)
            .or()
            .like(PrInspectionRecord::getContent, search));
        lqw.orderByDesc(PrInspectionRecord::getCreateTime);
        Page<PrInspectionRecord> result = inspectionRecordService.page(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @SaCheckPermission("thermal:property:inspection:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrInspectionRecord> getById(@PathVariable String id) {
        return R.ok(inspectionRecordService.getById(id));
    }

    @SaCheckPermission("thermal:property:inspection:add")
    @SaCheckLogin
    @Log(title = "巡检记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrInspectionRecord record) {
        return toAjax(inspectionRecordService.save(record));
    }

    @SaCheckPermission("thermal:property:inspection:edit")
    @SaCheckLogin
    @Log(title = "巡检记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrInspectionRecord record) {
        return toAjax(inspectionRecordService.updateById(record));
    }

    @SaCheckPermission("thermal:property:inspection:remove")
    @SaCheckLogin
    @Log(title = "巡检记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(inspectionRecordService.removeById(id));
    }
}
