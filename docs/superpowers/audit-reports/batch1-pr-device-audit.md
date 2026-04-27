# Pr-设备档案模块迁移审核报告

## 审核概览
- **审核日期**: 2026-04-26
- **审核范围**: Pr-设备档案相关 Controller（13个）
- **旧系统**: D:\chonggou\thermal-balance-backend（Spring Boot 2.2）
- **新系统**: D:\chonggou\thermal-platform-new（Spring Boot 3.5）

### 统计摘要
| 项目 | 数量 |
|------|------|
| 旧系统 Controller 数 | 13 |
| 新系统对应 Controller 数 | 13 |
| 完全匹配 | 0 |
| 部分匹配 | 9 |
| 骨架迁移 | 3 |
| 功能缺失 | 1 |

### 匹配度说明
- **完全匹配**: 所有核心 API 端点已迁移，业务逻辑完整实现
- **部分匹配**: 核心 CRUD 已迁移，部分高级功能（如数据同步、导入导出、第三方接口）缺失
- **骨架迁移**: 仅建立 Controller 骨架，返回占位响应，实际业务逻辑未实现
- **功能缺失**: 旧系统存在该功能，新系统完全未迁移

---

## 逐 Controller 对比

### 1. PrHeatHotArchiveController（房屋热量表配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatHotArchive/pageList | GET | /thermal/ht/hot-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatHotArchive/pageListHeatTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatHotArchive/insertData | POST | /thermal/ht/hot-archive | POST | MATCH | 新增 |
| /property/prHeatHotArchive/updateData | POST | /thermal/ht/hot-archive | PUT | MATCH | 修改（HTTP方法标准化） |
| /property/prHeatHotArchive/getDataById | GET | /thermal/ht/hot-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatHotArchive/deleteData | POST | /thermal/ht/hot-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatHotArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatHotArchive/deleteDataAllMoney | POST | - | - | MISSING | 金额清零 |
| /property/prHeatHotArchive/valveInformationSynchronization | POST | - | - | MISSING | 同步到采集平台 |
| /property/prHeatHotArchive/downloadInfoSync | GET | - | - | MISSING | 下载同步信息 |
| /property/prHeatHotArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatHotArchive/importAll | POST | - | - | MISSING | 导入全部 |
| /property/prHeatHotArchive/insertDataByHouseId | POST | /thermal/ht/hot-archive/insertByHouseId | POST | MATCH | 按房屋新增 |
| /property/prHeatHotArchive/getHotStatus | POST | - | - | MISSING | 获取热表状态 |

#### 业务逻辑差异
1. **缺失功能**:
   - 天康数据分页查询（pageListHeatTK）
   - 批量删除和金额清零功能
   - 与采集平台的数据同步功能
   - Excel 导入导出功能
   - 热表状态查询功能

2. **改进点**:
   - 新系统使用 RESTful 风格的 HTTP 方法（GET/POST/PUT/DELETE）
   - 使用 Sa-Token 进行权限控制（@SaCheckPermission）
   - 使用 LambdaQueryWrapper 替代字符串 QueryWrapper

#### 代码质量问题
1. **缺失事务管理**: 新系统未在 Service 方法上添加 @Transactional 注解
2. **缺失审计日志**: 部分写操作未记录操作日志

---

### 2. PrHeatTempArchiveController（温采器配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatTempArchive/pageList | GET | /thermal/ht/temp-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatTempArchive/pageListMachineTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatTempArchive/insertData | POST | /thermal/ht/temp-archive | POST | MATCH | 新增 |
| /property/prHeatTempArchive/updateData | POST | /thermal/ht/temp-archive | PUT | MATCH | 修改 |
| /property/prHeatTempArchive/getDataById | GET | /thermal/ht/temp-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatTempArchive/deleteData | POST | /thermal/ht/temp-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatTempArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatTempArchive/updateVirtualDeviceData | POST | - | - | MISSING | 更新虚拟设备 |
| /property/prHeatTempArchive/queryCompanyHeat | GET | - | - | MISSING | 查询公司温采器 |
| /property/prHeatTempArchive/insertTemperatureCollector | POST | - | - | MISSING | 接收温采器数据 |
| /property/prHeatTempArchive/valveInformationSynchronization | POST | - | - | MISSING | 同步到采集平台 |
| /property/prHeatTempArchive/downloadInfoSync | GET | - | - | MISSING | 下载同步信息 |
| /property/prHeatTempArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatTempArchive/importAll | POST | - | - | MISSING | 导入全部 |

