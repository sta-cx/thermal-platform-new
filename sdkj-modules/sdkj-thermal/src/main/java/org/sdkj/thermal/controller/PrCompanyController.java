package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.bo.UserOrgBo;
import org.sdkj.thermal.service.IPrCompanyService;
import org.sdkj.thermal.vo.TreeNode;
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
    public R<List<PrCompany>> list() {
        return R.ok(companyService.listCompanies());
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
        boolean saved = companyService.save(company);
        if (saved) {
            companyService.createOrgRootNode(String.valueOf(company.getId()), company.getName(), company.getCode());
        }
        return toAjax(saved);
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
        return toAjax(companyService.deleteCompanyWithCascade(id));
    }

    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/organizationTree")
    public R<List<SysOrganization>> organizationTree(@RequestParam String companyId) {
        List<SysOrganization> orgs = companyService.getOrganizationsByCompanyId(companyId);
        return R.ok(buildOrgTree(orgs, "-1"));
    }

    private List<SysOrganization> buildOrgTree(List<SysOrganization> all, String parentId) {
        return all.stream()
                .filter(o -> parentId.equals(o.getParentId()))
                .peek(o -> o.setChildren(buildOrgTree(all, o.getId())))
                .collect(Collectors.toList());
    }

    // ==================== 新增端点 ====================

    /**
     * 含楼栋的组织机构树
     */
    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/buildingTree")
    public R<List<TreeNode>> queryBuildingTrees(@RequestParam String companyId) {
        return R.ok(companyService.queryBuildingTrees(companyId));
    }

    /**
     * 基于当前用户权限的含楼栋组织机构树
     */
    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/buildingTree/user")
    public R<List<TreeNode>> queryUserBuildingTrees() {
        Long userId = LoginHelper.getUserId();
        return R.ok(companyService.queryUserBuildingTrees(userId));
    }

    /**
     * 获取用户数据权限树
     */
    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/dataGrantOrg")
    public R<List<TreeNode>> getDataGrantOrg(@RequestParam String companyId,
                                             @RequestParam Long userId) {
        return R.ok(companyService.getDataGrantOrg(companyId, userId));
    }

    /**
     * 获取当前用户可访问的小区列表
     */
    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/userOrg")
    public R<List<SysOrganization>> getUserOrg() {
        Long userId = LoginHelper.getUserId();
        return R.ok(companyService.getUserOrg(userId));
    }

    /**
     * 获取用户所属分公司列表
     */
    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/userOrgBranch")
    public R<List<SysOrganization>> getUserOrgBranch() {
        Long userId = LoginHelper.getUserId();
        return R.ok(companyService.getUserOrgBranch(userId));
    }

    /**
     * 级联删除组织机构及其子节点
     */
    @SaCheckPermission("thermal:property:company:remove")
    @SaCheckLogin
    @Log(title = "组织机构级联删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteAll/{orgId}")
    public R<Integer> deleteAllData(@PathVariable String orgId) {
        int result = companyService.deleteAllData(orgId);
        if (result > 0) {
            return R.ok(result);
        } else {
            return R.fail("删除失败：该节点存在下级节点或总公司不可删除");
        }
    }

    @SaCheckPermission("thermal:property:company:edit")
    @SaCheckLogin
    @GetMapping("/userOrgIds/{userId}")
    public R<List<String>> getUserOrgIds(@PathVariable Long userId,
                                          @RequestParam String companyId) {
        return R.ok(companyService.getUserOrgIds(userId, companyId));
    }

    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/userCompanyId/{userId}")
    public R<String> getUserCompanyId(@PathVariable Long userId) {
        return R.ok("操作成功", companyService.getUserCompanyId(userId));
    }

    // ==================== 组织机构 CRUD ====================

    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/organization/{id}")
    public R<SysOrganization> getOrganization(@PathVariable String id) {
        return R.ok(companyService.getOrganizationById(id));
    }

    @SaCheckPermission("thermal:property:company:add")
    @SaCheckLogin
    @Log(title = "组织机构", businessType = BusinessType.INSERT)
    @PostMapping("/organization")
    public R<Void> addOrganization(@Validated @RequestBody SysOrganization org) {
        companyService.addOrganization(org);
        return R.ok();
    }

    @SaCheckPermission("thermal:property:company:edit")
    @SaCheckLogin
    @Log(title = "组织机构", businessType = BusinessType.UPDATE)
    @PutMapping("/organization")
    public R<Void> updateOrganization(@Validated @RequestBody SysOrganization org) {
        companyService.updateOrganization(org);
        return R.ok();
    }

    @SaCheckPermission("thermal:property:company:remove")
    @SaCheckLogin
    @Log(title = "组织机构", businessType = BusinessType.DELETE)
    @DeleteMapping("/organization/{id}")
    public R<Void> deleteOrganization(@PathVariable String id) {
        companyService.deleteOrganization(id);
        return R.ok();
    }

    @SaCheckPermission("thermal:property:company:edit")
    @SaCheckLogin
    @Log(title = "用户组织权限", businessType = BusinessType.INSERT)
    @PostMapping("/userOrg")
    public R<Void> saveUserOrg(@Validated @RequestBody UserOrgBo bo) {
        companyService.saveUserOrg(bo.getUserId(), bo.getCompanyId(), bo.getOrgIds());
        return R.ok();
    }

    @SaCheckPermission("thermal:property:company:edit")
    @SaCheckLogin
    @Log(title = "用户组织权限", businessType = BusinessType.DELETE)
    @DeleteMapping("/userOrg/{userId}")
    public R<Void> clearUserOrg(@PathVariable Long userId) {
        companyService.clearUserOrg(userId);
        return R.ok();
    }
}
