# Pr 物业/收费档案导入模块迁移审核报告

**审核时间**: 2026-04-25
**审核范围**: 热站/分区、热表档案、抄表读数、热控、导入、巡检等模块
**旧系统路径**: D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/
**新系统路径**: D:/chonggou/thermal-platform-new/sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/

---

## 一、迁移状态总表

| 旧系统 Controller | 旧系统 API 数量 | 新系统对应 | 迁移状态 | 备注 |
|---|---|---|---|---|
| **热站/分区** | | | | |
| PrHeatStationController | 7 | PrHeatStationController | 已迁移 | API 兼容性好 |
| PrHeatStationPartitionController | 7 | PrHeatStationPartitionController | 已迁移 | API 兼容性好 |
| **热表档案** | | | | |
| PrHeatArchiveController | 18 | PrHeatArchiveController | 部分迁移 | 缺少大量业务逻辑 |
| PrHeatDtuArchiveController | ? | 未找到对应 | 未迁移 | DTU 档案待迁移 |
| PrHeatValveArchiveController | ? | PrHeatValveArchiveController | 未迁移 | 阀门档案待迁移 |
| PrHeatCommandValveArchiveController | ? | PrHeatCommandValveArchiveController | 未迁移 | 指令阀门档案待迁移 |
| PrHeatCommandUnitValveArchiveController | ? | PrHeatCommandUnitValveArchiveController | 未迁移 | 指令单元阀门待迁移 |
| PrHeatTempArchiveController | ? | PrHeatTempArchiveController | 未迁移 | 温控器档案待迁移 |
| PrHeatHotArchiveController | ? | PrHeatHotArchiveController | 未迁移 | 热表档案待迁移 |
| PrHeatUnitHotArchiveController | ? | PrHeatUnitHotArchiveController | 未迁移 | 单元热表待迁移 |
| PrHeatUnitValveArchiveController | ? | PrHeatUnitValveArchiveController | 未迁移 | 单元阀门待迁移 |
| **抄表/读数** | | | | |
| PrHeatDailyController | 3 | PrHeatDailyController | 部分迁移 | 生成功能待实现 |
| PrHeatMonthController | ? | PrHeatMonthController | 未迁移 | 月表功能待迁移 |
| PrHeatReadingController | 4 | PrHeatReadingController | 部分迁移 | 缺少导出和趋势功能 |
| PrHeatReadingCopy1Controller | ? | 未找到对应 | 未迁移 | 备用抄表待迁移 |
| **热控** | | | | |
| PrHeatControlController | 10 | PrHeatControlController | 部分迁移 | 通信逻辑已简化 |
| **导入** | | | | |
| PrImportHeatController | 5 | PrImportHeatController | 骨架迁移 | 仅 API 框架，无实现 |
| PrImportHeatTempController | ? | 未找到对应 | 未迁移 | 温控器导入待迁移 |
| PrImportHistoryController | 5 | PrImportHistoryController | 骨架迁移 | 仅 API 框架，无实现 |
| PrImportRecordController | 5 | PrImportRecordController | 骨架迁移 | 仅 API 框架，无实现 |
| PrImportUnitHeatController | ? | 未找到对应 | 未迁移 | 单元热表导入待迁移 |
| PrImportUnitValveController | ? | 未找到对应 | 未迁移 | 单元阀门导入待迁移 |
| PrImportValveController | 5 | PrImportValveController | 骨架迁移 | 仅 API 框架，无实现 |
| PrImportAuthorizationCodeController | ? | PrImportAuthorizationCodeController | 未迁移 | 授权码导入待迁移 |
| PrImportBasicDataController | ? | PrImportBasicDataController | 未迁移 | 基础数据导入待迁移 |
| **巡检** | | | | |
| PrInspectionPersonController | ? | PrInspectionPersonController | 未迁移 | 巡检人员待迁移 |
| PrInspectionPlanController | ? | PrInspectionPlanController | 未迁移 | 巡检计划待迁移 |
| PrInspectionRecordController | ? | PrInspectionRecordController | 未迁移 | 巡检记录待迁移 |
| **其他** | | | | |
| PrPrintTemplateController | ? | PrPrintTemplateController | 未迁移 | 打印模板待迁移 |
| PrPetController | ? | PrPetController | 未迁移 | 宠物管理待迁移 |
| PrOptionsController | ? | PrOptionsController | 未迁移 | 配置管理待迁移 |
| PrOptionsHeatController | ? | PrOptionsHeatController | 未迁移 | 热力配置待迁移 |
| PrRepairPersonController | ? | PrRepairPersonController | 未迁移 | 维修人员待迁移 |
| PrRepairRecordController | 11 | PrRepairRecordController | 部分迁移 | 缺少 OSS 图片处理 |
| PrSchedulingController | ? | PrSchedulingController | 未迁移 | 排班管理待迁移 |
| PrStrategyController | 6 | PrStrategyController | 部分迁移 | 缺少层级校验 |
| PrNoticeController | ? | PrNoticeController | 未迁移 | 通知公告待迁移 |
| PrWechatBindRecordController | ? | PrWechatBindRecordController | 未迁移 | 微信绑定待迁移 |