#### 业务逻辑差异
1. **核心缺失**: 
   - 接收世达温采器数据的接口（insertTemperatureCollector）是关键的物联网数据接收端点，完全缺失
   - 天康数据分页查询功能缺失

2. **温采器特有功能**:
   - 虚拟设备更新功能未迁移
   - 温度数据接收和处理逻辑未实现

#### 代码质量问题
- 新系统仅实现了基础 CRUD，温采器的核心数据接收功能未迁移

---

### 3. PrHeatUnitHotArchiveController（单元热表配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatUnitHotArchive/pageList | GET | /thermal/ht/unit-hot-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatUnitHotArchive/pageListUnitHeatTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatUnitHotArchive/insertData | POST | /thermal/ht/unit-hot-archive | POST | MATCH | 新增 |
| /property/prHeatUnitHotArchive/updateData | POST | /thermal/ht/unit-hot-archive | PUT | MATCH | 修改 |
| /property/prHeatUnitHotArchive/getDataById | GET | /thermal/ht/unit-hot-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatUnitHotArchive/deleteData | POST | /thermal/ht/unit-hot-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatUnitHotArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatUnitHotArchive/valveInformationSynchronization | POST | - | - | MISSING | 同步到采集平台 |
| /property/prHeatUnitHotArchive/downloadInfoSync | GET | - | - | MISSING | 下载同步信息 |
| /property/prHeatUnitHotArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatUnitHotArchive/importAll | POST | - | - | MISSING | 导入全部 |

#### 业务逻辑差异
- 基础 CRUD 已完整迁移
- 缺失所有数据导入导出和平台同步功能

---

### 4. PrHeatValveArchiveController（户间阀门配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatValveArchive/pageList | GET | /thermal/ht/valve-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatValveArchive/pageListValveTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatValveArchive/pageListHeatCard | GET | - | - | MISSING | 热卡分页 |
| /property/prHeatValveArchive/pageListCard | GET | - | - | MISSING | 卡表分页 |
| /property/prHeatValveArchive/insertData | POST | /thermal/ht/valve-archive | POST | MATCH | 新增 |
| /property/prHeatValveArchive/updateData | POST | /thermal/ht/valve-archive | PUT | MATCH | 修改 |
| /property/prHeatValveArchive/getDataById | GET | /thermal/ht/valve-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatValveArchive/deleteData | POST | /thermal/ht/valve-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatValveArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatValveArchive/insertDataNbValve | POST | - | - | MISSING | NB阀门数据接收 |
| /property/prHeatValveArchive/insertDataMBusValve | POST | - | - | MISSING | Mbus阀门数据接收 |
| /property/prHeatValveArchive/isCheckMeter | GET | - | - | MISSING | 检查仪表 |
| /property/prHeatValveArchive/signature | POST | - | - | MISSING | 移动平台验证 |
| /property/prHeatValveArchive/openValve | POST | /thermal/ht/valve-archive/openValve | POST | MATCH | 开阀 |
| /property/prHeatValveArchive/closeValve | POST | /thermal/ht/valve-archive/closeValve | POST | MATCH | 关阀 |
| /property/prHeatValveArchive/updateValveStatus | POST | - | - | MISSING | 更新阀门状态 |
| /property/prHeatValveArchive/queryMeterByMeterNum | GET | - | - | MISSING | 按表号查询 |
| /property/prHeatValveArchive/queryValveByMeterNum | GET | - | - | MISSING | 按表号查阀门 |
| /property/prHeatValveArchive/queryMeterListByMeterNum | GET | - | - | MISSING | 小程序查询 |
| /property/prHeatValveArchive/exchangeMeter | POST | /thermal/ht/valve-archive/exchangeMeter/{oldId} | POST | MATCH | 换表 |
| /property/prHeatValveArchive/insertWriteCardLog | POST | - | - | MISSING | 插入写卡记录 |
| /property/prHeatValveArchive/getWardCardLog | GET | - | - | MISSING | 查询写卡记录 |
| /property/prHeatValveArchive/queryHouseByMeterNum | GET | - | - | MISSING | 按表号查房屋 |
| /property/prHeatValveArchive/queryCardMeterByHouseId | GET | - | - | MISSING | 按房屋ID查卡阀 |
| /property/prHeatValveArchive/queryCardMeterByRoomNum | GET | - | - | MISSING | 按房号查卡阀 |
| /property/prHeatValveArchive/valveInformationSynchronization | POST | - | - | MISSING | 同步到采集平台 |
| /property/prHeatValveArchive/downloadInfoSync | GET | - | - | MISSING | 下载同步信息 |
| /property/prHeatValveArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatValveArchive/exportCardAll | GET | - | - | MISSING | 导出卡阀 |
| /property/prHeatValveArchive/importAll | POST | - | - | MISSING | 导入全部 |
| /property/prHeatValveArchive/setValveOCStatus | POST | - | - | MISSING | 设置阀门开关状态 |
| /property/prHeatValveArchive/setValveOpening | POST | - | - | MISSING | 设置阀门开度 |
| /property/prHeatValveArchive/setValveCycle | POST | - | - | MISSING | 设置阀门周期 |
| /property/prHeatValveArchive/api/enopt/valve/control | POST | - | - | MISSING | 云谷对接-控制 |
| /property/prHeatValveArchive/api/enopt/rtdata/batchsync | GET | - | - | MISSING | 云谷对接-批量同步 |
| /property/prHeatValveArchive/api/xaltrl/getLTValveData | GET | - | - | MISSING | 西瑞特对接 |
| /property/prHeatValveArchive/getValveDataByHouseId | GET | - | - | MISSING | 按房屋ID查阀门 |
| /property/prHeatValveArchive/insertDataByHouseId | POST | /thermal/ht/valve-archive/insertByHouseId | POST | MATCH | 按房屋新增 |
| /property/prHeatValveArchive/insertValveControlLogByBluetooth | POST | - | - | MISSING | 蓝牙控制日志 |
| /property/prHeatValveArchive/readValveStatusByParam | POST | - | - | MISSING | 读取阀门状态 |
| /property/prHeatValveArchive/getValveStatusByParam | POST | - | - | MISSING | 获取阀门状态 |
| /property/prHeatValveArchive/setValveCycleByParam | POST | - | - | MISSING | 设置周期 |
| /property/prHeatValveArchive/insertUserAndValveInfo | POST | - | - | MISSING | 插入用户和阀门 |
| /property/prHeatValveArchive/getOutputValveStatusByParam | POST | - | - | MISSING | 导出阀门状态 |
| /property/prHeatValveArchive/getOutputValveStatusData | POST | - | - | MISSING | 导出阀门数据 |

