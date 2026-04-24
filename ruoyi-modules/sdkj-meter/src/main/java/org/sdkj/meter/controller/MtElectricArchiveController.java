package org.sdkj.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.meter.domain.MtElectricArchive;
import org.sdkj.meter.domain.vo.MtElectricArchiveVo;
import org.sdkj.meter.service.IMtElectricArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 电表档案管理
 * 迁移自旧系统 MtElectricArchiveController
 * 旧端点: /electricArchive/* -> 新端点: /thermal/meter/electric/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/electric")
public class MtElectricArchiveController extends BaseController {

    private final IMtElectricArchiveService electricArchiveService;

    /**
     * 分页查询电表档案列表
     * 旧端点: GET /electricArchive/pageList
     * 新端点: GET /thermal/meter/electric/pageList
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtElectricArchiveVo> pageList(@RequestParam @NotBlank String sortId,
                                                        @RequestParam(required = false) String search,
                                                        PageQuery pageQuery) {
        LambdaQueryWrapper<MtElectricArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtElectricArchive::getSortId, sortId);
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.eq(MtElectricArchive::getCode, search.trim())
                          .or().eq(MtElectricArchive::getName, search.trim()));
        }
        lqw.orderByAsc(MtElectricArchive::getSeq).orderByDesc(MtElectricArchive::getCreateTime);
        return electricArchiveService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增电表档案
     * 旧端点: POST /electricArchive/insertData
     * 新端点: POST /thermal/meter/electric
     */
    @SaCheckLogin
    @Log(title = "电表档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtElectricArchive archive) {
        return toAjax(electricArchiveService.save(archive));
    }

    /**
     * 修改电表档案
     * 旧端点: POST /electricArchive/updateData
     * 新端点: PUT /thermal/meter/electric
     */
    @SaCheckLogin
    @Log(title = "电表档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtElectricArchive archive) {
        return toAjax(electricArchiveService.updateById(archive));
    }

    /**
     * 删除电表档案
     * 旧端点: POST /electricArchive/deleteData
     * 新端点: DELETE /thermal/meter/electric/{id}
     */
    @SaCheckLogin
    @Log(title = "电表档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(electricArchiveService.removeById(id));
    }

}