**统计**:
- 已迁移: 2 个 (PrHeatStationController, PrHeatStationPartitionController)
- 部分迁移: 7 个 (PrHeatArchiveController, PrHeatDailyController, PrHeatReadingController, PrHeatControlController, 4个导入Controller骨架, PrRepairRecordController, PrStrategyController)
- 未迁移: 29 个

---

## 二、已迁移功能审核

### 2.1 PrHeatStationController（换热站管理）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prHeatStation/pageList | /thermal/ht/station/list | GET → GET | 兼容 |
| /property/prHeatStation/getDataById/{id} | /thermal/ht/station/{id} | GET → GET | 兼容 |
| /property/prHeatStation/insertData | /thermal/ht/station | POST → POST | 兼容 |
| /property/prHeatStation/updateData | /thermal/ht/station | POST → PUT | 兼容 |
| /property/prHeatStation/deleteData | /thermal/ht/station/{id} | POST → DELETE | 兼容 |
| /property/prHeatStation/getDataByCompanyId | /thermal/ht/station/company/{companyId} | GET → GET | 兼容 |
| /property/prHeatStation/getDataByOrgId | /thermal/ht/station/org/{orgId} | GET → GET | 兼容 |

**业务逻辑差异**:
- 旧系统: 使用 `@RequestMapping` 不区分 HTTP 方法
- 新系统: 使用 RESTful 风格的 `@GetMapping/@PostMapping/@PutMapping/@DeleteMapping`
- 新系统: 增加了 `@SaCheckPermission` 权限校验
- 新系统: 增加了 `@SaCheckLogin` 登录校验
- 新系统: 使用 `PageQuery` 统一分页参数

**问题列表**:
- 无重大问题

---

### 2.2 PrHeatStationPartitionController（换热站分区）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prHeatStationPartition/pageList | /thermal/ht/station-partition/list | GET → GET | 兼容 |
| /property/prHeatStationPartition/getDataById/{id} | /thermal/ht/station-partition/{id} | GET → GET | 兼容 |
| /property/prHeatStationPartition/insertData | /thermal/ht/station-partition | POST → POST | 兼容 |
| /property/prHeatStationPartition/updateData | /thermal/ht/station-partition | POST → PUT | 兼容 |
| /property/prHeatStationPartition/deleteData | /thermal/ht/station-partition/{id} | POST → DELETE | 兼容 |
| /property/prHeatStationPartition/getDataByCompanyId | /thermal/ht/station-partition/company/{companyId} | GET → GET | 兼容 |
| /property/prHeatStationPartition/getPartitionByHeatStationId | /thermal/ht/station-partition/station/{stationId} | GET → GET | 兼容 |

**业务逻辑差异**:
- 与 PrHeatStationController 类似的改进
- 新系统增加了 `seq` 字段排序

**问题列表**:
- 无重大问题

---