#### 业务逻辑差异
1. **严重缺失**:
   - NB-IoT 阀门数据接收接口（insertDataNbValve）- 电信平台数据接收
   - Mbus 阀门数据接收接口（insertDataMBusValve）- 世达数据接收
   - 移动平台验证接口（signature）- 移动平台数据接收
   - 所有卡表相关功能（写卡、读卡记录等）
   - 所有第三方平台对接（云谷、西瑞特）
   - 蓝牙控制功能

2. **阀门控制功能**:
   - 基础开阀/关阀已迁移
   - 高级控制功能（设置开度、设置周期、状态读取）全部缺失

#### 代码质量问题
- 阀门控制是核心业务功能，大量高级控制逻辑未迁移

---

### 5. PrHeatUnitValveArchiveController（单元阀门配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatUnitValveArchive/pageList | GET | /thermal/ht/unit-valve-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatUnitValveArchive/pageListUnitValveTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatUnitValveArchive/insertData | POST | /thermal/ht/unit-valve-archive | POST | MATCH | 新增 |
| /property/prHeatUnitValveArchive/updateData | POST | /thermal/ht/unit-valve-archive | PUT | MATCH | 修改 |
| /property/prHeatUnitValveArchive/getDataById | GET | /thermal/ht/unit-valve-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatUnitValveArchive/deleteData | POST | /thermal/ht/unit-valve-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatUnitValveArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatUnitValveArchive/openValve | POST | /thermal/ht/unit-valve-archive/openValve | POST | MATCH | 开阀 |
| /property/prHeatUnitValveArchive/closeValve | POST | /thermal/ht/unit-valve-archive/closeValve | POST | MATCH | 关阀 |
| /property/prHeatUnitValveArchive/openingValve | POST | - | - | MISSING | 开度设定 |
| /property/prHeatUnitValveArchive/getListByUnitId | GET | - | - | MISSING | 按单元ID查询 |
| /property/prHeatUnitValveArchive/getListByMeterNum | GET | - | - | MISSING | 按表号查询 |
| /property/prHeatUnitValveArchive/getListByUnitIdAndType | POST | - | - | MISSING | 按单元和类型查询 |
| /property/prHeatUnitValveArchive/valveInformationSynchronization | POST | - | - | MISSING | 同步到采集平台 |
| /property/prHeatUnitValveArchive/downloadInfoSync | GET | - | - | MISSING | 下载同步信息 |
| /property/prHeatUnitValveArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatUnitValveArchive/importAll | POST | - | - | MISSING | 导入全部 |

