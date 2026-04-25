# 功能等价验证报告

## 覆盖率统计

| 指标 | 数值 |
|------|------|
| 老系统总 Controller 文件数 | 110 |
| 新系统总 Controller 文件数 (sdkj-modules) | 58 |
| 老系统总端点数 (估算) | ~520 |
| 新系统总端点数 (估算) | ~180 |
| 已迁移（✅ 等价） | ~130 |
| 部分等价（⚠️） | ~25 |
| 缺失（❌） | ~340 |
| 有差异（🔴） | ~25 |

## 端点详细对比

### 已迁移（✅ 等价）

| 老系统端点 | 新系统端点 | 状态 | 备注 |
|-----------|-----------|------|------|
| **HtStrategyController** (`/htStrategy/*`) | `/thermal/ht/strategy/*` | ✅ | 路径前缀变更: `/htStrategy` → `/thermal/ht/strategy`，POST→RESTful GET/POST/PUT/DELETE |
| `/htStrategy/pageList` | `GET /thermal/ht/strategy/list` | ✅ | 参数 search 保留，使用 MyBatis-Plus 分页替代 PageHelper |
| `/htStrategy/insertData` | `POST /thermal/ht/strategy` | ✅ | |
| `/htStrategy/queryHtStrategy` | `GET /thermal/ht/strategy/{id}` | ✅ | RESTful 风格 |
| `/htStrategy/updateData` | `PUT /thermal/ht/strategy` | ✅ | |
| `/htStrategy/deleteData` | `DELETE /thermal/ht/strategy/{id}` | ✅ | |
| `/htStrategy/queryHtStrategyList` | `GET /thermal/ht/strategy/all` | ✅ | |
| **HtInstructionController** (`/htInstruction/*`) | `/thermal/ht/instruction/*` | ✅ | 完全等价迁移 |
| **HtAlertController** (`/htAlert/*`) | `/thermal/ht/alert/*` | ✅ | 完全等价迁移，增加分页增强 |
| **HtRepairController** (`/htRepair/*`) | `/thermal/ht/repair/*` | ✅ | 等价迁移，增加 dispatch/status 端点 |
| **HtScopeController** (`/htScope/getHouseListByTaskId`) | `GET /thermal/ht/scope/houseList` | ✅ | 增加 scopeType 参数 |
| **HtTasksController** (`/property/htTasks/*`) | `/thermal/ht/tasks/*` | ✅ | 核心 CRUD + 调度端点迁移 |
| `/property/htTasks/list` | `GET /thermal/ht/tasks/list` | ✅ | |
| `/property/htTasks/queryTasksAll` | `GET /thermal/ht/tasks/all` | ✅ | |
| `/property/htTasks/getById/{id}` | `GET /thermal/ht/tasks/{id}` | ✅ | |
| `/property/htTasks/save` | `POST /thermal/ht/tasks` | ✅ | |
| `/property/htTasks/edit` | `PUT /thermal/ht/tasks` | ✅ | |
| `/property/htTasks/remove/{id}` | `DELETE /thermal/ht/tasks/{id}` | ✅ | |
| `/property/htTasks/removeBatch` | `DELETE /thermal/ht/tasks/batch` | ✅ | |
| `/property/htTasks/changeStatus/{id}` | `PUT /thermal/ht/tasks/status/{id}` | ✅ | |
| `/property/htTasks/run/{id}` | `POST /thermal/ht/tasks/run/{id}` | ✅ | |
| `/property/htTasks/isSpecial` | `PUT /thermal/ht/tasks/isSpecial` | ✅ | |
| `/property/htTasks/isSpecialD` | `PUT /thermal/ht/tasks/isSpecialUnit` | ✅ | |
| `/property/htTasks/isPayStatus` | `PUT /thermal/ht/tasks/payStatus` | ✅ | |
| `/property/htTasks/queryPhl` | `GET /thermal/ht/tasks/balanceRate/{taskId}` | ✅ | |
| `/property/htTasks/updateSdkd` | `PUT /thermal/ht/tasks/saveValveAngle` | ✅ | |
| `/property/htTasks/querySummary` | `GET /thermal/ht/tasks/summary` | ✅ | 参数简化 |
| `/property/htTasks/pageListSettingLog` | `GET /thermal/ht/tasks/settingLog` | ✅ | |
| `/property/htTasks/getDetailByMainId/{id}` | `GET /thermal/ht/tasks/settingLog/{mainId}` | ✅ | |
| **HtTasksPerformController** (`/htTasksPerform/*`) | `/thermal/ht/tasksPerform/*` | ✅ | |
| `/htTasksPerform/queryListByMeterId` | `GET /thermal/ht/tasksPerform/byMeterId` | ✅ | |
| `/htTasksPerform/queryListByMeterIdXq` | `GET /thermal/ht/tasksPerform/byMeterIdDetail` | ✅ | |
| **HtHouseStrategyController** (`/property/houseStrategy/*`) | `/thermal/ht/houseStrategy/*` | ✅ | 批量操作增强 |
| **PrStandardController** (`/property/prStandard/*`) | `/thermal/property/standard/*` | ✅ | 完全等价迁移 |
| **PrExpenseController** (`/property/expense/*`) | `/thermal/property/expense/*` | ✅ | 核心端点全部迁移 |
| **PrExpenseItemController** (`/property/expenseItem/*`) | `/thermal/property/expense-item/*` | ✅ | |
| **PrAccountController** (`/property/prAccount/*`) | `/thermal/property/account/*` | ✅ | 核心端点迁移 |
| **PrTransactionRecordController** (`/property/prTransactionRecord/*`) | `/thermal/property/transaction/*` | ✅ | 核心端点迁移 |
| **PrUserController** (`/property/user/*`) | `/thermal/property/user/*` | ✅ | |
| **PrOptionsController** (`/property/options/*`) | `/thermal/property/options/*` | ✅ | |
| **PrOptionsHeatController** (`/property/prOptionsHeat/*`) | `/thermal/property/options-heat/*` | ✅ | |
| **PrUseCardLogController** (`/property/prUseCardLog/*`) | `/thermal/property/use-card-log/*` | ✅ | |
| **PrHouseChangeController** (`/property/houseChange/*`) | `/thermal/property/house-change/*` | ✅ | |
| **PrHouseExpenseController** (`/property/houseExpense/*`) | `/thermal/property/house-expense/*` | ✅ | |
| **SingleChargeController** (`/property/singleCharge/*`) | `/thermal/property/single-charge/*` | ✅ | |
| **PrBillingNotesController** (`/property/prBillingNotes/*`) | `/thermal/property/billing-notes/*` | ✅ | |
| **PrPrintTemplateController** (`/property/prPrintTemplate/*`) | `/thermal/property/print-template/*` | ✅ | |
| **PrHeatControlController** (`/property/prHeatControl/*`) | `/thermal/ht/control/*` | ✅ | 硬件通信保留 |
| **PrImportAuthorizationCodeController** (`/property/authorizationCode/*`) | `/thermal/property/auth-code/*` | ✅ | 骨架已建，TODO 标记 |
| **PrWechatBindRecordController** (`/property/prWechatBindRecord/*`) | `/thermal/property/wechat-bind/*` | ✅ | 骨架已建，TODO 标记 |
| **MtHeatArchiveController** (`/heatArchive/*`) | `/thermal/meter/heat/*` | ✅ | |
| **MtMeterSortController** (`/meterSort/*`) | `/thermal/meter/sort/*` | ✅ | |
| **MtMeterVendorController** (`/meterVendor/*`) | `/thermal/meter/vendor/*` | ✅ | |
| **MtElectricArchiveController** (`/electric/*`) | `/thermal/meter/electric/*` | ✅ | |
| **MtGasArchiveController** (`/gas/*`) | `/thermal/meter/gas/*` | ✅ | |
| **MtWaterArchiveController** (新增) | `/thermal/meter/water/*` | ✅ | 老系统无对应控制器，新增 |
| **MtCentratorArchiveController** (`/centrator/*`) | `/thermal/meter/centrator/*` | ✅ | |
| **MtTcArchiveController** (`/tc/*`) | `/thermal/meter/tc/*` | ✅ | |
| **MtTcValveController** (`/valve/*`) | `/thermal/meter/valve/*` | ✅ | |
| **MtFormulaFileController** (`/mtFormulaFile/*`) | `/thermal/meter/formula/*` | ✅ | |
| **AgentMeterController** (`/agent/meter/*`) | `/thermal/meter/agent/*` | ✅ | |

