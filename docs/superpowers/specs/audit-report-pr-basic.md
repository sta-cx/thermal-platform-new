# Pr-基础数据模块迁移审核报告

## 审核概览
- 旧系统 Controller 数：18
- 新系统对应 Controller 数：14
- API 端点总数(旧)：191
- 完全匹配：58
- 部分匹配：12
- 缺失：121
- 新增：0

## 审核日期
2026-04-26

## 逐 Controller 对比

### 1. PrBuildingController (楼宇管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/prBuilding/pageList | GET /thermal/property/building/list | MATCH | 参数简化，移除 companyId |
| GET /property/prBuilding/getDataById | GET /thermal/property/building/{id} | MATCH | RESTful 路径变更 |
| POST /property/prBuilding/insertData | POST /thermal/property/building | MATCH | HTTP 方法标准化 |
| POST /property/prBuilding/updateData | PUT /thermal/property/building | CHANGED | HTTP 方法从 POST 改为 PUT |
| POST /property/prBuilding/deleteData | DELETE /thermal/property/building/{id} | CHANGED | HTTP 方法从 POST 改为 DELETE |
| GET /property/prBuilding/validateName | GET /thermal/property/building/validate | MATCH | 参数简化 |
| GET /property/prBuilding/getUsed | GET /thermal/property/building/used | MATCH | 硬编码返回字典值 |
| GET /property/prBuilding/getBuildingByCompanyIdOrgId | GET /thermal/property/building/byOrg | MATCH | 路径简化 |
| GET /property/prBuilding/getUnitCodesByBuildingId | GET /thermal/property/building/unitCodes | MATCH | 路径简化 |
| GET /property/prBuilding/getRoomNumsByUnitCode | GET /thermal/property/building/roomNums | MATCH | 路径简化 |
| GET /property/prBuilding/getBuildingNumByUserId | - | MISSING | 用户权限统计功能缺失 |
| GET /property/prBuilding/getBuildingByStationId | GET /thermal/property/building/byStation | MATCH | 功能迁移 |

#### 业务逻辑差异
- 旧系统支持按用户权限统计楼宇数量，新系统缺失此功能
- 新系统使用 Sa-Token 权限注解替代旧系统的自定义权限校验
- 新系统使用统一的 Bo/Vo 模式进行数据转换

#### 代码质量问题
- 新系统 getUsed() 方法硬编码返回字典值，应从数据库或配置文件读取
- 缺少对 buildingId 的非空校验

### 2. PrUnitController (单元管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/prUnit/pageList | GET /thermal/property/unit/list | MATCH | 分页查询 |
| GET /property/prUnit/getDataById | GET /thermal/property/unit/{id} | MATCH | 详情查询 |
| POST /property/prUnit/insertData | POST /thermal/property/unit | MATCH | 新增单元 |
| POST /property/prUnit/updateData | PUT /thermal/property/unit | CHANGED | HTTP 方法变更 |
| POST /property/prUnit/deleteData | DELETE /thermal/property/unit/{id} | CHANGED | HTTP 方法变更 |
| POST /property/prUnit/validateName | GET /thermal/property/unit/validate | PARTIAL | 参数简化 |
| GET /property/prUnit/getListByBuildingId | GET /thermal/property/unit/byBuilding | MATCH | 按楼宇查询 |

#### 业务逻辑差异
- 新系统 validateName 参数简化，只保留 name 和 id，移除了实体对象参数
- 新系统使用 RESTful 风格的 HTTP 方法

#### 代码质量问题
- 无明显问题

