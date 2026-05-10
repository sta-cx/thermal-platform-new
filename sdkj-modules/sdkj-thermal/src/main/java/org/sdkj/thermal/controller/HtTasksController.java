package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.domain.HtTaskSettingLog;
import org.sdkj.thermal.domain.PrHouseLog;
import org.sdkj.thermal.domain.bo.HtTasksBo;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogItemVo;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogVo;
import org.sdkj.thermal.domain.vo.HtTasksVo;
import org.sdkj.thermal.service.IHtTaskSettingLogService;
import org.sdkj.thermal.service.IHtTasksService;
import org.sdkj.thermal.service.IPrHouseLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;

/**
 * 调控任务管理
 * 迁移自旧系统 HtTasksController
 * Phase 4c-2: 基础 CRUD + 查询（不含 Quartz 调度和指令生成）
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/tasks")
public class HtTasksController extends BaseController {

    private final IHtTasksService tasksService;
    private final IHtTaskSettingLogService settingLogService;
    private final IPrHouseLogService houseLogService;

    /**
     * 分页查询任务列表
     */
    @SaCheckPermission("thermal:ht:tasks:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtTasksVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer scopeType,
            @RequestParam(required = false) Integer adjustBasis,
            @RequestParam(required = false) String strategyId,
            @RequestParam(required = false) Integer priority,
            PageQuery pageQuery) {
        String keyword = search != null ? search.trim() : null;
        LambdaQueryWrapper<HtTasks> lqw = new LambdaQueryWrapper<>();
        lqw.like(keyword != null && !keyword.isEmpty(), HtTasks::getName, keyword);
        lqw.eq(orgId != null && !orgId.isEmpty(), HtTasks::getOrgId, orgId);
        lqw.eq(status != null, HtTasks::getStatus, status);
        lqw.eq(type != null, HtTasks::getType, type);
        lqw.eq(scopeType != null, HtTasks::getScopeType, scopeType);
        lqw.eq(adjustBasis != null, HtTasks::getAdjustBasis, adjustBasis);
        lqw.eq(strategyId != null && !strategyId.isEmpty(), HtTasks::getStrategyId, strategyId);
        lqw.eq(priority != null, HtTasks::getPriority, priority);
        lqw.orderByDesc(HtTasks::getCreateTime);
        return tasksService.selectPageList(lqw, pageQuery);
    }

    /**
     * 查询所有任务（按组织）
     */
    @SaCheckPermission("thermal:ht:tasks:list")
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<HtTasks>> all(@RequestParam(required = false) String orgId) {
        return R.ok(tasksService.selectAllByOrgId(orgId));
    }

    /**
     * 根据ID查询任务详情
     */
    @SaCheckPermission("thermal:ht:tasks:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtTasks> getById(@PathVariable Long id) {
        return R.ok(tasksService.getById(id));
    }

    /**
     * 新增调控任务
     */
    @SaCheckPermission("thermal:ht:tasks:add")
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> save(@Validated @RequestBody HtTasksBo bo,
                        @RequestParam(required = false) List<Long> scopeIds) {
        HtTasks task = MapstructUtils.convert(bo, HtTasks.class);
        task.setBeanClass("org.sdkj.thermal.quartz.ThermalJob");
        if (task.getNumber() == null) task.setNumber(0);
        if (task.getStatus() == null) task.setStatus(0);
        if (task.getIsUseReportRate() == null) task.setIsUseReportRate(0);
        if (task.getReportRate() == null) task.setReportRate(0);
        if (task.getIsUseFirstControl() == null) task.setIsUseFirstControl(0);
        return toAjax(tasksService.saveWithScope(task, scopeIds));
    }

    /**
     * 修改调控任务
     */
    @SaCheckPermission("thermal:ht:tasks:edit")
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtTasksBo bo,
                          @RequestParam(required = false) List<Long> scopeIds) {
        HtTasks task = MapstructUtils.convert(bo, HtTasks.class);
        HtTasks existing = tasksService.getById(task.getId());
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            return R.fail("运行中的任务不允许修改，请先停止任务！");
        }
        return toAjax(tasksService.updateWithScope(task, scopeIds));
    }

    /**
     * 删除调控任务
     */
    @SaCheckPermission("thermal:ht:tasks:remove")
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        HtTasks existing = tasksService.getById(id);
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            return R.fail("运行中的任务不允许删除，请先停止任务！");
        }
        return toAjax(tasksService.removeById(id));
    }

    /**
     * 批量删除调控任务
     */
    @SaCheckPermission("thermal:ht:tasks:remove")
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> removeBatch(@RequestParam List<Long> ids) {
        List<HtTasks> tasks = tasksService.listByIds(ids);
        for (HtTasks t : tasks) {
            if (t.getStatus() != null && t.getStatus() == 1) {
                return R.fail("任务「" + t.getName() + "」正在运行，不允许删除，请先停止任务！");
            }
        }
        return toAjax(tasksService.removeByIds(ids));
    }

    /**
     * 分页查询设定日志
     */
    @SaCheckPermission("thermal:ht:tasks:list")
    @SaCheckLogin
    @GetMapping("/settingLog")
    public TableDataInfo<HtTaskSettingLogVo> settingLog(
            @RequestParam(required = false) String taskId,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtTaskSettingLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(taskId != null && !taskId.isEmpty(), HtTaskSettingLog::getTaskId, taskId);
        lqw.orderByDesc(HtTaskSettingLog::getId);
        return settingLogService.selectPageList(lqw, pageQuery);
    }

    /**
     * 根据主表ID查询设定日志子表明细
     */
    @SaCheckPermission("thermal:ht:tasks:query")
    @SaCheckLogin
    @GetMapping("/settingLog/{mainId}")
    public R<List<HtTaskSettingLogItemVo>> getDetailByMainId(@PathVariable String mainId) {
        return R.ok(settingLogService.selectItemsByMainId(mainId));
    }

    // ==================== Phase 4c-3: 核心调控功能 ====================

    /**
     * 启动/停止任务
     */
    @SaCheckPermission("thermal:ht:tasks:status")
    @SaCheckLogin
    @Log(title = "任务状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status/{id}")
    public R<Void> changeStatus(@PathVariable Long id, @RequestParam Boolean status) {
        HtTasks existing = tasksService.getById(id);
        if (existing == null) return R.fail("任务不存在");
        return toAjax(tasksService.changeStatus(id, status ? 1 : 0));
    }

    /**
     * 立即运行任务
     */
    @SaCheckPermission("thermal:ht:tasks:run")
    @SaCheckLogin
    @Log(title = "任务执行", businessType = BusinessType.UPDATE)
    @PostMapping("/run/{id}")
    public R<Void> run(@PathVariable Long id) {
        HtTasks existing = tasksService.getById(id);
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 0) {
            return R.fail("请先启动任务！");
        }
        return toAjax(tasksService.runTask(id));
    }

    /**
     * 标记特殊户
     */
    @SaCheckPermission("thermal:ht:tasks:edit")
    @SaCheckLogin
    @Log(title = "特殊户标记", businessType = BusinessType.UPDATE)
    @PutMapping("/isSpecial")
    public R<Void> isSpecial(@RequestBody List<Long> scopeIds,
                             @RequestParam String val,
                             @RequestParam(required = false) String remark) {
        return toAjax(tasksService.markSpecial(scopeIds, val, remark));
    }

    /**
     * 标记特殊单元
     */
    @SaCheckPermission("thermal:ht:tasks:edit")
    @SaCheckLogin
    @Log(title = "特殊单元标记", businessType = BusinessType.UPDATE)
    @PutMapping("/isSpecialUnit")
    public R<Void> isSpecialUnit(@RequestBody List<Long> scopeIds,
                                 @RequestParam String val,
                                 @RequestParam(required = false) String remark) {
        return toAjax(tasksService.markSpecialUnit(scopeIds, val, remark));
    }

    /**
     * 设置缴费状态
     */
    @SaCheckPermission("thermal:ht:tasks:edit")
    @SaCheckLogin
    @Log(title = "缴费状态", businessType = BusinessType.UPDATE)
    @PutMapping("/payStatus")
    public R<Void> isPayStatus(@RequestBody List<Long> scopeIds,
                               @RequestParam String val) {
        return toAjax(tasksService.markPayStatus(scopeIds, val));
    }

    /**
     * 刷新平衡率
     */
    @SaCheckPermission("thermal:ht:tasks:query")
    @SaCheckLogin
    @GetMapping("/balanceRate/{taskId}")
    public R<Double> refreshBalanceRate(@PathVariable Long taskId) {
        return R.ok(tasksService.refreshBalanceRate(taskId));
    }

    /**
     * 保存设定开度
     */
    @SaCheckPermission("thermal:ht:tasks:edit")
    @SaCheckLogin
    @Log(title = "设定开度", businessType = BusinessType.UPDATE)
    @PutMapping("/saveValveAngle")
    public R<Void> saveValveAngle(@RequestParam Long taskId,
                                  @RequestParam String scopeType) {
        return toAjax(tasksService.saveValveAngle(taskId, scopeType));
    }

    /**
     * 查询汇总统计
     */
    @SaCheckPermission("thermal:ht:tasks:query")
    @SaCheckLogin
    @GetMapping("/summary")
    public R<Map<String, Object>> summary(
            @RequestParam String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode) {
        return R.ok(tasksService.querySummary(orgId, buildingId, unitCode));
    }

    /**
     * 查询房屋变更记录
     * 旧端点: POST /property/htTasks/getHouseChangeDataList
     * 新端点: GET /thermal/ht/tasks/houseChangeData
     */
    @SaCheckPermission("thermal:ht:tasks:query")
    @SaCheckLogin
    @GetMapping("/houseChangeData")
    public R<List<PrHouseLog>> houseChangeData(@RequestParam(required = false) String changeType) {
        return R.ok(houseLogService.selectHouseChangeData(changeType));
    }

    /**
     * 查询单元变更记录
     * 旧端点: POST /property/htTasks/getUnitChangeDataList
     * 新端点: GET /thermal/ht/tasks/unitChangeData
     */
    @SaCheckPermission("thermal:ht:tasks:query")
    @SaCheckLogin
    @GetMapping("/unitChangeData")
    public R<List<PrHouseLog>> unitChangeData(@RequestParam(required = false) String changeType) {
        return R.ok(houseLogService.selectUnitChangeData(changeType));
    }

    /**
     * 按设备类型查询阀门配表列表（任务视角）
     * 旧端点: POST /property/htTasks/pageDeviceList
     * 新端点: GET /thermal/ht/tasks/deviceList
     */
    @SaCheckPermission("thermal:ht:tasks:list")
    @SaCheckLogin
    @GetMapping("/deviceList")
    public TableDataInfo<HtTasksVo> deviceList(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String deviceType,
            PageQuery pageQuery) {
        return tasksService.selectDeviceList(orgId, deviceType, pageQuery);
    }

    /**
     * 查询房屋其他编码
     * 旧端点: GET /property/htTasks/getHouseOtherCode
     * 新端点: GET /thermal/ht/tasks/otherCode/{id}
     */
    @SaCheckPermission("thermal:ht:tasks:query")
    @SaCheckLogin
    @GetMapping("/otherCode/{id}")
    public R<String> getOtherCode(@PathVariable String id) {
        String otherCode = tasksService.getHouseOtherCode(id);
        return R.ok(otherCode);
    }

    /**
     * 更新房屋其他编码
     * 旧端点: POST /property/htTasks/updateOtherCode
     * 新端点: PUT /thermal/ht/tasks/otherCode
     */
    @SaCheckPermission("thermal:ht:tasks:edit")
    @SaCheckLogin
    @Log(title = "房屋其他编码", businessType = BusinessType.UPDATE)
    @PutMapping("/otherCode")
    public R<Void> updateOtherCode(@RequestParam String id, @RequestParam String otherCode) {
        return toAjax(tasksService.updateHouseOtherCode(id, otherCode));
    }

    /**
     * 重新设定开度日志
     * 旧端点: POST /property/htTasks/updateSdkdLog
     * 新端点: PUT /thermal/ht/tasks/valveAngleLog
     */
    @SaCheckPermission("thermal:ht:tasks:edit")
    @SaCheckLogin
    @Log(title = "重新设定开度日志", businessType = BusinessType.UPDATE)
    @PutMapping("/valveAngleLog")
    public R<Void> updateValveAngleLog(@RequestParam String id, @RequestParam String scopeType) {
        return toAjax(tasksService.updateValveAngleLog(id, scopeType));
    }
}
