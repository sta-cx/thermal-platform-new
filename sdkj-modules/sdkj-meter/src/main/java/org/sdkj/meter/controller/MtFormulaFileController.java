package org.sdkj.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.meter.domain.MtFormulaFile;
import org.sdkj.meter.domain.bo.MtFormulaFileBo;
import org.sdkj.meter.domain.vo.MtFormulaFileVo;
import org.sdkj.meter.service.IMtFormulaFileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;

/**
 * 公式档案管理
 * 迁移自旧系统 MtFormulaFileController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/formula")
public class MtFormulaFileController extends BaseController {

    private final IMtFormulaFileService formulaService;

    /**
     * 分页查询公式档案
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<MtFormulaFileVo> list(@RequestParam(required = false) String name, PageQuery pageQuery) {
        String keyword = name != null ? name.trim() : null;
        LambdaQueryWrapper<MtFormulaFile> lqw = new LambdaQueryWrapper<>();
        lqw.like(keyword != null && !keyword.isEmpty(), MtFormulaFile::getName, keyword);
        lqw.orderByAsc(MtFormulaFile::getSeq).orderByDesc(MtFormulaFile::getCreateTime);
        return formulaService.selectPageList(lqw, pageQuery);
    }

    /**
     * 根据ID查询公式详情
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<MtFormulaFile> getById(@PathVariable String id) {
        return R.ok(formulaService.getById(id));
    }

    /**
     * 新增公式档案
     */
    @SaCheckLogin
    @Log(title = "公式档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtFormulaFileBo bo) {
        MtFormulaFile formula = MapstructUtils.convert(bo, MtFormulaFile.class);
        formula.setIsEnabled("1");
        return toAjax(formulaService.save(formula));
    }

    /**
     * 修改公式档案
     */
    @SaCheckLogin
    @Log(title = "公式档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtFormulaFileBo bo) {
        MtFormulaFile formula = MapstructUtils.convert(bo, MtFormulaFile.class);
        return toAjax(formulaService.updateById(formula));
    }

    /**
     * 删除公式档案
     */
    @SaCheckLogin
    @Log(title = "公式档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(formulaService.removeById(id));
    }

    /**
     * 启用公式
     */
    @SaCheckLogin
    @Log(title = "公式档案", businessType = BusinessType.UPDATE)
    @PutMapping("/enable/{id}")
    public R<Void> enable(@PathVariable String id) {
        return toAjax(formulaService.toggleEnabled(id, "1"));
    }

    /**
     * 禁用公式
     */
    @SaCheckLogin
    @Log(title = "公式档案", businessType = BusinessType.UPDATE)
    @PutMapping("/disable/{id}")
    public R<Void> disable(@PathVariable String id) {
        return toAjax(formulaService.toggleEnabled(id, "0"));
    }

    /**
     * 获取公式类型列表
     */
    @SaCheckLogin
    @GetMapping("/types")
    public R<List<Map<String, Object>>> types() {
        return R.ok(formulaService.getFormulaType());
    }

    /**
     * 校验名称是否重复
     */
    @SaCheckLogin
    @GetMapping("/validateName")
    public R<Boolean> validateName(@RequestParam @NotBlank String name,
                                   @RequestParam(required = false) String id) {
        return R.ok(formulaService.validateName(name, id) > 0);
    }

    /**
     * 获取公式元素列表
     */
    @SaCheckLogin
    @GetMapping("/elements")
    public R<List<Map<String, Object>>> elements() {
        return R.ok(formulaService.getFormulaElement());
    }

    /**
     * 根据类型查询启用的公式
     */
    @SaCheckLogin
    @GetMapping("/byType")
    public R<List<MtFormulaFileVo>> getByType(@RequestParam String type) {
        return R.ok(formulaService.getDataByType(type));
    }
}