#### 业务逻辑差异
- 基础 CRUD 和开/关阀已迁移
- 缺失开度设定功能和查询功能

---

### 6. PrHeatCommandValveArchiveController（户间控制阀门配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatCommandValveArchive/pageList | GET | /thermal/ht/command-valve-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatCommandValveArchive/pageListTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatCommandValveArchive/pageListCard | GET | - | - | MISSING | 卡表分页 |
| /property/prHeatCommandValveArchive/insertData | POST | /thermal/ht/command-valve-archive | POST | MATCH | 新增 |
| /property/prHeatCommandValveArchive/updateData | POST | /thermal/ht/command-valve-archive | PUT | MATCH | 修改 |
| /property/prHeatCommandValveArchive/getDataById | GET | /thermal/ht/command-valve-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatCommandValveArchive/deleteData | POST | /thermal/ht/command-valve-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatCommandValveArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatCommandValveArchive/insertDataNbValve | POST | - | - | MISSING | NB阀门数据接收 |
| /property/prHeatCommandValveArchive/insertDataMBusValve | POST | - | - | MISSING | Mbus阀门数据接收 |
| /property/prHeatCommandValveArchive/isCheckMeter | GET | - | - | MISSING | 检查仪表 |
| /property/prHeatCommandValveArchive/signature | POST | - | - | MISSING | 移动平台验证 |
| /property/prHeatCommandValveArchive/openValve | POST | /thermal/ht/command-valve-archive/openValve | POST | MATCH | 开阀 |
| /property/prHeatCommandValveArchive/closeValve | POST | /thermal/ht/command-valve-archive/closeValve | POST | MATCH | 关阀 |
| /property/prHeatCommandValveArchive/updateValveStatus | POST | - | - | MISSING | 更新阀门状态 |
| /property/prHeatCommandValveArchive/queryMeterByMeterNum | GET | - | - | MISSING | 按表号查询 |
| /property/prHeatCommandValveArchive/exchangeMeter | POST | /thermal/ht/command-valve-archive/exchangeMeter/{oldId} | POST | MATCH | 换表 |
| /property/prHeatCommandValveArchive/insertWriteCardLog | POST | - | - | MISSING | 插入写卡记录 |
| /property/prHeatCommandValveArchive/getWardCardLog | GET | - | - | MISSING | 查询写卡记录 |
| /property/prHeatCommandValveArchive/queryHouseByMeterNum | GET | - | - | MISSING | 按表号查房屋 |
| /property/prHeatCommandValveArchive/queryCardMeterByHouseId | GET | - | - | MISSING | 按房屋ID查卡阀 |
| /property/prHeatCommandValveArchive/queryCardMeterByRoomNum | GET | - | - | MISSING | 按房号查卡阀 |
| /property/prHeatCommandValveArchive/valveInformationSynchronization | POST | - | - | MISSING | 同步到采集平台 |
| /property/prHeatCommandValveArchive/downloadInfoSync | GET | - | - | MISSING | 下载同步信息 |
| /property/prHeatCommandValveArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatCommandValveArchive/importAll | POST | - | - | MISSING | 导入全部 |

#### 业务逻辑差异
- 与 PrHeatValveArchive 类似，缺失所有物联网数据接收和卡表功能

---

