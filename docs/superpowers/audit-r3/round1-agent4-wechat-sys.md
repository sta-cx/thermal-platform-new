# Round 1 — Agent 4: 微信支付 + 系统管理

## 审核日期
2026-04-26

## 审核范围
- 微信/支付模块 (6个旧系统 Controller)
- 系统管理模块 (14个旧系统 Controller)

---

## 统计

| 模块 | 旧端点数 | MATCH | PARTIAL | SKELETON | MISSING | NEW |
|------|---------|-------|---------|----------|---------|-----|
| 微信/支付 | 34 | 8 | 3 | 23 | 0 | 0 |
| 系统管理 | 78 | 0 | 0 | 42 | 36 | 0 |
| **合计** | **112** | **8** | **3** | **65** | **36** | **0** |

**覆盖率**: MATCH+PARTIAL = 9.8%
**骨架实现率**: 58.0%

---

## 端点明细

### 一、微信/支付模块

#### 1. WechatAuthController (微信授权管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /wechat/auth/getAuthUrl | GET | /thermal/wechat/auth/url | GET | MATCH | 功能一致 |
| /wechat/auth/callback | GET | /thermal/wechat/auth/callback | GET | SKELETON | Phase 6 待实现完整流程 |
| /wechat/auth/bind | POST | /thermal/wechat/auth/bind | POST | SKELETON | Phase 6 待实现 |
| /wechat/auth/userInfo | GET | /thermal/wechat/auth/userInfo | GET | SKELETON | Phase 6 待实现 |

**新系统文件**: `sdkj-thermal/controller/WechatAuthController.java`

#### 2. WechatPayController (微信支付)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /wechat/pay/createOrder | POST | /thermal/wechat/pay/createOrder | POST | SKELETON | Phase 6 待实现支付SDK |
| /wechat/pay/notify | POST | /thermal/wechat/pay/notify | POST | SKELETON | Phase 6 待实现验签 |
| /wechat/pay/queryOrder | GET | /thermal/wechat/pay/queryOrder | GET | SKELETON | Phase 6 待实现 |
| /wechat/pay/applyRefund | POST | /thermal/wechat/pay/applyRefund | POST | SKELETON | Phase 6 待实现 |
| /wechat/pay/refundNotify | POST | /thermal/wechat/pay/refundNotify | POST | SKELETON | Phase 6 待实现 |
| /wechat/pay/queryRefund | GET | /thermal/wechat/pay/queryRefund | GET | SKELETON | Phase 6 待实现 |

**新系统文件**: `sdkj-thermal/controller/WechatPayController.java`

#### 3. WxPortalController (微信消息入口)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /wx/portal/{appid} | GET | /thermal/wx/portal/{appid} | GET | MATCH | 签名验证逻辑一致 |
| /wx/portal/{appid} | POST | /thermal/wx/portal/{appid} | POST | PARTIAL | 路由逻辑Phase 6待完善 |

**新系统文件**: `sdkj-thermal/controller/WxPortalController.java`

#### 4. WxMaUserController (微信小程序用户)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /wx/user/{appid}/login | GET | /thermal/wx/user/{appid}/login | GET | SKELETON | Phase 6 待实现 |
| /wx/user/{appid}/info | GET | /thermal/wx/user/{appid}/info | GET | SKELETON | Phase 6 待实现 |
| /wx/user/{appid}/phone | GET | /thermal/wx/user/{appid}/phone | GET | SKELETON | Phase 6 待实现 |

**新系统文件**: `sdkj-thermal/controller/WxMaUserController.java`

#### 5. WxMaMediaController (微信素材管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /wx/media/{appid}/upload | POST | /thermal/wx/media/{appid}/upload | POST | SKELETON | Phase 6 待实现 |
| /wx/media/{appid}/download/{mediaId} | GET | /thermal/wx/media/{appid}/download/{mediaId} | GET | SKELETON | Phase 6 待实现 |

**新系统文件**: `sdkj-thermal/controller/WxMaMediaController.java`

