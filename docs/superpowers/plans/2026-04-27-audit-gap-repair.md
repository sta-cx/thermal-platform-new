# 新老系统架构迁移审核 — 问题修复计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复迁移审核中发现的8个模块中的关键功能缺失，确保新系统核心业务可用。

**Architecture:** 按P0→P2优先级分阶段修复。P0解决系统不可用问题（认证、组织机构、角色权限），P1补充核心业务缺失（抄表、月表、推送/工具），P2处理边缘模块存桩和数据库缺失。

**Tech Stack:** Spring Boot 3.5.12, MyBatis-Plus 3.5.16, Sa-Token 1.44.0, Java 17, MySQL 8.0

**审计基线:** 旧系统 thermal-balance-backend (Spring Boot 2.2.13 / Java 8 / MyBatis-Plus 3.3.2 / Sa-Token 1.39.0)

---

## P0 — 阻塞性问题（系统不可用）

### Task 1: 补全 SaOAuth2ServerController 多租户认证

**文件:**
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/SaOAuth2ServerController.java`
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IAuthServerService.java`
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AuthServerServiceImpl.java`
- 参考: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/SaOAuth2ServerController.java`
- 参考: `D:/chonggou/thermal-platform-new/sdkj-admin/src/main/java/org/sdkj/web/controller/AuthController.java`

- [ ] **Step 1: 从旧系统提取 OAuth2 认证端点清单**

阅读 `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/SaOAuth2ServerController.java`，列出所有端点：
```
POST /oauth2/authorize  — 授权端点（多租户登录）
POST /oauth2/token       — 令牌端点（密码模式+微信小程序登录）
POST /oauth2/refresh     — 刷新令牌
POST /oauth2/revoke      — 吊销令牌
GET  /oauth2/userinfo    — 获取用户信息
```

- [ ] **Step 2: 阅读旧系统认证核心逻辑**

阅读关键文件：
```
旧系统 UserController.java — 用户登录入口
旧系统 SaOAuth2ServerController.java — OAuth2 服务端
旧系统 TenantFilter.java — 多租户数据源切换
旧系统 TenantContextHolder.java — 租户上下文
新系统 AuthController.java — 新系统认证入口（作参考）
新系统 common-tenant 模块 — 新系统多租户机制
```

- [ ] **Step 3: 创建 AuthServerService 接口**

```java
// sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IAuthServerService.java
package org.sdkj.thermal.service;

import java.util.Map;

public interface IAuthServerService {
    /**
     * 多租户密码登录
     * @param tenantId 租户ID
     * @param username 用户名
     * @param password 密码（明文，服务端BCrypt校验）
     * @return 包含 tokenName + tokenValue 的 Map
     */
    Map<String, Object> login(String tenantId, String username, String password);

    /**
     * 微信小程序登录
     * @param tenantId 租户ID
     * @param code 微信小程序 code
     * @return 包含 tokenName + tokenValue 的 Map
     */
    Map<String, Object> miniappLogin(String tenantId, String code);

    /**
     * 退出登录
     * @param tokenValue 当前 token 值
     */
    void logout(String tokenValue);
}
```

- [ ] **Step 4: 实现 AuthServerServiceImpl**

```java
// sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AuthServerServiceImpl.java
package org.sdkj.thermal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.domain.AgUser;
import org.sdkj.thermal.mapper.AgUserMapper;
import org.sdkj.thermal.service.IAuthServerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServerServiceImpl implements IAuthServerService {

    private final AgUserMapper agUserMapper;

    @Override
    public Map<String, Object> login(String tenantId, String username, String password) {
        // 1. 查询用户（在新系统的 sys_user 或 ag_user 表中）
        LambdaQueryWrapper<AgUser> qw = new LambdaQueryWrapper<>();
        qw.eq(AgUser::getUserName, username);
        AgUser agUser = agUserMapper.selectOne(qw);
        if (agUser == null) {
            throw new RuntimeException("用户不存在");
        }
        // 2. BCrypt 密码校验
        if (!BCrypt.checkpw(password, agUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 3. Sa-Token 登录（标记租户ID）
        StpUtil.login(agUser.getId());
        StpUtil.getSession().set("tenantId", tenantId);
        // 4. 返回 token
        Map<String, Object> result = new HashMap<>();
        result.put("tokenName", StpUtil.getTokenName());
        result.put("tokenValue", StpUtil.getTokenValue());
        return result;
    }

    @Override
    public Map<String, Object> miniappLogin(String tenantId, String code) {
        // TODO: 集成 WxMaService 获取 openid，查找绑定用户
        // 参考: D:/chonggou/thermal-balance-backend/.../SaOAuth2ServerController.java
        // 旧系统使用 wxMaService.getUserService().getUserInfo(code) 获取 openid
        throw new UnsupportedOperationException("小程序登录待集成微信SDK");
    }

    @Override
    public void logout(String tokenValue) {
        StpUtil.logoutByTokenValue(tokenValue);
    }
}
```

