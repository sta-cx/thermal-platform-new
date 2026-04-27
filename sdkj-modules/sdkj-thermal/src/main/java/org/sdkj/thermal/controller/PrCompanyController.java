package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.service.IPrCompanyService;
import org.sdkj.thermal.vo.TreeNode;
import org.sdkj.thermal.vo.TreeUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物业公司管理
 * 迁移自旧系统 PrCompanyController
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/company")
public class PrCompanyController extends BaseController {

    private final IPrCompanyService companyService;

    @SaCheckPermission("thermal:property:company:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<TreeNode>> list(@RequestParam(required = false) String companyId) {
        List<PrCompany> companies = companyService.listCompanies();
        List<TreeNode> trees = companies.stream().map(c -> {
            TreeNode node = new TreeNode();
            node.setId(c.getId());
            node.setParentId(c.getParentId() != null ? c.getParentId() : "-1");
            node.setLabel(c.getName());
            return node;
        }).collect(Collectors.toList());
        String parentId = "-1";
        if (companyId != null && !companyId.isEmpty()) {
            PrCompany c = companyService.getById(companyId);
            if (c != null && c.getParentId() != null) parentId = c.getParentId();
        }
        return R.ok(TreeUtil.buildByLoop(trees, parentId));
    }

    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrCompany> getInfo(@PathVariable String id) {
        return R.ok(companyService.getById(id));
    }

    @SaCheckPermission("thermal:property:company:add")
    @SaCheckLogin
    @Log(title = "物业公司", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrCompany company) {
        return toAjax(companyService.save(company));
    }

    @SaCheckPermission("thermal:property:company:edit")
    @SaCheckLogin
    @Log(title = "物业公司", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrCompany company) {
        return toAjax(companyService.updateById(company));
    }

    @SaCheckPermission("thermal:property:company:remove")
    @SaCheckLogin
    @Log(title = "物业公司", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(companyService.removeById(id));
    }

    @SaCheckLogin
    @GetMapping("/organizationTree")
    public R<List<TreeNode>> organizationTree(@RequestParam String companyId) {
        List<SysOrganization> orgs = companyService.getOrganizationsByCompanyId(companyId);
        List<TreeNode> trees = orgs.stream().map(o -> {
            TreeNode node = new TreeNode();
            node.setId(o.getId());
            node.setParentId(o.getParentId());
            node.setLabel(o.getName());
            return node;
        }).collect(Collectors.toList());
        return R.ok(TreeUtil.buildByLoop(trees, "-1"));
    }
}