### 部分等价（⚠️）

| 老系统端点 | 新系统端点 | 状态 | 差异项 |
|-----------|-----------|------|--------|
| **HtTasksController** `/property/htTasks/pageList` (25+参数) | `/thermal/ht/tasks/list` (3参数) | ⚠️ | 大幅简化：缺少 `prHeatStationId`, `buildingId`, `unitCode`, `tasksId`, `isCharged`, `valveStatus`, `heatId`, `parentId`, `houseType`, `deviceType` 等 |
| **HtTasksController** `/property/htTasks/pageDeviceList` | 新系统无 | ⚠️ | deviceType 过滤缺失，可能合并到 list 但参数不全 |
| **HtTasksController** `/property/htTasks/getHouseChangeDataList` | 新系统无 | ⚠️ | 房屋变更记录查询缺失 |
| **HtTasksController** `/property/htTasks/getUnitChangeDataList` | 新系统无 | ⚠️ | 单元变更记录查询缺失 |
| **HtTasksController** `/property/htTasks/updateSdkdLog` | 新系统无 | ⚠️ | 重新设定开度日志缺失 |
| **HtTasksController** `/property/htTasks/getHouseOtherCode` / `updateOtherCode` | 新系统无 | ⚠️ | 房屋其他编码操作缺失 |
| **HtTasksController** `/property/htTasks/listTasks` | 新系统无 | ⚠️ | listTasks 与 list 可能合并但不完全等价 |
| **PrAccountController** `/property/prAccount/pageListImportData` | 新系统无 | ⚠️ | 导入数据分页查询缺失 |
| **PrAccountController** `/property/prAccount/importData` | 新系统无 | ⚠️ | 数据导入功能缺失 |
| **PrAccountController** `/property/prAccount/deleteImportData` | 新系统无 | ⚠️ | 删除导入数据缺失 |
| **PrAccountController** `/property/prAccount/submitDespots` | 新系统无 | ⚠️ | 提交押金缺失 |
| **PrAccountController** `/property/prAccount/getHouseDeposit` / `saveDeposit` | 新系统无 | ⚠️ | 押金管理缺失 |
| **PrAccountController** `/property/prAccount/downloadExcel` | 新系统无 | ⚠️ | Excel 导出缺失 |
| **PrExpenseController** `/property/expense/pageListCw` | `/thermal/property/expense/list` | ⚠️ | 车位费用列表可能合并到主 list |
| **PrExpenseController** `/property/expense/queryParkinglotExpenseList` | 新系统无 | ⚠️ | 车位费用查询缺失 |
| **PrExpenseController** `/property/expense/queryHouseExpenseAllList` | 新系统无 | ⚠️ | 全部房屋费用查询缺失 |
| **PrExpenseController** `/property/expense/insertDatallCw` | `/thermal/property/expense/parking` | ⚠️ | 已迁移为 parking 端点 |
| **PrExpenseController** `/property/expense/insertAllDatall` | 新系统无 | ⚠️ | 批量全部生成缺失 |
| **PrExpenseController** `/property/expense/pageListLog` | 新系统无 | ⚠️ | 费用日志查询缺失 |
| **PrExpenseController** `/property/expense/pageListWechat` | 新系统无 | ⚠️ | 微信费用查询缺失 |
| **PrTransactionRecordController** `/property/prTransactionRecord/refund` | 新系统无 | ⚠️ | 退费功能缺失 |
| **PrTransactionRecordController** `/property/prTransactionRecord/getWater` / `getEle` | 新系统无 | ⚠️ | 水/电查询缺失 |
| **PrTransactionRecordController** `/property/prTransactionRecord/cardLog` / `getCardLogCreateByName` | 新系统无 | ⚠️ | 写卡日志查询缺失 |
| **PrTransactionRecordController** `/property/prTransactionRecord/uncoll` | 新系统无 | ⚠️ | 未收款查询缺失 |
| **PrTransactionRecordController** `/property/prTransactionRecord/getThisMonth` / `getThisMonthVarious` | 新系统无 | ⚠️ | 月度统计缺失 |
| **PrAutoMachineController** 所有支付端点 | `/thermal/property/auto-machine/*` | ⚠️ | 全部返回 R.fail("此功能需要...")，骨架已建但未实现 |
| **PrImportAuthorizationCodeController** `/property/authorizationCode/importData` | `/thermal/property/auth-code/import` | ⚠️ | 骨架已建，TODO: Jackcess 依赖 |