- [ ] **Step 5: 创建 SaOAuth2ServerController**

```java
// sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/SaOAuth2ServerController.java
package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.service.IAuthServerService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth2")
public class SaOAuth2ServerController {

    private final IAuthServerService authServerService;

    @SaIgnore
    @PostMapping("/login")
    public R<Map<String, Object>> login(
            @RequestParam String tenantId,
            @RequestParam String username,
            @RequestParam String password) {
        Map<String, Object> token = authServerService.login(tenantId, username, password);
        return R.ok(token);
    }

    @SaIgnore
    @PostMapping("/miniapp")
    public R<Map<String, Object>> miniappLogin(
            @RequestParam String tenantId,
            @RequestParam String code) {
        Map<String, Object> token = authServerService.miniappLogin(tenantId, code);
        return R.ok(token);
    }

    @PostMapping("/logout")
    public R<Void> logout(@RequestHeader("Authorization") String token) {
        authServerService.logout(token);
        return R.ok();
    }
}
```

- [ ] **Step 6: 验证编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am
```
预期: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/SaOAuth2ServerController.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IAuthServerService.java
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AuthServerServiceImpl.java
git commit -m "fix: add multi-tenant OAuth2 login controller (P0 audit fix)

Restores SaOAuth2ServerController functionality from old system.
Supports tenant-aware password login, built-in BCrypt validation.
Mini-program login marked as TODO pending WeChat SDK integration."
```

---

### Task 2: 补全 PrCompanyController 组织机构管理

**文件:**
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrCompanyController.java`
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrCompanyService.java`
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrCompanyServiceImpl.java`
- 参考: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/SysCompanyController.java`
- 参考: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/SysCompanyServiceImpl.java`

- [ ] **Step 1: 阅读旧系统完整组织机构管理代码**

阅读旧系统 `SysCompanyController.java` 全部方法，列出旧系统有而新系统缺失的10+个方法：
```
findDataByCompanyId     — 组织机构树形结构
queryBuildingTrees      — 含楼栋的组织机构树
getAllDept              — 所有部门列表
verifyOrgName           — 组织名称校验
getSysOrgDataById       — 按ID查组织（含详细字段）
deleteAllData           — 级联删除
getTree                 — 树形结构（不显示部门）
getDataGrantOrg         — 数据权限树
getUserOrg              — 用户小区列表
getUserOrgBranch        — 分公司列表
getUserOrgByBranch      — 按分公司查小区
queryOrgByCompanyId     — 按公司查组织
```

- [ ] **Step 2: 阅读新系统已有实现**

阅读 `PrCompanyController.java` 现有代码，确认已有7个端点：
```
getById, organizationTree, add, edit, remove, list
```

- [ ] **Step 3: 在 IPrCompanyService 中新增缺失方法签名**

```java
// 在 IPrCompanyService.java 中追加
/**
 * 获取含楼栋的组织机构树
 * @param companyId 公司ID
 * @return 树形数据
 */
List<TreeNode<Long>> queryBuildingTrees(String companyId);

/**
 * 获取用户数据权限树（用于数据权限分配）
 * @param companyId 公司ID
 * @param userId 当前用户ID
 * @return 权限树
 */
List<TreeNode<Long>> getDataGrantOrg(String companyId, Long userId);

/**
 * 获取当前用户可访问的小区列表（分支机构过滤）
 * @param userId 用户ID
 * @return 小区列表
 */
List<SysOrganizationVo> getUserOrg(Long userId);

/**
 * 获取用户所属分公司列表
 * @param userId 用户ID
 * @return 分公司列表
 */
List<SysOrganizationVo> getUserOrgBranch(Long userId);

/**
 * 级联删除组织机构及其子节点
 * @param orgId 组织ID
 * @return 删除数量
 */