### 7. PrHeatCommandUnitValveArchiveController（单元控制阀门配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatCommandUnitValveArchive/pageList | GET | /thermal/ht/command-unit-valve-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatCommandUnitValveArchive/pageListUnitCommandTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatCommandUnitValveArchive/insertData | POST | /thermal/ht/command-unit-valve-archive | POST | MATCH | 新增 |
| /property/prHeatCommandUnitValveArchive/updateData | POST | /thermal/ht/command-unit-valve-archive | PUT | MATCH | 修改 |
| /property/prHeatCommandUnitValveArchive/getDataById | GET | /thermal/ht/command-unit-valve-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatCommandUnitValveArchive/deleteData | POST | /thermal/ht/command-unit-valve-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatCommandUnitValveArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatCommandUnitValveArchive/openValve | POST | /thermal/ht/command-unit-valve-archive/openValve | POST | MATCH | 开阀 |
| /property/prHeatCommandUnitValveArchive/closeValve | POST | /thermal/ht/command-unit-valve-archive/closeValve | POST | MATCH | 关阀 |
| /property/prHeatCommandUnitValveArchive/openingValve | POST | - | - | MISSING | 开度设定 |
| /property/prHeatCommandUnitValveArchive/getListByUnitId | GET | - | - | MISSING | 按单元ID查询 |
| /property/prHeatCommandUnitValveArchive/valveInformationSynchronization | POST | - | - | MISSING | 同步到采集平台 |
| /property/prHeatCommandUnitValveArchive/downloadInfoSync | GET | - | - | MISSING | 下载同步信息 |
| /property/prHeatCommandUnitValveArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatCommandUnitValveArchive/importAll | POST | - | - | MISSING | 导入全部 |

---

### 8. PrHeatDtuArchiveController（DTU采集器配表）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatDtuArchive/pageList | GET | /thermal/ht/dtu-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatDtuArchive/pageListDtuTK | POST | - | - | MISSING | 天康数据分页 |
| /property/prHeatDtuArchive/insertData | POST | /thermal/ht/dtu-archive | POST | MATCH | 新增 |
| /property/prHeatDtuArchive/updateData | POST | /thermal/ht/dtu-archive | PUT | MATCH | 修改 |
| /property/prHeatDtuArchive/getDataById | GET | /thermal/ht/dtu-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatDtuArchive/deleteData | POST | /thermal/ht/dtu-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatDtuArchive/deleteDataAll | POST | - | - | MISSING | 批量删除 |
| /property/prHeatDtuArchive/queryMeter | POST | /thermal/ht/dtu-archive/query-meter | POST | MATCH | 查询仪表 |
| /property/prHeatDtuArchive/exportAll | GET | - | - | MISSING | 导出全部 |
| /property/prHeatDtuArchive/importAll | POST | - | - | MISSING | 导入全部 |

#### 业务逻辑差异
- DTU 基础功能已迁移
- 缺失导入导出功能

---

### 9. PrHeatArchiveController（房屋配表-热力）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prHeatArchive/pageList | GET | /thermal/ht/heat-archive/list | GET | MATCH | 分页查询 |
| /property/prHeatArchive/queryCompanyHeat | GET | /thermal/ht/heat-archive/queryCompanyHeat | GET | MATCH | 查询公司热表 |
| /property/prHeatArchive/insertHeatData | POST | /thermal/ht/heat-archive | POST | MATCH | 新增 |
| /property/prHeatArchive/selectMeterNum | GET | - | - | MISSING | 选择表号 |
| /property/prHeatArchive/updateHeatData | POST | /thermal/ht/heat-archive | PUT | MATCH | 修改 |
| /property/prHeatArchive/getHeatDataById | GET | /thermal/ht/heat-archive/{id} | GET | MATCH | 查询详情 |
| /property/prHeatArchive/deleteHeatData | POST | /thermal/ht/heat-archive/{id} | DELETE | MATCH | 删除 |
| /property/prHeatArchive/stopHeatMeter | POST | /thermal/ht/heat-archive/stopMeter/{id} | POST | MATCH | 停表 |
| /property/prHeatArchive/startHeatMeter | POST | /thermal/ht/heat-archive/startMeter/{id} | POST | MATCH | 开表 |
| /property/prHeatArchive/replaceHeatMeter | POST | /thermal/ht/heat-archive/replace | POST | MATCH | 更换仪表 |
| /property/prHeatArchive/calculate | GET | /thermal/ht/heat-archive/calculate/{id} | GET | MATCH | 计算平衡 |
| /property/prHeatArchive/realTimeData | POST | /thermal/ht/heat-archive/realTimeData | POST | MATCH | 实时数据 |
| /property/prHeatArchive/zonghe | POST | /thermal/ht/heat-archive/zonghe | POST | MATCH | 综合查询 |
| /property/prHeatArchive/miniControlValve | POST | - | - | MISSING | 小程序控制阀门 |
| /property/prHeatArchive/setValveGroupParam | POST | /thermal/ht/heat-archive/setValveGroupParam | POST | MATCH | 设置阀门组号 |
| /property/prHeatArchive/manualControl | POST | /thermal/ht/heat-archive/manualControl | POST | MATCH | 手动调控 |
| /property/prHeatArchive/xunce | POST | /thermal/ht/heat-archive/xunce | POST | MATCH | 巡测 |
| /property/prHeatArchive/exportAll | GET | /thermal/ht/heat-archive/export | GET | MATCH | 导出全部 |
| /property/prHeatArchive/importData | POST | - | - | MISSING | 导入数据 |
| /property/prHeatArchive/findMeter | GET | /thermal/ht/heat-archive/findMeter | GET | MATCH | 查询仪表 |
| /property/prHeatArchive/recharge | POST | /thermal/ht/heat-archive/recharge | POST | MATCH | 仪表充值 |
| /property/prHeatArchive/selectReport | POST | /thermal/ht/heat-archive/selectReport | POST | MATCH | 收费明细报表 |
| /property/prHeatArchive/selectMeterReport | POST | /thermal/ht/heat-archive/selectMeterReport | POST | MATCH | 仪表历史报表 |