### 2.3 PrHeatArchiveController（房屋热表配表）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prHeatArchive/pageList | /thermal/ht/heat-archive/list | GET → GET | 兼容 |
| /property/prHeatArchive/getHeatDataById/{id} | /thermal/ht/heat-archive/{id} | GET → GET | 兼容 |
| /property/prHeatArchive/insertHeatData | /thermal/ht/heat-archive | POST → POST | 兼容 |
| /property/prHeatArchive/updateHeatData | /thermal/ht/heat-archive | PUT → PUT | 兼容 |
| /property/prHeatArchive/deleteHeatData/{id} | /thermal/ht/heat-archive/{id} | POST → DELETE | 兼容 |
| /property/prHeatArchive/stopHeatMeter/{id} | /thermal/ht/heat-archive/stopMeter/{id} | GET → POST | 兼容 |
| /property/prHeatArchive/startHeatMeter/{id} | /thermal/ht/heat-archive/startMeter/{id} | GET → POST | 兼容 |
| /property/prHeatArchive/queryCompanyHeat | /thermal/ht/heat-archive/queryCompanyHeat | GET → GET | 兼容 |
| /property/prHeatArchive/calculate/{id} | /thermal/ht/heat-archive/calculate/{id} | GET → GET | 待实现 |

**业务逻辑差异**:
- 新系统增加了表号重复校验
- 新系统使用了 BO/VO 分层模式
- 新系统使用软删除 `@TableLogic`

**缺失功能** (Critical):
1. **仪表更换功能** (`replaceHeatMeter`) - 旧系统 line 164-204
2. **实时数据查询** (`realTimeData`) - 旧系统 line 217-220
3. **综合查询** (`zonghe`) - 旧系统 line 223-227
4. **手动控制** (`manualControl`) - 旧系统 line 312-1113，非常复杂的阀门控制逻辑
5. **设置阀门组号** (`setValveGroupParam`) - 旧系统 line 244-298
6. **巡测功能** (`xunce`) - 旧系统 line 1125-1166
7. **导出全部配表** (`exportAll`) - 旧系统 line 1172-1176
8. **导入修改配表** (`importData`) - 旧系统 line 1240-1284
9. **充值查询** (`findMeter`) - 旧系统 line 1296-1299
10. **仪表充值** (`recharge`) - 旧系统 line 1304-1404，包含交易记录生成
11. **收费明细报表** (`selectReport`) - 旧系统 line 1420-1428
12. **仪表历史报表** (`selectMeterReport`) - 旧系统 line 1433-1441

**问题列表**:
- **Critical**: 新系统只迁移了基础的 CRUD 功能，缺少核心业务逻辑
- **Critical**: 仪表更换逻辑涉及余额处理，缺失会影响财务准确性
- **Important**: 实时数据查询是热力调控的基础功能
- **Important**: 手动控制功能涉及多种指令类型（开度、开关、状态查询、上报周期等）

---

### 2.4 PrHeatReadingController（热表抄表）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prHeatReading/pageList | /thermal/ht/heat-reading/list | GET → GET | 兼容 |
| /property/prHeatReading/pageListTrend | /thermal/ht/heat-reading/trend | POST → POST | 待实现 |
| /property/prHeatReading/pageListTrendS | 未迁移 | - | 未迁移 |
| /property/prHeatReading/exportAll | /thermal/ht/heat-reading/exportAll | GET → GET | 待实现 |

**业务逻辑差异**:
- 旧系统导出功能支持大文件分批处理（每批 2000 条）
- 旧系统支持多种仪表类型的导出（阀门、热表、温控器）

**问题列表**:
- **Important**: 缺少阀门趋势数据查询功能
- **Important**: 缺少分批导出逻辑，可能存在内存溢出风险
- **Minor**: 缺少首页阀门趋势图功能 (`pageListTrendS`)

---

### 2.5 PrHeatDailyController（热表日表）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prHeatDaily/pageList | /thermal/ht/heat-daily/list | GET → GET | 兼容 |
| /property/prHeatDaily/setHeat | /thermal/ht/heat-daily/setHeat | GET → POST | 待实现 |

**业务逻辑差异**:
- 旧系统的 `setHeat` 方法包含 5 个步骤：
  1. `setIsValid` - 设置有效性
  2. `setHeatDaily` - 生成日表
  3. `setSteps` - 设置阶梯
  4. `setQtyStepsN` - 设置阶梯用量
  5. `setCurrentReading` - 设置当前读数

