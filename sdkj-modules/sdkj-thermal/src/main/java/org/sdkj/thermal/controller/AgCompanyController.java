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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 查询公司列表
     */
    @SaCheckPermission("thermal:agent:company:list")
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<AgCompany>> list() {
        return R.ok(companyService.listCompanies());
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

    /**
     * 公司保存（含自动创建管理员用户+关联角色）
     * 迁移自旧系统 SysCompanyController.save()
     */
    @SaCheckPermission("thermal:agent:company:add")
    @SaCheckLogin
    @Log(title = "代理商公司注册", businessType = BusinessType.INSERT)
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody AgCompanyBo companyBo) {
        return toAjax(companyService.registerCompany(companyBo));
    }

    /**
     * 验证是否可删除（判断公司名下是否有业务数据）
     * 迁移自旧系统 SysCompanyController.verifyDelete()
     */
    @SaCheckPermission("thermal:agent:company:remove")
    @SaCheckLogin
    @GetMapping("/{id}/verifyDelete")
    public R<Boolean> verifyDelete(@PathVariable String id) {
        return R.ok(companyService.canDeleteCompany(id));
    }

    /**
     * 编辑公司详情（富文本详情编辑）
     * 迁移自旧系统 SysCompanyController.editDetails()
     */
    @SaCheckPermission("thermal:agent:company:edit")
    @SaCheckLogin
    @Log(title = "编辑代理商公司详情", businessType = BusinessType.UPDATE)
    @PutMapping("/editDetails")
    public R<Void> editDetails(@Validated @RequestBody AgCompanyBo companyBo) {
        return toAjax(companyService.editDetails(companyBo));
    }

    /**
     * 查看公司详情（含 OSS 图片路径替换）
     * 迁移自旧系统 SysCompanyController.selectDetails()
     */
    @SaCheckPermission("thermal:agent:company:query")
    @SaCheckLogin
    @GetMapping("/{id}/details")
    public R<String> selectDetails(@PathVariable String id) {
        return R.ok(companyService.getCompanyDetails(id));
    }
}