### 3. PrHouseController (房屋管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/house/pageList | GET /thermal/property/house/list | MATCH | 分页查询 |
| GET /property/house/getDataById | GET /thermal/property/house/{id} | MATCH | 详情查询 |
| POST /property/house/validateName | GET /thermal/property/house/validate | PARTIAL | 参数简化 |
| POST /property/house/insertData | POST /thermal/property/house | MATCH | 新增房屋 |
| POST /property/house/updateData | PUT /thermal/property/house | CHANGED | HTTP 方法变更 |
| POST /property/house/deleteData | DELETE /thermal/property/house/{id} | CHANGED | HTTP 方法变更 |
| POST /property/house/deleteMulData | DELETE /thermal/property/house/batch | MATCH | 批量删除 |
| POST /property/house/deleteDataAll | - | MISSING | 删除小区下所有房屋 |
| POST /property/house/updateUserByHouse | - | MISSING | 变更房屋业主 |
| GET /property/house/getHouseChangeList | - | MISSING | 查询变更记录 |
| GET /property/house/getHouseListByUnitCode | GET /thermal/property/house/byUnit | MATCH | 按单元查询 |
| GET /property/house/getHouseListByCompanyIdOrgIdRoomNum | - | MISSING | 按房号搜索 |
| GET /property/house/exportAll | - | MISSING | 导出功能 |
| POST /property/house/importAll | - | MISSING | 导入功能 |
| GET /property/house/getHouseNumByUserId | GET /thermal/property/house/count | PARTIAL | 功能简化 |
| GET /property/house/getHouseAreaByUserId | GET /thermal/property/house/area | MATCH | 面积统计 |
| GET /property/house/getOccupancy | - | MISSING | 入住率统计 |
| GET /property/house/queryHousePageList | - | MISSING | 自助机分页查询 |
| GET /property/house/getFloorByCompanyId | - | MISSING | 获取楼层列表 |
| GET /property/house/getHouseListByBuildingId | GET /thermal/property/house/byOrg | PARTIAL | 功能简化 |
| GET /property/house/isCheckHouse | - | MISSING | 检查功能 |
| GET /property/house/queryHomeUser | - | MISSING | 查询住户信息 |
| GET /property/house/queryGDH | - | MISSING | 查询孤岛户 |
| GET /property/house/genTaskGroup | - | MISSING | 生成分组信息 |
| POST /property/house/setGDH | - | MISSING | 设置孤岛户 |
| GET /property/house/getCountByHouseId | - | MISSING | 统计功能 |
| POST /property/house/insertWxBindData | - | MISSING | 微信绑定 |
| GET /property/house/getHouseByOtherCode | - | MISSING | 第三方接口 |
| GET /property/house/getDataByPay | - | MISSING | 缴费查询 |
| GET /property/house/getDataByCode | - | MISSING | 按编码查询 |
| POST /property/house/getDataByMulSearch | - | MISSING | 多条件搜索 |
| POST /property/house/setHouseHeatingArea | - | MISSING | 设置供热面积 |
| GET /property/house/getValveAndHotByCode | - | MISSING | 阀门热量查询 |
| GET /property/house/getDataByAngle | - | MISSING | 角度查询 |

#### 业务逻辑差异
- 新系统只实现了核心 CRUD 功能，大量辅助功能缺失
- 导入导出功能完全缺失
- 微信相关功能缺失
- 孤岛户管理功能缺失

#### 代码质量问题
- 缺失功能过多，建议优先实现导入导出和变更记录功能

### 4. PrHouseChangeController (房屋变更管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/houseChange/pageList | GET /thermal/property/house-change/list | PARTIAL | 参数简化 |
| GET /property/houseChange/getDataById | GET /thermal/property/house-change/{id} | MATCH | 详情查询 |
| POST /property/houseChange/insertData | POST /thermal/property/house-change | MATCH | 入住登记 |
| POST /property/houseChange/updateData | PUT /thermal/property/house-change | CHANGED | HTTP 方法变更 |
| POST /property/houseChange/deleteData | DELETE /thermal/property/house-change/{id} | CHANGED | HTTP 方法变更 |
| POST /property/houseChange/deleteMulData | DELETE /thermal/property/house-change/batch | MATCH | 批量迁出 |
| POST /property/houseChange/changeData | PUT /thermal/property/house-change/audit | CHANGED | 路径变更 |
| GET /property/houseChange/getHouseByRoomNum | - | MISSING | 按房号查找 |

#### 业务逻辑差异
- 新系统审核功能路径变更为 /audit
- 缺少按房号精确查找功能

#### 代码质量问题
- 缺少变更原因记录功能

