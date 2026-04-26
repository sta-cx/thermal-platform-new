package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.bo.AgPropertyBo;
import org.sdkj.thermal.domain.vo.AgPropertyVo;
import org.sdkj.thermal.service.IAgPropertyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 代理商关联物业管理
 * 迁移自旧系统 AgentPropertyController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/property")
public class AgPropertyController extends BaseController {

    private final IAgPropertyService propertyService;

    /**
     * 分页查询代理商关联物业列表
     */
    @SaCheckPermission("thermal:agent:property:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<AgPropertyVo> list(
            @RequestParam(required = false) String isEnabled,
            @RequestParam(required = false) String isAudited,
            @RequestParam(required = false) String searchContent,
            @RequestParam(required = false) String agentCode,
            PageQuery pageQuery) {
        return TableDataInfo.build(propertyService.selectPropertyPage(pageQuery.build(), isEnabled, isAudited, searchContent, agentCode));
    }

    /**
     * 查询未绑定物业列表
     */
    @SaCheckPermission("thermal:agent:property:list")
    @SaCheckLogin
    @GetMapping("/unbound")
    public TableDataInfo<AgPropertyVo> unbound(
            @RequestParam(required = false) String agentCode,
            @RequestParam(required = false) String searchContent,
            PageQuery pageQuery) {
        return TableDataInfo.build(propertyService.selectUnboundPage(pageQuery.build(), agentCode, searchContent));
    }

    /**
     * 关联物业
     */
    @SaCheckPermission("thermal:agent:property:add")
    @SaCheckLogin
    @Log(title = "关联物业", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody AgPropertyBo propertyBo) {
        return toAjax(propertyService.bindProperty(propertyBo));
    }

    /**
     * 解除关联
     */
    @SaCheckPermission("thermal:agent:property:remove")
    @SaCheckLogin
    @Log(title = "解除物业关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(propertyService.unbindProperty(id));
    }
}