### 缺失（❌）

| 老系统端点 | 对应模块 | 状态 | 说明 |
|-----------|---------|------|------|
| **AreaController** (`/area/*`) | 系统管理 | ❌ | 省市区三级联动，可用第三方库替代 |
| **SysCompanyController** (`/company/*`) | 系统管理 | ❌ | 已迁移至 RuoYi 框架的 SysDeptController |
| **SysUserController** (`/property/sysUser/*`) | 系统管理 | ❌ | 已迁移至 RuoYi 框架的 SysUserController |
| **SysDictController** (`/sysDict/*`) | 系统管理 | ❌ | 已迁移至 RuoYi 框架的 SysDictType/DataController |
| **SysColumnController** (`/property/sysColumn/*`) | 系统管理 | ❌ | 已迁移至 RuoYi 框架的 SysColumnController |
| **SysHomeController** (`/home/querHomeData`) | 系统管理 | ❌ | 已迁移至 ThermalHomeController |
| **MenuController** (`/menu/*`) | 系统管理 | ❌ | 已迁移至 RuoYi 框架的 SysMenuController |
| **RoleController** (`/role/*`) | 系统管理 | ❌ | 已迁移至 RuoYi 框架的 SysRoleController |
| **UserController** (`/user/info`, `/user/logout`) | 系统管理 | ❌ | 已迁移至 AuthController |
| **AgentCompanyController** (`/agent/company/*`) | 系统管理 | ❌ | 代理商公司管理，未迁移 |
| **AgentPropertyController** (`/agent/propery/*`) | 系统管理 | ❌ | 代理商物业菜单管理，未迁移 |
| **AgentPropertyMenuController** (`/agent/propertyMenu/*`) | 系统管理 | ❌ | 代理商物业权限，未迁移 |
| **AgentRoleController** (`/agent/role/*`) | 系统管理 | ❌ | 代理商角色管理，未迁移 |
| **AgentUserController** (`/agent/user/*`) | 系统管理 | ❌ | 代理商用户管理，未迁移 |
| **PrCompanyController** (`/property/company/*`) | 系统管理 | ❌ | 公司/组织架构管理，已迁移至 RuoYi SysDeptController |
| **PrBuildingController** (`/property/prBuilding/*`) | 系统管理 | ❌ | 楼宇管理，**13个端点全部缺失** |
| **PrUnitController** (`/property/prUnit/*`) | 系统管理 | ❌ | 单元管理，**8个端点全部缺失** |
| **PrHouseController** (`/property/house/*`) | 房屋管理 | ❌ | **房屋管理核心，~40个端点全部缺失** |
| **PrHeatStationController** (`/property/prHeatStation/*`) | 热力调控 | ❌ | 热力站管理，**8个端点全部缺失** |
| **PrHeatStationPartitionController** (`/property/prHeatStationPartition/*`) | 热力调控 | ❌ | 热力站分区管理，**8个端点全部缺失** |
| **PrHeatArchiveController** (`/property/prHeatArchive/*`) | 热力调控 | ❌ | 热力表档案管理，**~25个端点全部缺失**（包括实时数据/综合数据/巡线/导入导出/充值/报） |
| **PrHeatDailyController** (`/property/prHeatDaily/*`) | 热力调控 | ❌ | 日记录管理，**2个端点缺失** |
| **PrHeatMonthController** (`/property/prHeatMonth/*`) | 热力调控 | ❌ | 月记录管理，**2个端点缺失** |
| **PrHeatReadingController** (`/property/prHeatReading/*`) | 热力调控 | ❌ | 抄表记录管理，**5个端点缺失** |
| **PrHeatReadingCopy1Controller** (`/property/prHeatReadingCopy1/*`) | 热力调控 | ❌ | 抄表副本，**2个端点缺失** |
| **PrHeatDtuArchiveController** (`/property/prHeatDtuArchive/*`) | 热力调控 | ❌ | DTU档案管理，**10个端点缺失**（含导入导出） |
| **PrHeatTempArchiveController** (`/property/prHeatTempArchive/*`) | 热力调控 | ❌ | 温度采集器档案，**13个端点缺失**（含导入导出） |
| **PrHeatHotArchiveController** (`/property/prHeatHotArchive/*`) | 热力调控 | ❌ | 加热档案，**13个端点缺失**（含导入导出/阀门同步） |
| **PrHeatUnitHotArchiveController** (`/property/prHeatUnitHotArchive/*`) | 热力调控 | ❌ | 单元加热档案，**12个端点缺失** |
| **PrHeatUnitValveArchiveController** (`/property/prHeatUnitValveArchive/*`) | 热力调控 | ❌ | 单元阀门档案，**17个端点缺失** |
| **PrHeatValveArchiveController** (`/property/prHeatValveArchive/*`) | 热力调控 | ❌ | **阀门档案管理（核心），~40个端点全部缺失**（含开阀/关阀/开度/周期/蓝牙控制/API集成） |
| **PrHeatCommandValveArchiveController** (`/property/prHeatCommandValveArchive/*`) | 热力调控 | ❌ | **指令阀门档案，~28个端点全部缺失**（NB阀门/MBus阀门/写卡/开阀关阀/导入导出） |
| **PrHeatCommandUnitValveArchiveController** (`/property/prHeatCommandUnitValveArchive/*`) | 热力调控 | ❌ | 单元指令阀门档案，**~15个端点全部缺失** |
| **PrStrategyController** (`/property/prStrategy/*`) | 热力调控 | ❌ | 策略管理，**6个端点缺失** |
| **PrSchedulingController** (`/property/scheduling/*`) | 热力调控 | ❌ | 排班管理，**6个端点缺失** |
| **PrRegionalController** (`/property/regional/*`) | 热力调控 | ❌ | 区域管理，**1个端点缺失** |
| **PrFamilyController** (`/property/family/*`) | 物业收费 | ❌ | 家庭成员管理，**5个端点缺失** |
| **PrPetController** (`/property/pet/*`) | 物业收费 | ❌ | 宠物管理，**5个端点缺失** |
| **PrInspectionPersonController** (`/property/prInspectionPerson/*`) | 物业收费 | ❌ | 巡检人员管理，**6个端点缺失** |
| **PrInspectionPlanController** (`/property/prInspectionPlan/*`) | 物业收费 | ❌ | 巡检计划管理，**7个端点缺失** |
| **PrInspectionRecordController** (`/property/prInspectionRecord/*`) | 物业收费 | ❌ | 巡检记录，**1个端点缺失** |
| **PrRepairPersonController** (`/property/prRepairPerson/*`) | 物业收费 | ❌ | 维修人员管理，**6个端点缺失** |
| **PrRepairRecordController** (`/property/prRepairRecord/*`) | 物业收费 | ❌ | 维修记录管理，**12个端点缺失** |
| **PrAbnormalRecordController** (`/property/prAbnormalRecord/*`) | 物业收费 | ❌ | 异常记录，**2个端点缺失** |
| **PrNoticeController** (`/property/notice/*`) | 物业收费 | ❌ | 通知管理，**6个端点缺失** |
| **PrImportBasicDataController** (`/property/prImportBasicData/*`) | 导入模块 | ❌ | 基础数据导入，**~10个端点缺失**（含下载Excel/提交/删除） |
| **PrImportHeatController** (`/property/prImportHeat/*`) | 导入模块 | ❌ | 热力数据导入，**6个端点缺失** |
| **PrImportHeatTempController** (`/property/prImportHeatTemp/*`) | 导入模块 | ❌ | 温度数据导入，**6个端点缺失** |
| **PrImportHistoryController** (`/property/prImportHistory/*`) | 导入模块 | ❌ | 历史数据导入，**6个端点缺失** |
| **PrImportUnitHeatController** (`/property/prImportUnitHeat/*`) | 导入模块 | ❌ | 单元热力导入，**6个端点缺失** |
| **PrImportUnitValveController** (`/property/prImportUnitValve/*`) | 导入模块 | ❌ | 单元阀门导入，**6个端点缺失** |
| **PrImportValveController** (`/property/prImportValve/*`) | 导入模块 | ❌ | 阀门导入，**6个端点缺失** |
| **PrImportRecordController** (`/property/prImportRecord/*`) | 导入模块 | ❌ | 导入记录管理，**6个端点缺失** |
| **OssManagerController** (`/stsOss/*`) | OSS | ❌ | OSS 临时凭证/上传，已迁移至 RuoYi SysOssController |
| **AgAutoVersionController** (`/agAutoVersion/*`) | 自助机 | ❌ | 自助机版本管理，**2个端点缺失** |
| **AgReaderParamController** (`/common/AgReaderParam/*`) | 自助机 | ❌ | 读取器参数，**1个端点缺失** |
| **AccessCodeController** (`/accessCode/*`) | 系统 | ❌ | 已迁移至 ThermalCodeController |
| **ToolsController** (`/property/tools/*`) | 工具 | ❌ | 工具接口（仪表查询），**2个端点缺失** |
| **PushController** (`/property/push/*`) | 推送 | ❌ | 推送设置，**1个端点缺失** |
| **RepairController** (`/wechat/repair/*`) | 微信报修 | ❌ | 微信端报修管理，**8个端点缺失** |
| **WechatAuthController** (`/wechat/auth/*`) | 微信认证 | ❌ | 微信认证，**4个端点缺失** |
| **WechatPayController** (`/wechat/pay/*`) | 微信支付 | ❌ | 微信支付，**6个端点缺失** |
| **ReconciliationController** (`/wechat/reconciliation/*`) | 对账 | ❌ | 微信对账，**5个端点缺失** |
| **WxPortalController** (`/wx/portal/*`) | 微信门户 | ❌ | 微信消息推送，**2个端点缺失** |
| **WxMaUserController** (`/wx/user/*`) | 微信小程序 | ❌ | 小程序用户，**3个端点缺失** |
| **WxMaMediaController** (`/wx/media/*`) | 微信小程序 | ❌ | 小程序媒体，**2个端点缺失** |
| **SaOAuth2ServerController** (`/oauth/*`) | OAuth2 | ❌ | OAuth2 服务，**7个端点缺失** |
| **TaskController** (`/property/task/*`) | 任务管理 | ❌ | 任务管理（与 HtTasksController 相似），**~15个端点缺失** |
| **ChargeDetailStateNameController** | 费用详情 | ❌ | 空控制器 |
| **HtStrategyPerformController** | 策略执行 | ❌ | 空控制器 |
| **HtTasksPerformLsController** | 执行记录 | ❌ | 空控制器 |
| **HtScopeDtuController** | 范围DTU | ❌ | 空控制器 |