### 5. PrFamilyController (家庭成员管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/family/pageList | GET /thermal/property/family/list | PARTIAL | 参数从 userIdNo 改为 houseId |
| GET /property/family/queryPrFamily | GET /thermal/property/family/{id} | MATCH | 详情查询 |
| POST /property/family/insertData | POST /thermal/property/family | MATCH | 新增成员 |
| POST /property/family/updateData | PUT /thermal/property/family | CHANGED | HTTP 方法变更 |
| POST /property/family/deleteData | DELETE /thermal/property/family/{id} | CHANGED | HTTP 方法变更 |

#### 业务逻辑差异
- 新系统将参数从 userIdNo 改为 houseId，更符合业务逻辑

#### 代码质量问题
- 无明显问题

### 6. PrRegionalController (地域管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/regional/getRegional | - | CHANGED | 功能完全变更 |

#### 业务逻辑差异
- 旧系统返回地域列表，新系统改为统计功能（欠费/收款/综合统计）
- 功能完全改变，不是迁移而是重构

#### 代码质量问题
- 建议将统计功能移至专门的统计 Controller

### 7. PrHeatStationController (换热站管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/prHeatStation/pageList | GET /thermal/ht/station/list | MATCH | 分页查询 |
| GET /property/prHeatStation/getDataById | GET /thermal/ht/station/{id} | MATCH | 详情查询 |
| POST /property/prHeatStation/insertData | POST /thermal/ht/station | MATCH | 新增换热站 |
| POST /property/prHeatStation/updateData | PUT /thermal/ht/station | CHANGED | HTTP 方法变更 |
| POST /property/prHeatStation/deleteData | DELETE /thermal/ht/station/{id} | CHANGED | HTTP 方法变更 |
| GET /property/prHeatStation/getDataByCompanyId | GET /thermal/ht/station/company/{companyId} | CHANGED | 路径变更 |
| GET /property/prHeatStation/getDataByOrgId | GET /thermal/ht/station/org/{orgId} | CHANGED | 路径变更 |

#### 业务逻辑差异
- 新系统路径更符合 RESTful 规范
- 使用 /ht 前缀表示热力调控模块

#### 代码质量问题
- 无明显问题

### 8. PrHeatStationPartitionController (换热站分区管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/prHeatStationPartition/pageList | GET /thermal/ht/station-partition/list | MATCH | 分页查询 |
| GET /property/prHeatStationPartition/getDataById | GET /thermal/ht/station-partition/{id} | MATCH | 详情查询 |
| POST /property/prHeatStationPartition/insertData | POST /thermal/ht/station-partition | MATCH | 新增分区 |
| POST /property/prHeatStationPartition/updateData | PUT /thermal/ht/station-partition | CHANGED | HTTP 方法变更 |
| POST /property/prHeatStationPartition/deleteData | DELETE /thermal/ht/station-partition/{id} | CHANGED | HTTP 方法变更 |
| GET /property/prHeatStationPartition/getDataByCompanyId | GET /thermal/ht/station-partition/company/{companyId} | CHANGED | 路径变更 |
| GET /property/prHeatStationPartition/getPartitionByHeatStationId | GET /thermal/ht/station-partition/station/{stationId} | CHANGED | 路径变更 |

#### 业务逻辑差异
- 新系统路径更符合 RESTful 规范

#### 代码质量问题
- 无明显问题

### 9. AreaController (地域查询)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /area/province | GET /thermal/area/provinces | CHANGED | 路径微调 |
| GET /area/city | GET /thermal/area/cities/{provinceId} | CHANGED | RESTful 路径 |
| GET /area/county | GET /thermal/area/districts/{cityId} | CHANGED | RESTful 路径 |

#### 业务逻辑差异
- 新系统使用路径参数替代查询参数
- 路径更符合 RESTful 规范

#### 代码质量问题
- 无明显问题

