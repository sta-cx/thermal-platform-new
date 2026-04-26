# 系统管理覆盖度验证报告

## 概述

本报告对比旧系统(`thermal-balance-backend`)和新系统(`thermal-platform-new`)的系统管理功能覆盖情况。

## 旧系统系统管理端点列表

### 1. SysUserController (物业员工管理)
**路由**: `/property/sysUser`
**端点**:
- `GET /getAllUser` - 获取所有系统操作员
- `POST /insertData` - 新增系统操作员
- `GET /checkTele` - 检查手机号是否被注册
- `GET /getDataById` - 根据ID查找员工
- `GET /startUsing` - 启用人员
- `GET /endUsing` - 禁用人员
- `GET /deleteData` - 删除人员
- `POST /updateData` - 修改人员
- `GET /getAllOrg` - 查询所有小区(数据权限)
- `GET /hasAlready` - 查询已有的数据权限
- `POST /removeDept` - 移除部门
- `GET /updatePassword` - 修改密码
- `GET /getMeterNum` - 获取所管辖小区仪表数量
- `GET /queryDecryption` - 获取授权文件
- `GET /getDataByWXOpenId` - 根据微信OpenId获取数据

**新系统对应**: `sdkj-system` 的 `SysUserController` (`/system/user`)
**覆盖状态**: ✅ **基座覆盖** - RuoYi-Plus 基座提供完整用户管理功能，但旧系统的物业特有功能(如数据权限、仪表数量统计)需要在 thermal 模块补充

---

### 2. SysCompanyController (商家档案管理)
**路由**: `/company`
**端点**:
- `GET /pageList` - 商家列表
- `GET /checkTele` - 保存前校验手机号
- `POST /save` - 商家保存(含用户创建和角色关联)
- `GET /updateData` - 商家编辑
- `GET /verifyDelete` - 验证删除接口
- `GET /del` - 删除商家
- `GET /editDetails` - 编辑详情
- `GET /selectDetails` - 查看商家详情

**新系统对应**: `sdkj-system` 的 `SysTenantController` (`/system/tenant`)
**覆盖状态**: ✅ **基座覆盖** - 多租户模式替代公司管理，但旧系统的商家特有功能(如详情编辑、关联用户创建)需要迁移

---

### 3. SysDictController (字典管理)
**路由**: `/sysDict`
**端点**:
- `GET /dictTpyeList` - 字典类型列表分页查询
- `GET /dictList` - 根据字典类型查询字典列表
- `GET /dictTpyeFrom` - 字典类型表查询
- `GET /isDictTypeRepeat` - 验证字典类型编码/名称是否存在
- `GET /isDictRepeat` - 验证字典编码/名称是否存在
- `POST /insertData` - 字典类型、字典添加
- `GET /deleteData` - 删除字典类型
- `GET /getDataByType` - 根据类型获取字典数据

**新系统对应**: `sdkj-system` 的 `SysDictTypeController` 和 `SysDictDataController`
**覆盖状态**: ✅ **基座覆盖**

---

### 4. MenuController (系统菜单管理)
**路由**: `/menu`
**端点**:
- `GET` - 返回当前用户的树形菜单集合
- `GET /tree1` - 返回树形菜单集合(未分配)
- `GET /tree/{roleId}` - 返回角色的菜单集合
- `GET /{id}` - 通过ID查询菜单详细信息
- `POST` - 新增菜单
- `DELETE /{id}` - 删除菜单
- `PUT` - 更新菜单

**新系统对应**: `sdkj-system` 的 `SysMenuController`
**覆盖状态**: ✅ **基座覆盖**

---

### 5. RoleController (角色管理)
**路由**: `/role`
**端点**:
- `GET /{id}` - 通过ID查询角色信息
- `POST` - 添加角色
- `PUT` - 修改角色
- `DELETE /{id}` - 删除角色
- `GET /list` - 获取角色列表
- `GET /page` - 分页查询角色信息
- `PUT /menu` - 更新角色菜单

**新系统对应**: `sdkj-system` 的 `SysRoleController`
**覆盖状态**: ✅ **基座覆盖**

---

### 6. SysColumnController (用户自定义表格列)
**路由**: `/property/sysColumn`
**端点**:
- `GET /pageList` - 根据表格名称查找自定义列
- `GET /insertData` - 新增表格自定义列

**新系统对应**: `sdkj-system` 的 `SysColumnController`
**覆盖状态**: ✅ **基座覆盖**

---

### 7. SysHomeController (首页统计)
**路由**: `/home`
**端点**:
- `POST /querHomeData` - 查询首页数据(异步并发查询多个数据源)

**新系统对应**: ❌ **无对应** - 需要迁移
**覆盖状态**: ⚠️ **需要迁移**

---