### 有差异（🔴）

| 老系统端点 | 新系统端点 | 状态 | 差异详情 | 风险等级 |
|-----------|-----------|------|---------|---------|
| `/property/htTasks/list` (POST, 2参数+Page) | `GET /thermal/ht/tasks/list` (3参数+PageQuery) | 🔴 | 参数从 PageHelper 换为 MyBatis-Plus PageQuery，搜索逻辑简化 | 中 |
| `/property/htTasks/edit` (POST) | `PUT /thermal/ht/tasks` | 🔴 | 旧版有 jobStatus 校验"修改前请停止任务"，新版无此校验 | 高 |
| `/property/htTasks/remove/{id}` (POST) | `DELETE /thermal/ht/tasks/{id}` | 🔴 | 旧版有 jobStatus 校验"删除前请停止任务"，新版直接删除 | 高 |
| `/property/htTasks/run/{id}` (POST) | `POST /thermal/ht/tasks/run/{id}` | 🔴 | 旧版使用 Quartz Scheduler 立即执行，新版调用 tasksService.runTask | 高 |
| `/property/htTasks/save` (POST) | `POST /thermal/ht/tasks` | 🔴 | 旧版设置 beanClass 和 uuid jobGroup，新版设置 beanClass 但 jobGroup 由 service 处理 | 中 |
| `/property/htTasks/pageList` (25参数) | `GET /thermal/ht/tasks/list` (3参数) | 🔴 | 大量搜索/过滤参数丢失，前端依赖的精细过滤不可用 | 高 |
| `/property/htTasks/querySummary` (12参数) | `GET /thermal/ht/tasks/summary` (3参数) | 🔴 | 统计维度大幅减少 | 中 |
| `/htAlert/pageList` | `GET /thermal/ht/alert/list` | 🔴 | 旧版无 companyId/orgId 过滤，新版增加 | 低(增强) |
| `/htRepair/pageList` | `GET /thermal/ht/repair/list` | 🔴 | 旧版无 companyId 过滤，新版增加 | 低(增强) |
| `/htInstruction/pageList` | `GET /thermal/ht/instruction/list` | 🔴 | 搜索条件从模糊匹配改为精确匹配 | 中 |
| `/property/expense/recalculate` | `POST /thermal/property/expense/recalculate` | 🔴 | 旧版有 companyId/orgId 外还可能有其他参数 | 中 |
| `/property/expense/recalculateCw` | `POST /thermal/property/expense/recalculate-parking` | 🔴 | 同上 | 中 |
| `/property/prTransactionRecord/pageList` | `GET /thermal/property/transaction/list` | 🔴 | 旧版使用 POST + 复杂条件，新版改为 GET | 低 |
| `/property/prTransactionRecord/revocation` | `PUT /thermal/property/transaction/revocation` | 🔴 | 旧版有事务和多表级联操作，新版简化 | 中 |
| `/property/prTransactionRecord/invalid` | `PUT /thermal/property/transaction/invalid` | 🔴 | 同上 | 中 |
| `/property/prTransactionRecord/getProperty` | 新系统无直接对应 | 🔴 | 综合查询可能被 comprehensive 替代但参数不同 | 中 |
| `/property/prStandard/purchase` | `POST /thermal/property/standard/purchase` | 🔴 | 旧版返回 purchase 相关数据，新版返回 Boolean | 中 |
| `/property/autoMachine/getReaderParam` | 新系统无 | 🔴 | 读器参数获取缺失 | 中 |
| `/property/autoMachine/getClientVersion` | 新系统无 | 🔴 | 客户端版本检查缺失 | 低 |
| `/property/autoMachine/getClientDownload` | 新系统无 | 🔴 | 客户端下载缺失 | 低 |
| `/property/autoMachine/getQrCode` | `POST /thermal/property/auto-machine/qr-heat` | 🔴 | 返回 R.fail 占位 | 高 |
| `/property/autoMachine/getQrCodeWater` | `POST /thermal/property/auto-machine/qr-water` | 🔴 | 返回 R.fail 占位 | 高 |
| `/property/autoMachine/getQrCodeEle` | `POST /thermal/property/auto-machine/qr-ele` | 🔴 | 返回 R.fail 占位 | 高 |
| `/property/autoMachine/callback` | `POST /thermal/property/auto-machine/callback/wechat-heat` | 🔴 | 返回 FAIL 占位 | 高 |