int deleteAllData(Long orgId);
```

- [ ] **Step 4: 在 PrCompanyServiceImpl 中实现 queryBuildingTrees**

阅读旧系统 `SysCompanyServiceImpl.queryBuildingTrees()` 的完整逻辑：
- 查全部 sys_organization 记录
- 查全部 pr_building 记录
- 以 organization 为父节点，building 为叶节点构建树
- 使用递归方法构建

然后实现这个逻辑到新系统的 `PrCompanyServiceImpl` 中，使用新系统的 `SysOrganization` 实体和 `PrBuilding` 实体。

```java
@Override
public List<TreeNode<Long>> queryBuildingTrees(String companyId) {
    // 1. 查询组织机构列表
    List<SysOrganization> orgs = sysOrganizationMapper.selectList(
        new LambdaQueryWrapper<SysOrganization>()
            .eq(SysOrganization::getCompanyId, companyId)
            .orderByAsc(SysOrganization::getOrderNum));
    // 2. 查询楼栋列表
    List<PrBuilding> buildings = prBuildingMapper.selectList(
        new LambdaQueryWrapper<PrBuilding>()
            .eq(PrBuilding::getCompanyId, companyId));
    // 3. 构建树：parentId=0的org作为根节点，递归构建
    return buildTree(orgs, buildings, 0L);
}

private List<TreeNode<Long>> buildTree(
        List<SysOrganization> orgs, List<PrBuilding> buildings, Long parentId) {
    List<TreeNode<Long>> nodes = new ArrayList<>();
    // 添加当前层级组织节点
    for (SysOrganization org : orgs) {
        if (org.getParentId().equals(parentId)) {
            TreeNode<Long> node = new TreeNode<>();
            node.setId(org.getId());
            node.setLabel(org.getOrgName());
            node.setType("org");
            node.setChildren(buildTree(orgs, buildings, org.getId()));
            // 该组织的叶节点添加楼栋
            for (PrBuilding b : buildings) {
                if (b.getOrgId().equals(org.getId())) {
                    TreeNode<Long> bnode = new TreeNode<>();
                    bnode.setId(b.getId());
                    bnode.setLabel(b.getBuildingName());
                    bnode.setType("building");
                    node.getChildren().add(bnode);
                }
            }
            nodes.add(node);
        }
    }
    return nodes;
}
```

- [ ] **Step 5: 在 PrCompanyController 中添加缺失端点**

```java
// 追加到 PrCompanyController

@SaCheckPermission("thermal:company:tree")
@GetMapping("/buildingTree")
public R<List<TreeNode<Long>>> queryBuildingTrees(@RequestParam String companyId) {
    return R.ok(companyService.queryBuildingTrees(companyId));
}

@SaCheckPermission("thermal:company:grant")
@GetMapping("/dataGrantOrg")
public R<List<TreeNode<Long>>> getDataGrantOrg(
        @RequestParam String companyId, @RequestParam Long userId) {
    return R.ok(companyService.getDataGrantOrg(companyId, userId));
}

@SaCheckPermission("thermal:company:org")
@GetMapping("/userOrg")
public R<List<SysOrganizationVo>> getUserOrg() {
    Long userId = LoginHelper.getUserId();
    return R.ok(companyService.getUserOrg(userId));
}

@SaCheckPermission("thermal:company:org")
@GetMapping("/userOrgBranch")
public R<List<SysOrganizationVo>> getUserOrgBranch() {
    Long userId = LoginHelper.getUserId();
    return R.ok(companyService.getUserOrgBranch(userId));
}

@SaCheckPermission("thermal:company:remove")
@DeleteMapping("/deleteAll/{orgId}")
public R<Integer> deleteAllData(@PathVariable Long orgId) {
    return R.ok(companyService.deleteAllData(orgId));
}
```

- [ ] **Step 6: 验证编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am
```
预期: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add sdkj-modules/sdkj-thermal
git commit -m "fix: restore PrCompanyController org tree and data permissions (P0 audit fix)

Adds 5 missing endpoints: buildingTree, dataGrantOrg, userOrg,
userOrgBranch, deleteAllData. Implements recursive tree building
with building leaf nodes."
```

---

### Task 3: 补全 PrRoleController 物业角色权限管理

**文件:**
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrRoleController.java`
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrRoleService.java`
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrRoleServiceImpl.java`
- 参考: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/PrRoleController.java`

- [ ] **Step 1: 阅读旧系统 PrRoleController 完整代码**

列出旧系统15+方法，确认缺失的8个方法：
```
getAllRoles           — 查询所有角色列表
getAllRolesByCreate   — 查询当前用户创建的角色
getRoleByUserId       — 按用户ID查询角色
deleteRolesData       — 批量删除
verifyIdent           — 角色标识唯一性校验
verifyName            — 角色名称唯一性校验
findYes               — 查询已分配菜单
findNo                — 查询未分配菜单
permissionUpd         — 权限分配（核心功能）
```

- [ ] **Step 2: 理解新旧角色体系差异**

旧系统使用 `Role` 实体（表 `sys_role`）：
```java
// 旧系统 Role 实体关键字段
String id;         // UUID
String roleName;   // 角色名称
String roleIdent;  // 角色标识
String nature;     // 角色类型
String companyId;  // 所属公司
```

新系统 `AgRole` 实体（表 `sys_role`）：
```java
// 新系统 AgRole 实体关键字段  
Long roleId;       // 自增ID
String roleName;
String roleKey;    // 角色标识（替代 roleIdent）
Long deptId;       // 部门ID
```

- [ ] **Step 3: 在 PrRoleController 中添加缺失端点**

```java
// 追加到 PrRoleController

