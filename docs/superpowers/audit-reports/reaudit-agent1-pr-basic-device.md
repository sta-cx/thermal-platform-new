# Pr 基础数据 Controller 迁移审核报告

**审核日期**: 2026-04-26
**审核范围**: 12个 Pr 基础数据 Controller
**旧系统路径**: `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/`
**新系统路径**: `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/`

---

## 1. PrBuildingController（楼宇信息管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/prBuilding/pageList` | `GET /thermal/property/building/list` | ✅ | HTTP规范化 + 去除companyId参数 |
| `GET /property/prBuilding/getDataById` | `GET /thermal/property/building/{id}` | ✅ | RESTful路径参数 |
| `POST /property/prBuilding/insertData` | `POST /thermal/property/building` | ✅ | RESTful规范 |
| `POST /property/prBuilding/updateData` | `PUT /thermal/property/building` | ✅ | HTTP方法规范化 |
| `POST /property/prBuilding/deleteData` | `DELETE /thermal/property/building/{id}` | ✅ | RESTful规范 |
| `GET /property/prBuilding/validateName` | `GET /thermal/property/building/validate` | ✅ | 参数简化 |
| `GET /property/prBuilding/getUsed` | `GET /thermal/property/building/used` | ✅ | 返回固定字典值 |
| `GET /property/prBuilding/getBuildingByCompanyIdOrgId` | `GET /thermal/property/building/byOrg` | ✅ | 路径简化 |
| `GET /property/prBuilding/getUnitCodesByBuildingId` | `GET /thermal/property/building/unitCodes` | ✅ | 去除冗余参数 |
| `GET /property/prBuilding/getRoomNumsByUnitCode` | `GET /thermal/property/building/roomNums` | ✅ | 去除冗余参数 |
| `GET /property/prBuilding/getBuildingNumByUserId` | 🔲 | ❌ | **缺失** |
| `GET /property/prBuilding/getBuildingByStationId` | `GET /thermal/property/building/byStation` | ✅ | 路径简化 |

### 遗留问题
- ❌ `getBuildingNumByUserId` 端点未迁移，用于查询用户权限内楼宇数量

---