**问题列表**:
- **Important**: 日表生成功能未实现，影响费用结算

---

### 2.6 PrHeatControlController（手动阀门控制）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prHeatControl/handControl | /thermal/ht/control/manual | GET → POST | 兼容 |
| /property/prHeatControl/selectMeter | /thermal/ht/control/query | GET → GET | 兼容 |
| /property/prHeatControl/openValve | /thermal/ht/control/openValve | GET → POST | 兼容 |
| /property/prHeatControl/closeValve | /thermal/ht/control/closeValve | GET → POST | 兼容 |
| /property/prHeatControl/add | /thermal/ht/control/add | GET → POST | 兼容 |
| /property/prHeatControl/sub | /thermal/ht/control/sub | GET → POST | 兼容 |
| /property/prHeatControl/add1 | 未迁移 | - | 未迁移 |
| /property/prHeatControl/sub1 | 未迁移 | - | 未迁移 |
| /property/prHeatControl/pageList | 未迁移 | - | 未迁移 |

**业务逻辑差异**:
- 新系统封装了 `HeatMeterControl` 工具类
- 新系统增加了 `generateCommand` 调试端点
- 新系统增加了参数校验（maxAdjust, minAdjust）

**问题列表**:
- **Minor**: 缺少 `add1`/`sub1` 方法（带线程等待的版本）
- **Minor**: 缺少 `pageList` 方法（查询阀门状态）

---

### 2.7 导入模块（PrImport*Controller 系列）

**迁移状态**: 所有导入模块仅有 API 骨架，核心业务逻辑全部缺失

**旧系统导入流程** (以 PrImportHeatController 为例):
1. **下载模板** (`downloadExcel`) - 包含已有数据
2. **检查未提交数据** - 防止重复导入
3. **Excel 解析** - 使用 EasyExcel 从第 3 行开始读取（跳过标题和示例行）
4. **数据导入** (`importData`) - 插入临时表
5. **更新房屋 ID** (`updateHouseId`)
6. **数据校验** (`check`) - 检查仪表 ID、单价 ID 是否匹配
7. **返回未匹配数据** - 前端展示错误
8. **提交数据** (`submitData`) - 从临时表提交到正式表
9. **删除数据** (`deleteData`) - 清理临时数据

**新系统缺失内容** (Critical):
1. **Excel 模板生成** - 包含表头样式和已有数据
2. **Excel 数据解析** - EasyExcel 配置和校验
3. **临时表机制** - 所有导入模块都需要临时表
4. **数据校验逻辑** - 房屋匹配、仪表匹配、单价匹配
5. **错误数据处理** - 返回未匹配的记录
6. **数据提交逻辑** - 从临时表到正式表的迁移

**问题列表**:
- **Critical**: 所有导入功能仅有 API 骨架，无法实际使用
- **Critical**: 缺少临时表设计和实现
- **Important**: 缺少 Excel 单元格样式配置（旧系统使用橙色标识必填项）
- **Important**: 缺少数据校验和错误处理机制

---

### 2.8 PrRepairRecordController（报修记录）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prRepairRecord/pageList | /thermal/property/repair-record/list | GET → GET | 兼容 |
| /property/prRepairRecord/getPersonByCompanyId | /thermal/property/repair-record/person/company/{companyId} | GET → GET | 兼容 |
| /property/prRepairRecord/getPersonByOrgId | /thermal/property/repair-record/person/org | GET → GET | 兼容 |
| /property/prRepairRecord/updateData | /thermal/property/repair-record/dispatch | POST → PUT | 兼容 |
| /property/prRepairRecord/select | 未迁移 | - | 未迁移 |
| /property/prRepairRecord/insertRepairItems | 未迁移 | - | 未迁移 |
| /property/prRepairRecord/insertRecordData | /thermal/property/repair-record | POST → POST | 兼容 |
| /property/prRepairRecord/updateRecordData | /thermal/property/repair-record | POST → PUT | 兼容 |
| /property/prRepairRecord/deleteRecordData | /thermal/property/repair-record/{id} | GET → DELETE | 兼容 |
| /property/prRepairRecord/getHouseIsOwe | 未迁移 | - | 未迁移 |
| /property/prRepairRecord/updateDataService | /thermal/property/repair-record/evaluate | GET → PUT | 兼容 |
| /property/prRepairRecord/getAllTypeCount | /thermal/property/repair-record/count/{companyId} | GET → GET | 兼容 |

