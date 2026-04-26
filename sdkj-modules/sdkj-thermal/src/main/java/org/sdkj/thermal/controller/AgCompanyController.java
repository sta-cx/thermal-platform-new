package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.AgCompany;
import org.sdkj.thermal.domain.bo.AgCompanyBo;
import org.sdkj.thermal.service.IAgCompanyService;
import org.sdkj.thermal.vo.TreeNode;
import org.sdkj.thermal.vo.TreeUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 代理商公司管理
 * 迁移自旧系统 AgentCompanyController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/company")
public class AgCompanyController extends BaseController {

    private final IAgCompanyService companyService;

    /**
     * 查询公司树形列表
     */
    @SaCheckPermission("thermal:agent:company:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<TreeNode>> list(@RequestParam(required = false) String companyId) {
        List<AgCompany> companies = companyService.listCompanies();
        List<TreeNode> trees = companies.stream()
            .map(TreeNode::new)
            .collect(Collectors.toList());

        String parentId = "-1";
        if (companyId != null && !companyId.isEmpty()) {
            AgCompany company = companyService.getCompanyById(companyId);
            if (company != null && company.getParentId() != null) {
                parentId = company.getParentId();
            }
        }
        return R.ok(TreeUtil.buildByLoop(trees, parentId));
    }

    /**
     * 根据ID查询公司详情
     */
    @SaCheckPermission("thermal:agent:company:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<AgCompany> getInfo(@PathVariable String id) {
        return R.ok(companyService.getCompanyById(id));
    }

    /**
     * 新增公司
     */
    @SaCheckPermission("thermal:agent:company:add")
    @SaCheckLogin
    @Log(title = "代理商公司", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody AgCompanyBo companyBo) {
        return toAjax(companyService.insertCompany(companyBo));
    }

    /**
     * 修改公司
     */
    @SaCheckPermission("thermal:agent:company:edit")
    @SaCheckLogin
    @Log(title = "代理商公司", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody AgCompanyBo companyBo) {
        return toAjax(companyService.updateCompany(companyBo));
    }

    /**
     * 删除公司
     */
    @SaCheckPermission("thermal:agent:company:remove")
    @SaCheckLogin
    @Log(title = "代理商公司", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(companyService.deleteCompany(id));
    }

    /**
     * 校验手机号
     */
    @SaCheckLogin
    @GetMapping("/verifyTele")
    public R<Boolean> verifyTele(@RequestParam String tele) {
        return R.ok(companyService.verifyTele(tele));
    }

    /**
     * 校验编码
     */
    @SaCheckLogin
    @GetMapping("/verifyCode")
    public R<Boolean> verifyCode(@RequestParam String code) {
        return R.ok(companyService.verifyCode(code));
    }

    /**
     * 校验名称
     */
    @SaCheckLogin
    @GetMapping("/verifyName")
    public R<Boolean> verifyName(@RequestParam String name) {
        return R.ok(companyService.verifyName(name));
    }

    /**
     * 启用公司
     */
    @SaCheckPermission("thermal:agent:company:edit")
    @SaCheckLogin
    @Log(title = "启用代理商公司", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/enable")
    public R<Void> enable(@PathVariable String id) {
        return toAjax(companyService.enableCompany(id));
    }

    /**
     * 禁用公司
     */
    @SaCheckPermission("thermal:agent:company:edit")
    @SaCheckLogin
    @Log(title = "禁用代理商公司", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/disable")
    public R<Void> disable(@PathVariable String id) {
        return toAjax(companyService.disableCompany(id));
    }
}
