# Pr-基础数据模块迁移审核报告

## 审核概览
- **审核日期**: 2026-04-26
- **旧系统路径**: D:\chonggou\thermal-balance-backend
- **新系统路径**: D:\chonggou\thermal-platform-new
- **旧系统 Controller 数**: 16
- **新系统对应 Controller 数**: 14
- **完全匹配**: 8
- **部分匹配**: 5
- **缺失**: 3
- **未迁移**: 2

## 审核范围

本次审核涵盖以下 Controller：
1. PrBuilding - 楼宇管理
2. PrUnit - 单元管理
3. PrHouse - 房屋管理
4. PrHouseChange - 房屋入住/迁出
5. PrFamily - 家庭成员管理
6. PrRegional - 地域管理
7. PrHeatStation - 换热站管理
8. PrHeatStationPartition - 换热站分区管理
9. Area - 地区管理
10. PrCompany - 组织机构管理
11. PrUser - 客户管理
12. PrRole - 角色管理
13. PrOptions - 系统选项管理
14. PrOptionsHeat - 供热选项管理
15. PrPet - 宠物管理
16. PrStrategy - 策略管理
17. PrPrintTemplate - 打印模板管理
18. SysColumn - 自定义列管理

---

## 逐 Controller 对比

### 1. PrBuildingController - 楼宇管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prBuilding/pageList | POST | /thermal/property/building/list | GET | MATCH | 新系统使用RESTful风格 |
| /property/prBuilding/getDataById | POST | /thermal/property/building/{id} | GET | MATCH | 新系统使用路径参数 |
| /property/prBuilding/insertData | POST | /thermal/property/building | POST | MATCH | 符合REST规范 |
| /property/prBuilding/updateData | POST | /thermal/property/building | PUT | MATCH | 新系统使用PUT方法 |
| /property/prBuilding/deleteData | POST | /thermal/property/building/{id} | DELETE | MATCH | 符合REST规范 |
| /property/prBuilding/validateName | POST | /thermal/property/building/validate | GET | MATCH | 新系统使用GET方法 |
| /property/prBuilding/getUsed | POST | /thermal/property/building/used | GET | MATCH | 获取楼宇用途字典 |
| /property/prBuilding/getBuildingByCompanyIdOrgId | POST | /thermal/property/building/byOrg | GET | MATCH | 根据小区查询楼宇 |
| /property/prBuilding/getUnitCodesByBuildingId | POST | /thermal/property/building/unitCodes | GET | MATCH | 获取单元编码列表 |
| /property/prBuilding/getRoomNumsByUnitCode | POST | /thermal/property/building/roomNums | GET | MATCH | 获取房间号列表 |
| /property/prBuilding/getBuildingNumByUserId | POST | - | - | MISSING | **缺失**：根据用户获取楼宇数量 |
| /property/prBuilding/getBuildingByStationId | POST | /thermal/property/building/byStation | GET | MATCH | 根据热力站查询楼宇 |

#### 业务逻辑差异
- 新系统增加了 Sa-Token 权限注解（@SaCheckPermission）
- 新系统增加了操作日志注解（@Log）
- 新系统使用 BO/VO 模式进行数据转换
- 新系统移除了 companyId 参数（从 Sa-Token 上下文获取）

#### 代码质量问题
- **安全改进**: 新系统使用 @SaCheckPermission 替代旧系统的隐式权限检查
- **参数验证**: 新系统使用 @Validated 注解进行参数校验

---

### 2. PrUnitController - 单元管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prUnit/pageList | POST | /thermal/property/unit/list | GET | MATCH | 新系统使用RESTful风格 |
| /property/prUnit/getDataById | POST | /thermal/property/unit/{id} | GET | MATCH | |
| /property/prUnit/insertData | POST | /thermal/property/unit | POST | MATCH | |
| /property/prUnit/updateData | POST | /thermal/property/unit | PUT | MATCH | |
| /property/prUnit/deleteData | POST | /thermal/property/unit/{id} | DELETE | MATCH | |
| /property/prUnit/validateName | POST | /thermal/property/unit/validate | GET | MATCH | |
| /property/prUnit/getListByBuildingId | POST | /thermal/property/unit/byBuilding | GET | MATCH | 根据楼宇查询单元列表 |