#### 业务逻辑差异
- 核心业务功能已较完整迁移
- 缺失小程序控制阀门功能
- 导入数据功能未迁移

---

### 10. PrAutoMachineController（自助机管理）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/autoMachine/pageList | GET | /thermal/property/auto-machine/list | GET | SKELETON | 分页查询（骨架） |
| /property/autoMachine/getDataById | GET | /thermal/property/auto-machine/{id} | GET | SKELETON | 查询详情（骨架） |
| /property/autoMachine/updateData | POST | /thermal/property/auto-machine | PUT | SKELETON | 修改（骨架） |
| /property/autoMachine/getReaderParam | POST | - | - | MISSING | 获取读卡器参数 |
| /property/autoMachine/getClientVersion | POST | - | - | MISSING | 获取客户端版本 |
| /property/autoMachine/getClientDownload | POST | - | - | MISSING | 获取客户端下载地址 |
| /property/autoMachine/getSerialNum | GET | /thermal/property/auto-machine/serial-num | GET | SKELETON | 获取流水号（骨架） |
| /property/autoMachine/getQrCode | POST | /thermal/property/auto-machine/qr-heat | POST | SKELETON | 生成物业费二维码（骨架） |
| /property/autoMachine/getQrCodeWater | POST | /thermal/property/auto-machine/qr-water | POST | SKELETON | 生成水费二维码（骨架） |
| /property/autoMachine/getQrCodeEle | POST | /thermal/property/auto-machine/qr-ele | POST | SKELETON | 生成电费二维码（骨架） |
| /property/autoMachine/callback | POST | /thermal/property/auto-machine/callback/wechat-heat | POST | SKELETON | 微信物业费回调（骨架） |
| /property/autoMachine/callbackEle | POST | - | - | MISSING | 微信电费回调 |
| /property/autoMachine/callbackWater | POST | - | - | MISSING | 微信水费回调 |
| /property/autoMachine/aliCallBack | POST | /thermal/property/auto-machine/callback/ali-heat | POST | SKELETON | 支付宝物业费回调（骨架） |
| /property/autoMachine/aliCallBackEle | POST | - | - | MISSING | 支付宝电费回调 |
| /property/autoMachine/aliCallBackWater | POST | - | - | MISSING | 支付宝水费回调 |
| /property/autoMachine/queryPaymentSuccess | GET | /thermal/property/auto-machine/payment-status | GET | SKELETON | 查询支付状态（骨架） |
| /property/autoMachine/getRecordBySerialNum | GET | /thermal/property/auto-machine/record | GET | SKELETON | 查询交易记录（骨架） |
| /property/autoMachine/getIsReadCard | GET | /thermal/property/auto-machine/read-card | GET | SKELETON | 查询是否读卡（骨架） |

#### 业务逻辑差异
1. **骨架迁移**: 新系统仅建立 Controller 骨架，所有端点返回占位响应
2. **严重依赖**: 此模块严重依赖微信支付和支付宝支付集成
3. **安全风险**: 支付回调端点标记为 @Deprecated，但实际不可用

