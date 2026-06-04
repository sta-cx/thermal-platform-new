package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.tenant.helper.TenantHelper;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.system.domain.SysRole;
import org.sdkj.system.domain.SysUser;
import org.sdkj.system.domain.vo.SysUserVo;
import org.sdkj.system.mapper.SysRoleMapper;
import org.sdkj.system.mapper.SysUserMapper;
import org.sdkj.system.mapper.SysUserRoleMapper;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.bo.HtRepairBo;
import org.sdkj.thermal.domain.vo.HtRepairVo;
import org.sdkj.thermal.service.IHtRepairService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 报修记录管理
 * 迁移自旧系统 HtRepairController
 * 旧端点: /htRepair/* -> 新端点: /thermal/ht/repair/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/repair")
public class HtRepairController extends BaseController {

    private final IHtRepairService htRepairService;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 分页查询报修记录列表
     * 旧端点: GET /htRepair/pageList
     * 新端点: GET /thermal/ht/repair/list
     */
    @SaCheckPermission("thermal:ht:repair:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtRepairVo> list(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) Integer repairType,
            @RequestParam(required = false) Integer repairStatus,
            @RequestParam(required = false) Integer urgentType,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getIsDelete, 0);
        lqw.eq(orgId != null, HtRepair::getOrgId, orgId);
        lqw.eq(buildingId != null, HtRepair::getBuildingId, buildingId);
        lqw.eq(repairType != null, HtRepair::getRepairType, repairType);
        lqw.eq(repairStatus != null, HtRepair::getRepairStatus, repairStatus);
        lqw.eq(urgentType != null, HtRepair::getUrgentType, urgentType);
        lqw.orderByDesc(HtRepair::getRepairTime);
        return htRepairService.selectPageList(lqw, pageQuery);
    }

    /**
     * 根据报修编号查询报修详情
     * 新端点: GET /thermal/ht/repair/{repairNo}
     */
    @SaCheckPermission("thermal:ht:repair:query")
    @SaCheckLogin
    @GetMapping("/{repairNo}")
    public R<HtRepair> getInfo(@PathVariable String repairNo) {
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getRepairNo, repairNo);
        lqw.eq(HtRepair::getIsDelete, 0);
        return R.ok(htRepairService.getOne(lqw));
    }

    /**
     * 新增报修记录
     * 旧端点: POST /htRepair/insertData
     * 新端点: POST /thermal/ht/repair
     */
    @SaCheckPermission("thermal:ht:repair:add")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> insert(@Validated @RequestBody HtRepairBo bo) {
        HtRepair repair = MapstructUtils.convert(bo, HtRepair.class);
        repair.setRepairNo(htRepairService.generateRepairNo());
        // repair_time 为 NOT NULL 且无 DB 默认值；普通新增（如报警转报修）未传时兜底当前时间，
        // 避免 MySQL 严格模式 "Field 'repair_time' doesn't have a default value" 报错
        if (repair.getRepairTime() == null) {
            repair.setRepairTime(new Date());
        }
        return toAjax(htRepairService.save(repair));
    }

    /**
     * 修改报修记录
     * 旧端点: POST /htRepair/updateData
     * 新端点: PUT /thermal/ht/repair
     */
    @SaCheckPermission("thermal:ht:repair:edit")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> update(@Validated @RequestBody HtRepairBo bo) {
        HtRepair repair = MapstructUtils.convert(bo, HtRepair.class);
        return toAjax(htRepairService.updateById(repair));
    }

    /**
     * 获取可派单的维修人员列表
     * 查询当前租户下拥有 repair_worker 角色的正常状态用户
     * 新端点: GET /thermal/ht/repair/fixers
     */
    @SaCheckPermission("thermal:ht:repair:dispatch")
    @SaCheckLogin
    @GetMapping("/fixers")
    public R<List<Map<String, String>>> fixers() {
        // 1. 查找 repair_worker 角色（SysRoleMapper 有 @DS("master")）
        SysRole workerRole = sysRoleMapper.selectOne(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, "repair_worker")
                .eq(SysRole::getStatus, "0")
        );
        if (workerRole == null) {
            return R.ok(List.of());
        }

        // 2. 查询该角色关联的用户 ID 列表
        //    SysUserRoleMapper 没有 @DS("master")，需手动切换
        DynamicDataSourceContextHolder.push("master");
        List<Long> userIds;
        try {
            userIds = sysUserRoleMapper.selectUserIdsByRoleId(workerRole.getRoleId());
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        if (userIds == null || userIds.isEmpty()) {
            return R.ok(List.of());
        }

        // 3. 查询用户信息（SysUserMapper 有 @DS("master")）
        String tenantId = TenantHelper.getTenantId();
        List<SysUserVo> users = sysUserMapper.selectVoList(
            new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getUserId, SysUser::getNickName)
                .eq(SysUser::getStatus, "0")
                .eq(SysUser::getTenantId, tenantId)
                .in(SysUser::getUserId, userIds)
        );

        // 4. 组装返回：雪花 Long ID 转为字符串，与前端 ID=string 约定一致
        List<Map<String, String>> result = users.stream().map(u -> {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("userId", String.valueOf(u.getUserId()));
            m.put("nickName", u.getNickName());
            return m;
        }).toList();

        return R.ok(result);
    }

    /**
     * 派单报修
     * 旧端点: POST /htRepair/updateDispatchData
     * 新端点: PUT /thermal/ht/repair/dispatch
     * 仅接收 fixId，后端校验 repair_worker 角色并回填 fixName
     */
    @SaCheckPermission("thermal:ht:repair:dispatch")
    @SaCheckLogin
    @Log(title = "报修记录派单", businessType = BusinessType.UPDATE)
    @PutMapping("/dispatch")
    public R<Void> dispatch(
            @RequestParam String repairNo,
            @RequestParam String fixId) {
        // 1. 校验 fixId 对应的用户存在且正常
        Long fixIdLong;
        try {
            fixIdLong = Long.parseLong(fixId);
        } catch (NumberFormatException e) {
            return R.fail("维修人员ID格式错误");
        }
        SysUser fixUser = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserId, fixIdLong)
                .eq(SysUser::getStatus, "0")
        );
        if (fixUser == null) {
            return R.fail("指定的维修人员不存在或已停用");
        }

        // 2. 校验该用户拥有 repair_worker 角色
        SysRole workerRole = sysRoleMapper.selectOne(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, "repair_worker")
                .eq(SysRole::getStatus, "0")
        );
        if (workerRole == null) {
            return R.fail("系统未配置维修人员角色，请联系管理员");
        }
        DynamicDataSourceContextHolder.push("master");
        boolean isWorker;
        try {
            List<Long> workerUserIds = sysUserRoleMapper.selectUserIdsByRoleId(workerRole.getRoleId());
            isWorker = workerUserIds != null && workerUserIds.contains(fixIdLong);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        if (!isWorker) {
            return R.fail("指定的用户不是维修人员，无法派单");
        }

        // 3. 写入派单信息
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getRepairNo, repairNo);
        HtRepair repair = new HtRepair();
        repair.setFixId(fixId);
        repair.setFixName(fixUser.getNickName());
        repair.setDispatchTime(new Date());
        // 派单后状态 0待派单 → 1已派单
        repair.setRepairStatus(1);
        // 记录派单操作人
        repair.setDispatchId(String.valueOf(LoginHelper.getUserId()));
        repair.setDispatchName(LoginHelper.getLoginUser().getNickname());
        return toAjax(htRepairService.update(repair, lqw));
    }

    /**
     * 删除报修记录（逻辑删除）
     * 旧端点: POST /htRepair/deleteData
     * 新端点: DELETE /thermal/ht/repair/{repairNo}
     */
    @SaCheckPermission("thermal:ht:repair:remove")
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{repairNo}")
    public R<Void> delete(@PathVariable String repairNo) {
        int rows = htRepairService.markAsDeleted(repairNo);
        if (rows > 0) {
            return toAjax(true);
        }
        return R.fail("删除失败，请检查报修编号");
    }

    /**
     * 处理报修（状态 1已派单 → 2处理中）
     * 新端点: PUT /thermal/ht/repair/process
     */
    @SaCheckPermission("thermal:ht:repair:edit")
    @SaCheckLogin
    @Log(title = "报修记录处理", businessType = BusinessType.UPDATE)
    @PutMapping("/process")
    public R<Void> process(
            @RequestParam String repairNo,
            @RequestParam String repairResult) {
        // 校验当前状态必须为已派单(1)
        HtRepair current = htRepairService.getOne(
            new LambdaQueryWrapper<HtRepair>()
                .eq(HtRepair::getRepairNo, repairNo));
        if (current == null) {
            return R.fail("报修记录不存在");
        }
        if (current.getRepairStatus() != 1) {
            return R.fail("当前状态不允许处理，需为已派单状态");
        }
        HtRepair repair = new HtRepair();
        repair.setRepairStatus(2);
        if (StringUtils.isNotBlank(repairResult)) {
            repair.setRepairResult(repairResult);
        }
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getRepairNo, repairNo);
        return toAjax(htRepairService.update(repair, lqw));
    }

    /**
     * 完成报修（状态 2处理中 → 3已完成）
     * 新端点: PUT /thermal/ht/repair/complete
     */
    @SaCheckPermission("thermal:ht:repair:edit")
    @SaCheckLogin
    @Log(title = "报修记录完成", businessType = BusinessType.UPDATE)
    @PutMapping("/complete")
    public R<Void> complete(@RequestParam String repairNo) {
        HtRepair current = htRepairService.getOne(
            new LambdaQueryWrapper<HtRepair>()
                .eq(HtRepair::getRepairNo, repairNo));
        if (current == null) {
            return R.fail("报修记录不存在");
        }
        if (current.getRepairStatus() != 2) {
            return R.fail("当前状态不允许完成，需为处理中状态");
        }
        HtRepair repair = new HtRepair();
        repair.setRepairStatus(3);
        repair.setFixTime(new Date());
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getRepairNo, repairNo);
        return toAjax(htRepairService.update(repair, lqw));
    }

    /**
     * 验收报修（状态 3已完成 → 4已验收）
     * 新端点: PUT /thermal/ht/repair/verify
     */
    @SaCheckPermission("thermal:ht:repair:verify")
    @SaCheckLogin
    @Log(title = "报修记录验收", businessType = BusinessType.UPDATE)
    @PutMapping("/verify")
    public R<Void> verify(
            @RequestParam String repairNo,
            @RequestParam String repairResult) {
        HtRepair current = htRepairService.getOne(
            new LambdaQueryWrapper<HtRepair>()
                .eq(HtRepair::getRepairNo, repairNo));
        if (current == null) {
            return R.fail("报修记录不存在");
        }
        if (current.getRepairStatus() != 3) {
            return R.fail("当前状态不允许验收，需为已完成状态");
        }
        HtRepair repair = new HtRepair();
        repair.setRepairStatus(4);
        if (StringUtils.isNotBlank(repairResult)) {
            repair.setRepairResult(repairResult);
        }
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getRepairNo, repairNo);
        return toAjax(htRepairService.update(repair, lqw));
    }

    /**
     * 维修人员视图 - 分页查询分配给当前用户的报修任务
     * 仅返回当前登录用户作为维修人员（fixId）的报修记录
     * 新端点: GET /thermal/ht/repair/repair-view
     */
    @SaCheckLogin
    @GetMapping("/repair-view")
    public TableDataInfo<HtRepairVo> pageListForRepair(
            @RequestParam(required = false) Integer repairStatus,
            PageQuery pageQuery) {
        String currentUserId = LoginHelper.getUserIdStr();
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtRepair::getIsDelete, 0);
        lqw.eq(HtRepair::getFixId, currentUserId);
        lqw.eq(repairStatus != null, HtRepair::getRepairStatus, repairStatus);
        lqw.orderByDesc(HtRepair::getRepairTime);
        return htRepairService.selectPageList(lqw, pageQuery);
    }

    /**
     * 按报修类型统计数量
     * 旧端点: GET /htRepair/queryTypeCount
     * 新端点: GET /thermal/ht/repair/typeCount
     */
    @SaCheckPermission("thermal:ht:repair:query")
    @SaCheckLogin
    @GetMapping("/typeCount")
    public R<List<Map<String, Object>>> typeCount() {
        return R.ok(htRepairService.selectTypeCount());
    }

    /**
     * 根据房间ID查询报修记录
     * 旧端点: GET /htRepair/queryRoomId
     * 新端点: GET /thermal/ht/repair/room
     */
    @SaCheckPermission("thermal:ht:repair:query")
    @SaCheckLogin
    @GetMapping("/room")
    public R<List<HtRepairVo>> getByRoom(@RequestParam String roomId) {
        return R.ok(htRepairService.selectByRoomId(roomId));
    }

}
