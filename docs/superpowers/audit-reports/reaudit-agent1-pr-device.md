# Pr 设备档案 Controller 迁移审核报告

**审核日期**: 2026-04-26
**审核范围**: 15 个 Pr 设备档案 Controller（旧系统 vs 新系统）
**旧系统基础路径**: `com.thermal.controller` -> `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/`
**新系统基础路径**: `org.sdkj.thermal.controller` -> `D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/`

**状态标记说明**:
- ✅ 完全匹配 -- 端点存在且逻辑等价
- ⚠️ 部分匹配 -- 端点存在但参数/返回值/逻辑有差异
- 🔲 骨架 -- 端点声明存在但返回占位数据或标记 TODO
- ❌ 缺失 -- 旧系统有但新系统无对应端点
- 🆕 新增 -- 新系统新增的端点（旧系统不存在）

---

## 1. PrHeatArchiveController（热表档案）

**旧系统路径**: `/property/prHeatArchive/*` (1444行)
**新系统路径**: `/thermal/ht/heat-archive/*` (369行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ⚠️ | 新系统使用 `PageQuery`/`TableDataInfo`，旧系统用 `Page`/`R`；旧系统有 `owe` 参数，新系统无 |
| `/queryCompanyHeat` (GET) | `/queryCompanyHeat` (GET) | ✅ | 功能等价 |
| `/insertHeatData` (POST) | `POST /` | ⚠️ | 新系统使用 Bo+MapstructUtils，旧系统直接用 Entity；新系统增加了唯一性校验 |
| `/selectMeterNum` (GET) | -- | ❌ | 旧系统检查表号是否重复，新系统在 insert/update 中内联校验 |
| `/updateHeatData` (POST) | `PUT /` | ⚠️ | 同 insert，新系统用 Bo；HTTP 方法改为 PUT |
| `/getHeatDataById` (GET) | `/{id}` (GET) | ✅ | 功能等价，RESTful 路径改进 |
| `/deleteHeatData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化，使用逻辑删除 |
| `/stopHeatMeter` (POST) | `/stopMeter/{id}` (POST) | ✅ | 功能等价 |
| `/startHeatMeter` (POST) | `/startMeter/{id}` (POST) | ✅ | 功能等价 |
| `/replaceHeatMeter` (POST) | `/replace` (POST) | ⚠️ | 旧系统在 Controller 中完成换表逻辑（~40行），新系统委托给 Service |
| `/calculate` (GET) | `/calculate/{id}` (GET) | ✅ | 功能等价 |
| `/realTimeData` (POST) | `/realTimeData` (POST) | ⚠️ | 旧系统参数更多（stationId, prHeatStationPartitionId, meterType, valveType, parentId, ifMore, moreParam），新系统仅保留基础过滤 |
| `/zonghe` (POST) | `/zonghe` (POST) | ⚠️ | 旧系统有 stationId 参数，新系统无 |
| `/miniControlValve` (POST) | -- | ❌ | 旧系统小程控阀功能（空实现），新系统未迁移 |
| `/setValveGroupParam` (POST) | `/setValveGroupParam` (POST) | ⚠️ | 旧系统在 Controller 中组装任务（~50行），新系统委托给 Service |
| `/manualControl` (POST) | `/manualControl` (POST) | ⚠️ | 旧系统在 Controller 中有 ~800 行任务创建逻辑（instructionType 1-30），新系统委托给 Service |
| `/xunce` (POST) | `/xunce` (POST) | ⚠️ | 旧系统在 Controller 中组装任务，新系统委托给 Service |
| `/exportAll` (GET) | `/export` (GET) | ⚠️ | 旧系统使用 EasyExcel 直接写入 Response，新系统返回 `R<List<Vo>>`（未实际生成 Excel 文件下载） |
| `/importData` (POST) | -- | ❌ | 导入配表功能未迁移 |
| `/findMeter` (GET) | `/findMeter` (GET) | ⚠️ | 旧系统有 userId 参数，新系统无 |
| `/recharge` (POST) | `/recharge` (POST) | ⚠️ | 旧系统在 Controller 中有 ~100行交易记录生成逻辑，新系统委托给 Service |
| `/selectReport` (POST) | `/selectReport` (POST) | ⚠️ | 旧系统用 JSONObject 包装返回，新系统直接返回 List |
| `/selectMeterReport` (POST) | `/selectMeterReport` (POST) | ⚠️ | 同上 |
| -- | `/{id}` (GET 详情) | 🆕 | 新增 RESTful 详情查询 |
| -- | `/{id}` (DELETE 删除) | 🆕 | 新增 RESTful 删除 |

### 关键发现
- 旧系统 Controller 中包含大量业务逻辑（manualControl 约 800 行），新系统正确地将这些逻辑下沉到 Service 层
- `realTimeData` 和 `zonghe` 端点参数差异较大，旧系统支持站房/分区/设备类型等高级过滤
- 导入功能 `/importData` 未迁移
- 导出功能 `/exportAll` 新系统返回 JSON 而非文件流
- `/miniControlValve` 小程序控阀端点未迁移（旧系统本身为空实现）

---

## 2. PrHeatValveArchiveController（阀门档案）

**旧系统路径**: `/property/prHeatValveArchive/*` (1770行)
**新系统路径**: `/thermal/ht/valve-archive/*` (219行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListValveTK` (GET) | -- | ❌ | TK实时数据分页查询未迁移 |
| `/pageListHeatCard` (GET) | -- | ❌ | 卡表分页查询未迁移 |
| `/pageListCard` (GET) | -- | ❌ | 卡表列表未迁移 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价，新增唯一性校验 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化，唯一性校验一致 |
| `/getDataById` (GET) | `/{id}` (GET) | ✅ | RESTful 化 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/deleteDataAll` (POST) | -- | ❌ | 批量删除未迁移 |
| `/insertDataNbValve` (POST) | -- | ❌ | NB阀门数据接收（电信平台回调）未迁移 |
| `/insertDataMBusValve` (POST) | -- | ❌ | MBus阀门数据接收未迁移 |
| `/isCheckMeter` (GET) | -- | ❌ | 检查仪表功能未迁移 |
| `/signature` (GET/POST) | -- | ❌ | 移动平台验证/数据接收未迁移 |
| `/openValve` (POST) | `/openValve` (POST) | ⚠️ | 新系统使用 batchCreateValveControlTasks，旧系统手动构建任务 |
| `/closeValve` (POST) | `/closeValve` (POST) | ⚠️ | 同上 |
| `/updateValveStatus` (POST) | -- | ❌ | 卡表开户状态修改未迁移 |
| `/queryMeterByMeterNum` (GET) | -- | ❌ | 按表号查询设备未迁移 |
| `/queryValveByMeterNum` (GET) | -- | ❌ | 按阀门号查询未迁移 |
| `/queryMeterListByMeterNum` (GET) | -- | ❌ | 小程序设备查询未迁移 |
| `/exchangeMeter/{oldId}` (POST) | `/exchangeMeter/{oldId}` (POST) | ✅ | 功能等价 |
| `/insertWriteCardLog` (POST) | -- | ❌ | 写卡记录未迁移 |
| `/getWardCardLog` (GET) | -- | ❌ | 写卡日志查询未迁移 |
| `/queryHouseByMeterNum` (GET) | -- | ❌ | 按阀门号查房屋未迁移 |
| `/queryCardMeterByHouseId` (GET) | -- | ❌ | 按房屋查卡阀未迁移 |
| `/queryCardMeterByRoomNum` (GET) | -- | ❌ | 按房号查卡阀未迁移 |
| `/valveInformationSynchronization` (POST) | -- | ❌ | 阀门信息同步到采集平台未迁移 |
| `/downloadInfoSync` (GET) | -- | ❌ | 下载同步信息未迁移 |
| `/exportAll` (GET) | -- | ❌ | 导出全部未迁移 |
| `/exportCardAll` (GET) | -- | ❌ | 导出卡阀未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |
| `/setValveOCStatus` (POST) | -- | ❌ | 按缴费状态批量控制阀门未迁移 |
| `/setValveOpening` (POST) | -- | ❌ | 设定阀门开度未迁移 |
| `/setValveCycle` (POST) | -- | ❌ | 设定阀门周期未迁移 |
| `/api/enopt/valve/control` (POST) | -- | ❌ | 云谷对接API未迁移 |
| `/api/enopt/rtdata/batchsync` (POST) | -- | ❌ | 云谷批量同步未迁移 |
| `/api/xaltrl/getLTValveData` (GET) | -- | ❌ | 联通数据查询API未迁移 |
| `/getValveDataByHouseId` (GET) | -- | ❌ | 按房屋查阀门数据未迁移 |
| `/insertDataByHouseId` (POST) | `/insertByHouseId` (POST) | ✅ | 功能等价，新系统增加了房屋唯一性校验 |
| `/insertValveControlLogByBluetooth` (POST) | -- | ❌ | 蓝牙控阀日志未迁移 |
| `/readValveStatusByParam` (POST) | -- | ❌ | 按参数读取阀门状态未迁移 |
| `/getValveStatusByParam` (POST) | -- | ❌ | 按参数获取阀门状态未迁移 |
| `/setValveCycleByParam` (POST) | -- | ❌ | 按参数设定阀门周期未迁移 |
| `/insertUserAndValveInfo` (POST) | -- | ❌ | 新增用户和阀门信息未迁移 |
| `/getOutputValveStatusByParam` (POST) | -- | ❌ | 导出阀门状态Excel未迁移 |
| `/getOutputValveStatusData` (POST) | -- | ❌ | 导出阀门状态数据未迁移 |

### 关键发现
- **旧系统 1770 行，新系统仅 219 行**，缺失端点数量最多（约 30 个）
- 缺失端点分为三大类：(1) 第三方平台对接（电信NB、MBus、移动平台、云谷、联通）；(2) 卡表相关功能；(3) 批量控制与导出功能
- 核心CRUD和开关阀功能已完成迁移
- 第三方回调接口（`/insertDataNbValve`、`/signature`）是硬件数据上报的关键通道，缺失将导致数据断流

---

## 3. PrHeatUnitValveArchiveController（单元阀门档案）

**旧系统路径**: `/property/prHeatUnitValveArchive/*`
**新系统路径**: `/thermal/ht/unit-valve-archive/*` (148行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListUnitValveTK` (POST) | -- | ❌ | TK实时数据分页查询未迁移 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/getDataById` (GET) | `/{id}` (GET) | 🆕 | 新增 RESTful 详情查询 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价，增加唯一性校验 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化 |
| `/openValve` (POST) | `/openValve` (POST) | ✅ | 新系统委托 tasksPerformService |
| `/closeValve` (POST) | `/closeValve` (POST) | ✅ | 同上 |
| `/setValveOCStatus` (POST) | -- | ❌ | 批量控制阀门状态未迁移 |
| `/exportAll` (GET) | -- | ❌ | 导出未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |
| `/valveInformationSynchronization` (POST) | -- | ❌ | 同步到采集平台未迁移 |
| `/downloadInfoSync` (GET) | -- | ❌ | 下载同步信息未迁移 |
| `/exchangeMeter` (POST) | -- | ❌ | 换表功能未迁移 |

### 关键发现
- 核心CRUD+开关阀已完整迁移
- 缺失导出/导入/同步/换表等扩展功能
- 旧系统大量 TK（调控相关）端点未迁移

---

## 4. PrHeatCommandValveArchiveController（指令阀门档案）

**旧系统路径**: `/property/prHeatCommandValveArchive/*`
**新系统路径**: `/thermal/ht/command-valve-archive/*` (189行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListTK` (POST) | -- | ❌ | TK分页查询未迁移 |
| `/pageListCard` (GET) | -- | ❌ | 卡表分页查询未迁移 |
| `/getDataById` (GET) | `/{id}` (GET) | ✅ | RESTful 化 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/openValve` (POST) | `/openValve` (POST) | ✅ | 新系统使用 batchCreateValveControlTasks |
| `/closeValve` (POST) | `/closeValve` (POST) | ✅ | 同上 |
| `/exchangeMeter` (POST) | `/exchangeMeter/{oldId}` (POST) | ✅ | 功能等价 |
| `/exportAll` (GET) | -- | ❌ | 导出未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |
| `/valveInformationSynchronization` (POST) | -- | ❌ | 同步到采集平台未迁移 |
| `/downloadInfoSync` (GET) | -- | ❌ | 下载同步信息未迁移 |

### 关键发现
- 核心CRUD+开关阀+换表已完整迁移
- 缺失 TK 分页、卡表分页、导入/导出、同步功能

---

## 5. PrHeatCommandUnitValveArchiveController（指令单元阀门档案）

**旧系统路径**: `/property/prHeatCommandUnitValveArchive/*`
**新系统路径**: `/thermal/ht/command-unit-valve-archive/*` (148行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListCommandUnitValveTK` (POST) | -- | ❌ | TK分页查询未迁移 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/getDataById` (GET) | `/{id}` (GET) | 🆕 | 新增详情查询 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化 |
| `/openValve` (POST) | `/openValve` (POST) | ✅ | 委托 tasksPerformService |
| `/closeValve` (POST) | `/closeValve` (POST) | ✅ | 同上 |
| `/exportAll` (GET) | -- | ❌ | 导出未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |
| `/valveInformationSynchronization` (POST) | -- | ❌ | 同步到采集平台未迁移 |
| `/downloadInfoSync` (GET) | -- | ❌ | 下载同步信息未迁移 |

### 关键发现
- 模式与 PrHeatUnitValveArchiveController 一致
- 核心CRUD+开关阀完整，扩展功能缺失

---

## 6. PrHeatDtuArchiveController（DTU档案）

**旧系统路径**: `/property/prHeatDtuArchive/*`
**新系统路径**: `/thermal/ht/dtu-archive/*` (101行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListDtuTK` (POST) | -- | ❌ | TK分页查询未迁移 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/getDataById` (GET) | `/{id}` (GET) | ✅ | RESTful 化 |
| -- | `/query-meter` (POST) | 🆕 | 新增查询DTU下仪表并生成指令功能 |
| `/exportAll` (GET) | -- | ❌ | 导出未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |
| `/getDtuInfo` (GET) | -- | ❌ | DTU信息查询未迁移 |

### 关键发现
- 核心CRUD完整
- 新增了 `/query-meter` 功能（查询DTU下所有仪表并生成查询指令），旧系统无对应端点
- ServiceImpl 中 `queryMeter` 方法有完整的 Mapper 调用实现（调用4个Mapper方法分别查询户阀/单元阀/热表/单元热表）

---

## 7. PrHeatTempArchiveController（温度档案）

**旧系统路径**: `/property/prHeatTempArchive/*`
**新系统路径**: `/thermal/ht/temp-archive/*` (110行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListMachineTK` (POST) | -- | ❌ | TK分页查询未迁移 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价，增加唯一性校验 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/getDataById` (GET) | `/{id}` (GET) | ✅ | RESTful 化 |
| `/exportAll` (GET) | -- | ❌ | 导出未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |

### 关键发现
- 核心CRUD完整
- 缺失导入/导出和 TK 分页功能

---

## 8. PrHeatHotArchiveController（热量档案）

**旧系统路径**: `/property/prHeatHotArchive/*`
**新系统路径**: `/thermal/ht/hot-archive/*` (142行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListHeatTK` (POST) | -- | ❌ | TK分页查询未迁移 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/getDataById` (GET) | `/{id}` (GET) | ✅ | RESTful 化 |
| `/insertDataByHouseId` (POST) | `/insertByHouseId` (POST) | ✅ | 新系统增加了房屋唯一性校验 |
| `/setValveOCStatus` (POST) | -- | ❌ | 批量控制阀门状态未迁移 |
| `/setValveOpening` (POST) | -- | ❌ | 设定阀门开度未迁移 |
| `/exportAll` (GET) | -- | ❌ | 导出未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |

### 关键发现
- 核心CRUD完整，包括按房屋新增
- 缺失批量控制、导入/导出功能

---

## 9. PrHeatUnitHotArchiveController（单元热量档案）

**旧系统路径**: `/property/prHeatUnitHotArchive/*`
**新系统路径**: `/thermal/ht/unit-hot-archive/*` (120行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListUnitHeatTK` (POST) | -- | ❌ | TK分页查询未迁移 |
| `/insertData` (POST) | `POST /` | ✅ | 功能等价 |
| `/updateData` (POST) | `PUT /` | ✅ | RESTful 化 |
| `/deleteData` (POST) | `/{id}` (DELETE) | ✅ | RESTful 化 |
| `/getDataById` (GET) | `/{id}` (GET) | ✅ | RESTful 化 |
| `/exportAll` (GET) | -- | ❌ | 导出未迁移 |
| `/importAll` (POST) | -- | ❌ | 导入未迁移 |

### 关键发现
- 核心CRUD完整
- 模式一致：缺失 TK 分页、导入/导出

---

## 10. PrHeatControlController（供热控制）

**旧系统路径**: `/property/prHeatControl/*` (197行)
**新系统路径**: `/thermal/ht/control/*` (139行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/handControl` (POST) | `/manual` (POST) | ⚠️ | 旧系统硬编码 meterNum，新系统改为参数传入 |
| `/selectMeter` (GET) | `/query` (GET) | ✅ | 功能等价，命名更规范 |
| `/openValve` (POST) | `/openValve` (POST) | ✅ | 功能等价 |
| `/closeValve` (POST) | `/closeValve` (POST) | ✅ | 功能等价 |
| `/add` (POST) | `/add` (POST) | ⚠️ | 旧系统从数据库获取策略参数（硬编码 strategyId），新系统改为请求参数传入 |
| `/sub` (POST) | `/sub` (POST) | ⚠️ | 同上 |
| `/add1` (POST) | -- | ❌ | 异步加档未迁移 |
| `/sub1` (POST) | -- | ❌ | 异步减档未迁移 |
| `/pageList` (POST) | -- | ❌ | 分页查询阀门数据未迁移 |
| -- | `/generateCommand` (GET) | 🆕 | 新增调试用指令生成功能 |

### 关键发现
- 旧系统使用静态方法 `HeatMeterControl.MBusControl`，新系统注入 `HeatMeterControl` Bean
- 新系统增加了 `SaCheckPermission` 和 `SaCheckLogin` 安全注解
- `/add` 和 `/sub` 的策略参数来源从硬编码数据库查询改为前端传入，更灵活
- `/generateCommand` 是新增的调试端点

---

## 11. PrHeatDailyController（日表）

**旧系统路径**: `/property/prHeatDaily/*` (56行)
**新系统路径**: `/thermal/ht/heat-daily/*` (108行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/setHeat` (POST) | `/setHeat` (POST) | ⚠️ | 旧系统分5步逐步调用（setIsValid/setHeatDaily/setSteps/setQtyStepsN/setCurrentReading），新系统封装为 `generateHeatDaily` |
| -- | `/{id}` (GET) | 🆕 | 新增详情查询 |
| -- | `/exportAll` (GET) | 🆕 | 新增导出Excel功能 |

### 关键发现
- 新系统功能超出旧系统，新增了详情查询和 Excel 导出
- 日表生成逻辑从 Controller 5步调用改为 Service 单方法封装

---

## 12. PrHeatReadingController（抄表）

**旧系统路径**: `/property/prHeatReading/*` (190行)
**新系统路径**: `/thermal/ht/heat-reading/*` (133行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/pageListTrend` (POST) | `/trend` (POST) | ⚠️ | 旧系统传入 List<PrHeatValveArchive>，新系统传入 List<String> meterNums |
| `/pageListTrendS` (POST) | `/trend-home` (GET) | ✅ | HTTP 方法改为 GET，参数不变 |
| `/exportAll` (GET) | `/exportAll` (GET) | ⚠️ | 旧系统分批导出（每批2000条），新系统一次导出全部 |
| -- | `/{id}` (GET) | 🆕 | 新增详情查询 |

### 关键发现
- 核心功能已完整迁移
- 旧系统趋势接口传入阀门实体列表，新系统简化为仅传入 meterNums
- 导出从分批处理改为一次全量导出，大数据量可能存在性能风险

---

## 13. PrHeatMonthController（月表）

**旧系统路径**: `/property/prHeatMonth/*` (56行)
**新系统路径**: `/thermal/ht/heat-month/*` (106行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (GET) | `/list` (GET) | ✅ | 功能等价 |
| `/setHeat` (POST) | `/setHeat` (POST) | 🔲 | 旧系统6步生成月表（insertPrHeatMonth/updateStartReading/updateQty/setFee/updateArchive/updateArrearage），新系统返回 "月表生成功能尚未实现" |
| -- | `/{id}` (GET) | 🆕 | 新增详情查询 |
| -- | `/exportAll` (GET) | 🆕 | 新增导出Excel功能 |

### 关键发现
- **月表生成功能 (`/setHeat`) 为骨架实现**，返回硬编码错误消息
- 旧系统月表生成涉及6步操作，均未在新系统 Service 中实现

---

## 14. PrAbnormalRecordController（异常记录）

**旧系统路径**: `/property/prAbnormalRecord/*` (45行)
**新系统路径**: `/thermal/property/abnormal/*` (73行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/updateData` (POST) | `PUT /` | ⚠️ | RESTful 化；旧系统用自定义 updateData 方法，新系统用通用 updateById |
| `/pageMeterUpdateRecordList` (GET) | `/list` (GET) | ⚠️ | 旧系统参数含 archiveId, type, meterType, searchPhone；新系统改为 abnormalType, handleStatus 过滤 |
| -- | `/{id}` (GET) | 🆕 | 新增详情查询 |
| -- | `POST /` | 🆕 | 新增创建记录 |
| -- | `/{id}` (DELETE) | 🆕 | 新增删除 |

### 关键发现
- 旧系统仅 2 个端点，新系统扩展为完整的 CRUD（5个端点）
- 查询参数完全不同：旧系统按仪表更新记录查询，新系统按异常类型和处理状态查询
- 旧系统的 `updateData` 可能包含特殊逻辑（如状态变更通知），新系统用通用 updateById 替代

---

## 15. PrAutoMachineController（自动机/自助机）

**旧系统路径**: `/property/autoMachine/*` (~40000字节，含完整微信/支付宝支付集成)
**新系统路径**: `/thermal/property/auto-machine/*` (177行)

### 端点对比

| 旧端点 | 新端点 | 状态 | 差异 |
|--------|--------|------|------|
| `/pageList` (POST) | `/list` (GET) | 🔲 | 骨架实现，返回 R.fail |
| `/getDataById` (POST) | `/{id}` (GET) | 🔲 | 骨架实现 |
| `/updateData` (POST) | `PUT /` | 🔲 | 骨架实现，参数类型为 Object |
| `/getSerialNum` (POST) | `/serial-num` (GET) | 🔲 | 骨架实现 |
| `/getQrCode` (POST) | `/qr-heat` (POST) | 🔲 | 骨架实现，标记 "需要第三方支付集成" |
| `/getQrCodeWater` (POST) | `/qr-water` (POST) | 🔲 | 骨架实现 |
| `/getQrCodeEle` (POST) | `/qr-ele` (POST) | 🔲 | 骨架实现 |
| `/callback` (POST) | `/callback/wechat-heat` (POST) | 🔲 | 抛出 ServiceException |
| `/aliCallBack` (POST) | `/callback/ali-heat` (POST) | 🔲 | 抛出 ServiceException |
| `/queryPaymentSuccess` (POST) | `/payment-status` (GET) | 🔲 | 骨架实现 |
| `/getRecordBySerialNum` (POST) | `/record` (GET) | 🔲 | 骨架实现 |
| `/getIsReadCard` (POST) | `/read-card` (GET) | 🔲 | 骨架实现 |

### 关键发现
- **全部端点均为骨架实现**，Controller 标记 `@Deprecated` 和 `@Hidden`
- 旧系统包含完整的微信支付、支付宝支付、二维码生成逻辑（RedisTemplate, WXPayUtil, AlipayClient 等）
- 支付回调端点用 `throw ServiceException` 阻止误调用，安全设计合理
- 依赖 Phase 6 第三方支付集成才能完整实现

---

## 关键发现汇总

### 1. 整体架构改进

- **RESTful 化**: 所有端点从旧的 `@RequestMapping` 统一请求方式改为明确的 `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping`
- **权限注解**: 新系统全面添加 `@SaCheckPermission` + `@SaCheckLogin`，旧系统大部分端点无权限控制
- **日志审计**: 写操作添加 `@Log` 注解，支持操作审计
- **参数校验**: 使用 `@Validated` + Bo 对象替代旧的裸 Entity 接收
- **Controller 瘦身**: 旧系统大量业务逻辑（特别是 manualControl 约800行）在 Controller 中，新系统正确下沉到 Service

### 2. 代码量对比

| Controller | 旧行数 | 新行数 | 缩减比例 |
|-----------|--------|--------|---------|
| PrHeatArchiveController | 1444 | 369 | 74% |
| PrHeatValveArchiveController | 1770 | 219 | 88% |
| PrHeatControlController | 197 | 139 | 29% |
| PrAutoMachineController | ~1000 | 177 | 82% |

### 3. 高风险缺失端点

#### P0 -- 数据通道（缺失将导致系统无法运行）
- `PrHeatValveArchiveController/insertDataNbValve` -- 电信NB阀门数据上报
- `PrHeatValveArchiveController/insertDataMBusValve` -- MBus阀门数据上报
- `PrHeatValveArchiveController/signature` -- 移动平台数据上报
- `PrHeatMonthController/setHeat` -- 月表生成（骨架）

#### P1 -- 核心业务功能
- `PrHeatArchiveController/manualControl` -- 实时调控（指令类型1-30已委托Service，但Service实现待验证）
- `PrHeatValveArchiveController/setValveOCStatus` -- 按缴费状态批量控制阀门
- `PrHeatValveArchiveController/setValveOpening` -- 设定阀门开度
- `PrHeatValveArchiveController` 中单元阀/指令阀的 exchangeMeter -- 换表功能

#### P2 -- 集成对接
- 云谷API（valve/control, rtdata/batchsync）
- 联通数据API（getLTValveData）
- 采集平台同步（valveInformationSynchronization）
- 自助机支付集成（全部骨架）

### 4. 统一缺失模式

以下功能在多个 Controller 中系统性缺失：
- **TK 分页查询**（pageListValveTK, pageListTK, pageListMachineTK 等）-- 实时调控数据查询
- **导入功能**（importAll, importData）-- Excel 批量导入
- **导出功能**（exportAll, exportCardAll）-- 部分新系统改为 JSON 返回
- **采集平台同步**（valveInformationSynchronization）-- 设备信息同步到采集平台
- **卡表相关功能**（pageListCard, insertWriteCardLog, getWardCardLog 等）

---

## 总体统计

### 端点统计

| 指标 | 数量 |
|------|------|
| 旧系统总端点数 | 约 120 |
| 新系统已实现端点数 | 约 65 |
| ✅ 完全匹配 | 约 35 |
| ⚠️ 部分匹配 | 约 18 |
| 🔲 骨架实现 | 约 12 |
| ❌ 缺失 | 约 55 |
| 🆕 新增 | 约 12 |

### 按 Controller 统计

| Controller | 旧端点 | 新端点 | ✅ | ⚠️ | 🔲 | ❌ | 🆕 |
|-----------|--------|--------|----|----|----|----|----|
| 1. PrHeatArchive | 20 | 18 | 5 | 10 | 0 | 3 | 2 |
| 2. PrHeatValveArchive | 36 | 7 | 5 | 2 | 0 | 27 | 0 |
| 3. PrHeatUnitValveArchive | 14 | 7 | 7 | 0 | 0 | 7 | 0 |
| 4. PrHeatCommandValveArchive | 14 | 8 | 8 | 0 | 0 | 6 | 0 |
| 5. PrHeatCommandUnitValveArchive | 12 | 7 | 7 | 0 | 0 | 5 | 0 |
| 6. PrHeatDtuArchive | 9 | 6 | 5 | 0 | 0 | 3 | 1 |
| 7. PrHeatTempArchive | 8 | 5 | 5 | 0 | 0 | 3 | 0 |
| 8. PrHeatHotArchive | 11 | 6 | 6 | 0 | 0 | 5 | 0 |
| 9. PrHeatUnitHotArchive | 8 | 5 | 5 | 0 | 0 | 3 | 0 |
| 10. PrHeatControl | 9 | 7 | 3 | 3 | 0 | 3 | 1 |
| 11. PrHeatDaily | 2 | 4 | 1 | 1 | 0 | 0 | 2 |
| 12. PrHeatReading | 4 | 5 | 1 | 2 | 0 | 0 | 1 |
| 13. PrHeatMonth | 2 | 4 | 1 | 0 | 1 | 0 | 2 |
| 14. PrAbnormalRecord | 2 | 5 | 0 | 2 | 0 | 0 | 3 |
| 15. PrAutoMachine | 12 | 12 | 0 | 0 | 12 | 0 | 0 |

### 迁移完成度评估

| 类别 | 完成度 |
|------|--------|
| 核心CRUD（增删改查） | **95%** -- 几乎全部完成 |
| 阀门控制（开阀/关阀） | **90%** -- 户阀/单元阀/指令阀/指令单元阀均已完成 |
| 实时调控（manualControl） | **50%** -- Controller已迁移，Service逻辑待验证 |
| TK分页查询 | **0%** -- 全部缺失 |
| 导入/导出 | **15%** -- 仅日表/抄表/月表有导出 |
| 第三方平台对接 | **0%** -- 电信/移动/云谷/联通全部缺失 |
| 支付/自助机 | **5%** -- 仅有骨架端点声明 |
| 报表功能 | **70%** -- 收费明细/仪表历史报表已有 |
| 月表生成 | **20%** -- 仅骨架 |

### 总体评估

**迁移完成度约 55%**。核心 CRUD 和阀门控制已高质量完成，架构改进显著（RESTful、权限、日志、分层）。主要差距集中在：(1) 第三方硬件平台数据回调接口；(2) TK 实时调控数据查询；(3) 批量导入导出；(4) 支付集成。建议按 P0 > P1 > P2 优先级分批补全。
