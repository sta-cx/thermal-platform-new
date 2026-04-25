package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
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
    public R<Page<PrInspectionRecord>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String companyId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PrInspectionRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PrInspectionRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(orgId != null && !orgId.isEmpty(), PrInspectionRecord::getOrgId, orgId);
        lqw.eq(companyId != null && !companyId.isEmpty(), PrInspectionRecord::getCompanyId, companyId);
        lqw.orderByDesc(PrInspectionRecord::getCreateTime);
        return R.ok(inspectionRecordService.page(page, lqw));
    }
}