#### 6. RepairController (微信报修)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /wechat/repair/pageList | POST | /thermal/wechat/repair/list | GET | PARTIAL | 新系统用RESTful GET |
| /wechat/repair/insertData | POST | /thermal/wechat/repair | POST | MATCH | 新增报修 |
| /wechat/repair/details | POST | /thermal/wechat/repair/{id} | GET | PARTIAL | 新系统用RESTful GET |
| /wechat/repair/updateData | POST | /thermal/wechat/repair | PUT | PARTIAL | 新系统用RESTful PUT |
| /wechat/repair/deleteData | POST | /thermal/wechat/repair/{id} | DELETE | PARTIAL | 新系统用RESTful DELETE |
| /wechat/repair/updateStatus | POST | /thermal/wechat/repair/status | PUT | MATCH | 更新状态 |
| /wechat/repair/itemsdetails | POST | - | - | MISSING | OSS详情转换未迁移 |
| /wechat/repair/queryCodeList | POST | - | - | MISSING | 查询编码列表未迁移 |

**新系统文件**: `sdkj-thermal/controller/WechatRepairController.java`

---

### 二、系统管理模块

#### 7. SysUserController (物业员工管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/sysUser/getAllUser | POST | /thermal/agent/user/list | GET | SKELETON | 新系统用AgUserController |
| /property/sysUser/insertData | POST | /thermal/agent/user | POST | SKELETON | 新系统用AgUserController |
| /property/sysUser/checkTele | POST | /thermal/agent/user/checkTele | GET | MATCH | 手机号校验 |
| /property/sysUser/getDataById | POST | - | - | MISSING | 详情查询未迁移 |
| /property/sysUser/startUsing | POST | /thermal/agent/user/{id}/enable | PUT | SKELETON | 启用用户 |
| /property/sysUser/endUsing | POST | /thermal/agent/user/{id}/disable | PUT | SKELETON | 禁用用户 |
| /property/sysUser/deleteData | POST | /thermal/agent/user/{id} | DELETE | SKELETON | 删除用户 |
| /property/sysUser/updateData | POST | /thermal/agent/user | PUT | SKELETON | 修改用户 |
| /property/sysUser/getAllOrg | POST | - | - | MISSING | 组织权限未迁移 |
| /property/sysUser/hasAlready | POST | - | - | MISSING | 已有权限未迁移 |
| /property/sysUser/removeDept | POST | - | - | MISSING | 移除部门未迁移 |
| /property/sysUser/updatePassword | POST | - | - | MISSING | 密码修改未迁移 |
| /property/sysUser/getMeterNum | POST | - | - | MISSING | 仪表数量未迁移 |
| /property/sysUser/queryDecryption | POST | - | - | MISSING | 授权文件未迁移 |
| /property/sysUser/getDataByWXOpenId | POST | - | - | MISSING | 微信OpenID查询未迁移 |

**迁移说明**: 旧系统的物业员工管理已迁移到 `AgUserController` (代理商用户管理)，但部分功能缺失。

#### 8. SysCompanyController (商家档案)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /company/pageList | POST | /thermal/agent/company/list | GET | SKELETON | 新系统用AgCompanyController |
| /company/checkTele | POST | /thermal/agent/company/verifyTele | GET | SKELETON | 手机号校验 |
| /company/save | POST | /thermal/agent/company | POST | SKELETON | 新增公司 |
| /company/updateData | POST | /thermal/agent/company | PUT | SKELETON | 修改公司 |
| /company/verifyDelete | POST | - | - | MISSING | 删除验证未迁移 |
| /company/deleteData | POST | /thermal/agent/company/{id} | DELETE | SKELETON | 删除公司 |
| /company/editDetails | POST | - | - | MISSING | 编辑详情未迁移 |
| /company/selectDetails | POST | - | - | MISSING | 查看详情未迁移 |

**迁移说明**: 已迁移到 `AgCompanyController`，但详情相关功能缺失。

#### 9. SysDictController (字典管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /sysDict/dictTpyeList | POST | /system/dict/type/list | GET | SKELETON | sdkj-system基座提供 |
| /sysDict/dictList | POST | /system/dict/data/list | GET | SKELETON | sdkj-system基座提供 |
| /sysDict/dictTpyeFrom | POST | /system/dict/type/{dictId} | GET | SKELETON | sdkj-system基座提供 |
| /sysDict/isDictTypeRepeat | POST | - | - | MISSING | 重复校验未迁移 |
| /sysDict/isDictRepeat | POST | - | - | MISSING | 重复校验未迁移 |
| /sysDict/insertData | POST | /system/dict/type + /system/dict/data | POST | SKELETON | 拆分为两个接口 |
| /sysDict/deleteData | POST | /system/dict/type/{dictIds} | DELETE | SKELETON | sdkj-system基座提供 |
| /sysDict/getDataByType | POST | /system/dict/data/type/{dictType} | GET | SKELETON | sdkj-system基座提供 |

