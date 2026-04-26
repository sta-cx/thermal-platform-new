# Agent 角色菜单管理补全报告

## 任务概述

补全新系统中Agent角色管理缺失的菜单查询功能，确保与旧系统功能对等。

## 旧系统功能分析

### AgentRoleController 菜单相关端点

**路由**: `/agent/role`

1. **GET /findYes** - 查询角色已分配的菜单列表
   - 参数: `roleId`, `companyId`
   - 返回: 树形菜单结构
   - 实现: `AgentRoleService.getYesMenuByRoleId()`

2. **GET /findNo** - 查询角色未分配的菜单列表
   - 参数: `roleId`, `companyId`
   - 返回: 树形菜单结构(含父节点)
   - 实现: `AgentRoleService.getNoMenuByRoleId()`
   - 特殊逻辑: 需要递归添加所有父节点到结果中

3. **POST /permissionUpd** - 分配菜单给角色
   - 参数: `roleId`, `menuIds`
   - 返回: 布尔值
   - 实现: `AgentRoleService.permissionUpd()`

## 新系统实现状态

### 已有功能
- ✅ `/thermal/agent/role/permission` - 分配角色菜单权限(对应 `permissionUpd`)

### 新增功能

#### 1. Mapper层 (`AgRoleMapper.java`)

新增方法:
```java
/**
 * 查询角色已分配的菜单列表
 */
List<SysMenuVo> selectYesMenuByRoleId(@Param("roleId") String roleId);

/**
 * 查询角色未分配的菜单列表
 */
List<SysMenuVo> selectNoMenuByRoleId(@Param("roleId") String roleId);

/**
 * 查询所有代理商菜单
 */
List<SysMenuVo> selectAgentMenus();
```

#### 2. Mapper XML (`AgRoleMapper.xml`)

新增SQL查询:
- `selectYesMenuByRoleId` - 从 `sys_role_menu` 关联 `sys_menu` 查询已分配菜单
- `selectNoMenuByRoleId` - 从 `sys_menu` 排除已分配的菜单
- `selectAgentMenus` - 查询所有可用菜单

#### 3. Service层 (`IAgRoleService.java` + `AgRoleServiceImpl.java`)

新增方法:
```java
/**
 * 查询角色已分配的菜单列表
 */
List<SysMenuVo> listYesMenus(String roleId);

/**
 * 查询角色未分配的菜单列表
 */
List<SysMenuVo> listNoMenus(String roleId);
```

实现细节:
- `listYesMenus()`: 直接返回已分配菜单列表
- `listNoMenus()`: 查询未分配菜单后，递归添加所有父节点(保持与旧系统逻辑一致)

#### 4. Controller层 (`AgRoleController.java`)

新增端点:

**GET /thermal/agent/role/menus/assigned**
- 查询角色已分配的菜单列表
- 对应旧系统: `/agent/role/findYes`
- 返回树形结构

**GET /thermal/agent/role/menus/unassigned**
- 查询角色未分配的菜单列表
- 对应旧系统: `/agent/role/findNo`
- 返回树形结构(含父节点)

## 数据库表结构

### sys_role_menu
```sql
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### sys_menu (使用sdkj-system基座表)
- 使用 `SysMenuVo` 作为返回类型
- 字段: `menu_id`, `parent_id`, `menu_name`, `order_num`, `path`, `component`, etc.

## 端点对照表

| 功能 | 旧系统端点 | 新系统端点 | 状态 |
|------|-----------|-----------|------|
| 分配角色菜单 | `POST /agent/role/permissionUpd` | `PUT /thermal/agent/role/permission` | ✅ 已有 |
| 查询已分配菜单 | `GET /agent/role/findYes` | `GET /thermal/agent/role/menus/assigned` | ✅ 新增 |
| 查询未分配菜单 | `GET /agent/role/findNo` | `GET /thermal/agent/role/menus/unassigned` | ✅ 新增 |

## 技术要点

1. **菜单树构建**: 使用 `TreeUtil.buildByLoop()` 方法构建树形结构
2. **父节点补全**: `listNoMenus()` 方法中实现了递归添加父节点的逻辑
3. **类型转换**: 使用 `convertToTreeNode()` 方法将 `SysMenuVo` 转换为 `TreeNode`
4. **权限控制**: 使用 `@SaCheckPermission` 注解控制访问权限
5. **登录校验**: 使用 `@SaCheckLogin` 注解确保登录状态

## 文件变更清单

1. `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/mapper/AgRoleMapper.java` - 新增3个方法
2. `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IAgRoleService.java` - 新增2个方法
3. `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AgRoleServiceImpl.java` - 实现2个新方法
4. `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AgRoleController.java` - 新增2个端点
5. `sdkj-modules/sdkj-thermal/src/main/resources/mapper/AgRoleMapper.xml` - 新增3个SQL查询

## 测试建议

1. 测试查询已分配菜单列表，验证返回正确的树形结构
2. 测试查询未分配菜单列表，验证父节点是否正确补全
3. 测试分配菜单权限功能，验证数据库正确更新
4. 测试权限注解是否生效