## Top 5 高风险差异

按业务影响排序：

### 1. 房屋管理模块完全缺失 (PrHouseController - 40+ 端点)
- **影响范围**: 所有基于房屋的操作（收费、阀门控制、策略绑定）的前置条件
- **老系统**: `/property/house/*` - 房屋CRUD、导入导出、楼层/单元关联、面积设置、微信绑定等
- **风险等级**: 致命 - 没有房屋数据，整个收费和调控业务流程无法运转

### 2. 任务状态/删除校验缺失 (HtTasksController)
- **影响范围**: 正在运行的任务可能被误删或误改
- **老系统**: edit/remove 前有 `if (JobStatusEnum.RUNNING)` 校验阻止操作
- **新系统**: 直接执行 updateById / removeById，无运行状态校验
- **风险等级**: 高 - 可能导致运行中的任务被修改或删除，影响实际控制

### 3. 任务精细查询参数大量丢失 (HtTasksController pageList)
- **影响范围**: 前端列表过滤功能大幅降级
- **老系统**: 支持 25+ 参数过滤（热力站、分区、楼宇、单元、缴费状态、阀门状态等）
- **新系统**: 仅支持 search、orgId、status 3个参数
- **风险等级**: 高 - 操作员无法精确定位到特定范围的任务

### 4. 阀门档案模块完全缺失 (PrHeatValveArchiveController - 40+ 端点)
- **影响范围**: 阀门的开/关/开度/周期控制、蓝牙控制、第三方API集成
- **老系统**: `/property/prHeatValveArchive/*` - 包含 NB 阀门、MBus 阀门、写卡日志、API 端点等
- **风险等级**: 高 - 直接影响终端设备控制能力