**迁移说明**: 字典功能由 `sdkj-system` 基座的 `SysDictTypeController` 和 `SysDictDataController` 提供。

#### 10. SysColumnController (表格列自定义)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/sysColumn/pageList | POST | - | - | MISSING | 未迁移 |
| /property/sysColumn/insertData | POST | - | - | MISSING | 未迁移 |

**迁移说明**: 自定义列功能未迁移。

#### 11. SysHomeController (首页数据)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /home/querHomeData | POST | - | - | MISSING | 首页数据未迁移 |

**迁移说明**: 首页功能未迁移。

#### 12. MenuController (菜单管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /menu | GET | /system/menu/getRouters | GET | SKELETON | sdkj-system基座提供 |
| /menu/tree1 | POST | - | - | MISSING | 树形菜单未迁移 |
| /menu/tree/{roleId} | GET | /system/menu/roleMenuTreeselect/{roleId} | GET | SKELETON | sdkj-system基座提供 |
| /menu/{id} | GET | /system/menu/{menuId} | GET | SKELETON | sdkj-system基座提供 |
| /menu | POST | /system/menu | POST | SKELETON | sdkj-system基座提供 |
| /menu/{id} | DELETE | /system/menu/{menuId} | DELETE | SKELETON | sdkj-system基座提供 |
| /menu | PUT | /system/menu | PUT | SKELETON | sdkj-system基座提供 |

**迁移说明**: 菜单功能由 `sdkj-system` 基座的 `SysMenuController` 提供。

#### 13. RoleController (角色管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /role/{id} | GET | /thermal/agent/role/list | GET | SKELETON | 新系统用AgRoleController |
| /role | POST | /thermal/agent/role | POST | SKELETON | 新系统用AgRoleController |
| /role | PUT | /thermal/agent/role | PUT | SKELETON | 新系统用AgRoleController |
| /role/{id} | DELETE | /thermal/agent/role/{id} | DELETE | SKELETON | 新系统用AgRoleController |
| /role/list | GET | /thermal/agent/role/list | GET | SKELETON | 新系统用AgRoleController |
| /role/page | GET | /thermal/agent/role/list | GET | SKELETON | 分页方式变更 |
| /role/menu | PUT | /thermal/agent/role/permission | PUT | SKELETON | 权限分配 |

**迁移说明**: 角色功能已迁移到 `AgRoleController`，部分功能由 `sdkj-system` 基座提供。

#### 14. PropertyMenuController (物业菜单管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /propertyMenu/tree | GET | /thermal/agent/property-menu/list | GET | SKELETON | 新系统用AgPropertyMenuController |
| /propertyMenu | POST | - | - | MISSING | 新增菜单未迁移 |
| /propertyMenu/{id} | GET | - | - | MISSING | 详情查询未迁移 |
| /propertyMenu/{id} | DELETE | - | - | MISSING | 删除菜单未迁移 |
| /propertyMenu | PUT | /thermal/agent/property-menu | PUT | SKELETON | 更新菜单 |

**迁移说明**: 部分迁移到 `AgPropertyMenuController`。

#### 15. UserController (用户认证)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /user/info | POST | - | - | MISSING | 由sdkj-admin的登录接口替代 |
| /user/logout | POST | - | - | MISSING | 由Sa-Token的logout替代 |

**迁移说明**: 用户认证功能由 `sdkj-admin` 模块的登录认证和 Sa-Token 框架提供。

#### 16. OssManagerController (OSS管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /stsOss/read | POST | - | - | MISSING | OSS读取未迁移 |
| /stsOss/write | POST | - | - | MISSING | OSS写入未迁移 |
| /stsOss/manager | POST | - | - | MISSING | OSS管理未迁移 |
| /stsOss/getCode | POST | - | - | MISSING | 短信验证码未迁移 |
| /stsOss/uploadFile | POST | /system/oss/upload | POST | SKELETON | sdkj-system基座提供 |
| /stsOss/getUrl | POST | - | - | MISSING | URL获取未迁移 |
| /stsOss/hasFileInOss | POST | - | - | MISSING | 文件检查未迁移 |
| /stsOss/validateTele | POST | - | - | MISSING | 手机号验证未迁移 |
| /stsOss/validateAgentCode | POST | - | - | MISSING | 代理商验证未迁移 |
| /stsOss/registerCompany | POST | - | - | MISSING | 公司注册未迁移 |

**迁移说明**: OSS功能部分由 `sdkj-system` 基座的 `SysOssController` 提供，但OSS Token管理和短信功能未迁移。

