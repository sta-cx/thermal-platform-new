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
import org.sdkj.thermal.domain.bo.AgUserBo;
import org.sdkj.thermal.domain.vo.AgUserVo;
import org.sdkj.thermal.service.IAgUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 代理商用户管理
 * 迁移自旧系统 AgentUserController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/user")
public class AgUserController extends BaseController {

    private final IAgUserService userService;

    /**
     * 分页查询代理商用户列表
     */
    @SaCheckPermission("thermal:agent:user:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<AgUserVo> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyId,
            PageQuery pageQuery) {
        return TableDataInfo.build(userService.selectUserPage(pageQuery.build(), name, companyId));
    }

    /**
     * 根据ID查询用户详情
     */
    @SaCheckPermission("thermal:agent:user:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<AgUserVo> getInfo(@PathVariable String id) {
        return R.ok(userService.selectUserById(id));
    }

    /**
     * 校验手机号是否已被注册
     */
    @SaCheckLogin
    @GetMapping("/checkTele")
    public R<Boolean> checkTele(@RequestParam String phone) {
        return R.ok(userService.checkPhone(phone));
    }

    /**
     * 新增用户
     */
    @SaCheckPermission("thermal:agent:user:add")
    @SaCheckLogin
    @Log(title = "代理商用户", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody AgUserBo userBo) {
        return toAjax(userService.insertUser(userBo));
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("thermal:agent:user:edit")
    @SaCheckLogin
    @Log(title = "代理商用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody AgUserBo userBo) {
        return toAjax(userService.updateUser(userBo));
    }

    /**
     * 删除用户
     */
    @SaCheckPermission("thermal:agent:user:remove")
    @SaCheckLogin
    @Log(title = "代理商用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(userService.deleteUser(id));
    }

    /**
     * 启用用户
     */
    @SaCheckPermission("thermal:agent:user:edit")
    @SaCheckLogin
    @Log(title = "启用代理商用户", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/enable")
    public R<Void> enable(@PathVariable String id) {
        return toAjax(userService.enableUser(id));
    }

    /**
     * 禁用用户
     */
    @SaCheckPermission("thermal:agent:user:edit")
    @SaCheckLogin
    @Log(title = "禁用代理商用户", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/disable")
    public R<Void> disable(@PathVariable String id) {
        return toAjax(userService.disableUser(id));
    }

    /**
     * 分配角色
     */
    @SaCheckPermission("thermal:agent:user:edit")
    @SaCheckLogin
    @Log(title = "分配用户角色", businessType = BusinessType.UPDATE)
    @PostMapping("/role")
    public R<Void> assignRoles(@RequestParam String userId, @RequestParam String roleIds) {
        return toAjax(userService.assignRoles(userId, roleIds));
    }

    /**
     * 重置密码
     */
    @SaCheckPermission("thermal:agent:user:edit")
    @SaCheckLogin
    @Log(title = "重置代理商用户密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody AgUserBo userBo) {
        return toAjax(userService.resetPassword(userBo.getId(), userBo.getUserPwd()));
    }
}
