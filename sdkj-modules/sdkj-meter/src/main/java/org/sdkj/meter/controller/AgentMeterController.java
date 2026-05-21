package org.sdkj.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.meter.domain.MtHeatArchive;
import org.sdkj.meter.service.IMtMeterMatchService;
import org.sdkj.meter.service.IMtHeatArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Arrays;
import java.util.List;

/**
 * 仪表分配管理
 * 迁移自旧系统 AgentMeterController
 * 注：电表/水表/燃气表档案已废弃（老前端已去掉），仅保留热力表
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/agent")
public class AgentMeterController extends BaseController {

    private final IMtHeatArchiveService heatService;
    private final IMtMeterMatchService meterMatchService;

    /**
     * 分页查询已分配的仪表列表
     */
    @SaCheckPermission("thermal:meter:agentMeter:list")
    @SaCheckLogin
    @GetMapping("/allocated")
    public TableDataInfo<?> allocated(
            @RequestParam(required = false) String meterType,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        if (meterType == null || meterType.isBlank()) {
            return TableDataInfo.build();
        }
        if ("03".equals(meterType)) {
            LambdaQueryWrapper<MtHeatArchive> lqw = archiveSearch(MtHeatArchive::getCode, MtHeatArchive::getName, search);
            return heatService.selectPageList(lqw, pageQuery);
        }
        return TableDataInfo.build();
    }

    /**
     * 查询所有仪表（含未分配）
     */
    @SaCheckPermission("thermal:meter:agentMeter:list")
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<?>> all(
            @RequestParam(required = false) String meterType,
            @RequestParam(required = false) String search) {
        if (meterType == null || meterType.isBlank()) {
            return R.ok(List.of());
        }
        if ("03".equals(meterType)) {
            return R.ok(heatService.list(archiveSearch(MtHeatArchive::getCode, MtHeatArchive::getName, search)));
        }
        return R.ok(List.of());
    }

    /**
     * 批量分配仪表给公司（事务操作：先删后插）
     */
    @SaCheckPermission("thermal:meter:agentMeter:add")
    @SaCheckLogin
    @Log(title = "仪表分配", businessType = BusinessType.INSERT)
    @PostMapping("/allocate")
    public R<Void> allocate(
            @RequestParam String companyId,
            @RequestParam String archiveIds,
            @RequestParam String meterType) {
        List<Long> ids = Arrays.stream(archiveIds.split(",")).map(String::trim).map(Long::valueOf).toList();
        meterMatchService.batchAllocate(companyId, ids, meterType);
        return R.ok();
    }

    private <T> LambdaQueryWrapper<T> archiveSearch(
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> codeField,
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> nameField,
            String search) {
        String keyword = search != null ? search.trim() : null;
        LambdaQueryWrapper<T> lqw = new LambdaQueryWrapper<>();
        lqw.like(keyword != null && !keyword.isEmpty(), codeField, keyword)
           .or(keyword != null && !keyword.isEmpty(), w -> w.like(nameField, keyword));
        return lqw;
    }
}