@SaCheckPermission("thermal:role:list")
@GetMapping("/allRoles")
public R<List<AgRole>> getAllRoles(@RequestParam String companyId) {
    return R.ok(roleService.getAllRoles(companyId));
}

@SaCheckPermission("thermal:role:list")
@GetMapping("/allRolesByCreate")
public R<List<AgRole>> getAllRolesByCreate() {
    Long userId = LoginHelper.getUserId();
    return R.ok(roleService.getAllRolesByCreate(userId));
}

@SaCheckPermission("thermal:role:query")
@GetMapping("/byUser/{userId}")
public R<List<AgRole>> getRoleByUserId(@PathVariable Long userId) {
    return R.ok(roleService.getRoleByUserId(userId));
}

@SaCheckPermission("thermal:role:remove")
@DeleteMapping("/batch")
public R<Void> deleteRolesData(@RequestBody List<Long> ids) {
    roleService.deleteRolesData(ids);
    return R.ok();
}

@SaCheckPermission("thermal:role:query")
@GetMapping("/verifyIdent")
public R<Boolean> verifyIdent(
        @RequestParam String roleKey, @RequestParam(required = false) Long roleId) {
    return R.ok(roleService.verifyIdent(roleKey, roleId));
}

@SaCheckPermission("thermal:role:query")
@GetMapping("/verifyName")
public R<Boolean> verifyName(
        @RequestParam String roleName, @RequestParam(required = false) Long roleId) {
    return R.ok(roleService.verifyName(roleName, roleId));
}

@SaCheckPermission("thermal:role:menu")
@GetMapping("/allocatedMenus/{roleId}")
public R<List<SysMenuVo>> findYes(@PathVariable Long roleId) {
    return R.ok(roleService.findAllocatedMenus(roleId));
}

@SaCheckPermission("thermal:role:menu")
@GetMapping("/unallocatedMenus/{roleId}")
public R<List<SysMenuVo>> findNo(@PathVariable Long roleId) {
    return R.ok(roleService.findUnallocatedMenus(roleId));
}

@Log(title = "角色权限分配", businessType = BusinessType.UPDATE)
@SaCheckPermission("thermal:role:edit")
@PutMapping("/permission/{roleId}")
public R<Void> permissionUpd(
        @PathVariable Long roleId, @RequestBody List<Long> menuIds) {
    roleService.permissionUpd(roleId, menuIds);
    return R.ok();
}
```

- [ ] **Step 4: 在 PrRoleServiceImpl 中实现权限分配核心逻辑**

```java
// 在 PrRoleServiceImpl.java 中追加

@Override
public List<AgRole> getAllRoles(String companyId) {
    return agRoleMapper.selectList(
        new LambdaQueryWrapper<AgRole>().eq(AgRole::getCompanyId, companyId));
}

@Override
public void permissionUpd(Long roleId, List<Long> menuIds) {
    // 1. 删除已有角色-菜单关联
    sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
        .eq(SysRoleMenu::getRoleId, roleId));
    // 2. 批量插入新关联
    if (CollUtil.isNotEmpty(menuIds)) {
        List<SysRoleMenu> list = menuIds.stream().map(menuId -> {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            return rm;
        }).collect(Collectors.toList());
        sysRoleMenuMapper.insertBatch(list);
    }
    // 3. 清除该角色的权限缓存（Sa-Token 角色权限）
    StpUtil.getTokenSessionListByRoleId(roleId)
        .forEach(s -> s.logout());
}

@Override
public List<SysMenuVo> findAllocatedMenus(Long roleId) {
    List<Long> menuIds = sysRoleMenuMapper.selectList(
        new LambdaQueryWrapper<SysRoleMenu>()
            .eq(SysRoleMenu::getRoleId, roleId))
        .stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    if (CollUtil.isEmpty(menuIds)) return Collections.emptyList();
    return MapstructUtils.convert(
        sysMenuMapper.selectBatchIds(menuIds), SysMenuVo.class);
}
```

- [ ] **Step 5: 验证编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am
```
预期: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add sdkj-modules/sdkj-thermal
git commit -m "fix: restore PrRoleController permission management (P0 audit fix)