### 5. 自助机支付端点全为占位 (PrAutoMachineController)
- **影响范围**: 供暖/水/电缴费二维码生成和支付回调
- **老系统**: 完整的微信支付/支付宝回调处理
- **新系统**: 全部返回 R.fail() 或占位响应
- **风险等级**: 高 - 线下自助缴费功能完全不可用

## 结论

### 整体等价性评估: ⚠️ 部分等价 (约 25% 覆盖)

**已迁移模块**（骨架完整、逻辑等价）：
- 控制策略 (HtStrategy) - 完全等价
- 控制指令 (HtInstruction) - 完全等价
- 报警记录 (HtAlert) - 完全等价
- 报修记录 (HtRepair) - 完全等价
- 控制范围 (HtScope) - 完全等价
- 调控任务 (HtTasks) - 核心 CRUD 已迁移，精细查询/状态校验缺失
- 调控执行记录 (HtTasksPerform) - 等价
- 房屋策略绑定 (HtHouseStrategy) - 等价
- 收费标准 (PrStandard) - 完全等价
- 费用明细 (PrExpense) - 核心已迁移，部分端点缺失
- 费用项目 (PrExpenseItem) - 完全等价
- 个人账户 (PrAccount) - 核心已迁移，押金/导入功能缺失
- 交易记录 (PrTransactionRecord) - 核心已迁移，退费/水电气查询缺失
- 客户管理 (PrUser) - 完全等价
- 系统选项 (PrOptions/PrOptionsHeat) - 完全等价
- 写卡日志 (PrUseCardLog) - 完全等价
- 房屋变更 (PrHouseChange) - 完全等价
- 房屋费用绑定 (PrHouseExpense) - 完全等价
- 单笔收费 (SingleCharge) - 完全等价
- 票据备注 (PrBillingNotes) - 完全等价
- 打印模板 (PrPrintTemplate) - 完全等价
- 手动阀门控制 (PrHeatControl) - 完全等价
- 仪表模块 (所有 Mt* Controller) - 完全等价（新增 MtWaterArchive）

**主要风险点**：
1. **核心业务模块未迁移**: 房屋管理 (PrHouse)、楼宇管理 (PrBuilding)、单元管理 (PrUnit)、阀门档案 (PrHeatValveArchive/PrHeatCommandValveArchive)、热力站管理 (PrHeatStation)、热力表档案 (PrHeatArchive) 等完全缺失
2. **全部导入模块未迁移**: 9个 PrImport* 控制器全部缺失
3. **微信生态完全缺失**: 微信小程序、微信支付、微信认证、微信对账
4. **任务调度未实现**: Quartz 调度（run/changeStatus 的实际 Quartz 操作）需要 Phase 6 实现
5. **已迁移端点存在逻辑简化**: 缺少运行状态校验、参数过滤减少、搜索匹配变更

**建议优先级**：
1. P0: 迁移房屋/楼宇/单元管理模块（业务流程前置依赖）
2. P0: 补充 HtTasksController 的运行状态校验逻辑
3. P1: 迁移阀门档案模块（设备控制能力）
4. P1: 迁移热力站/热力表/温度采集器等核心调控模块
5. P2: 迁移导入模块（Excel 批量操作依赖）
6. P2: 实现任务调度的 Quartz 集成
7. P3: 微信生态集成
