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
import org.sdkj.meter.domain.MtTcArchive;
import org.sdkj.meter.domain.bo.MtTcArchiveBo;
import org.sdkj.meter.domain.vo.MtTcArchiveVo;
import org.sdkj.meter.service.IMtTcArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 温控器档案管理
 * 迁移自旧系统 MtTcArchiveController
 * 旧端点: /tcArchive/* -> 新端点: /thermal/meter/tc/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/tc")
public class MtTcArchiveController extends BaseController {

    private final IMtTcArchiveService tcService;

    /**
     * 分页查询温控器档案列表
     * 旧端点: GET /tcArchive/pageList
     * 新端点: GET /thermal/meter/tc/pageList
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtTcArchiveVo> pageList(@RequestParam @NotBlank String sortId,
                                                  @RequestParam(required = false) String name,
                                                  PageQuery pageQuery) {
        LambdaQueryWrapper<MtTcArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtTcArchive::getSortId, sortId);
        if (name != null && !name.trim().isEmpty()) {
            lqw.like(MtTcArchive::getName, name.trim());
        }
        lqw.orderByAsc(MtTcArchive::getSeq).orderByDesc(MtTcArchive::getCreateTime);
        return tcService.selectPageList(lqw, pageQuery);
    }

    /**
     * 查询所有启用的温控器列表
     * 旧端点: GET /tcArchive/list
     * 新端点: GET /thermal/meter/tc/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<MtTcArchiveVo>> list() {
        LambdaQueryWrapper<MtTcArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtTcArchive::getIsEnabled, 1);
        return R.ok(tcService.selectList(lqw));
    }

    /**
     * 新增温控器档案
     * 旧端点: POST /tcArchive/insertData
     * 新端点: POST /thermal/meter/tc
     */
    @SaCheckLogin
    @Log(title = "温控器档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtTcArchiveBo bo) {
        MtTcArchive archive = MapstructUtils.convert(bo, MtTcArchive.class);
        return toAjax(tcService.save(archive));
    }

    /**
     * 修改温控器档案
     * 旧端点: POST /tcArchive/updateData
     * 新端点: PUT /thermal/meter/tc
     */
    @SaCheckLogin
    @Log(title = "温控器档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtTcArchiveBo bo) {
        MtTcArchive archive = MapstructUtils.convert(bo, MtTcArchive.class);
        return toAjax(tcService.updateById(archive));
    }

    /**
     * 删除温控器档案
     * 旧端点: POST /tcArchive/deleteData
     * 新端点: DELETE /thermal/meter/tc/{id}
     */
    @SaCheckLogin
    @Log(title = "温控器档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(tcService.removeById(id));
    }

    /**
     * 条件查询温控器（按id/name/code）
     * 旧端点: GET /tcArchive/query
     * 新端点: GET /thermal/meter/tc/query
     */
    @SaCheckLogin
    @GetMapping("/query")
    public R<List<MtTcArchiveVo>> query(@RequestParam(required = false) String id,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) String code) {
        LambdaQueryWrapper<MtTcArchive> lqw = new LambdaQueryWrapper<>();
        if (id != null && !id.trim().isEmpty()) {
            lqw.eq(MtTcArchive::getId, id.trim());
        }
        if (name != null && !name.trim().isEmpty()) {
            lqw.like(MtTcArchive::getName, name.trim());
        }
        if (code != null && !code.trim().isEmpty()) {
            lqw.like(MtTcArchive::getCode, code.trim());
        }
        return R.ok(tcService.selectList(lqw));
    }

}