Adds 9 missing endpoints: allRoles, allRolesByCreate, getRoleByUserId,
batchDelete, verifyIdent, verifyName, allocatedMenus, unallocatedMenus,
permissionUpd. Implements role-menu RBAC permission assignment."
```

---

## P1 — 核心业务功能缺失

### Task 4: 补全 PrHeatReadingCopy1Controller 远传抄表

**文件:**
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrHeatReadingCopy1Controller.java`
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IPrHeatReadingCopy1Service.java`
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatReadingCopy1ServiceImpl.java`
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/PrHeatReadingMapper.java`
- 参考: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/PrHeatReadingCopy1Controller.java`

- [ ] **Step 1: 阅读旧系统实现**

阅读 `PrHeatReadingCopy1Controller.java` 的全部方法：
```
POST /pageList              — 根据 PrHeatReadingLabel 标签列表查询抄表数据
GET  /pageHeatReadingList   — 热表配表读数情况查询
```

对应的 Service 层：
```
PrHeatReadingCopy1Service.pageList(Page<PrHeatReadingCopy1>, List<PrHeatReadingLabel>, startTime, endTime)
PrHeatReadingCopy1Service.pageHeatReadingList(Page, companyId, orgId, ...)
```

对应的 Mapper：
```
PrHeatReadingCopy1Mapper — 对应的SQL查询方法
```

实体 `PrHeatReadingLabel` — 远传抄表标签（含 meterArcCode, companyName, orgName, buildingName, unitCode, roomNum 等）

- [ ] **Step 2: 创建 PrHeatReadingLabel 类**

```java
// sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/vo/PrHeatReadingLabelVo.java
package org.sdkj.thermal.domain.vo;

import lombok.Data;

@Data
public class PrHeatReadingLabelVo {
    private String meterArcCode;
    private String companyName;
    private String orgName;
    private String buildingName;
    private String unitCode;
    private String roomNum;
    private String labelDate;
}
```

- [ ] **Step 3: 创建 Service 接口和实现**

```java
// IPrHeatReadingCopy1Service.java
package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.sdkj.thermal.domain.vo.PrHeatReadingCopy1Vo;
import org.sdkj.thermal.domain.vo.PrHeatReadingLabelVo;
import java.util.List;

public interface IPrHeatReadingCopy1Service {
    Page<PrHeatReadingCopy1Vo> pageList(
        List<PrHeatReadingLabelVo> labels, String startTime, String endTime,
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<?> page);

    Page<PrHeatReadingCopy1Vo> pageHeatReadingList(
        String companyId, String orgId, String buildingId,
        String unitCode, String meterArcCode, String search,
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<?> page);
}
```

```java
// PrHeatReadingCopy1ServiceImpl.java — 委托给 PrHeatReadingMapper 的 XML SQL 查询
// 实际SQL逻辑在 step 4 的 Mapper XML 中定义
```

- [ ] **Step 4: 在 PrHeatReadingMapper 中添加查询方法**

```java
// 追加到 PrHeatReadingMapper.java
List<PrHeatReadingCopy1Vo> selectPageListCopy1(
    @Param("labels") List<PrHeatReadingLabelVo> labels,
    @Param("startTime") String startTime,
    @Param("endTime") String endTime);

List<PrHeatReadingCopy1Vo> selectPageHeatReadingList(
    @Param("companyId") String companyId,
    @Param("orgId") String orgId,
    @Param("buildingId") String buildingId,
    @Param("unitCode") String unitCode,
    @Param("meterArcCode") String meterArcCode,
    @Param("search") String search);
```

- [ ] **Step 5: 创建控制器**

```java
// PrHeatReadingCopy1Controller.java
@RestController
@RequestMapping("/thermal/ht/heat-reading-copy1")
public class PrHeatReadingCopy1Controller extends BaseController {

    private final IPrHeatReadingCopy1Service copy1Service;

    @SaCheckPermission("thermal:ht:reading:query")
    @PostMapping("/pageList")
    public TableDataInfo<PrHeatReadingCopy1Vo> pageList(
            @RequestBody List<PrHeatReadingLabelVo> labels,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            PageQuery pageQuery) {
        Page<?> page = pageQuery.build();
        return TableDataInfo.build(copy1Service.pageList(labels, startTime, endTime, page));
    }

    @SaCheckPermission("thermal:ht:reading:query")
    @GetMapping("/pageHeatReadingList")
    public TableDataInfo<PrHeatReadingCopy1Vo> pageHeatReadingList(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String meterArcCode,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        Page<?> page = pageQuery.build();
        return TableDataInfo.build(copy1Service.pageHeatReadingList(
            companyId, orgId, buildingId, unitCode, meterArcCode, search, page));
    }
}
```