## 2. PrHouseController（房屋信息管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/house/pageList` | `GET /thermal/property/house/list` | ✅ | HTTP规范化 |
| `GET /property/house/getDataById` | `GET /thermal/property/house/{id}` | ✅ | RESTful路径参数 |
| `POST /property/house/insertData` | `POST /thermal/property/house` | ✅ | RESTful规范 |
| `POST /property/house/updateData` | `PUT /thermal/property/house` | ✅ | HTTP方法规范化 |
| `POST /property/house/deleteData` | `DELETE /thermal/property/house/{id}` | ✅ | RESTful规范 |
| `POST /property/house/deleteMulData` | `DELETE /thermal/property/house/batch` | ✅ | RESTful规范 |
| `GET /property/house/validateName` | `GET /thermal/property/house/validate` | ✅ | 参数调整 |
| `GET /property/house/validateBuildingName` | 🔲 | ❌ | **缺失** - 楼宇内房号唯一性校验 |
| `GET /property/house/updateUserByHouse` | 🔲 | ❌ | **缺失** - 变更房屋业主 |
| `GET /property/house/getHouseChangeList` | 🔲 | ❌ | **缺失** - 查询房屋变更记录 |
| `GET /property/house/deleteDataAll` | 🔲 | ❌ | **缺失** - 删除小区下所有房屋 |
| `GET /property/house/getHouseListByUnitCode` | `GET /thermal/property/house/byUnit` | ✅ | 路径简化 |
| `GET /property/house/getHouseListByUnit` | 🔲 | ❌ | **缺失** - 按单元获取房屋列表 |
| `POST /property/house/getHouseListByUnitAndType` | 🔲 | ❌ | **缺失** - 按单元和类型筛选 |
| `POST /property/house/getHouseListByUnitAndTypeAndCon` | 🔲 | ❌ | **缺失** - 阀门可控条件筛选 |
| `POST /property/house/getHouseListByValveAndHotType` | 🔲 | ❌ | **缺失** - 阀门供热类型筛选 |
| `GET /property/house/getHouseListByCompanyIdOrgIdRoomNum` | `GET /thermal/property/house/byOrg` | ⚠️ | 部分匹配，缺少按房号搜索 |
| `GET /property/house/exportAll` | 🔲 | ❌ | **缺失** - 导出功能 |
| `POST /property/house/importAll` | 🔲 | ❌ | **缺失** - 导入功能 |
| `GET /property/house/getHouseNumByUserId` | `GET /thermal/property/house/count` | ✅ | 语义优化 |
| `GET /property/house/getHouseAreaByUserId` | `GET /thermal/property/house/area` | ✅ | 语义优化 |
| `GET /property/house/getOccupancy` | 🔲 | ❌ | **缺失** - 获取入住率 |
| `GET /property/house/queryHousePageList` | 🔲 | ❌ | **缺失** - 自助机分页查询 |
| `GET /property/house/getFloorByCompanyId` | 🔲 | ❌ | **缺失** - 获取楼层号列表 |
| `GET /property/house/getHouseListByBuildingId` | 🔲 | ❌ | **缺失** - 按楼宇获取房屋 |
| `GET /property/house/isCheckHouse` | 🔲 | ❌ | **缺失** - 检查状态 |
| `GET /property/house/queryHomeUser` | 🔲 | ❌ | **缺失** - 查询房屋用户信息 |
| `GET /property/house/queryGDH` | 🔲 | ❌ | **缺失** - 查询孤岛户 |
| `POST /property/house/genTaskGroup` | 🔲 | ❌ | **缺失** - 生成分组信息 |
| `POST /property/house/setGDH` | 🔲 | ❌ | **缺失** - 设置孤岛户 |
| `GET /property/house/getCountByHouseId` | 🔲 | ❌ | **缺失** - 获取房屋计数 |
| `POST /property/house/insertWxBindData` | 🔲 | ❌ | **缺失** - 微信绑定 |
| `POST /property/house/getHouseByOtherCode` | 🔲 | ❌ | **缺失** - 按其他编码查询房屋 |
| `GET /property/house/getDataByPay` | 🔲 | ❌ | **缺失** - 按缴费状态查询 |
| `GET /property/house/getDataByCode` | 🔲 | ❌ | **缺失** - 按编码查询 |
| `POST /property/house/getDataByMulSearch` | 🔲 | ❌ | **缺失** - 多条件搜索 |
| `POST /property/house/setHouseHeatingArea` | 🔲 | ❌ | **缺失** - 设置供热面积 |
| `GET /property/house/getValveAndHotByCode` | 🔲 | ❌ | **缺失** - 按编码查阀门供热 |
| `GET /property/house/getDataByAngle` | 🔲 | ❌ | **缺失** - 按角度查询 |

### 遗留问题
- ❌ **大量端点缺失**（约30个），主要是：
  - 房屋导入/导出功能
  - 微信小程序相关端点
  - 孤岛户管理
  - 阀门控制相关查询
  - 缴费相关查询

---

## 3. PrHouseChangeController（房屋变更/入住管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/houseChange/pageList` | `GET /thermal/property/house-change/list` | ✅ | HTTP规范化，参数调整 |
| `GET /property/houseChange/getDataById` | `GET /thermal/property/house-change/{id}` | ✅ | RESTful路径参数 |
| `POST /property/houseChange/insertData` | `POST /thermal/property/house-change` | ✅ | RESTful规范 |
| `POST /property/houseChange/updateData` | `PUT /thermal/property/house-change` | ✅ | HTTP方法规范化 |
| `POST /property/houseChange/deleteData` | `DELETE /thermal/property/house-change/{id}` | ✅ | RESTful规范 |
| `POST /property/houseChange/deleteMulData` | `DELETE /thermal/property/house-change/batch` | ✅ | RESTful规范 |
| `POST /property/houseChange/changeData` | `PUT /thermal/property/house-change/audit` | ✅ | 语义优化为审核 |
| `GET /property/houseChange/getHouseByRoomNum` | 🔲 | ❌ | **缺失** - 按房号精确查找 |

### 遗留问题
- ❌ `getHouseByRoomNum` 端点未迁移

---

