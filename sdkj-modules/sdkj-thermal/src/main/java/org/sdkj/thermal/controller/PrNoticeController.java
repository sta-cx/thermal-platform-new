package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrNotice;
import org.sdkj.thermal.service.IPrNoticeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/notice")
public class PrNoticeController extends BaseController {

    private final IPrNoticeService noticeService;

    @SaCheckPermission("thermal:property:notice:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrNotice> list(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String type,
            PageQuery pageQuery) {
        Page<PrNotice> page = pageQuery.build();
        LambdaQueryWrapper<PrNotice> lqw = new LambdaQueryWrapper<>();
        lqw.eq(orgId != null && !orgId.isEmpty(), PrNotice::getOrgId, orgId);
        lqw.eq(type != null && !type.isEmpty(), PrNotice::getType, type);
        lqw.orderByDesc(PrNotice::getCreateTime);
        noticeService.page(page, lqw);
        return TableDataInfo.build(page);
    }

    @SaCheckPermission("thermal:property:notice:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrNotice> getById(@PathVariable String id) {
        return R.ok(noticeService.getById(id));
    }

    @SaCheckPermission("thermal:property:notice:add")
    @SaCheckLogin
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrNotice notice) {
        return toAjax(noticeService.save(notice));
    }

    @SaCheckPermission("thermal:property:notice:edit")
    @SaCheckLogin
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrNotice notice) {
        return toAjax(noticeService.updateById(notice));
    }

    @SaCheckPermission("thermal:property:notice:remove")
    @SaCheckLogin
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(noticeService.removeById(id));
    }
}