**业务逻辑差异**:
- 旧系统 `select` 方法包含阿里云 OSS 图片路径转换
- 旧系统 `insertRepairItems` 涉及富文本内容处理
- 旧系统 `getHouseIsOwe` 查询欠费房屋

**问题列表**:
- **Important**: 缺少报修项目图片的 OSS 处理
- **Minor**: 缺少欠费房屋查询
- **Minor**: 缺少报修项目富文本处理

---

### 2.9 PrStrategyController（策略设置）

**API 对比**:
| 旧端点 | 新端点 | HTTP 方法 | 状态 |
|---|---|---|---|
| /property/prStrategy/pageStrategyList | /thermal/property/strategy/list | GET → GET | 兼容 |
| /property/prStrategy/insertData | /thermal/property/strategy | POST → POST | 兼容 |
| /property/prStrategy/updateData | /thermal/property/strategy | POST → PUT | 兼容 |
| /property/prStrategy/deleteData | /thermal/property/strategy/{id} | GET → DELETE | 兼容 |
| /property/prStrategy/getDataByCompanyId | 未迁移 | - | 未迁移 |

**业务逻辑差异**:
- 旧系统在 `insertData`/`updateData` 中检查基础策略唯一性
- 旧系统返回值包含错误信息

**问题列表**:
- **Important**: 缺少基础策略层级校验（level=0 只能有一个）
- **Minor**: 缺少按公司查询策略列表

---

## 三、未迁移功能清单

| 旧系统功能 | 优先级 | 建议处理方式 |
|---|---|---|
| **热表档案子类** | | |
| PrHeatDtuArchiveController | High | 创建 DTU 档案管理模块 |
| PrHeatValveArchiveController | High | 创建阀门档案管理模块 |
| PrHeatCommandValveArchiveController | High | 创建指令阀门档案模块 |
| PrHeatCommandUnitValveArchiveController | High | 创建指令单元阀门模块 |
| PrHeatTempArchiveController | Medium | 创建温控器档案模块 |
| PrHeatHotArchiveController | High | 创建热表档案模块 |
| PrHeatUnitHotArchiveController | Medium | 创建单元热表模块 |
| PrHeatUnitValveArchiveController | Medium | 创建单元阀门模块 |
| **导入功能** | | |
| PrImportHeatTempController | High | 完成温控器导入功能 |
| PrImportUnitHeatController | High | 完成单元热表导入功能 |
| PrImportUnitValveController | High | 完成单元阀门导入功能 |
| PrImportAuthorizationCodeController | Medium | 完成授权码导入功能 |
| PrImportBasicDataController | High | 完成基础数据导入功能 |
| **抄表功能** | | |
| PrHeatMonthController | High | 创建月表管理模块 |
| PrHeatReadingCopy1Controller | Low | 评估是否需要备用抄表 |
| **巡检功能** | | |
| PrInspectionPersonController | Medium | 创建巡检人员管理 |
| PrInspectionPlanController | Medium | 创建巡检计划管理 |
| PrInspectionRecordController | Medium | 创建巡检记录管理 |
| **其他功能** | | |
| PrPrintTemplateController | Low | 评估打印模板需求 |
| PrPetController | Low | 评估宠物管理需求 |
| PrOptionsController | Medium | 迁移配置管理 |
| PrOptionsHeatController | High | 迁移热力配置 |
| PrRepairPersonController | Medium | 迁移维修人员管理 |
| PrSchedulingController | Low | 评估排班管理需求 |
| PrNoticeController | Medium | 迁移通知公告 |
| PrWechatBindRecordController | Medium | 迁移微信绑定 |

---

## 四、问题汇总

### Critical（阻塞性问题，必须修复）

1. **导入功能完全缺失**
   - 所有 `PrImport*Controller` 只有 API 骨架
   - 缺少 Excel 解析、临时表、数据校验、提交逻辑
   - 影响范围: 热表、阀门、交易记录、历史数据等所有导入场景