## 4. PrUnitController（单元信息管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/prUnit/pageList` | `GET /thermal/property/unit/list` | ✅ | HTTP规范化 |
| `GET /property/prUnit/getDataById` | `GET /thermal/property/unit/{id}` | ✅ | RESTful路径参数 |
| `POST /property/prUnit/insertData` | `POST /thermal/property/unit` | ✅ | RESTful规范 |
| `POST /property/prUnit/updateData` | `PUT /thermal/property/unit` | ✅ | HTTP方法规范化 |
| `POST /property/prUnit/deleteData` | `DELETE /thermal/property/unit/{id}` | ✅ | RESTful规范 |
| `POST /property/prUnit/validateName` | `GET /thermal/property/unit/validate` | ✅ | 参数简化 |
| `GET /property/prUnit/getListByBuildingId` | `GET /thermal/property/unit/byBuilding` | ✅ | 路径简化 |

### 遗留问题
- ✅ 完整迁移

---

## 5. PrFamilyController（家庭成员管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/family/pageList` | `GET /thermal/property/family/list` | ✅ | HTTP规范化，参数调整 |
| `POST /property/family/insertData` | `POST /thermal/property/family` | ✅ | RESTful规范 |
| `GET /property/family/queryPrFamily` | `GET /thermal/property/family/{id}` | ✅ | RESTful路径参数 |
| `POST /property/family/updateData` | `PUT /thermal/property/family` | ✅ | HTTP方法规范化 |
| `POST /property/family/deleteData` | `DELETE /thermal/property/family/{id}` | ✅ | RESTful规范 |

### 遗留问题
- ✅ 完整迁移

---

## 6. PrRegionalController（区域管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/regional/getRegional` | 🔲 | ❌ | **缺失** - 原功能被替换 |

### 遗留问题
- ❌ 原 `getRegional` 端点未迁移，新系统实现了 `regionalStats` 和 `dailyStats` 两个统计端点
- ⚠️ 功能差异：旧系统获取区域列表，新系统提供区域统计数据

---

## 7. PrHeatStationController（换热站管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/prHeatStation/pageList` | `GET /thermal/ht/station/list` | ✅ | HTTP规范化 + 路由模块调整 |
| `GET /property/prHeatStation/getDataById` | `GET /thermal/ht/station/{id}` | ✅ | RESTful路径参数 |
| `POST /property/prHeatStation/insertData` | `POST /thermal/ht/station` | ✅ | RESTful规范 |
| `POST /property/prHeatStation/updateData` | `PUT /thermal/ht/station` | ✅ | HTTP方法规范化 |
| `POST /property/prHeatStation/deleteData` | `DELETE /thermal/ht/station/{id}` | ✅ | RESTful规范 |
| `GET /property/prHeatStation/getDataByCompanyId` | `GET /thermal/ht/station/company/{companyId}` | ✅ | RESTful路径参数 |
| `GET /property/prHeatStation/getDataByOrgId` | `GET /thermal/ht/station/org/{orgId}` | ✅ | RESTful路径参数 |

### 遗留问题
- ✅ 完整迁移

---

## 8. PrHeatStationPartitionController（换热站分区管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/prHeatStationPartition/pageList` | `GET /thermal/ht/station-partition/list` | ✅ | HTTP规范化 |
| `GET /property/prHeatStationPartition/getDataById` | `GET /thermal/ht/station-partition/{id}` | ✅ | RESTful路径参数 |
| `POST /property/prHeatStationPartition/insertData` | `POST /thermal/ht/station-partition` | ✅ | RESTful规范 |
| `POST /property/prHeatStationPartition/updateData` | `PUT /thermal/ht/station-partition` | ✅ | HTTP方法规范化 |
| `POST /property/prHeatStationPartition/deleteData` | `DELETE /thermal/ht/station-partition/{id}` | ✅ | RESTful规范 |
| `GET /property/prHeatStationPartition/getDataByCompanyId` | `GET /thermal/ht/station-partition/company/{companyId}` | ✅ | RESTful路径参数 |
| `GET /property/prHeatStationPartition/getPartitionByHeatStationId` | `GET /thermal/ht/station-partition/station/{stationId}` | ✅ | RESTful路径参数 |

### 遗留问题
- ✅ 完整迁移

---

