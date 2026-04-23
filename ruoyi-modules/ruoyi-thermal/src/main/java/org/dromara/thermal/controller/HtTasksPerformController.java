package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtTasksPerform;
import org.dromara.thermal.domain.vo.HtTasksPerformVo;
import org.dromara.thermal.service.IHtTasksPerformService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

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
    @SaCheckLogin
    @GetMapping("/byMeterIdDetail")
    public R<List<HtTasksPerformVo>> byMeterIdDetail(@RequestParam String meterId) {
        return R.ok(tasksPerformService.selectByMeterIdDetail(meterId));
    }
}