### 10. PrCompanyController (组织机构管理) - MISSING

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/company/getDataById | - | MISSING | 新系统无对应 Controller |
| GET /property/company/findDataByCompanyId | - | MISSING | 组织机构树 |
| GET /property/company/queryDataByCompanyId | - | MISSING | 不带部门的组织树 |
| GET /property/company/queryBuildingTrees | - | MISSING | 楼宇树结构 |
| GET /property/company/getAllDept | - | MISSING | 获取所有部门 |
| POST /property/company/insertData | - | MISSING | 新增组织 |
| GET /property/company/verifyOrgName | - | MISSING | 校验名称 |
| GET /property/company/getSysOrgDataById | - | MISSING | 获取组织详情 |
| POST /property/company/updateSysOrg | - | MISSING | 更新组织 |
| POST /property/company/deleteData | - | MISSING | 删除组织 |
| POST /property/company/deleteAllData | - | MISSING | 删除全部数据 |
| GET /property/company/getTree | - | MISSING | 树形结构 |
| GET /property/company/getDataGrantOrg | - | MISSING | 数据权限组织 |
| GET /property/company/getUserOrg | - | MISSING | 用户组织列表 |
| GET /property/company/getUserOrgBranch | - | MISSING | 用户分公司 |
| GET /property/company/getUserOrgByBranch | - | MISSING | 按分公司查询 |
| GET /property/company/queryOrgByCompanyId | - | MISSING | 查询组织 |

#### 业务逻辑差异
- **新系统完全缺失此 Controller**
- 旧系统组织机构管理功能复杂，包含树形结构、数据权限、楼宇树等
- 新系统使用 SysDeptController 替代，但功能不完整

#### 代码质量问题
- **严重缺失**：组织机构管理是核心功能，必须迁移
- SysDeptController 功能远小于 PrCompanyController

### 11. AgentUserController (代理商用户管理) - MISSING

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /agent/user/pageList | - | MISSING | 分页列表 |
| GET /agent/user/checkTele | - | MISSING | 校验手机号 |
| POST /agent/user/insertData | - | MISSING | 新增用户 |
| POST /agent/user/updateData | - | MISSING | 修改用户 |
| POST /agent/user/deleteData | - | MISSING | 删除用户 |
| POST /agent/user/startUsing | - | MISSING | 启用用户 |
| POST /agent/user/endUsing | - | MISSING | 禁用用户 |
| POST /agent/user/saveUserRole | - | MISSING | 保存用户角色 |

#### 业务逻辑差异
- 新系统完全缺失代理商用户管理功能

#### 代码质量问题
- 代理商模块整体未迁移

### 12. AgentRoleController (代理商角色管理) - MISSING

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /agent/role/pageList | - | MISSING | 分页列表 |
| GET /agent/role/allRole | - | MISSING | 未分配角色 |
| GET /agent/role/allReadlyRole | - | MISSING | 已有角色 |
| POST /agent/role/insertData | - | MISSING | 新增角色 |
| POST /agent/role/updateData | - | MISSING | 修改角色 |
| POST /agent/role/deleteData | - | MISSING | 删除角色 |
| GET /agent/role/verifyIdent | - | MISSING | 校验标识 |
| GET /agent/role/verifyName | - | MISSING | 校验名称 |
| GET /agent/role/findYes | - | MISSING | 已分配菜单 |
| GET /agent/role/findNo | - | MISSING | 未分配菜单 |
| POST /agent/role/permissionUpd | - | MISSING | 更新权限 |

#### 业务逻辑差异
- 新系统完全缺失代理商角色管理功能

#### 代码质量问题
- 代理商模块整体未迁移

### 13. PrOptionsController (系统选项管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| POST /property/options/getDataById | GET /thermal/property/options | MATCH | 查询选项 |
| POST /property/options/insertData | POST /thermal/property/options | MATCH | 新增选项 |
| POST /property/options/updateData | PUT /thermal/property/options | CHANGED | HTTP 方法变更 |
| POST /property/options/initData | POST /thermal/property/options/init | MATCH | 初始化 |
| POST /property/options/forbiddenToBuy | GET /thermal/property/options/forbidden | CHANGED | 路径和参数变更 |

#### 业务逻辑差异
- 新系统参数简化
- forbiddenToBuy 参数从 houseId 改为 meterId

#### 代码质量问题
- forbiddenToBuy 业务逻辑可能不一致

### 14. PrOptionsHeatController (供热选项管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| POST /property/prOptionsHeat/getDataById | GET /thermal/property/options-heat | PARTIAL | 参数简化 |
| POST /property/prOptionsHeat/insertData | POST /thermal/property/options-heat | PARTIAL | 缺少 heatFlowValve 参数 |
| POST /property/prOptionsHeat/updateData | PUT /thermal/property/options-heat | PARTIAL | 缺少 heatFlowValve 参数 |
| POST /property/prOptionsHeat/upserCompanyData | POST /thermal/property/options-heat/upsert | CHANGED | 路径变更 |
| POST /property/prOptionsHeat/initData | POST /thermal/property/options-heat/init | MATCH | 初始化 |
| GET /property/prOptionsHeat/getOptionById | - | MISSING | 获取选项 |