#### 业务逻辑差异
- 参数简化：新系统移除了 companyId 和 orgId 参数（从上下文获取）
- 新系统使用 PrUnitVo 进行返回数据封装

#### 代码质量问题
- 无明显问题

---

### 3. PrHouseController - 房屋管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/house/pageList | POST | /thermal/property/house/list | GET | MATCH | |
| /property/house/getDataById | POST | /thermal/property/house/{id} | GET | MATCH | |
| /property/house/validateName | POST | /thermal/property/house/validate | GET | MATCH | |
| /property/house/validateBuildingName | POST | - | - | MISSING | **缺失**：楼宇名称校验 |
| /property/house/insertData | POST | /thermal/property/house | POST | MATCH | |
| /property/house/updateData | POST | /thermal/property/house | PUT | MATCH | |
| /property/house/deleteData | POST | /thermal/property/house/{id} | DELETE | MATCH | |
| /property/house/deleteMulData | POST | /thermal/property/house/batch | DELETE | MATCH | 批量删除 |
| /property/house/deleteDataAll | POST | - | - | MISSING | **缺失**：删除小区下所有房屋 |
| /property/house/updateUserByHouse | POST | - | - | MISSING | **缺失**：变更房屋业主 |
| /property/house/getHouseChangeList | POST | - | - | MISSING | **缺失**：查询房屋变更记录 |
| /property/house/getHouseListByUnitCode | POST | /thermal/property/house/byUnit | GET | MATCH | |
| /property/house/getHouseListByCompanyIdOrgIdRoomNum | POST | - | - | MISSING | **缺失**：根据房号搜索 |
| /property/house/exportAll | POST | - | - | MISSING | **缺失**：房屋信息导出 |
| /property/house/importAll | POST | - | - | MISSING | **缺失**：房屋信息导入 |
| /property/house/getHouseNumByUserId | POST | /thermal/property/house/count | GET | MATCH | |
| /property/house/getHouseAreaByUserId | POST | /thermal/property/house/area | GET | MATCH | |
| /property/house/getOccupancy | POST | - | - | MISSING | **缺失**：获取入住率 |
| /property/house/queryGDH | POST | - | - | MISSING | **缺失**：查询孤岛户 |
| /property/house/genTaskGroup | POST | - | - | MISSING | **缺失**：生成分组信息 |
| /property/house/setGDH | POST | - | - | MISSING | **缺失**：设置孤岛户 |
| /property/house/getCountByHouseId | POST | - | - | MISSING | **缺失**：获取房屋计数 |
| /property/house/insertWxBindData | POST | - | - | MISSING | **缺失**：微信绑定数据 |
| /property/house/getHouseByOtherCode | POST | - | - | MISSING | **缺失**：根据缴费码查询 |
| /property/house/getDataByPay | POST | - | - | MISSING | **缺失**：缴费数据查询 |
| /property/house/getDataByCode | POST | - | - | MISSING | **缺失**：根据编码查询 |
| /property/house/getDataByMulSearch | POST | - | - | MISSING | **缺失**：多条件搜索 |
| /property/house/setHouseHeatingArea | POST | - | - | MISSING | **缺失**：设置供热面积 |
| /property/house/getValveAndHotByCode | POST | - | - | MISSING | **缺失**：阀门和热表查询 |
| /property/house/getDataByAngle | POST | - | - | MISSING | **缺失**：角度数据查询 |

#### 业务逻辑差异
- **重大功能缺失**：新系统缺失大量导入导出、孤岛户管理、微信集成等功能
- 新系统仅保留基础 CRUD 操作

#### 代码质量问题
- **功能缺失风险**：缺失导入导出功能可能导致数据迁移困难
- **集成缺失**：缺少微信相关端点，需要确认是否移至其他模块

---

### 4. PrHouseChangeController - 房屋入住/迁出

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/houseChange/pageList | POST | /thermal/property/house-change/list | GET | MATCH | |
| /property/houseChange/getDataById | POST | /thermal/property/house-change/{id} | GET | MATCH | |
| /property/houseChange/insertData | POST | /thermal/property/house-change | POST | MATCH | |
| /property/houseChange/updateData | POST | /thermal/property/house-change | PUT | MATCH | |
| /property/houseChange/deleteData | POST | /thermal/property/house-change/{id} | DELETE | MATCH | |
| /property/houseChange/deleteMulData | POST | /thermal/property/house-change/batch | DELETE | MATCH | |
| /property/houseChange/changeData | POST | /thermal/property/house-change/audit | PUT | MATCH | 审核功能 |
| /property/houseChange/getHouseByRoomNum | POST | - | - | MISSING | **缺失**：根据房号查找 |

