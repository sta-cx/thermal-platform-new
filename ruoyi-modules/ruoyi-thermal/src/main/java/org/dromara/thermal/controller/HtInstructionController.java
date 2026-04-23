package org.dromara.thermal.controller;

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
import org.dromara.thermal.domain.HtInstruction;
import org.dromara.thermal.domain.vo.HtInstructionVo;
import org.dromara.thermal.service.IHtInstructionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 控制指令管理
 * 迁移自旧系统 HtInstructionController
 * 旧端点: /htInstruction/* -> 新端点: /thermal/ht/instruction/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/instruction")
public class HtInstructionController extends BaseController {

    private final IHtInstructionService htInstructionService;

    /**
     * 分页查询控制指令列表
     * 旧端点: GET /htInstruction/pageList
     * 新端点: GET /thermal/ht/instruction/list
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtInstructionVo> list(@RequestParam(required = false) String search, PageQuery pageQuery) {
        LambdaQueryWrapper<HtInstruction> lqw = new LambdaQueryWrapper<>();
        String keyword = search != null ? search.trim() : null;
        boolean hasSearch = keyword != null && !keyword.isEmpty();
        lqw.eq(hasSearch, HtInstruction::getName, keyword);
        lqw.orderByDesc(HtInstruction::getCreateTime);
        return htInstructionService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增控制指令
     * 旧端点: POST /htInstruction/insertData
     * 新端点: POST /thermal/ht/instruction
     */
    @SaCheckLogin
    @Log(title = "控制指令", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody HtInstruction instruction) {
        return toAjax(htInstructionService.save(instruction));
    }

    /**
     * 根据ID查询控制指令详情
     * 旧端点: GET /htInstruction/queryHtInstruction
     * 新端点: GET /thermal/ht/instruction/{id}
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtInstruction> getInfo(@PathVariable @NotBlank String id) {
        return R.ok(htInstructionService.getById(id));
    }

    /**
     * 修改控制指令
     * 旧端点: POST /htInstruction/updateData
     * 新端点: PUT /thermal/ht/instruction
     */
    @SaCheckLogin
    @Log(title = "控制指令", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtInstruction instruction) {
        return toAjax(htInstructionService.updateById(instruction));
    }

    /**
     * 删除控制指令（校验是否被策略子表引用）
     * 旧端点: POST /htInstruction/deleteData
     * 新端点: DELETE /thermal/ht/instruction/{id}
     */
    @SaCheckLogin
    @Log(title = "控制指令", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable @NotBlank String id) {
        int count = htInstructionService.countByStrategySub(id);
        if (count > 0) {
            return R.fail("该指令已被策略引用，无法删除");
        }
        return toAjax(htInstructionService.removeById(id));
    }

    /**
     * 查询所有控制指令
     * 旧端点: GET /htInstruction/queryInstructionList
     * 新端点: GET /thermal/ht/instruction/all
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<HtInstruction>> all() {
        return R.ok(htInstructionService.selectAllList());
    }

}
