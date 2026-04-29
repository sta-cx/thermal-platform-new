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
import org.sdkj.meter.domain.MtGasArchive;
import org.sdkj.meter.domain.bo.MtGasArchiveBo;
import org.sdkj.meter.domain.vo.MtGasArchiveVo;
import org.sdkj.meter.service.IMtGasArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 燃气表档案管理
 * 迁移自旧系统 MtGasArchiveController
 * 旧端点: /gasArchive/* -> 新端点: /thermal/meter/gas/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/gas")
public class MtGasArchiveController extends BaseController {

    private final IMtGasArchiveService gasArchiveService;

    /**
     * 分页查询燃气表档案列表
     * 旧端点: GET /gasArchive/pageList
     * 新端点: GET /thermal/meter/gas/pageList
     */
    @SaCheckPermission("thermal:meter:gas:list")
    @SaCheckLogin
    @GetMapping({"/pageList", "/list"})
    public TableDataInfo<MtGasArchiveVo> pageList(@RequestParam(required = false) String sortId,
                                                   @RequestParam(required = false) String search,
                                                   PageQuery pageQuery) {
        LambdaQueryWrapper<MtGasArchive> lqw = new LambdaQueryWrapper<>();
        if (sortId != null && !sortId.trim().isEmpty()) {
            lqw.eq(MtGasArchive::getSortId, sortId);
        }
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.eq(MtGasArchive::getCode, search.trim())
                          .or().eq(MtGasArchive::getName, search.trim()));
        }
        lqw.orderByAsc(MtGasArchive::getSeq).orderByDesc(MtGasArchive::getCreateTime);
        return gasArchiveService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增燃气表档案
     * 旧端点: POST /gasArchive/insertData
     * 新端点: POST /thermal/meter/gas
     */
    @SaCheckPermission("thermal:meter:gas:add")
    @SaCheckLogin
    @Log(title = "燃气表档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtGasArchiveBo bo) {
        MtGasArchive archive = MapstructUtils.convert(bo, MtGasArchive.class);
        return toAjax(gasArchiveService.save(archive));
    }

    /**
     * 修改燃气表档案
     * 旧端点: POST /gasArchive/updateData
     * 新端点: PUT /thermal/meter/gas
     */
    @SaCheckPermission("thermal:meter:gas:edit")
    @SaCheckLogin
    @Log(title = "燃气表档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtGasArchiveBo bo) {
        MtGasArchive archive = MapstructUtils.convert(bo, MtGasArchive.class);
        return toAjax(gasArchiveService.updateById(archive));
    }

    /**
     * 删除燃气表档案
     * 旧端点: POST /gasArchive/deleteData
     * 新端点: DELETE /thermal/meter/gas/{id}
     */
    @SaCheckPermission("thermal:meter:gas:remove")
    @SaCheckLogin
    @Log(title = "燃气表档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(gasArchiveService.removeById(id));
    }

}
