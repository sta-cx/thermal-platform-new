package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;

/**
 * 调控执行记录查询
 * 迁移自旧系统 HtTasksPerformController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/tasksPerform")
public class HtTasksPerformController extends BaseController {

    private final IHtTasksPerformService tasksPerformService;

    /**
     * 根据仪表ID分页查询执行记录
     */
    @SaCheckPermission("thermal:ht:tasksPerform:list")
    @SaCheckLogin
    @GetMapping("/byMeterId")
    public TableDataInfo<HtTasksPerformVo> byMeterId(
            @RequestParam String meterId,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtTasksPerform> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtTasksPerform::getMeterId, meterId);
        lqw.orderByDesc(HtTasksPerform::getCreateTime);
        return tasksPerformService.selectPageList(lqw, pageQuery);
    }

    /**
     * 根据仪表ID查询执行记录详情
     */
    @SaCheckPermission("thermal:ht:tasksPerform:query")
    @SaCheckLogin
    @GetMapping("/byMeterIdDetail")
    public R<List<HtTasksPerformVo>> byMeterIdDetail(@RequestParam String meterId) {
        return R.ok(tasksPerformService.selectByMeterIdDetail(meterId));
    }

    /**
     * 更新指令发送状态
     */
    @SaCheckPermission("thermal:ht:tasksPerform:edit")
    @SaCheckLogin
    @PutMapping("/status")
    public R<Void> updateStatus(@RequestParam String performId, @RequestParam Integer status) {
        return toAjax(tasksPerformService.updateInstructionStatus(performId, status));
    }

    /**
     * 查询指定任务下待发送的指令
     */
    @SaCheckPermission("thermal:ht:tasksPerform:list")
    @SaCheckLogin
    @GetMapping("/pending")
    public R<List<HtTasksPerformVo>> pendingByTask(@RequestParam String taskId) {
        return R.ok(tasksPerformService.selectPendingByTaskId(taskId));
    }

    /**
     * 查询执行统计
     */
    @SaCheckPermission("thermal:ht:tasksPerform:query")
    @SaCheckLogin
    @GetMapping("/stats")
    public R<Map<String, Object>> stats(@RequestParam String taskId) {
        return R.ok(tasksPerformService.selectPerformStats(taskId));
    }

    /**
     * 按任务ID统计执行状态（各状态数量）
     */
    @SaCheckPermission("thermal:ht:tasksPerform:query")
    @SaCheckLogin
    @GetMapping("/statistics/{taskId}")
    public R<Map<String, Object>> statisticsByTaskId(@PathVariable String taskId) {
        return R.ok(tasksPerformService.selectPerformStats(taskId));
    }

    /**
     * 全局状态汇总
     */
    @SaCheckPermission("thermal:ht:tasksPerform:list")
    @SaCheckLogin
    @GetMapping("/statusSummary")
    public R<Map<String, Object>> statusSummary() {
        return R.ok(tasksPerformService.selectGlobalStatusSummary());
    }
}
