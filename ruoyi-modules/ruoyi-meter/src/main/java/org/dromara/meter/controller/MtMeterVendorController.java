package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.MtMeterVendor;
import org.dromara.meter.domain.vo.MtMeterVendorVo;
import org.dromara.meter.service.IMtMeterVendorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * 仪表厂商管理
 * 迁移自旧系统 MtMeterVendorController
 * 旧端点: /meterVendor/* -> 新端点: /thermal/meter/vendor/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/vendor")
public class MtMeterVendorController extends BaseController {

    private final IMtMeterVendorService mtMeterVendorService;

    /**
     * 分页查询仪表厂商列表
     * 旧端点: GET /meterVendor/pageList
     * 新端点: GET /thermal/meter/vendor/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<MtMeterVendorVo> list(@RequestParam(required = false) String search, PageQuery pageQuery) {
        LambdaQueryWrapper<MtMeterVendor> lqw = new LambdaQueryWrapper<>();
        String keyword = search != null ? search.trim() : null;
        boolean hasSearch = keyword != null && !keyword.isEmpty();
        lqw.like(hasSearch, MtMeterVendor::getCode, keyword)
           .or(hasSearch, w -> w.like(MtMeterVendor::getName, keyword));
        lqw.orderByAsc(MtMeterVendor::getSeq)
           .orderByDesc(MtMeterVendor::getCreateTime);
        return mtMeterVendorService.selectPageList(lqw, pageQuery);
    }

    /**
     * 校验厂商名称是否重复
     * 旧端点: GET /meterVendor/verifyName
     * 新端点: GET /thermal/meter/vendor/verifyName
     */
    @SaCheckLogin
    @GetMapping("/verifyName")
    public R<Integer> verifyName(@RequestParam @NotBlank String name,
                                  @RequestParam(required = false) String id) {
        return R.ok(mtMeterVendorService.verifyName(name, id));
    }

    /**
     * 新增仪表厂商
     * 旧端点: POST /meterVendor/insertData
     * 新端点: POST /thermal/meter/vendor
     */
    @SaCheckLogin
    @Log(title = "仪表厂商", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody MtMeterVendor vendor) {
        vendor.setIsEnabled(1);
        return toAjax(mtMeterVendorService.save(vendor));
    }

    /**
     * 修改仪表厂商
     * 旧端点: POST /meterVendor/updateData
     * 新端点: PUT /thermal/meter/vendor
     */
    @SaCheckLogin
    @Log(title = "仪表厂商", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody MtMeterVendor vendor) {
        return toAjax(mtMeterVendorService.updateById(vendor));
    }

    /**
     * 删除仪表厂商（校验是否被仪表分类引用）
     * 旧端点: POST /meterVendor/deleteData
     * 新端点: DELETE /thermal/meter/vendor/{id}
     */
    @SaCheckLogin
    @Log(title = "仪表厂商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        int count = mtMeterVendorService.countByVendorId(id);
        if (count > 0) {
            return R.fail("该厂商已被仪表分类引用，无法删除");
        }
        return toAjax(mtMeterVendorService.removeById(id));
    }

    /**
     * 查询所有启用的仪表厂商
     * 旧端点: GET /meterVendor/allVendor
     * 新端点: GET /thermal/meter/vendor/all
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<MtMeterVendorVo>> all() {
        return R.ok(mtMeterVendorService.selectAllEnabled());
    }

}