#### 业务逻辑差异
- 新系统增加了审核端点（/audit）
- 参数简化：移除了 companyId 参数

#### 代码质量问题
- 无明显问题

---

### 5. PrFamilyController - 家庭成员管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/family/pageList | POST | /thermal/property/family/list | GET | MATCH | |
| /property/family/insertData | POST | /thermal/property/family | POST | MATCH | |
| /property/family/queryPrFamily | POST | /thermal/property/family/{id} | GET | MATCH | |
| /property/family/updateData | POST | /thermal/property/family | PUT | MATCH | |
| /property/family/deleteData | POST | /thermal/property/family/{id} | DELETE | MATCH | |

#### 业务逻辑差异
- 参数变更：旧系统使用 userIdNo，新系统使用 houseId

#### 代码质量问题
- 无明显问题

---

### 6. PrRegionalController - 地域管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/regional/getRegional | POST | - | - | MISSING | **完全重构** |
| - | - | /thermal/property/regional/stats | GET | EXTRA | 新增统计功能 |
| - | - | /thermal/property/regional/daily | GET | EXTRA | 新增日报功能 |

#### 业务逻辑差异
- **重大变更**：新系统完全重构了此模块，从简单数据查询改为统计分析功能
- 旧系统返回地域列表，新系统返回欠费、收款等统计数据

#### 代码质量问题
- 需要确认业务需求是否要求保留原功能

---

### 7. PrHeatStationController - 换热站管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatStation/pageList | POST | /thermal/ht/station/list | GET | MATCH | |
| /property/prHeatStation/getDataById | POST | /thermal/ht/station/{id} | GET | MATCH | |
| /property/prHeatStation/insertData | POST | /thermal/ht/station | POST | MATCH | |
| /property/prHeatStation/updateData | POST | /thermal/ht/station | PUT | MATCH | |
| /property/prHeatStation/deleteData | POST | /thermal/ht/station/{id} | DELETE | MATCH | |
| /property/prHeatStation/getDataByCompanyId | POST | /thermal/ht/station/company/{companyId} | GET | MATCH | |
| /property/prHeatStation/getDataByOrgId | POST | /thermal/ht/station/org/{orgId} | GET | MATCH | |

#### 业务逻辑差异
- 路径变更：从 /property/prHeatStation 改为 /thermal/ht/station
- 新系统增加了 seq 字段排序

#### 代码质量问题
- 无明显问题

---

### 8. PrHeatStationPartitionController - 换热站分区管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatStationPartition/pageList | POST | /thermal/ht/station-partition/list | GET | MATCH | |
| /property/prHeatStationPartition/getDataById | POST | /thermal/ht/station-partition/{id} | GET | MATCH | |
| /property/prHeatStationPartition/insertData | POST | /thermal/ht/station-partition | POST | MATCH | |
| /property/prHeatStationPartition/updateData | POST | /thermal/ht/station-partition | PUT | MATCH | |
| /property/prHeatStationPartition/deleteData | POST | /thermal/ht/station-partition/{id} | DELETE | MATCH | |
| /property/prHeatStationPartition/getDataByCompanyId | POST | /thermal/ht/station-partition/company/{companyId} | GET | MATCH | |
| /property/prHeatStationPartition/getPartitionByHeatStationId | POST | /thermal/ht/station-partition/station/{stationId} | GET | MATCH | |

#### 业务逻辑差异
- 路径变更符合 RESTful 规范

#### 代码质量问题
- 无明显问题

---

### 9. AreaController - 地区管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /area/province | GET | /thermal/area/provinces | GET | MATCH | |
| /area/city | GET | /thermal/area/cities/{provinceId} | GET | MATCH | 新系统使用路径参数 |
| /area/county | GET | /thermal/area/districts/{cityId} | GET | MATCH | 新系统使用路径参数 |

#### 业务逻辑差异
- 新系统使用路径参数替代查询参数，更符合 RESTful 规范

#### 代码质量问题
- 无明显问题

---