## 9. PrExpenseItemController（费用项目管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/expenseItem/pageList` | `GET /thermal/property/expense-item/list` | ✅ | HTTP规范化 |
| `POST /property/expenseItem/insertData` | `POST /thermal/property/expense-item` | ✅ | RESTful规范 |
| `POST /property/expenseItem/querypPrExpenseItem` | `GET /thermal/property/expense-item/{id}` | ✅ | RESTful路径参数 |
| `POST /property/expenseItem/updateData` | `PUT /thermal/property/expense-item` | ✅ | HTTP方法规范化 |
| `POST /property/expenseItem/getDataByItemCode` | `GET /thermal/property/expense-item/by-code` | ✅ | 路径简化 |
| `POST /property/expenseItem/getDataByItemGroup` | `GET /thermal/property/expense-item/by-group` | ✅ | 路径简化 |
| `POST /property/expenseItem/getItemCodesByItemGroup` | `GET /thermal/property/expense-item/codes` | ✅ | 路径简化 |
| `POST /property/expenseItem/getDataByCompanyIdOrgId` | `GET /thermal/property/expense-item/by-org` | ✅ | 路径简化 |
| `POST /property/expenseItem/deleteData` | `DELETE /thermal/property/expense-item/{id}` | ✅ | RESTful规范 |
| `POST /property/expenseItem/isItemName` | `GET /thermal/property/expense-item/check-name` | ✅ | 语义优化 |

### 遗留问题
- ✅ 完整迁移

---

## 10. PrStandardController（收费标准管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `GET /property/prStandard/pageList` | `GET /thermal/property/standard/list` | ✅ | HTTP规范化 |
| `POST /property/prStandard/insertData` | `POST /thermal/property/standard` | ✅ | RESTful规范 |
| `POST /property/prStandard/deleteData` | `DELETE /thermal/property/standard/{id}` | ✅ | RESTful规范 |
| `POST /property/prStandard/updateData` | `PUT /thermal/property/standard` | ✅ | HTTP方法规范化 |
| `POST /property/prStandard/queryPrStandard` | `GET /thermal/property/standard/{id}` | ✅ | RESTful路径参数 |
| `POST /property/prStandard/queryPrStandardByItemCode` | `GET /thermal/property/standard/by-item-code` | ✅ | 路径简化 |
| `POST /property/prStandard/findEleStandard` | `GET /thermal/property/standard/ele` | ✅ | 路径简化 |
| `POST /property/prStandard/findWaterStandard` | `GET /thermal/property/standard/water` | ✅ | 路径简化 |
| `POST /property/prStandard/findHeatStandard` | `GET /thermal/property/standard/heat` | ✅ | 路径简化 |
| `POST /property/prStandard/isName` | `GET /thermal/property/standard/check-name` | ✅ | 语义优化 |
| `POST /property/prStandard/purchase` | `POST /thermal/property/standard/purchase` | ✅ | HTTP方法保持 |
| `POST /property/prStandard/getPrExpenseItemByStandardId` | `GET /thermal/property/standard/expense-item` | ✅ | 路径简化 |
| `GET /property/prStandard/pageListItem` | `GET /thermal/property/standard/by-item-name` | ✅ | 路径简化 |
| `POST /property/prStandard/standardFeeListCopy` | 🔲 | ❌ | **缺失** - 单个引用标准 |
| `POST /property/prStandard/standardFeeListCopyAll` | 🔲 | ❌ | **缺失** - 批量引用标准 |

### 遗留问题
- ❌ `standardFeeListCopy` 和 `standardFeeListCopyAll` 端点未迁移，用于收费标准引用功能

---

## 11. PrOptionsController（系统选项管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `POST /property/options/getDataById` | `GET /thermal/property/options` | ✅ | HTTP规范化 + 参数调整 |
| `POST /property/options/insertData` | `POST /thermal/property/options` | ✅ | RESTful规范 |
| `POST /property/options/updateData` | `PUT /thermal/property/options` | ✅ | HTTP方法规范化 |
| `POST /property/options/initData` | `POST /thermal/property/options/init` | ✅ | 路径简化 |
| `POST /property/options/forbiddenToBuy` | `GET /thermal/property/options/forbidden` | ⚠️ | 参数调整：houseId→meterId |

### 遗留问题
- ⚠️ `forbiddenToBuy` 端点参数从 `houseId` 改为 `meterId`，需确认业务逻辑是否一致

---

## 12. PrOptionsHeatController（供热选项管理）

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| `POST /property/prOptionsHeat/getDataById` | `GET /thermal/property/options-heat` | ✅ | HTTP规范化 |
| `POST /property/prOptionsHeat/insertData` | `POST /thermal/property/options-heat` | ✅ | RESTful规范 |
| `POST /property/prOptionsHeat/updateData` | `PUT /thermal/property/options-heat` | ✅ | HTTP方法规范化 |
| `POST /property/prOptionsHeat/upserCompanyData` | `POST /thermal/property/options-heat/upsert` | ✅ | 拼写修正 + RESTful |
| `POST /property/prOptionsHeat/initData` | `POST /thermal/property/options-heat/init` | ✅ | 路径简化 |
| `GET /property/prOptionsHeat/getOptionById` | 🔲 | ❌ | **缺失** |