- [ ] **Step 6: 验证编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am
```

- [ ] **Step 7: Commit**

```bash
git add sdkj-modules/sdkj-thermal
git commit -m "fix: restore PrHeatReadingCopy1Controller for remote meter reading (P1)"
```

---

### Task 5: 实现月表生成逻辑 (PrHeatMonth.setHeat)

**文件:**
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrHeatMonthServiceImpl.java`
- 参考: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/PrHeatMonthServiceImpl.java`
- 参考: 新系统 `PrHeatMonthController.setHeat()` 当前存桩代码

- [ ] **Step 1: 阅读旧系统月表生成逻辑**

阅读旧系统 `PrHeatMonthServiceImpl.setHeat()` 方法：
- 从 `pr_heat_daily` 表汇总日数据
- 按 month/year/houseId 分组聚合
- 插入到 `pr_heat_month` 表
- 处理边界条件：月末最后几天、跨月汇总

- [ ] **Step 2: 替换存桩实现**

找到新系统 `PrHeatMonthServiceImpl` 中的 `setHeat` 方法，当前为：
```java
// 当前存桩
public String setHeat(Boolean force) {
    return "月表生成功能尚未实现，依赖日表数据完整性";
}
```

替换为完整实现：
```java
@Override
@Transactional(rollbackFor = Exception.class)
public String setHeat(Boolean force) {
    // 1. 确定生成月份（force=true 时重算当月，否则查已有记录跳过）
    LocalDate now = LocalDate.now();
    String yearMonth = force != null && force
        ? now.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        : now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    int year = Integer.parseInt(yearMonth.substring(0, 4));
    int month = Integer.parseInt(yearMonth.substring(5, 7));

    // 2. 检查是否已生成
    Long count = prHeatMonthMapper.selectCount(new LambdaQueryWrapper<PrHeatMonth>()
        .eq(PrHeatMonth::getYear, year)
        .eq(PrHeatMonth::getMonth, month));
    if (count > 0 && (force == null || !force)) {
        return "月表数据已存在，无需重复生成";
    }

    // 3. 从日表汇总
    List<PrHeatMonth> monthList = prHeatDailyMapper.aggregateToMonth(year, month);
    if (CollUtil.isEmpty(monthList)) {
        return "日表数据为空，无法生成月表";
    }

    // 4. 删除旧数据 + 批量插入
    if (force != null && force && count > 0) {
        prHeatMonthMapper.delete(new LambdaQueryWrapper<PrHeatMonth>()
            .eq(PrHeatMonth::getYear, year)
            .eq(PrHeatMonth::getMonth, month));
    }
    prHeatMonthMapper.insertBatch(monthList);
    return "月表生成成功，共 " + monthList.size() + " 条记录";
}
```

- [ ] **Step 3: 添加日表汇总 Mapper 方法**

在 `PrHeatDailyMapper` 中添加：
```java
List<PrHeatMonth> aggregateToMonth(@Param("year") int year, @Param("month") int month);
```

在 `PrHeatDailyMapper.xml` 中添加 SQL：
```xml
<select id="aggregateToMonth" resultType="org.sdkj.thermal.domain.PrHeatMonth">
    SELECT
        house_id,
        #{year} AS year,
        #{month} AS month,
        SUM(heat_value) AS totalHeat,
        SUM(flow_value) AS totalFlow,
        SUM(heat_money) AS totalMoney
    FROM pr_heat_daily
    WHERE YEAR(create_time) = #{year} AND MONTH(create_time) = #{month}
    GROUP BY house_id
</select>
```

- [ ] **Step 4: 验证编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am
```

- [ ] **Step 5: Commit**

```bash
git add sdkj-modules/sdkj-thermal
git commit -m "fix: implement PrHeatMonth.setHeat monthly table generation (P1)"
```

---

### Task 6: 处理14个Service层边缘模块 — 决策与裁剪

**决策：** 逐一评估每个模块是否在新系统中需要保留。如不需要，明确标记裁剪；如需要，创建最小化迁移任务。

- [ ] **Step 1: 确认旧系统边缘模块清单**

