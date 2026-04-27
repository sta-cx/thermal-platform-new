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
import org.sdkj.thermal.service.IPrCompanyService;
import org.sdkj.thermal.vo.TreeNode;
import org.sdkj.thermal.vo.TreeUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<TreeNode> trees = TreeUtil.fromPrCompanyList(companies);
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

    @SaCheckPermission("thermal:property:company:query")
    @SaCheckLogin
    @GetMapping("/organizationTree")
    public R<List<TreeNode>> organizationTree(@RequestParam String companyId) {
        List<SysOrganization> orgs = companyService.getOrganizationsByCompanyId(companyId);
        return R.ok(TreeUtil.buildByLoop(TreeUtil.fromSysOrganizationList(orgs), "-1"));
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
}