### 8. OssManagerController (OSS文件管理)
**路由**: `/stsOss`
**端点**:
- `GET /read` - 获取读权限token
- `GET /write` - 获取写权限token
- `GET /manager` - 获取管理权限token
- `GET /getCode` - 发送手机验证码
- `POST /uploadFile` - 上传文件到OSS
- `POST /getUrl` - 获取OSS文件地址
- `POST /hasFileInOss` - 检查文件是否存在
- `GET /validateTele` - 验证注册手机号是否重复
- `GET /validateAgentCode` - 验证代理商编码
- `GET /registerCompany` - 注册公司

**新系统对应**: `sdkj-system` 的 `SysOssController` (仅OSS管理)
**覆盖状态**: ⚠️ **部分覆盖** - OSS基础功能基座覆盖，但注册、验证码等业务功能需要迁移

---

### 9. ToolsController (工具控制器)
**路由**: `/property/tools`
**端点**:
- `GET /getCompanyMeter` - 查询公司被分配的仪表厂商
- `GET /getMeterList` - 查询公司下仪表集合

**新系统对应**: ❌ **无对应** - 仪表相关工具功能
**覆盖状态**: ⚠️ **需要迁移**

---

### 10. PushController (推送管理)
**路由**: `/property/push`
**端点**:
- `POST /setPush` - 设置推送

**新系统对应**: ❌ **无对应** - 推送功能
**覆盖状态**: ⚠️ **需要迁移**

---

### 11. TaskController (定时任务)
**路由**: `/property/task`
**端点**:
- `POST /list` - 任务列表
- `POST /listPost` - POST任务列表
- `GET /getById/{id}` - 获取任务详情
- `GET /getPostById/{id}` - 获取POST任务详情
- `POST /edit` - 编辑任务
- `POST /editPost` - 编辑POST任务
- `POST /changeStatus/{id}` - 修改任务状态
- `POST /changePostStatus/{id}` - 修改POST任务状态
- `POST /remove/{id}` - 删除任务
- `POST /removePost/{id}` - 删除POST任务
- `POST /run/{id}` - 立即运行任务
- `POST /runPost/{id}` - 立即运行POST任务
- `POST /removeBatch` - 批量删除
- `POST /save` - 新增保存
- `POST /savePost` - 新增POST保存
- `POST /getTaskClass` - 获取任务类
- `POST /getTaskPostClass` - 获取POST任务类
- `POST /getTaskDetails` - 获取任务详情

**新系统对应**: `sdkj-job` 模块 (Quartz定时任务)
**覆盖状态**: ✅ **基座覆盖**

---

### 12. AreaController (地区管理)
**路由**: `/area`
**端点**:
- `GET /province` - 选择省
- `GET /city` - 选择市
- `GET /county` - 选择县

**新系统对应**: `sdkj-thermal` 的 `AreaController` (`/thermal/area`)
**覆盖状态**: ✅ **已迁移**

---

### 13. PropertyMenuController (物业菜单管理)
**路由**: `/propertyMenu`
**端点**:
- `GET /tree` - 返回树形菜单集合
- `POST` - 新增物业菜单
- `GET /{id}` - 通过ID查询菜单详细信息
- `DELETE /{id}` - 删除菜单
- `PUT` - 更新菜单

**新系统对应**: `sdkj-thermal` 的 `AgPropertyMenuController` (`/thermal/agent/property-menu`)
**覆盖状态**: ✅ **已迁移**

---

## 总结统计

| 功能模块 | 旧系统端点数 | 新系统状态 | 说明 |
|---------|-------------|-----------|------|
| 用户管理 | 15 | ✅ 基座覆盖 | 物业特有功能需补充 |
| 公司/租户管理 | 8 | ✅ 基座覆盖 | 多租户模式替代 |
| 字典管理 | 8 | ✅ 基座覆盖 | |
| 菜单管理 | 7 | ✅ 基座覆盖 | |
| 角色管理 | 7 | ✅ 基座覆盖 | |
| 列配置 | 2 | ✅ 基座覆盖 | |
| 首页统计 | 1 | ⚠️ 需要迁移 | 并发数据查询 |
| OSS管理 | 10 | ⚠️ 部分覆盖 | 业务功能需迁移 |
| 工具 | 2 | ⚠️ 需要迁移 | 仪表厂商查询 |
| 推送 | 1 | ⚠️ 需要迁移 | |
| 定时任务 | 18 | ✅ 基座覆盖 | Quartz模块 |
| 地区管理 | 3 | ✅ 已迁移 | |
| 物业菜单 | 5 | ✅ 已迁移 | |

**总计**: 87 个端点
- ✅ 完全覆盖/已迁移: 68 个 (78%)
- ⚠️ 需要迁移: 19 个 (22%)

## 需要迁移的功能清单

1. **SysHomeController** - 首页统计数据查询
2. **OssManagerController** - 注册、验证码、文件上传等业务功能
3. **ToolsController** - 仪表厂商和仪表列表查询工具
4. **PushController** - 推送设置功能
5. **SysUserController** - 物业特有功能(数据权限、仪表数量统计、微信OpenId查询)