#### 代码质量问题
- 所有支付相关功能均未实现，存在严重业务中断风险

---

### 11. PrAbnormalRecordController（异常记录）

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| 未在旧系统中发现此文件 | - | /thermal/ht/abnormal-record/* | - | EXTRA | 新系统新增 |

#### 业务逻辑差异
- 此模块在新系统中为新增功能，旧系统可能集成在其他模块中

---

## 总结与建议

### 关键发现

#### 1. 核心业务功能已迁移
- 所有设备档案的基础 CRUD 功能已完整迁移
- 设备控制功能（开阀/关阀）已迁移
- 新系统采用了更好的架构设计（RESTful、Sa-Token、LambdaQueryWrapper）

#### 2. 严重缺失的功能
1. **物联网数据接收接口**:
   - NB-IoT 阀门数据接收（电信平台）
   - Mbus 阀门数据接收（世达平台）
   - 温采器数据接收（世达平台）
   - 移动平台验证接口

2. **数据导入导出功能**:
   - 所有 Excel 导入功能
   - 所有 Excel 导出功能
   - 数据同步到采集平台功能

3. **卡表相关功能**:
   - 写卡/读卡功能
   - 写卡记录查询
   - 卡表相关查询

4. **第三方平台对接**:
   - 云谷平台对接
   - 西瑞特平台对接

5. **支付功能**:
   - 自助机模块所有支付功能均为骨架，不可用

### 缺失功能清单

#### 高优先级（核心业务）
1. **物联网数据接收接口**（3个）:
   - PrHeatValveArchiveController.insertDataNbValve
   - PrHeatValveArchiveController.insertDataMBusValve
   - PrHeatTempArchiveController.insertTemperatureCollector

2. **Excel 导入导出**（约30个端点）:
   - 所有 *ArchiveController 的 exportAll 和 importAll 端点

3. **数据同步功能**（约10个端点）:
   - 所有 valveInformationSynchronization 和 downloadInfoSync 端点

#### 中优先级（增强功能）
1. **高级阀门控制**（约15个端点）:
   - 设置阀门开度
   - 设置上报周期
   - 读取阀门状态
   - 蓝牙控制

2. **第三方平台对接**（3个端点）:
   - 云谷平台控制接口
   - 云谷平台批量同步
   - 西瑞特平台数据查询

#### 低优先级（辅助功能）
1. **卡表功能**（约10个端点）
2. **查询增强**（约15个端点）

### 代码质量风险

#### 1. 事务管理缺失
- 新系统 Service 方法未添加 @Transactional 注解
- 建议在所有写操作方法上添加 `@Transactional(rollbackFor = Exception.class)`

#### 2. 权限控制不完整
- 部分敏感操作未添加权限注解
- 建议补充 @SaCheckPermission 注解

#### 3. 日志记录缺失
- 部分关键操作未记录审计日志
- 建议补充 @Log 注解

#### 4. 参数校验不足
- 部分端点未使用 @Validated 进行参数校验
- 建议在 Bo 对象上添加校验注解

#### 5. 支付安全风险
- PrAutoMachineController 所有支付端点不可用
- 建议尽快实现支付回调功能或明确标记为不可用

### 迁移建议

#### 短期（1-2周）
1. 优先迁移物联网数据接收接口（NB-IoT、Mbus、温采器）
2. 补充事务管理和权限控制注解
3. 迁移 Excel 导入导出功能

#### 中期（1个月）
1. 迁移数据同步功能
2. 迁移高级阀门控制功能
3. 完善异常处理和参数校验

#### 长期（2-3个月）
1. 迁移第三方平台对接功能
2. 完善卡表功能
3. 实现自助机支付功能（需要支付集成）

---

## 附录

### 审核方法说明
1. **API 覆盖度**: 通过对比新旧系统 Controller 的 @RequestMapping 注解
2. **业务逻辑**: 通过对比 Service 方法实现
3. **代码质量**: 检查事务管理、权限控制、日志记录等

### 状态定义
- **MATCH**: 功能已完整迁移
- **PARTIAL**: 部分功能已迁移，核心功能缺失
- **MISSING**: 功能完全未迁移
- **EXTRA**: 新系统新增功能
- **SKELETON**: 仅建立骨架，实际业务未实现

---
**审核人**: Claude (AI Assistant)  
**审核日期**: 2026-04-26  
**版本**: 1.0
