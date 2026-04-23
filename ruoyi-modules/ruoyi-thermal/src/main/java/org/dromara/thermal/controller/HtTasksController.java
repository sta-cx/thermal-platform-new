package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.domain.HtTaskSettingLog;
import org.dromara.thermal.domain.vo.HtTaskSettingLogItemVo;
import org.dromara.thermal.domain.vo.HtTaskSettingLogVo;
import org.dromara.thermal.domain.vo.HtTasksVo;
import org.dromara.thermal.service.IHtTaskSettingLogService;
import org.dromara.thermal.service.IHtTasksService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

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

    /**
     * 分页查询任务列表
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtTasksVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) Integer status,
            PageQuery pageQuery) {
        String keyword = search != null ? search.trim() : null;
        LambdaQueryWrapper<HtTasks> lqw = new LambdaQueryWrapper<>();
        lqw.like(keyword != null && !keyword.isEmpty(), HtTasks::getName, keyword);
        lqw.eq(orgId != null && !orgId.isEmpty(), HtTasks::getOrgId, orgId);
        lqw.eq(status != null, HtTasks::getStatus, status);
        lqw.orderByDesc(HtTasks::getCreateTime);
        return tasksService.selectPageList(lqw, pageQuery);
    }

    /**
     * 查询所有任务（按组织）
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<HtTasks>> all(@RequestParam(required = false) String orgId) {
        return R.ok(tasksService.selectAllByOrgId(orgId));
    }

    /**
     * 根据ID查询任务详情
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtTasks> getById(@PathVariable Integer id) {
        return R.ok(tasksService.getById(id));
    }

    /**
     * 新增调控任务
     */
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> save(@Validated @RequestBody HtTasks task,
                        @RequestParam(required = false) List<String> scopeIds) {
        task.setBeanClass("com.thermal.job.ControlJob");
        if (task.getNumber() == null) task.setNumber(0);
        return toAjax(tasksService.saveWithScope(task, scopeIds));
    }

    /**
     * 修改调控任务
     */
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtTasks task,
                          @RequestParam(required = false) List<String> scopeIds) {
        HtTasks existing = tasksService.getById(task.getId());
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            return R.fail("修改之前请先停止任务！");
        }
        return toAjax(tasksService.updateWithScope(task, scopeIds));
    }

    /**
     * 删除调控任务
     */
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Integer id) {
        HtTasks existing = tasksService.getById(id);
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            return R.fail("删除前请先停止任务！");
        }
        return toAjax(tasksService.removeById(id));
    }

    /**
     * 批量删除调控任务
     */
    @SaCheckLogin
    @Log(title = "调控任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> removeBatch(@RequestParam List<Integer> ids) {
        for (Integer id : ids) {
            HtTasks existing = tasksService.getById(id);
            if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
                return R.fail("任务[ID:" + id + "]正在运行，删除前请先停止！");
            }
        }
        return toAjax(tasksService.removeByIds(ids));
    }

    /**
     * 分页查询设定日志
     */
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
    @SaCheckLogin
    @GetMapping("/settingLog/{mainId}")
    public R<List<HtTaskSettingLogItemVo>> getDetailByMainId(@PathVariable String mainId) {
        return R.ok(settingLogService.selectItemsByMainId(mainId));
    }
}
