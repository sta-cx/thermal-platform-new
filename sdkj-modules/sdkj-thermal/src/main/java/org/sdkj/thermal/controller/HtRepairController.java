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
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.bo.HtRepairBo;
import org.sdkj.thermal.domain.vo.HtRepairVo;
import org.sdkj.thermal.service.IHtRepairService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报修记录管理
 * 迁移自旧系统 HtRepairController
 * 旧端点: /htRepair/* -> 新端点: /thermal/ht/repair/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/repair")
public class HtRepairController extends BaseController {

    private final IHtRepairService htRepairService;

    /**
     * 分页查询报修记录列表
     * 旧端点: GET /htRepair/pageList
     * 新端点: GET /thermal/ht/repair/list
     */
    @SaCheckPermission("thermal:ht:repair:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtRepairVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) Integer repairType,
            @RequestParam(required = false) Integer repairStatus,
            @RequestParam(required = false) Integer urgentType,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getIsDelete, 0);
        lqw.eq(companyId != null, HtRepair::getCompanyId, companyId);
        lqw.eq(orgId != null, HtRepair::getOrgId, orgId);
        lqw.eq(buildingId != null, HtRepair::getBuildingId, buildingId);
        lqw.eq(repairType != null, HtRepair::getRepairType, repairType);
        lqw.eq(repairStatus != null, HtRepair::getRepairStatus, repairStatus);
        lqw.eq(urgentType != null, HtRepair::getUrgentType, urgentType);
        lqw.orderByDesc(HtRepair::getRepairTime);
        return htRepairService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增报修记录
     * 旧端点: POST /htRepair/insertData
     * 新端点: POST /thermal/ht/repair
     */
    @SaCheckPermission("thermal:ht:repair:add")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody HtRepairBo bo) {
        HtRepair repair = MapstructUtils.convert(bo, HtRepair.class);
        repair.setRepairNo(htRepairService.generateRepairNo());
        return toAjax(htRepairService.save(repair));
    }

    /**
     * 修改报修记录
     * 旧端点: POST /htRepair/updateData
     * 新端点: PUT /thermal/ht/repair
     */
    @SaCheckPermission("thermal:ht:repair:edit")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtRepairBo bo) {
        HtRepair repair = MapstructUtils.convert(bo, HtRepair.class);
        return toAjax(htRepairService.updateById(repair));
    }

    /**
     * 派单报修
     * 旧端点: POST /htRepair/updateDispatchData
     * 新端点: PUT /thermal/ht/repair/dispatch
     */
    @SaCheckPermission("thermal:ht:repair:edit")
    @SaCheckLogin
    @Log(title = "报修记录派单", businessType = BusinessType.UPDATE)
    @PutMapping("/dispatch")
    public R<Void> dispatch(
            @RequestParam String repairNo,
            @RequestParam String fixId,
            @RequestParam String fixName) {
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getRepairNo, repairNo);
        HtRepair repair = new HtRepair();
        repair.setFixId(fixId);
        repair.setFixName(fixName);
        repair.setDispatchTime(new Date());
        return toAjax(htRepairService.update(repair, lqw));
    }

    /**
     * 删除报修记录（逻辑删除）
     * 旧端点: POST /htRepair/deleteData
     * 新端点: DELETE /thermal/ht/repair/{repairNo}
     */
    @SaCheckPermission("thermal:ht:repair:remove")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{repairNo}")
    public R<Void> delete(@PathVariable String repairNo) {
        Long deptId = LoginHelper.getDeptId();
        int rows = htRepairService.markAsDeleted(repairNo, deptId != null ? deptId.toString() : null);
        if (rows > 0) {
            return toAjax(true);
        }
        return R.fail("删除失败，请检查报修编号");
    }

    /**
     * 更新报修状态和结果
     * 旧端点: POST /htRepair/updateStatusResultData
     * 新端点: PUT /thermal/ht/repair/status
     */
    @SaCheckPermission("thermal:ht:repair:edit")
    @SaCheckLogin
    @Log(title = "报修记录状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public R<Void> updateStatus(
            @RequestParam String repairNo,
            @RequestParam Integer repairStatus,
            @RequestParam(required = false) String repairResult) {
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getRepairNo, repairNo);
        HtRepair repair = new HtRepair();
        repair.setRepairStatus(repairStatus);
        repair.setRepairResult(repairResult);
        return toAjax(htRepairService.update(repair, lqw));
    }

    /**
     * 按报修类型统计数量
     * 旧端点: GET /htRepair/queryTypeCount
     * 新端点: GET /thermal/ht/repair/typeCount
     */
    @SaCheckPermission("thermal:ht:repair:query")
    @SaCheckLogin
    @GetMapping("/typeCount")
    public R<List<Map<String, Object>>> typeCount(@RequestParam String companyId) {
        return R.ok(htRepairService.selectTypeCount(companyId));
    }

    /**
     * 根据房间ID查询报修记录
     * 旧端点: GET /htRepair/queryRoomId
     * 新端点: GET /thermal/ht/repair/room
     */
    @SaCheckPermission("thermal:ht:repair:query")
    @SaCheckLogin
    @GetMapping("/room")
    public R<List<HtRepairVo>> getByRoom(@RequestParam String roomId) {
        return R.ok(htRepairService.selectByRoomId(roomId));
    }

}
