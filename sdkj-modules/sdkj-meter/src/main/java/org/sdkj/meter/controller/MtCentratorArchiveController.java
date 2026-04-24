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
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.meter.domain.MtCentratorArchive;
import org.sdkj.meter.domain.bo.MtCentratorArchiveBo;
import org.sdkj.meter.domain.vo.MtCentratorArchiveVo;
import org.sdkj.meter.service.IMtCentratorArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 集中器档案管理
 * 迁移自旧系统 MtCentratorArchiveController
 * 旧端点: /centratorArchive/* -> 新端点: /thermal/meter/centrator/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/centrator")
public class MtCentratorArchiveController extends BaseController {

    private final IMtCentratorArchiveService centratorArchiveService;

    /**
     * 分页查询集中器档案列表
     * 旧端点: GET /centratorArchive/pageList
     * 新端点: GET /thermal/meter/centrator/pageList
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtCentratorArchiveVo> pageList(@RequestParam @NotBlank String sortId,
                                                         @RequestParam(required = false) String search,
                                                         PageQuery pageQuery) {
        LambdaQueryWrapper<MtCentratorArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtCentratorArchive::getSortId, sortId);
        if (search != null && !search.trim().isEmpty()) {
            lqw.and(w -> w.eq(MtCentratorArchive::getCode, search.trim())
                          .or().eq(MtCentratorArchive::getName, search.trim()));
        }
        lqw.orderByAsc(MtCentratorArchive::getSeq).orderByDesc(MtCentratorArchive::getCreateTime);
        return centratorArchiveService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增集中器档案
     * 旧端点: POST /centratorArchive/insertData
     * 新端点: POST /thermal/meter/centrator
     */
    @SaCheckLogin
    @Log(title = "集中器档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtCentratorArchiveBo bo) {
        MtCentratorArchive archive = MapstructUtils.convert(bo, MtCentratorArchive.class);
        return toAjax(centratorArchiveService.save(archive));
    }

    /**
     * 修改集中器档案
     * 旧端点: POST /centratorArchive/updateData
     * 新端点: PUT /thermal/meter/centrator
     */
    @SaCheckLogin
    @Log(title = "集中器档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtCentratorArchiveBo bo) {
        MtCentratorArchive archive = MapstructUtils.convert(bo, MtCentratorArchive.class);
        return toAjax(centratorArchiveService.updateById(archive));
    }

    /**
     * 删除集中器档案
     * 旧端点: POST /centratorArchive/deleteData
     * 新端点: DELETE /thermal/meter/centrator/{id}
     */
    @SaCheckLogin
    @Log(title = "集中器档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(centratorArchiveService.removeById(id));
    }

}