### 10. PrCompanyController - 组织机构管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/company/getDataById | POST | - | - | MISSING | **未迁移** |
| /property/company/findDataByCompanyId | POST | - | - | MISSING | **未迁移** |
| /property/company/queryDataByCompanyId | POST | - | - | MISSING | **未迁移** |
| /property/company/queryBuildingTrees | POST | - | - | MISSING | **未迁移** |
| /property/company/getAllDept | POST | - | - | MISSING | **未迁移** |
| /property/company/insertData | POST | - | - | MISSING | **未迁移** |
| /property/company/verifyOrgName | POST | - | - | MISSING | **未迁移** |
| /property/company/getSysOrgDataById | POST | - | - | MISSING | **未迁移** |
| /property/company/updateSysOrg | POST | - | - | MISSING | **未迁移** |
| /property/company/deleteData | POST | - | - | MISSING | **未迁移** |
| /property/company/deleteAllData | POST | - | - | MISSING | **未迁移** |
| /property/company/getTree | POST | - | - | MISSING | **未迁移** |
| /property/company/getDataGrantOrg | POST | - | - | MISSING | **未迁移** |
| /property/company/getUserOrg | POST | - | - | MISSING | **未迁移** |
| /property/company/getUserOrgBranch | POST | - | - | MISSING | **未迁移** |
| /property/company/getUserOrgByBranch | POST | - | - | MISSING | **未迁移** |
| /property/company/queryOrgByCompanyId | POST | - | - | MISSING | **未迁移** |

#### 业务逻辑差异
- **完全未迁移**：新系统中没有找到 PrCompanyController
- 组织机构管理可能由 sdkj-system 模块的 SysOrganizationController 处理

#### 代码质量问题
- **迁移缺失**：需要确认组织机构管理功能是否已迁移到系统模块

---

### 11. PrUserController - 客户管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/user/pageList | POST | /thermal/property/user/list | GET | MATCH | |
| /property/user/insertData | POST | /thermal/property/user | POST | MATCH | |
| /property/user/updateData | POST | /thermal/property/user | PUT | MATCH | |
| /property/user/deleteData | POST | /thermal/property/user/{id} | DELETE | MATCH | |
| /property/user/queryPrUserVo | POST | /thermal/property/user/{userId} | GET | MATCH | |
| /property/user/hasUser | POST | /thermal/property/user/has-user | GET | MATCH | |
| /property/user/getUserByPhone | POST | /thermal/property/user/by-phone | GET | MATCH | |

#### 业务逻辑差异
- 新系统移除了头像上传功能（旧系统有 OSS 文件上传逻辑）
- 新系统简化了参数结构

#### 代码质量问题
- **功能简化**：新系统未包含头像上传功能，需要确认是否需要保留

---

### 12. PrRoleController - 角色管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/role/getAllRoles | POST | - | - | MISSING | **未迁移** |
| /property/role/getAllRolesByCreate | POST | - | - | MISSING | **未迁移** |
| /property/role/getRoleByUserId | POST | - | - | MISSING | **未迁移** |
| /property/role/pageList | POST | - | - | MISSING | **未迁移** |
| /property/role/insertData | POST | - | - | MISSING | **未迁移** |
| /property/role/updateData | POST | - | - | MISSING | **未迁移** |
| /property/role/deleteData | POST | - | - | MISSING | **未迁移** |
| /property/role/deleteRolesData | POST | - | - | MISSING | **未迁移** |
| /property/role/verifyIdent | POST | - | - | MISSING | **未迁移** |
| /property/role/verifyName | POST | - | - | MISSING | **未迁移** |
| /property/role/findYes | POST | - | - | MISSING | **未迁移** |
| /property/role/findNo | POST | - | - | MISSING | **未迁移** |
| /property/role/permissionUpd | POST | - | - | MISSING | **未迁移** |

#### 业务逻辑差异
- **完全未迁移**：新系统 thermal 模块中没有 PrRoleController
- 角色管理功能应由 sdkj-system 模块的 SysRoleController 处理

#### 代码质量问题
- 需要确认角色管理是否完全由系统模块处理

---

### 13. PrOptionsController - 系统选项管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/options/getDataById | POST | /thermal/property/options | GET | MATCH | |
| /property/options/insertData | POST | /thermal/property/options | POST | MATCH | |
| /property/options/updateData | POST | /thermal/property/options | PUT | MATCH | |
| /property/options/initData | POST | /thermal/property/options/init | POST | MATCH | |
| /property/options/forbiddenToBuy | POST | /thermal/property/options/forbidden | GET | PARTIAL | 参数变更 |

