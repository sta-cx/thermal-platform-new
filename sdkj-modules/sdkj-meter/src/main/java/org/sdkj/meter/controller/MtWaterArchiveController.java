package org.sdkj.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.meter.domain.MtWaterArchive;
import org.sdkj.meter.domain.bo.MtWaterArchiveBo;
import org.sdkj.meter.domain.vo.MtWaterArchiveVo;
import org.sdkj.meter.service.IMtWaterArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 水表档案管理
 * 迁移自旧系统 MtWaterArchiveController
 * 旧端点: /waterArchive/* -> 新端点: /thermal/meter/water/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/water")
public class MtWaterArchiveController extends BaseController {

    private final IMtWaterArchiveService waterArchiveService;

    /**
     * 分页查询水表档案列表
     * 旧端点: GET /waterArchive/pageList
     * 新端点: GET /thermal/meter/water/pageList
     */
    @SaCheckPermission("thermal:meter:water:list")
    @SaCheckLogin
    @GetMapping({"/pageList", "/list"})
    public TableDataInfo<MtWaterArchiveVo> pageList(@RequestParam @NotBlank String sortId,
                                                     @RequestParam(required = false) String search,
                                                     PageQuery pageQuery) {
        LambdaQueryWrapper<MtWaterArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtWaterArchive::getSortId, sortId);
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.eq(MtWaterArchive::getCode, search.trim())
                          .or().eq(MtWaterArchive::getName, search.trim()));
        }
        lqw.orderByAsc(MtWaterArchive::getSeq).orderByDesc(MtWaterArchive::getCreateTime);
        return waterArchiveService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增水表档案
     * 旧端点: POST /waterArchive/insertData
     * 新端点: POST /thermal/meter/water
     */
    @SaCheckPermission("thermal:meter:water:add")
    @SaCheckLogin
    @Log(title = "水表档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtWaterArchiveBo bo) {
        MtWaterArchive archive = MapstructUtils.convert(bo, MtWaterArchive.class);
        return toAjax(waterArchiveService.save(archive));
    }

    /**
     * 修改水表档案
     * 旧端点: POST /waterArchive/updateData
     * 新端点: PUT /thermal/meter/water
     */
    @SaCheckPermission("thermal:meter:water:edit")
    @SaCheckLogin
    @Log(title = "水表档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtWaterArchiveBo bo) {
        MtWaterArchive archive = MapstructUtils.convert(bo, MtWaterArchive.class);
        return toAjax(waterArchiveService.updateById(archive));
    }

    /**
     * 删除水表档案
     * 旧端点: POST /waterArchive/deleteData
     * 新端点: DELETE /thermal/meter/water/{id}
     */
    @SaCheckPermission("thermal:meter:water:remove")
    @SaCheckLogin
    @Log(title = "水表档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(waterArchiveService.removeById(id));
    }

}