| # | 模块 | 旧系统文件 | 决策 | 理由 |
|---|------|-----------|------|------|
| 1 | PrMaterials (物料) | Entity+Mapper+Service | 裁剪 | 未实现Controller，暂不需 |
| 2 | PrWareHouse (仓库) | Entity+Mapper+Service | 裁剪 | 未实现Controller |
| 3 | PrGuardClean (保安保洁) | Entity+Mapper+Service | 裁剪 | 未实现Controller |
| 4 | PrFixedAssets (固定资产) | Entity+Mapper+Service | 裁剪 | 未实现Controller |
| 5 | PrContractDirectory (合同) | Entity+Mapper+Service | 裁剪 | 未实现Controller |
| 6 | PrFileDirectory (档案) | Entity+Mapper+Service | 裁剪 | 未实现Controller |
| 7 | PrChecklistTemplate (检查清单) | Entity+Mapper+Service | 裁剪 | 未实现Controller |
| 8 | PrParameter (参数) | Mapper+Service(无Entity) | 裁剪 | 无Entity |
| 9 | PrPatrolCard (巡更卡) | Entity+Mapper+Service | 裁剪 | 未实现Controller |
| 10 | PrInspectionEquipment (巡检设备) | Entity+Mapper+Service | 保留 | 巡检模块已迁移Controller |
| 11 | PrApproval (审批) | Entity only (新系统) | 裁剪 | 无Controller，暂不需 |
| 12 | Pm* (停车管理 6个) | Entity+Mapper+Service (仅PmParkingSpace Entity保留) | 裁剪 | 仅PmParkingSpace保留 |
| 13 | Bn* (商户) | Entity+部分Service | 裁剪 | 未实现Controller |
| 14 | PrVisitInfo (来访信息) | 仅Entity | 裁剪 | 未实现Controller |

- [ ] **Step 2: 为保留的 PrInspectionEquipment 创建最小化实现**

```java
// 创建 entity + mapper + service 三层骨架
// 参考: sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/PrInspectionEquipment.java
// 参考: D:/chonggou/thermal-balance-backend/.../entity/PrInspectionEquipment.java

@TableName("pr_inspection_equipment")
public class PrInspectionEquipment extends BaseEntity {
    private Long id;
    private String equipmentName;
    private String equipmentCode;
    private String companyId;
    private String orgId;
    // getters/setters via Lombok
}
```

- [ ] **Step 3: 更新 CLAUDE.md 裁剪清单**

在 `D:/chonggou/thermal-platform-new/CLAUDE.md` 中追加：
```markdown
### 本次审核确认裁剪模块

以下模块经验证在旧系统中从未实现REST Controller，在新系统中确认不保留：
- PrMaterials, PrWareHouse, PrGuardClean, PrFixedAssets
- PrContractDirectory, PrFileDirectory, PrChecklistTemplate
- PrParameter, PrPatrolCard, PrApproval
- PmGate, PmParkinglot, PmParkingChange, PmSentryBox, PmStandard, PmVehicle
- BnMerchant, BnMerchantCategory, BnMerchantPromote, BnProduct, BnProductCategory
- PrVisitInfo, PrOperateCardLog, SysTaskClass

保留模块：
- PrInspectionEquipment — 巡检设备，巡检模块配套
- PmParkingSpace — 车位管理，物业收费模块配套
```

- [ ] **Step 4: Commit**

```bash
git add CLAUDE.md
git add sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/PrInspectionEquipment.java
git commit -m "docs: document edge-module audit decisions, keep PrInspectionEquipment (P1)"
```

---

## P2 — 功能存桩修复

### Task 7: 微信对账接入数据库

**文件:**
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrReconciliationServiceImpl.java`
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/PrWechatBillMapper.java`
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/PrWechatBill.java`
- 创建: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/domain/PrReconciliationDiff.java`

- [ ] **Step 1: 创建缺失的微信账单和对账差异实体**

参考旧系统对应实体：`PrWechatBill`, `PrReconciliationDiff`

- [ ] **Step 2: 替换对账服务中的 ConcurrentHashMap 为数据库操作**

找到 `PrReconciliationServiceImpl` 中标记 `Phase 6: 替换为实际数据库持久化` 的代码段，替换为 JPA/MyBatis-Plus 的数据库读写操作。

- [ ] **Step 3: 创建对应的数据库表（SQL脚本）**

在 `script/sql/` 下创建 `phase_p2_fixes.sql`：
```sql
CREATE TABLE IF NOT EXISTS pr_wechat_bill (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键',
    bill_date VARCHAR(10) COMMENT '账单日期',
    bill_type VARCHAR(20) COMMENT '账单类型',
    -- ... 其他字段
    tenant_id VARCHAR(20) DEFAULT '000000' COMMENT '租户ID'
) COMMENT '微信账单表';

CREATE TABLE IF NOT EXISTS pr_reconciliation_diff (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键',
    bill_id VARCHAR(32) COMMENT '账单ID',
    diff_type VARCHAR(20) COMMENT '差异类型',
    diff_amount DECIMAL(18,2) COMMENT '差异金额',
    status VARCHAR(10) DEFAULT '0' COMMENT '处理状态',
    tenant_id VARCHAR(20) DEFAULT '000000' COMMENT '租户ID'
) COMMENT '对账差异表';
```