#### 业务逻辑差异
- forbiddenToBuy 方法参数从 houseId 改为 meterId
- 新系统简化了禁购检查逻辑

#### 代码质量问题
- **参数变更**：需要确认前端是否已同步修改

---

### 14. PrOptionsHeatController - 供热选项管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prOptionsHeat/getDataById | POST | /thermal/property/options-heat | GET | MATCH | |
| /property/prOptionsHeat/insertData | POST | /thermal/property/options-heat | POST | MATCH | |
| /property/prOptionsHeat/updateData | POST | /thermal/property/options-heat | PUT | MATCH | |
| /property/prOptionsHeat/upserCompanyData | POST | /thermal/property/options-heat/upsert | POST | MATCH | |
| /property/prOptionsHeat/initData | POST | /thermal/property/options-heat/init | POST | MATCH | |
| /property/prOptionsHeat/getOptionById | POST | - | - | MISSING | **缺失**：根据ID获取选项 |

#### 业务逻辑差异
- 缺少 getOptionById 端点

#### 代码质量问题
- 无明显问题

---

### 15. PrPetController - 宠物管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/pet/pageList | POST | /thermal/property/pet/list | GET | MATCH | |
| /property/pet/insertData | POST | /thermal/property/pet | POST | MATCH | |
| /property/pet/deleteData | POST | /thermal/property/pet/{id} | DELETE | MATCH | |
| /property/pet/queryPrPet | POST | /thermal/property/pet/{id} | GET | MATCH | |

#### 业务逻辑差异
- 新系统增加了 update 方法（旧系统缺失）
- 参数变更：从 userId 改为 houseId/orgId

#### 代码质量问题
- 无明显问题

---

### 16. PrStrategyController - 策略管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prStrategy/pageStrategyList | POST | /thermal/property/strategy/list | GET | MATCH | |
| /property/prStrategy/insertData | POST | /thermal/property/strategy | POST | MATCH | |
| /property/prStrategy/updateData | POST | /thermal/property/strategy | PUT | MATCH | |
| /property/prStrategy/deleteData | POST | /thermal/property/strategy/{id} | DELETE | MATCH | |
| /property/prStrategy/getDataByCompanyId | POST | - | - | MISSING | **缺失**：根据公司获取策略 |

#### 业务逻辑差异
- 新系统移除了基础策略唯一性检查（旧系统有 level==0 检查）

#### 代码质量问题
- 无明显问题

---

### 17. PrPrintTemplateController - 打印模板管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prPrintTemplate/pageList | POST | /thermal/property/print-template/list | GET | MATCH | |
| /property/prPrintTemplate/uploadTemplate | POST | /thermal/property/print-template/upload | POST | MATCH | |
| /property/prPrintTemplate/downloadTemplate | POST | - | - | MISSING | **缺失**：模板下载 |
| /property/prPrintTemplate/findTemplate | POST | /thermal/property/print-template/find | GET | MATCH | |
| /property/prPrintTemplate/findTemplateBySerialNum | POST | /thermal/property/print-template/by-serial | GET | MATCH | |
| /property/prPrintTemplate/getTemplateByName | POST | - | - | MISSING | **缺失**：根据名称获取模板 |

#### 业务逻辑差异
- 新系统简化了模板存储方式（从 OSS 文件改为数据库存储）
- 缺少模板下载功能

#### 代码质量问题
- **功能缺失**：缺少模板下载功能

---

### 18. SysColumnController - 自定义列管理

#### API 端点覆盖度

| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/sysColumn/pageList | POST | /thermal/sysColumn/pageList | GET | MATCH | |
| /property/sysColumn/insertData | POST | /thermal/sysColumn/saveOrUpdate | POST | MATCH | 合并为新增/更新 |
| - | - | /thermal/sysColumn/{id} | DELETE | EXTRA | 新增删除功能 |

#### 业务逻辑差异
- 新系统增加了用户维度隔离（通过 LoginHelper.getUserId()）
- 新系统合并了 insertData 为 saveOrUpdate

#### 代码质量问题
- **安全改进**：增加了用户隔离，防止跨用户访问

