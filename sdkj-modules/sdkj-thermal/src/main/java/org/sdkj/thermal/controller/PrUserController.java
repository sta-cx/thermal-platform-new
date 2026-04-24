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
import org.sdkj.thermal.domain.vo.PrUserVo;
import org.sdkj.thermal.service.IPrUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理
 * 迁移自旧系统 PrUserController
 * 旧端点: /property/user/* -> 新端点: /thermal/property/user/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/user")
public class PrUserController extends BaseController {

    private final IPrUserService userService;

    /**
     * 分页查询客户列表
     * 旧端点: POST /property/user/pageList
     * 新端点: GET /thermal/property/user/list
     */
    @SaCheckPermission("thermal:property:user:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrUserVo> list(
            @RequestParam(required = false) String companyId,
            PageQuery pageQuery) {
        return userService.selectPageList(companyId, pageQuery);
    }

    /**
     * 新增客户
     * 旧端点: POST /property/user/insertData
     * 新端点: POST /thermal/property/user
     */
    @SaCheckPermission("thermal:property:user:add")
    @SaCheckLogin
    @Log(title = "客户", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody PrUserVo vo) {
        return toAjax(userService.insertData(vo));
    }

    /**
     * 修改客户
     * 旧端点: POST /property/user/updateData
     * 新端点: PUT /thermal/property/user
     */
    @SaCheckPermission("thermal:property:user:edit")
    @SaCheckLogin
    @Log(title = "客户", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrUserVo vo) {
        return toAjax(userService.updateData(vo));
    }

    /**
     * 删除客户
     * 旧端点: POST /property/user/deleteData
     * 新端点: DELETE /thermal/property/user/{id}
     */
    @SaCheckPermission("thermal:property:user:remove")
    @SaCheckLogin
    @Log(title = "客户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id,
                          @RequestParam(required = false) String idNo) {
        return toAjax(userService.deleteData(id, idNo));
    }

    /**
     * 查询客户详情
     * 旧端点: POST /property/user/queryPrUserVo
     * 新端点: GET /thermal/property/user/{userId}
     */
    @SaCheckPermission("thermal:property:user:query")
    @SaCheckLogin
    @GetMapping("/{userId}")
    public R<PrUserVo> getById(@PathVariable String userId) {
        return R.ok(userService.selectDetailById(userId));
    }

    /**
     * 查询房屋是否有业主
     * 旧端点: POST /property/user/hasUser
     * 新端点: GET /thermal/property/user/has-user
     */
    @SaCheckPermission("thermal:property:user:query")
    @SaCheckLogin
    @GetMapping("/has-user")
    public R<Boolean> hasUser(@RequestParam String houseId) {
        return R.ok(userService.hasUser(houseId));
    }

    /**
     * 根据手机号查询客户
     * 旧端点: POST /property/user/getUserByPhone
     * 新端点: GET /thermal/property/user/by-phone
     */
    @SaCheckPermission("thermal:property:user:query")
    @SaCheckLogin
    @GetMapping("/by-phone")
    public R<PrUserVo> getByPhone(@RequestParam String phone) {
        return R.ok(userService.selectByPhone(phone));
    }
}