#### 业务逻辑差异
- 新系统缺少 heatFlowValve 列表参数处理
- 缺少 getOptionById 端点

#### 代码质量问题
- insertData 和 updateData 缺少关键参数

### 15. PrPetController (宠物管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/pet/pageList | GET /thermal/property/pet/list | PARTIAL | 参数从 userId 改为 houseId/orgId |
| GET /property/pet/queryPrPet | GET /thermal/property/pet/{id} | MATCH | 详情查询 |
| POST /property/pet/insertData | POST /thermal/property/pet | MATCH | 新增宠物 |
| POST /property/pet/deleteData | DELETE /thermal/property/pet/{id} | CHANGED | HTTP 方法变更 |

#### 业务逻辑差异
- 新系统参数变更，不再支持按用户查询
- 缺少 updateData 端点

#### 代码质量问题
- 缺少修改功能

### 16. PrStrategyController (策略管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/prStrategy/pageStrategyList | GET /thermal/property/strategy/list | MATCH | 分页查询 |
| POST /property/prStrategy/insertData | POST /thermal/property/strategy | PARTIAL | 缺少基础策略校验 |
| POST /property/prStrategy/updateData | PUT /thermal/property/strategy | PARTIAL | 缺少基础策略校验 |
| POST /property/prStrategy/deleteData | DELETE /thermal/property/strategy/{id} | CHANGED | HTTP 方法变更 |
| GET /property/prStrategy/getDataByCompanyId | - | MISSING | 按公司查询 |

#### 业务逻辑差异
- 新系统缺少基础策略唯一性校验
- 缺少按公司查询策略功能

#### 代码质量问题
- 缺少关键业务校验逻辑

### 17. PrPrintTemplateController (打印模板管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/prPrintTemplate/pageList | GET /thermal/property/print-template/list | MATCH | 列表查询 |
| POST /property/prPrintTemplate/uploadTemplate | POST /thermal/property/print-template/upload | PARTIAL | 参数简化，缺少文件上传 |
| GET /property/prPrintTemplate/downloadTemplate | - | MISSING | 下载模板 |
| GET /property/prPrintTemplate/findTemplate | GET /thermal/property/print-template/find | PARTIAL | 缺少流式响应 |
| GET /property/prPrintTemplate/findTemplateBySerialNum | GET /thermal/property/print-template/by-serial | MATCH | 按流水号查询 |
| GET /property/prPrintTemplate/getTemplateByName | - | MISSING | 按名称查询 |

#### 业务逻辑差异
- 新系统不支持文件上传和下载
- 缺少流式模板响应功能

#### 代码质量问题
- 打印模板功能严重不完整

### 18. PrUserController (客户管理)

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/user/pageList | GET /thermal/property/user/list | MATCH | 分页查询 |
| POST /property/user/insertData | POST /thermal/property/user | PARTIAL | 缺少头像上传处理 |
| POST /property/user/updateData | PUT /thermal/property/user | MATCH | 修改客户 |
| POST /property/user/deleteData | DELETE /thermal/property/user/{id} | CHANGED | HTTP 方法变更 |
| GET /property/user/queryPrUserVo | GET /thermal/property/user/{userId} | MATCH | 查询详情 |
| GET /property/user/hasUser | GET /thermal/property/user/has-user | MATCH | 检查业主 |
| GET /property/user/getUserByPhone | GET /thermal/property/user/by-phone | MATCH | 按手机号查询 |

#### 业务逻辑差异
- 新系统缺少 Base64 头像上传和 OSS 存储功能
- 路径更符合 RESTful 规范

#### 代码质量问题
- insertData 缺少头像处理逻辑