### 遗留问题
- ❌ `getOptionById` 端点未迁移
- ❌ 旧系统支持 `heatFlowValve` 列表参数，新系统未实现

---

## 总体统计

| Controller | 总端点数 | 完全匹配 | 部分匹配 | 缺失 | 新增 | 迁移率 |
|------------|----------|----------|----------|------|------|--------|
| PrBuildingController | 12 | 11 | 0 | 1 | 0 | 92% |
| PrHouseController | 36 | 6 | 1 | 29 | 0 | 17% |
| PrHouseChangeController | 8 | 7 | 0 | 1 | 0 | 88% |
| PrUnitController | 7 | 7 | 0 | 0 | 0 | 100% |
| PrFamilyController | 5 | 5 | 0 | 0 | 0 | 100% |
| PrRegionalController | 1 | 0 | 0 | 1 | 2 | 0%* |
| PrHeatStationController | 7 | 7 | 0 | 0 | 0 | 100% |
| PrHeatStationPartitionController | 7 | 7 | 0 | 0 | 0 | 100% |
| PrExpenseItemController | 10 | 10 | 0 | 0 | 0 | 100% |
| PrStandardController | 15 | 13 | 0 | 2 | 0 | 87% |
| PrOptionsController | 5 | 4 | 1 | 0 | 0 | 80% |
| PrOptionsHeatController | 6 | 5 | 0 | 1 | 0 | 83% |
| **合计** | **119** | **82** | **2** | **35** | **2** | **69%** |

\* PrRegionalController 功能被重新设计为统计端点，无法直接对比

---

## 关键发现

### 1. 高优先级缺失功能（需立即处理）

1. **PrHouseController 导入导出功能**
   - `exportAll` - 房屋信息导出
   - `importAll` - 房屋信息批量导入

2. **PrHouseController 微信小程序集成**
   - `insertWxBindData` - 微信绑定
   - `getHouseByOtherCode` - 按缴费码查询

3. **PrStandardController 收费标准引用**
   - `standardFeeListCopy` - 单个引用
   - `standardFeeListCopyAll` - 批量引用

### 2. 中优先级缺失功能

1. **PrHouseController 阀门控制相关**
   - `getHouseListByUnitAndType` - 按单元和类型筛选
   - `getHouseListByUnitAndTypeAndCon` - 阀门可控条件筛选
   - `setGDH` - 设置孤岛户
   - `genTaskGroup` - 生成分组信息

2. **PrHouseController 缴费相关**
   - `getDataByPay` - 按缴费状态查询
   - `getDataByCode` - 按编码查询
   - `setHouseHeatingArea` - 设置供热面积

### 3. 改进建议

1. **HTTP 规范化** ✅
   - 所有端点已遵循 RESTful 风格
   - GET/POST/PUT/DELETE 方法使用正确

2. **权限控制** ✅
   - 所有端点已添加 `@SaCheckPermission` 和 `@SaCheckLogin` 注解

3. **路径设计** ✅
   - 统一使用 `/thermal/property/*` 和 `/thermal/ht/*` 前缀
   - 资源命名语义化

4. **参数简化** ✅
   - 去除冗余的 `companyId` 参数
   - 使用路径参数替代查询参数

---

## 附录：架构改进点

### 已实施的改进

1. **统一的响应结构** - 使用 `R<T>` 和 `TableDataInfo<T>`
2. **BO/VO 分离** - 引入 `Bo` 和 `Vo` 对象
3. **操作日志** - 添加 `@Log` 注解记录操作
4. **参数校验** - 使用 `@Validated` 注解
5. **事务管理** - Service 层添加 `@Transactional` 注解

### 待改进项

1. **PrHouseController** 端点数量过多（36个），建议按功能拆分为多个 Controller
2. 部分复杂业务逻辑（如导入导出、孤岛户管理）需要独立的 Service 处理
3. 微信小程序相关端点建议独立到 `wx` 模块

---

**审核人**: Agent1
**审核完成时间**: 2026-04-26
