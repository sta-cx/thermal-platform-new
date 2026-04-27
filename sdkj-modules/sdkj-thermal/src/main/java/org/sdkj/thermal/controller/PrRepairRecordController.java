package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrRepairRecord;
import org.sdkj.thermal.service.IPrRepairRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/repair-record")
public class PrRepairRecordController extends BaseController {

    private final IPrRepairRecordService repairRecordService;

    @SaCheckPermission("thermal:property:repair:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrRepairRecord> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) Integer status,
            PageQuery pageQuery) {
        Page<PrRepairRecord> page = pageQuery.build();
        LambdaQueryWrapper<PrRepairRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(companyId != null && !companyId.isEmpty(), PrRepairRecord::getCompanyId, companyId);
        lqw.eq(orgId != null && !orgId.isEmpty(), PrRepairRecord::getOrgId, orgId);
        lqw.eq(status != null, PrRepairRecord::getRepairStatus, status);
        lqw.orderByDesc(PrRepairRecord::getCreateTime);
        repairRecordService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckPermission("thermal:property:repair:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrRepairRecord> getById(@PathVariable String id) {
        return R.ok(repairRecordService.getById(id));
    }

    @SaCheckPermission("thermal:property:repair:add")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrRepairRecord record) {
        record.setRepairNo(repairRecordService.generateRepairNo());
        return toAjax(repairRecordService.save(record));
    }

    @SaCheckPermission("thermal:property:repair:edit")
    @SaCheckLogin
    @Log(title = "报修记录-编辑", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrRepairRecord record) {
        return toAjax(repairRecordService.updateById(record));
    }

    @SaCheckPermission("thermal:property:repair:remove")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(repairRecordService.removeById(id));
    }

    @SaCheckPermission("thermal:property:repair:dispatch")
    @SaCheckLogin
    @Log(title = "报修派单", businessType = BusinessType.UPDATE)
    @PutMapping("/dispatch")
    public R<Void> dispatch(@RequestParam String ids,
                            @RequestParam String dispatchId,
                            @RequestParam(required = false) String isReject,
                            @RequestParam(required = false) String rejectReason,
                            @RequestParam(required = false) String dispatchMoney) {
        return toAjax(repairRecordService.dispatch(
            ids.split(","), dispatchId, isReject, rejectReason, dispatchMoney));
    }

    @SaCheckPermission("thermal:property:repair:query")
    @SaCheckLogin
    @GetMapping("/count/{companyId}")
    public R<Map<String, Object>> allTypeCount(@PathVariable String companyId) {
        return R.ok(repairRecordService.getAllTypeCount(companyId));
    }

    @SaCheckPermission("thermal:property:repair:evaluate")
    @SaCheckLogin
    @Log(title = "报修评价", businessType = BusinessType.UPDATE)
    @PutMapping("/evaluate")
    public R<Void> evaluate(@RequestParam String id,
                            @RequestParam String value,
                            @RequestParam String type) {
        return toAjax(repairRecordService.updateService(id, value, type));
    }

    /**
     * 批量增加报修项目
     * 旧端点: POST /property/prRepairRecord/insertRepairItems
     * 新端点: POST /thermal/property/repair-record/items
     */
    @SaCheckPermission("thermal:property:repair:edit")
    @SaCheckLogin
    @Log(title = "报修项目", businessType = BusinessType.INSERT)
    @PostMapping("/items")
    public R<Void> insertRepairItems(@RequestBody List<PrRepairRecord> records) {
        return toAjax(repairRecordService.insertRepairItems(records));
    }

    /**
     * 查询房屋是否欠费
     * 旧端点: POST /property/prRepairRecord/getHouseIsOwe
     * 新端点: GET /thermal/property/repair-record/owe
     */
    @SaCheckPermission("thermal:property:repair:query")
    @SaCheckLogin
    @GetMapping("/owe")
    public R<Boolean> getHouseIsOwe(@RequestParam String houseId) {
        return R.ok(repairRecordService.getHouseIsOwe(houseId));
    }

    /**
     * 更新维修服务数据（服务事物/完成情况/物料/失败原因等）
     * 旧端点: POST /property/prRepairRecord/updateDataService
     * 新端点: PUT /thermal/property/repair-record/service
     */
    @SaCheckPermission("thermal:property:repair:edit")
    @SaCheckLogin
    @Log(title = "报修服务", businessType = BusinessType.UPDATE)
    @PutMapping("/service")
    public R<Void> updateDataService(@RequestBody PrRepairRecord record) {
        return toAjax(repairRecordService.updateDataService(record));
    }
}