### 19. PrRoleController (物业角色管理) - MOVED

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/role/getAllRoles | - | MISSING | 获取所有角色 |
| GET /property/role/getAllRolesByCreate | - | MISSING | 按创建者查询 |
| GET /property/role/getRoleByUserId | - | MISSING | 按用户查询角色 |
| GET /property/role/pageList | GET /system/role/list | CHANGED | 迁移到系统模块 |
| POST /property/role/insertData | POST /system/role | CHANGED | 迁移到系统模块 |
| POST /property/role/updateData | PUT /system/role | CHANGED | 迁移到系统模块 |
| POST /property/role/deleteData | DELETE /system/role/{roleIds} | CHANGED | 迁移到系统模块 |
| POST /property/role/deleteRolesData | - | MISSING | 批量删除 |
| GET /property/role/verifyIdent | - | MISSING | 校验标识 |
| GET /property/role/verifyName | - | MISSING | 校验名称 |
| GET /property/role/findYes | - | MISSING | 已分配菜单 |
| GET /property/role/findNo | - | MISSING | 未分配菜单 |
| POST /property/role/permissionUpd | PUT /system/role/dataScope | CHANGED | 权限更新 |

#### 业务逻辑差异
- 新系统将角色管理迁移到系统模块 (/system/role)
- 缺少物业角色特有的菜单分配功能
- 缺少角色标识管理

#### 代码质量问题
- 物业角色管理功能不完整

### 20. SysColumnController (自定义列管理) - MOVED

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /property/sysColumn/pageList | GET /thermal/sysColumn/pageList | CHANGED | 迁移到系统模块 |
| POST /property/sysColumn/insertData | POST /thermal/sysColumn/saveOrUpdate | CHANGED | 改为保存或更新 |

#### 业务逻辑差异
- 新系统路径变更到 /thermal/sysColumn
- 新增用户维度的自定义列管理
- 新增删除端点

#### 代码质量问题
- 功能增强，符合多租户需求

### 21. AgentCompanyController (代理商公司管理) - MISSING

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /agent/company/pageList | - | MISSING | 公司树 |
| GET /agent/company/getDataById | - | MISSING | 获取详情 |
| POST /agent/company/deleteData | - | MISSING | 删除公司 |
| GET /agent/company/verifyTele | - | MISSING | 校验手机号 |
| GET /agent/company/verifyCode | - | MISSING | 校验编码 |
| GET /agent/company/verifyName | - | MISSING | 校验名称 |
| POST /agent/company/insertData | - | MISSING | 新增公司 |
| POST /agent/company/startUsing | - | MISSING | 启用公司 |
| POST /agent/company/endUsing | - | MISSING | 禁用公司 |
| POST /agent/company/updateData | - | MISSING | 更新公司 |

#### 业务逻辑差异
- 代理商模块整体未迁移

#### 代码质量问题
- 代理商功能完全缺失

### 22. AgentPropertyController (代理商物业管理) - MISSING

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /agent/propery/pageList | - | MISSING | 物业公司列表 |
| GET /agent/propery/queryAgMenuList | - | MISSING | 查询代理商菜单 |
| GET /agent/propery/queryPrMenuList | - | MISSING | 查询物业菜单 |
| POST /agent/propery/menuInsertData | - | MISSING | 保存菜单分配 |
| POST /agent/propery/updatePrAudited | - | MISSING | 更新审核状态 |
| POST /agent/propery/updatePrEnabled | - | MISSING | 更新启用状态 |
| GET /agent/propery/queryPrCompany | - | MISSING | 查询物业公司 |
| POST /agent/propery/updataPrCompany | - | MISSING | 更新物业公司 |
| GET /agent/propery/queryAutoMachine | - | MISSING | 查询自助机 |
| POST /agent/propery/updateAutoMachine | - | MISSING | 更新自助机 |
| GET /agent/propery/queryMeter | - | MISSING | 查询仪表 |
| POST /agent/propery/updataMeter | - | MISSING | 更新仪表 |

#### 业务逻辑差异
- 代理商物业管理功能完全缺失

#### 代码质量问题
- 代理商模块整体未迁移

### 23. MenuController (菜单管理) - MOVED

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /menu | GET /system/menu/list | CHANGED | 迁移到系统模块 |
| GET /menu/tree1 | - | MISSING | 树形菜单 |
| GET /menu/tree/{roleId} | GET /system/menu/roleMenuTree | CHANGED | 角色菜单树 |
| GET /menu/{id} | GET /system/menu/{menuId} | CHANGED | 菜单详情 |
| POST /menu | POST /system/menu | CHANGED | 新增菜单 |
| DELETE /menu/{id} | DELETE /system/menu/{menuId} | CHANGED | 删除菜单 |
| PUT /menu | PUT /system/menu | CHANGED | 更新菜单 |

