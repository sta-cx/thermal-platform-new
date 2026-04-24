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
import org.sdkj.meter.domain.MtTcValve;
import org.sdkj.meter.domain.bo.MtTcValveBo;
import org.sdkj.meter.domain.vo.MtTcValveVo;
import org.sdkj.meter.service.IMtTcValveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阀门档案管理
 * 迁移自旧系统 MtTcValveController
 * 旧端点: /tcValve/* -> 新端点: /thermal/meter/valve/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/valve")
public class MtTcValveController extends BaseController {

    private final IMtTcValveService valveService;

    /**
     * 分页查询阀门档案列表
     * 旧端点: GET /tcValve/pageList
     * 新端点: GET /thermal/meter/valve/pageList
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtTcValveVo> pageList(@RequestParam @NotBlank String sortId,
                                                @RequestParam(required = false) String name,
                                                PageQuery pageQuery) {
        LambdaQueryWrapper<MtTcValve> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtTcValve::getSortId, sortId);
        if (name != null && !name.trim().isEmpty()) {
            lqw.like(MtTcValve::getName, name.trim());
        }
        lqw.orderByAsc(MtTcValve::getSeq).orderByDesc(MtTcValve::getCreateTime);
        return valveService.selectPageList(lqw, pageQuery);
    }

    /**
     * 查询当前用户所属公司的阀门列表
     * 旧端点: GET /tcValve/list
     * 新端点: GET /thermal/meter/valve/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<MtTcValveVo>> listByUserCompany() {
        return R.ok(valveService.selectValvesByUserCompany());
    }

    /**
     * 新增阀门档案
     * 旧端点: POST /tcValve/insertData
     * 新端点: POST /thermal/meter/valve
     */
    @SaCheckLogin
    @Log(title = "阀门档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtTcValveBo bo) {
        MtTcValve valve = MapstructUtils.convert(bo, MtTcValve.class);
        return toAjax(valveService.save(valve));
    }

    /**
     * 修改阀门档案
     * 旧端点: POST /tcValve/updateData
     * 新端点: PUT /thermal/meter/valve
     */
    @SaCheckLogin
    @Log(title = "阀门档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtTcValveBo bo) {
        MtTcValve valve = MapstructUtils.convert(bo, MtTcValve.class);
        return toAjax(valveService.updateById(valve));
    }

    /**
     * 删除阀门档案
     * 旧端点: POST /tcValve/deleteData
     * 新端点: DELETE /thermal/meter/valve/{id}
     */
    @SaCheckLogin
    @Log(title = "阀门档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(valveService.removeById(id));
    }

    /**
     * 条件查询阀门（按id/name/code）
     * 旧端点: GET /tcValve/query
     * 新端点: GET /thermal/meter/valve/query
     */
    @SaCheckLogin
    @GetMapping("/query")
    public R<List<MtTcValveVo>> query(@RequestParam(required = false) String id,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String code) {
        LambdaQueryWrapper<MtTcValve> lqw = new LambdaQueryWrapper<>();
        if (id != null && !id.trim().isEmpty()) {
            lqw.eq(MtTcValve::getId, id.trim());
        }
        if (name != null && !name.trim().isEmpty()) {
            lqw.like(MtTcValve::getName, name.trim());
        }
        if (code != null && !code.trim().isEmpty()) {
            lqw.like(MtTcValve::getCode, code.trim());
        }
        return R.ok(valveService.selectList(lqw));
    }

}
