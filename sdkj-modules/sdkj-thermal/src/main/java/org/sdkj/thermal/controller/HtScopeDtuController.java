package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.HtScopeDtu;
import org.sdkj.thermal.mapper.HtScopeDtuMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * DTU控制范围表控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/scope-dtu")
public class HtScopeDtuController extends BaseController {

    private final HtScopeDtuMapper htScopeDtuMapper;

    /**
     * 查询DTU控制范围列表
     */
    @SaCheckPermission("thermal:htScopeDtu:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtScopeDtu> list(
        @RequestParam(required = false) String tasksId,
        @RequestParam(required = false) String orgId,
        @RequestParam(required = false) String companyId,
        PageQuery pageQuery) {
        QueryWrapper<HtScopeDtu> qw = new QueryWrapper<>();
        qw.eq(tasksId != null && !tasksId.isEmpty(), "tasks_id", tasksId);
        qw.eq(orgId != null && !orgId.isEmpty(), "org_id", orgId);
        qw.eq(companyId != null && !companyId.isEmpty(), "company_id", companyId);
        qw.orderByDesc("create_time");
        Page<HtScopeDtu> result = htScopeDtuMapper.selectPage(pageQuery.build(), qw);
        return TableDataInfo.build(result);
    }

    /**
     * 获取DTU控制范围详细信息
     */
    @SaCheckPermission("thermal:htScopeDtu:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtScopeDtu> getInfo(@PathVariable String id) {
        return R.ok(htScopeDtuMapper.selectById(id));
    }

    /**
     * 新增DTU控制范围
     */
    @SaCheckPermission("thermal:htScopeDtu:add")
    @SaCheckLogin
    @Log(title = "DTU控制范围", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody HtScopeDtu entity) {
        return toAjax(htScopeDtuMapper.insert(entity));
    }

    /**
     * 修改DTU控制范围
     */
    @SaCheckPermission("thermal:htScopeDtu:edit")
    @SaCheckLogin
    @Log(title = "DTU控制范围", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody HtScopeDtu entity) {
        return toAjax(htScopeDtuMapper.updateById(entity));
    }

    /**
     * 删除DTU控制范围
     */
    @SaCheckPermission("thermal:htScopeDtu:remove")
    @SaCheckLogin
    @Log(title = "DTU控制范围", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable String[] ids) {
        return toAjax(htScopeDtuMapper.deleteBatchIds(Arrays.asList(ids)));
    }

}