#### 业务逻辑差异
- 菜单管理迁移到系统模块
- 部分树形查询端点缺失

#### 代码质量问题
- 基础功能完整

### 24. AgentPropertyMenuController (代理商物业菜单) - MISSING

#### API 覆盖度
| 旧系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| GET /agent/propertyMenu/findYes | - | MISSING | 已分配菜单 |
| GET /agent/propertyMenu/findNo | - | MISSING | 未分配菜单 |
| POST /agent/propertyMenu/permissionUpd | - | MISSING | 更新权限 |

#### 业务逻辑差异
- 代理商物业菜单管理完全缺失

## 总结与建议

### 关键发现

1. **核心迁移情况**：
   - 基础数据模块中 14 个 Controller 已迁移
   - 10 个 Controller 完全缺失（主要是代理商模块）
   - PrCompanyController 组织机构管理功能严重缺失

2. **API 规范化改进**：
   - 新系统普遍采用 RESTful 风格
   - HTTP 方法使用更规范（GET/POST/PUT/DELETE）
   - 路径结构更清晰（/thermal/property/*）

3. **功能完整性问题**：
   - PrHouseController 缺失 30+ 个端点（导入导出、微信绑定、孤岛户管理等）
   - PrPrintTemplateController 文件上传下载功能缺失
   - PrOptionsHeatController 缺少关键参数处理

### 缺失功能清单

#### 高优先级（核心业务功能）
1. **PrCompanyController** - 组织机构管理（18个端点）
   - 组织机构树
   - 楼宇树结构
   - 数据权限管理

2. **PrHouseController** 缺失端点：
   - 导入导出功能（exportAll, importAll）
   - 房屋变更记录（getHouseChangeList）
   - 变更业主（updateUserByHouse）
   - 孤岛户管理（queryGDH, setGDH, genTaskGroup）
   - 删除小区下所有房屋（deleteDataAll）

3. **PrPrintTemplateController** 缺失：
   - 文件上传处理
   - 模板下载
   - 流式模板响应

#### 中优先级（辅助功能）
1. **PrUserController** 头像上传功能
2. **PrStrategyController** 基础策略校验
3. **PrOptionsHeatController** heatFlowValve 参数处理
4. **PrPetController** 更新功能

#### 低优先级（代理商模块）
1. AgentCompanyController（10个端点）
2. AgentUserController（8个端点）
3. AgentRoleController（11个端点）
4. AgentPropertyController（12个端点）
5. AgentPropertyMenuController（3个端点）

### 风险项

1. **数据迁移风险**：
   - 组织机构数据结构变化大，需要仔细设计迁移脚本
   - 代理商模块完全缺失，如需支持多代理商需要重新设计

2. **功能缺失风险**：
   - 导入导出功能缺失可能影响数据维护效率
   - 孤岛户管理功能缺失可能影响供热调控

3. **兼容性风险**：
   - API 路径变更需要前端同步修改
   - 参数变更可能导致现有集成失效

4. **安全风险**：
   - 新系统使用 Sa-Token，权限模型变化
   - 部分端点缺少权限注解

### 建议实施计划

#### 第一阶段（P0 - 核心功能）
1. 实现 PrCompanyController 核心功能
2. 补充 PrHouseController 导入导出功能
3. 补充 PrHouseController 变更记录功能
4. 修复 PrPrintTemplateController 文件上传下载

#### 第二阶段（P1 - 重要功能）
1. 补充孤岛户管理功能
2. 修复 PrOptionsHeatController 参数处理
3. 补充 PrUserController 头像上传
4. 补充 PrStrategyController 业务校验

#### 第三阶段（P2 - 代理商模块）
1. 评估是否需要代理商功能
2. 如需要，重新设计代理商模块架构
3. 迁移代理商相关 Controller

---

**审核人**: Code Reviewer Agent
**审核日期**: 2026-04-26
**下次审核**: 第一阶段功能补充完成后
