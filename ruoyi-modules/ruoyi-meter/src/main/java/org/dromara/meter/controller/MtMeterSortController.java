package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.MtMeterSort;
import org.dromara.meter.domain.vo.MtMeterSortVo;
import org.dromara.meter.service.IMtMeterSortService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仪表分类管理
 * 迁移自旧系统 MtMeterSortController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/sort")
public class MtMeterSortController extends BaseController {

    private final IMtMeterSortService sortService;

    /**
     * 分页查询仪表分类
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtMeterSortVo> pageList(MtMeterSort sort, PageQuery pageQuery) {
        LambdaQueryWrapper<MtMeterSort> lqw = new LambdaQueryWrapper<>();
        lqw.like(sort.getName() != null && !sort.getName().isEmpty(), MtMeterSort::getName, sort.getName());
        lqw.eq(sort.getMeterType() != null && !sort.getMeterType().isEmpty(), MtMeterSort::getMeterType, sort.getMeterType());
        lqw.eq(sort.getVendorId() != null && !sort.getVendorId().isEmpty(), MtMeterSort::getVendorId, sort.getVendorId());
        lqw.orderByAsc(MtMeterSort::getSeq).orderByDesc(MtMeterSort::getCreateTime);
        return sortService.selectPageList(lqw, pageQuery);
    }

    /**
     * 校验名称是否重复
     */
    @SaCheckLogin
    @PostMapping("/verifyName")
    public R<Integer> verifyName(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String id) {
        return R.ok(sortService.verifyName(name, id));
    }

    /**
     * 新增仪表分类
     */
    @SaCheckLogin
    @Log(title = "仪表分类", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtMeterSort sort) {
        return toAjax(sortService.save(sort));
    }

    /**
     * 修改仪表分类
     */
    @SaCheckLogin
    @Log(title = "仪表分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtMeterSort sort) {
        return toAjax(sortService.updateById(sort));
    }

    /**
     * 删除仪表分类（检查是否被档案表引用）
     */
    @SaCheckLogin
    @Log(title = "仪表分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id, @RequestParam String meterType) {
        if (!java.util.List.of("01", "02", "03", "04").contains(meterType)) {
            return R.fail("无效的仪表类型");
        }
        int count = sortService.countBySortId(id, meterType);
        if (count > 0) {
            return R.fail("该分类已被仪表档案引用，无法删除");
        }
        return toAjax(sortService.removeById(id));
    }

    /**
     * 条件查询仪表分类
     */
    @SaCheckLogin
    @GetMapping("/queryMeterSort")
    public R<List<MtMeterSortVo>> queryMeterSort(MtMeterSort sort) {
        LambdaQueryWrapper<MtMeterSort> lqw = new LambdaQueryWrapper<>();
        lqw.like(sort.getName() != null && !sort.getName().isEmpty(), MtMeterSort::getName, sort.getName());
        lqw.eq(sort.getMeterType() != null && !sort.getMeterType().isEmpty(), MtMeterSort::getMeterType, sort.getMeterType());
        return R.ok(sortService.selectList(lqw));
    }
}
