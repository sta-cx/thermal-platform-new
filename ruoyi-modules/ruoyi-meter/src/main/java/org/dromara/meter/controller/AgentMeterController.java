package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.*;
import org.dromara.meter.domain.vo.*;
import org.dromara.meter.mapper.MtMeterMatchMapper;
import org.dromara.meter.service.IMtMeterMatchService;
import org.dromara.meter.service.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Arrays;
import java.util.List;

/**
 * 仪表分配管理
 * 迁移自旧系统 AgentMeterController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/agent")
public class AgentMeterController extends BaseController {

    private final IMtElectricArchiveService electricService;
    private final IMtWaterArchiveService waterService;
    private final IMtHeatArchiveService heatService;
    private final IMtGasArchiveService gasService;
    private final IMtMeterMatchService meterMatchService;

    /**
     * 分页查询已分配的仪表列表
     */
    @SaCheckLogin
    @GetMapping("/allocated")
    public TableDataInfo<?> allocated(
            @RequestParam String meterType,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return switch (meterType) {
            case "01" -> {
                LambdaQueryWrapper<MtElectricArchive> lqw = archiveSearch(MtElectricArchive::getCode, MtElectricArchive::getName, search);
                yield electricService.selectPageList(lqw, pageQuery);
            }
            case "02" -> {
                LambdaQueryWrapper<MtWaterArchive> lqw = archiveSearch(MtWaterArchive::getCode, MtWaterArchive::getName, search);
                yield waterService.selectPageList(lqw, pageQuery);
            }
            case "03" -> {
                LambdaQueryWrapper<MtHeatArchive> lqw = archiveSearch(MtHeatArchive::getCode, MtHeatArchive::getName, search);
                yield heatService.selectPageList(lqw, pageQuery);
            }
            case "04" -> {
                LambdaQueryWrapper<MtGasArchive> lqw = archiveSearch(MtGasArchive::getCode, MtGasArchive::getName, search);
                yield gasService.selectPageList(lqw, pageQuery);
            }
            default -> TableDataInfo.build();
        };
    }

    /**
     * 查询所有仪表（含未分配）
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<?>> all(
            @RequestParam String meterType,
            @RequestParam(required = false) String search) {
        return switch (meterType) {
            case "01" -> R.ok(electricService.list(archiveSearch(MtElectricArchive::getCode, MtElectricArchive::getName, search)));
            case "02" -> R.ok(waterService.list(archiveSearch(MtWaterArchive::getCode, MtWaterArchive::getName, search)));
            case "03" -> R.ok(heatService.list(archiveSearch(MtHeatArchive::getCode, MtHeatArchive::getName, search)));
            case "04" -> R.ok(gasService.list(archiveSearch(MtGasArchive::getCode, MtGasArchive::getName, search)));
            default -> R.ok(List.of());
        };
    }

    /**
     * 批量分配仪表给公司（事务操作：先删后插）
     */
    @SaCheckLogin
    @Log(title = "仪表分配", businessType = BusinessType.INSERT)
    @PostMapping("/allocate")
    public R<Void> allocate(
            @RequestParam String companyId,
            @RequestParam String archiveIds,
            @RequestParam String meterType) {
        List<String> ids = Arrays.asList(archiveIds.split(","));
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