- [ ] **Step 4: 验证编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am
```

- [ ] **Step 5: Commit**

```bash
git add sdkj-modules/sdkj-thermal
git add script/sql/phase_p2_fixes.sql
git commit -m "fix: wire Reconciliation to database instead of in-memory mock (P2)"
```

---

### Task 8: 补全押金登记、微信绑定等存桩功能

**文件:**
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/PrAccountServiceImpl.java` (saveDeposit)
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAccountController.java` (submitDespots)
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrWechatBindRecordController.java` (insertData)

- [ ] **Step 1: 实现押金登记 saveDeposit**

阅读旧系统 `PrAccountServiceImpl.saveDeposit()` 的完整流程：
- 创建 PrTransactionRecord
- 创建 PrTransactionDetail
- 更新 PrAccount 余额

在新系统中用 `PrAccountBalance` 和 `PrTransactionRecord` 实现等效逻辑。

- [ ] **Step 2: 实现押金提交 submitDespots**

阅读旧系统对应方法，在新系统 PrAccountController 中实现，调用 saveDeposit 逻辑。

- [ ] **Step 3: 实现微信绑定 PrWechatBindRecordController.insertData**

阅读旧系统 `PrWechatBindRecordServiceImpl.insertData()` 的查重+绑定逻辑，替换新系统存桩代码。

- [ ] **Step 4: 验证编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am
```

- [ ] **Step 5: Commit**

```bash
git add sdkj-modules/sdkj-thermal
git commit -m "fix: implement deposit registration and WeChat bind stubs (P2)"
```

---

### Task 9: 补全自助缴费机水/电/支付宝回调

**文件:**
- 修改: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/PrAutoMachineController.java`

- [ ] **Step 1: 阅读旧系统 PrAutoMachineController 完整流程**

列出存桩方法及其业务逻辑：
```
getQrCodeWater      — 生成水费QR码（调用微信/支付宝支付API）
getQrCodeEle        — 生成电费QR码
aliCallBackEle      — 支付宝电费回调
aliCallBackWater    — 支付宝水费回调
callbackEle         — 微信电费回调
callbackWater       — 微信水费回调
```

- [ ] **Step 2: 逐个替换存桩为实际实现**

每个方法按旧系统逻辑实现：参数校验 → 业务查询 → 调用支付API → 结果处理。

对于依赖第三方API的（如支付宝SDK已移除），明确标记：
```java
@Deprecated
@GetMapping("/qr-water")
public R<String> getQrCodeWater(...) {
    return R.fail("水费QR码生成依赖支付宝SDK（已移除），如需此功能请联系管理员");
}
```

- [ ] **Step 3: 验证编译、Commit**

---

### Task 10: 补充缺失的数据库表（精选核心表）

基于数据库Schema审计报告，从40+张缺失表中选择实际需要的表创建SQL。

- [ ] **Step 1: 创建核心缺失表的SQL脚本**

重点补充：
```sql
-- 微信支付辅助表
pr_wechat_refund       (微信退款记录)
pr_wechat_user         (微信用户)
pr_wechat_bind_record  (微信绑定记录)
pr_wechat_payment_record (支付记录)

-- 水电模块表（如需保留）
pr_water_archive       (水表档案)
pr_water_daily         (水表日记录)
pr_ele_archive         (电表档案)
pr_ele_daily           (电表日记录)

-- 导入暂存表
pr_import_heat         (热表导入暂存)
pr_import_basic_data   (基础数据导入暂存)
pr_import_record       (导入记录)

-- 其他业务表
pr_house_change        (房屋变更)
pr_inspection_equipment (巡检设备)
```

- [ ] **Step 2: 将SQL追加到 script/sql/phase_p2_fixes.sql**

- [ ] **Step 3: Commit**

---

## 执行顺序

```
P0: Task 1 (认证) → Task 2 (组织机构) → Task 3 (角色权限)
         ↓
P1: Task 4 (抄表) → Task 5 (月表) → Task 6 (边缘模块决策)
         ↓
P2: Task 7 (对账) → Task 8 (存桩修复) → Task 9 (缴费机) → Task 10 (DB表)
```

## 验证清单

每个阶段完成后执行：
```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl sdkj-modules/sdkj-thermal -am   # 编译
mvn test -pl sdkj-modules/sdkj-thermal           # 测试（如果存在）
```

每个Task有独立commit，可通过 `git log` 追溯修复历史。
