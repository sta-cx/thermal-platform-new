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
import org.sdkj.meter.domain.MtHeatArchive;
import org.sdkj.meter.domain.bo.MtHeatArchiveBo;
import org.sdkj.meter.domain.vo.MtHeatArchiveVo;
import org.sdkj.meter.service.IMtHeatArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 热力表档案管理
 * 迁移自旧系统 MtHeatArchiveController
 * 旧端点: /heatArchive/* -> 新端点: /thermal/meter/heat/*
 * TODO: 旧系统中删除热力表时有级联更新 pr_heat_hot_archive 的逻辑，本次迁移暂不实现
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/heat")
public class MtHeatArchiveController extends BaseController {

    private final IMtHeatArchiveService heatArchiveService;

    /**
     * 分页查询热力表档案列表
     * 旧端点: GET /heatArchive/pageList
     * 新端点: GET /thermal/meter/heat/pageList
     */
    @SaCheckPermission("thermal:meter:heat:list")
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtHeatArchiveVo> pageList(@RequestParam @NotBlank String sortId,
                                                    @RequestParam(required = false) String search,
                                                    PageQuery pageQuery) {
        LambdaQueryWrapper<MtHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtHeatArchive::getSortId, sortId);
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.eq(MtHeatArchive::getCode, search.trim())
                          .or().eq(MtHeatArchive::getName, search.trim()));
        }
        lqw.orderByAsc(MtHeatArchive::getSeq).orderByDesc(MtHeatArchive::getCreateTime);
        return heatArchiveService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增热力表档案
     * 旧端点: POST /heatArchive/insertData
     * 新端点: POST /thermal/meter/heat
     */
    @SaCheckPermission("thermal:meter:heat:add")
    @SaCheckLogin
    @Log(title = "热力表档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtHeatArchiveBo bo) {
        MtHeatArchive archive = MapstructUtils.convert(bo, MtHeatArchive.class);
        return toAjax(heatArchiveService.save(archive));
    }

    /**
     * 修改热力表档案
     * 旧端点: POST /heatArchive/updateData
     * 新端点: PUT /thermal/meter/heat
     */
    @SaCheckPermission("thermal:meter:heat:edit")
    @SaCheckLogin
    @Log(title = "热力表档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtHeatArchiveBo bo) {
        MtHeatArchive archive = MapstructUtils.convert(bo, MtHeatArchive.class);
        return toAjax(heatArchiveService.updateById(archive));
    }

    /**
     * 删除热力表档案
     * 旧端点: POST /heatArchive/deleteData
     * 新端点: DELETE /thermal/meter/heat/{id}
     */
    @SaCheckPermission("thermal:meter:heat:remove")
    @SaCheckLogin
    @Log(title = "热力表档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(heatArchiveService.removeById(id));
    }

}