2. **PrHeatArchiveController 核心业务逻辑缺失**
   - 缺少仪表更换功能（涉及财务处理）
   - 缺少实时数据查询（热力调控基础）
   - 缺少手动控制（多种指令类型）
   - 缺少充值功能（交易记录生成）

3. **热表档案子类全部未迁移**
   - DTU、阀门、温控器、热表等档案
   - 这些是热力系统的核心设备档案

4. **PrHeatDailyController 日表生成功能未实现**
   - 影响费用结算
   - 包含 5 个复杂步骤

### Important（重要问题，应该修复）

1. **PrHeatReadingController 导出功能**
   - 缺少大文件分批处理逻辑
   - 缺少阀门趋势数据查询

2. **PrRepairRecordController OSS 图片处理**
   - 缺少阿里云 OSS 路径转换
   - 影响报修图片展示

3. **PrOptionsHeatController 未迁移**
   - 热力配置是热力调控的基础
   - 包含电信 IoT 配置、流水号配置等

4. **PrHeatArchiveController 缺少多种查询**
   - 综合查询、仪表历史报表、收费明细报表

### Minor（次要问题，可以延后）

1. **PrHeatControlController 缺少部分方法**
   - add1/sub1（带线程等待）
   - pageList（查询阀门状态）

2. **PrStrategyController 缺少层级校验**
   - 基础策略唯一性检查

3. **巡检模块全部未迁移**
   - PrInspectionPerson/Plan/Record

4. **其他辅助功能未迁移**
   - 打印模板、宠物管理、排班管理

---

## 五、架构改进建议

### 5.1 导入模块设计

建议创建统一的导入基础架构：

```java
// 基础导入服务
public interface IBaseImportService<T> {
    // 下载模板（含已有数据）
    void downloadTemplate(HttpServletResponse response, String companyId, String orgId);
    
    // 解析 Excel
    List<T> parseExcel(MultipartFile file);
    
    // 校验数据
    ImportResult validate(List<T> data);
    
    // 提交到正式表
    void submitToFormal();
    
    // 清理临时数据
    void clearTemp();
}
```

### 5.2 热表档案统一管理

建议将所有热表档案子类统一管理：

```
PrHeatArchive (基类)
├── PrHeatValveArchive (阀门)
├── PrHeatHotArchive (热表)
├── PrHeatTempArchive (温控器)
├── PrHeatDtuArchive (DTU)
├── PrHeatCommandValveArchive (指令阀门)
└── PrHeatUnit*Archive (单元系列)
```

### 5.3 Excel 样式配置

建议使用 EasyExcel 的 `@ColumnWidth` 和 `@HeadFontStyle` 注解统一配置：

```java
@Data
@ColumnWidth(18)
@HeadFontStyle(fontHeightInPoints = 11, bold = true)
@ContentFillPatternType(FillPatternType.SOLID_FOREGROUND)
@ContentFontStyle(fontHeightInPoints = 10)
public class PrImportHeatBO {
    // ...
}
```

---

## 六、下一步行动计划

### Phase 1 - Critical 功能（优先）
1. 完成导入模块核心逻辑
2. 完成 PrHeatArchiveController 缺失功能
3. 迁移热表档案子类

### Phase 2 - Important 功能
1. 完成 PrHeatReadingController 导出和趋势
2. 完成 PrRepairRecordController OSS 处理
3. 迁移 PrOptionsHeatController

### Phase 3 - 其他功能
1. 迁移巡检模块
2. 迁移其他辅助功能

---

## 七、总结

Pr 物业/收费档案导入模块的迁移工作进展**约 30%**。基础 CRUD 功能已迁移，但核心业务逻辑大量缺失。

**主要成就**:
- 热站和分区管理已完整迁移
- API 风格统一为 RESTful
- 增加了权限和登录校验

**主要风险**:
- 导入功能完全不可用
- 热表档案核心业务逻辑缺失
- 设备档案子类全部未迁移

**建议**:
优先完成 Phase 1 的 Critical 功能，特别是导入模块和 PrHeatArchiveController 的核心业务逻辑。