---

## 总结与建议

### 关键发现

#### 1. 完全未迁移的 Controller
- **PrCompanyController**: 组织机构管理（17个端点完全缺失）
- **PrRoleController**: 角色管理（13个端点完全缺失）

这两个 Controller 的功能可能已迁移到 sdkj-system 模块，但需要验证功能完整性。

#### 2. 严重功能缺失的 Controller

**PrHouseController** 缺失以下关键功能：
- 房屋信息导出（exportAll）
- 房屋信息导入（importAll）
- 孤岛户管理（queryGDH, setGDH, genTaskGroup）
- 微信集成（insertWxBindData, getHouseByOtherCode）
- 多种数据查询端点（getDataByPay, getDataByCode, getDataByMulSearch 等）
- 阀门和热表相关查询（getValveAndHotByCode, getDataByAngle）

**PrBuildingController** 缺失：
- getBuildingNumByUserId（根据用户获取楼宇数量）

**PrHouseChangeController** 缺失：
- getHouseByRoomNum（根据房号查找房屋）

**PrOptionsHeatController** 缺失：
- getOptionById（根据ID获取选项）

**PrPrintTemplateController** 缺失：
- downloadTemplate（模板下载）
- getTemplateByName（根据名称获取模板）

#### 3. 完全重构的 Controller
**PrRegionalController**: 从简单地域查询改为统计分析功能，需要确认业务需求。

#### 4. 参数变更
- 多个端点从 POST 改为 GET，符合 RESTful 规范
- companyId/orgId 参数从请求参数改为从上下文获取
- 部分端点参数名称变更（如 userIdNo → houseId）

### 缺失功能清单

#### 高优先级缺失
1. **房屋导入导出功能**（PrHouseController）
   - exportAll
   - importAll
   
2. **组织机构管理**（PrCompanyController）
   - 完整的组织机构 CRUD
   - 树形结构查询
   - 数据权限相关接口

3. **角色管理**（PrRoleController）
   - 角色 CRUD
   - 权限分配
   - 用户角色关联

#### 中优先级缺失
4. **孤岛户管理**（PrHouseController）
   - queryGDH
   - setGDH
   - genTaskGroup

5. **微信集成**（PrHouseController）
   - insertWxBindData
   - getHouseByOtherCode

6. **高级查询功能**（PrHouseController）
   - getDataByPay
   - getDataByCode
   - getDataByMulSearch
   - getValveAndHotByCode
   - getDataByAngle

#### 低优先级缺失
7. **打印模板下载**（PrPrintTemplateController）
8. **根据用户获取楼宇数量**（PrBuildingController）
9. **根据房号查找房屋**（PrHouseChangeController）

### 代码质量风险

#### 安全性改进
- ✅ 新系统全面使用 Sa-Token 权限注解
- ✅ 新系统增加了用户维度隔离（SysColumnController）
- ✅ 新系统使用 @Validated 进行参数校验
- ⚠️ 需要确认所有端点的权限配置是否完整

#### 架构改进
- ✅ 新系统使用 RESTful 风格的 API 设计
- ✅ 新系统使用 BO/VO 模式进行数据转换
- ✅ 新系统增加了操作日志（@Log 注解）
- ⚠️ 部分 Controller 可能存在过度简化

#### 潜在问题
1. **功能完整性风险**：多个核心功能未迁移
2. **向后兼容性**：API 路径和方法的变更需要前端同步修改
3. **业务逻辑差异**：部分端点参数变更可能导致功能异常
4. **模块职责划分**：PrCompany/PrRole 未迁移的合理性需要确认

### 建议行动项

#### 立即处理
1. 确认 PrCompanyController 和 PrRoleController 的功能迁移计划
2. 评估房屋导入导出功能的迁移优先级
3. 确认微信集成功能是否需要保留

#### 短期处理
4. 补充缺失的高优先级 API 端点
5. 验证所有 Sa-Token 权限配置是否正确
6. 完成前端 API 调用的同步更新

#### 长期优化
7. 考虑将部分复杂查询逻辑迁移到独立的查询服务
8. 建立完善的 API 文档，记录所有变更
9. 增加集成测试覆盖所有 API 端点

---

**审核完成日期**: 2026-04-26
**审核人员**: Claude Code Agent
**下次审核建议**: 完成缺失功能迁移后重新审核