#### 17. ToolsController (工具类)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/tools/getCompanyMeter | POST | - | - | MISSING | 仪表厂商未迁移 |
| /property/tools/getMeterList | POST | - | - | MISSING | 仪表列表未迁移 |

**迁移说明**: 工具类功能未迁移。

#### 18. PushController (推送管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/push/setPush | POST | - | - | MISSING | 推送设置未迁移 |

**迁移说明**: 推送功能未迁移。

#### 19. TaskController (定时任务)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/task/list | POST | - | - | MISSING | 任务列表未迁移 |
| /property/task/listPost | POST | - | - | MISSING | POST任务列表未迁移 |
| /property/task/getById/{id} | POST | - | - | MISSING | 任务详情未迁移 |
| /property/task/getPostById/{id} | POST | - | - | MISSING | POST任务详情未迁移 |
| /property/task/edit | POST | - | - | MISSING | 编辑任务未迁移 |
| /property/task/editPost | POST | - | - | MISSING | 编辑POST任务未迁移 |
| /property/task/changeStatus/{id} | POST | - | - | MISSING | 状态变更未迁移 |
| /property/task/changePostStatus/{id} | POST | - | - | MISSING | POST状态变更未迁移 |
| /property/task/remove/{id} | POST | - | - | MISSING | 删除任务未迁移 |
| /property/task/removePost/{id} | POST | - | - | MISSING | 删除POST任务未迁移 |
| /property/task/run/{id} | POST | - | - | MISSING | 运行任务未迁移 |
| /property/task/runPost/{id} | POST | - | - | MISSING | 运行POST任务未迁移 |
| /property/task/runTask/{id} | POST | - | - | MISSING | 运行任务未迁移 |
| /property/task/runPostTask/{id} | POST | - | - | MISSING | 运行POST任务未迁移 |
| /property/task/removeBatch | POST | - | - | MISSING | 批量删除未迁移 |
| /property/task/save | POST | - | - | MISSING | 保存任务未迁移 |
| /property/task/savePost | POST | - | - | MISSING | 保存POST任务未迁移 |
| /property/task/getTaskClass | POST | - | - | MISSING | 任务类未迁移 |
| /property/task/getTaskPostClass | POST | - | - | MISSING | POST任务类未迁移 |
| /property/task/getTaskDetails | POST | - | - | MISSING | 任务详情未迁移 |

**迁移说明**: 定时任务功能未迁移。Quartz 任务应由 `sdkj-job` 模块提供，但未见对应 Controller。

#### 20. AreaController (地区管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /area/province | POST | /thermal/area/provinces | GET | MATCH | 省份列表 |
| /area/city | POST | /thermal/area/cities/{provinceId} | GET | MATCH | 城市列表 |
| /area/county | POST | /thermal/area/districts/{cityId} | GET | MATCH | 区县列表 |

**新系统文件**: `sdkj-thermal/controller/AreaController.java`

---

## 缺失功能汇总

### 微信/支付模块缺失
1. RepairController.itemsdetails - OSS详情转换
2. RepairController.queryCodeList - 查询编码列表

### 系统管理模块缺失
1. SysUserController 的大部分详情和权限管理功能
2. SysCompanyController 的详情相关功能
3. SysColumnController 全部功能
4. SysHomeController 全部功能
5. OssManagerController 的OSS Token和短信功能
6. ToolsController 全部功能
7. PushController 全部功能
8. TaskController 全部功能 (Quartz定时任务)

---

## 建议

1. **微信/支付模块**: 标记为 Phase 6 待实现，骨架已完成，需要集成微信SDK完成支付和授权功能。

2. **系统管理模块**: 大部分功能已被 `sdkj-system` 基座替代，但以下功能需要补充:
   - 定时任务管理 (Quartz)
   - OSS Token 管理
   - 短信验证码
   - 自定义表格列
   - 首页数据统计

3. **代理商管理**: 新系统的 `Ag*Controller` 系列已部分替代旧系统的系统管理功能，但需要验证完整性。

4. **RESTful改造**: 新系统已全面采用RESTful风格，旧系统的POST请求已转换为对应的GET/POST/PUT/DELETE。

---

## 审核人
Agent 4 (自动审核)

## 附件
- 新系统微信模块: `sdkj-thermal/controller/Wechat*.java`, `Wx*.java`
- 新系统代理商模块: `sdkj-thermal/controller/Ag*.java`
- 新系统基座模块: `sdkj-system/controller/system/*.java`
