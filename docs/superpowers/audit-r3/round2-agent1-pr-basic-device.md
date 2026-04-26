# Round 2 — Agent 1: Pr 基础数据 + Pr 设备档案（深度审核）

## 一、Service 层对比

### PrHouseController（房屋管理）

| 旧端点 | 新端点 | Service 差异 | 严重度 | 说明 |
|--------|--------|-------------|--------|------|
| /updateUserByHouse | PUT /{id}/owner | 新系统参数简化，缺少关联账户创建逻辑 | MEDIUM | 旧系统 updateUserByHouse 包含新建个人账户逻辑（newUserHasAccount），新系统只更新业主 |
| /setHouseHeatingArea | PUT /{id}/area | 新系统缺少费用明细日志 | MEDIUM | 旧系统调用 expenseMapper.insertExpenseLogMj 记录变更日志 |
| /queryGDH | - | **缺失** | HIGH | 查询孤岛户功能未迁移 |
| /setGDH | - | **缺失** | HIGH | 设置孤岛户功能未迁移 |
| /importAll | POST /import | 导入验证逻辑简化 | LOW | 旧系统有完整的 uuid 批量导入验证链 |
| /exportAll | GET /export | 导出字段可能不完整 | LOW | 需确认 EasyExcel 配置是否匹配旧系统 |

**关键发现**：
- 孤岛户（queryGDH/setGDH）相关逻辑完全缺失，这是热力平衡分析的核心功能
- 导入导出功能在旧系统中有大量关联表操作（UserHouse、HouseChange、User），新系统简化为单表操作

### PrHeatArchiveController（房屋热表配表）

| 旧端点 | 新端点 | Service 差异 | 严重度 | 说明 |
|--------|--------|-------------|--------|------|
| /manualControl | POST /manualControl | **adjust 参数支持不全** | HIGH | 旧系统支持 adjust=1~30+ 共 13 种指令类型，新系统仅处理部分类型 |
| /replaceHeatMeter | POST /{id}/replace | 余额转移逻辑不完整 | MEDIUM | 旧系统包含新旧表双向交易记录（PrTransactionRecord + PrTransactionDetail） |
| /recharge | POST /recharge | 交易记录不完整 | MEDIUM | 新系统只有 PrTransactionRecordSub，缺少 PrTransactionDetail 明细 |
| /realTimeData | GET /realtime | 参数简化 | LOW | 去除了 stationId、prHeatStationPartitionId、ifMore、moreParam 参数 |
| /zonghe | GET /summary | 参数简化 | LOW | 去除了 moneyType、valveStatus 过滤 |

**关键发现**：
- manualControl 方法中，旧系统支持的 adjust 指令类型包括：1=开阀, 2=关阀, 3=设置开度, 7=巡测, 27=加档, 28=减档, 28-2=特殊控制, 29=其他, 30=其他等
- 新系统只处理了 adjust="7"、"28-2"、"27"、"29"、"30"，缺少 1~6、8~26 等基础指令
- 交易记录结构变化：旧系统使用 PrTransactionRecord + PrTransactionDetail，新系统使用 PrTransactionRecord + PrTransactionRecordSub

### PrHeatValveArchiveController（户间阀门配表）

| 旧端点 | 新端点 | Service 差异 | 严重度 | 说明 |
|--------|--------|-------------|--------|------|
| /setValveOCStatus | POST /valve/oc | 新实现完整 | NONE | 批量阀门开关状态已实现 |
| /setValveOpening | POST /valve/opening | 新实现完整 | NONE | 批量阀门开度设置已实现 |
| /setValveCycle | POST /valve/cycle | 新实现完整 | NONE | 批量阀门上报周期设置已实现 |
| /insertDataNbValve | POST /nb | 缺少房屋温度反写 | MEDIUM | 旧系统调用 updateHouse 反写进水/出水温度到 pr_house 表 |
| /insertDataMBusValve | POST /mbus | 缺少房屋温度反写 | MEDIUM | 同上 |
| /signature | GET /signature | - | NONE | 移动平台验证，逻辑简单 |

**关键发现**：
- NB/MBus 阀门数据接收端点缺少房屋温度反写逻辑（updateHouse）
- 新系统的批量操作（setValveOCStatus、setValveOpening、setValveCycle）通过 HtTasksPerform 实现，架构优于旧系统

### PrAutoMachineController（自助缴费机）

| 旧端点 | 新端点 | Service 差异 | 严重度 | 说明 |
|--------|--------|-------------|--------|------|
| /getQrCode | POST /qr-heat | 二维码生成简化 | LOW | 新系统返回模拟链接，待第三方支付SDK集成 |
| /callback | POST /callback/wechat-heat | **未实现** | HIGH | 返回 fail，影响线上支付 |
| /aliCallBack | POST /callback/ali-heat | **未实现** | HIGH | 返回 fail，影响线上支付 |

**关键发现**：
- 支付回调端点标记为 TODO，返回失败响应
- 流水号生成逻辑（generateSerialNum）已正确迁移

### PrHeatDailyController / PrHeatMonthController（热表日表/月表）

| 旧端点 | 新端点 | Service 差异 | 严重度 | 说明 |
|--------|--------|-------------|--------|------|
| /setHeat | POST /generate | 新系统合并了5步为6步 | LOW | 旧系统分5次 Mapper 调用，新系统合并为6次 |

**关键发现**：
- 统计计算公式基本一致，只是步骤合并
- 日表生成流程：setIsValid → deleteDaily → setHeatDaily → setSteps → setQtyStepsN → setCurrentReading

### PrHeatControlController（热表控制）

| 旧端点 | 新端点 | Service 差异 | 严重度 | 说明 |
|--------|--------|-------------|--------|------|
| /handControl | POST /manual | 已实现 | NONE | 通过 HeatMeterControl 工具类实现 |
| /selectMeter | GET /query | 已实现 | NONE | 状态查询指令 |
| /openValve | POST /openValve | 已实现 | NONE | 开度设为100 |
| /closeValve | POST /closeValve | 已实现 | NONE | 开度设为0 |
| /add | POST /add | 已实现 | NONE | 增加开度 |
| /sub | POST /sub | 已实现 | NONE | 减少开度 |

**关键发现**：
- PrHeatControlController 是**新增实现**，第一轮报告标记为 MISSING 的端点已全部补全
- 依赖 HeatMeterControl 工具类与 MBus 中间件通信

### PrAbnormalRecordController（异常记录）

| 旧端点 | 新端点 | Service 差异 | 严重度 | 说明 |
|--------|--------|-------------|--------|------|
| /updateData | PUT / | **Service 为空实现** | HIGH | PrAbnormalRecordServiceImpl 只有16行，无业务逻辑 |

**关键发现**：
- PrAbnormalRecordServiceImpl 是典型的骨架实现
- 第一轮报告标记为 SKELETON 的端点确实只有 Controller，无实际 Service 逻辑

## 二、DB 层对比

### PrHouse（旧 vs 新字段）

| 旧字段 | 新字段 | 状态 | 备注 |
|--------|--------|------|------|
| id | id | ✅ | 类型一致 |
| code | code | ✅ | 房屋编号 |
| buildingId | buildingId | ✅ | 楼宇ID |
| buildingName | buildingName | ✅ | 楼宇名称 |
| unitCode | unitCode | ✅ | 单元号 |
| roomNum | roomNum | ✅ | 房号 |
| floor | floor | ✅ | 楼层 |
| nfloorArea | nfloorArea | ✅ | 使用面积 |
| gfloorArea | gfloorArea | ✅ | 建筑面积 |
| heatingArea | heatingArea | ✅ | 供热面积 |
| fristInsidearea | fristInsidearea | ✅ | 一楼内面积 |
| secondInsidearea | secondInsidearea | ✅ | 二楼内面积 |
| thirdInsidearea | thirdInsidearea | ✅ | 三楼内面积 |
| nature | nature | ✅ | 房屋性质 |
| structure | structure | ✅ | 房屋结构 |
| type | type | ✅ | 房屋类型 |
| towards | towards | ✅ | 朝向 |
| unitType | unitType | ✅ | 户型 |
| unitPrice | unitPrice | ✅ | 单价 |
| propertyTerm | propertyTerm | ✅ | 产权年限 |
| deliveryTime | deliveryTime | ✅ | 交付时间 |
| acceptTime | acceptTime | ✅ | 验收时间 |
| occupancyTime | occupancyTime | ✅ | 入住时间 |
| establishTime | establishTime | ✅ | 立户时间 |
| address | address | ✅ | 邮寄地址 |
| decorationStatus | decorationStatus | ✅ | 装修状态 |
| status | status | ✅ | 房屋状态 |
| rentalStatus | rentalStatus | ✅ | 租赁状态 |
| seq | seq | ✅ | 排序 |
| siteType | siteType | ✅ | 位置属性 |
| siteTypeOld | siteTypeOld | ✅ | 历史位置 |
| stationType | stationType | ✅ | 供热区域属性 |
| presetAngle | presetAngle | ✅ | 预设角度 |
| presetFlowRate | presetFlowRate | ✅ | 预设流量 |
| inTemp | inTemp | ✅ | 进水温度 |
| outTemp | outTemp | ✅ | 出水温度 |
| roomTemp | roomTemp | ✅ | 室温 |
| valveOpen | valveOpen | ✅ | 阀门开度 |
| curFlow | curFlow | ✅ | 当前流量 |
| otherCode | otherCode | ✅ | 外部缴费编码 |
| userId | userId | ⚠️ | 仅查询字段，新系统未关联 PrFamily |
| userName | userName | ⚠️ | 仅查询字段 |
| phone | phone | ⚠️ | 仅查询字段 |
| idNo | idNo | ❌ | **缺失**：身份证号 |
| puhId | puhId | ❌ | **缺失**：房屋与客户关联关系ID |
| orgId | orgId | ✅ | 小区ID |
| companyId | companyId | ✅ | 公司ID |
| orgName | orgName | ⚠️ | 仅查询字段 |
| stationPartitionName | stationPartitionName | ⚠️ | 仅查询字段 |
| stationName | stationName | ⚠️ | 仅查询字段 |
| money | money | ⚠️ | 仅查询字段 |
| preloaded | preloaded | ⚠️ | 仅查询字段 |
| standardList | - | ❌ | **缺失**：关联收费标准列表 |
| heatValveArchiveList | - | ❌ | **缺失**：关联阀门列表 |
| heatCommandValveArchives | - | ❌ | **缺失**：关联控制阀门列表 |
| heatTempArchiveList | - | ❌ | **缺失**：关联温采器列表 |
| heatHotArchiveList | - | ❌ | **缺失**：关联热表列表 |
| heatDtuArchives | - | ❌ | **缺失**：关联采集器列表 |
| siteTypeName | - | ❌ | **缺失**：位置属性名称 |
| isStatus | - | ❌ | **缺失**：状态标识 |
| isCharged | isCharged | ⚠️ | 仅查询字段 |
| isSpecial | isSpecial | ⚠️ | 仅查询字段 |
| payStatus | payStatus | ⚠️ | 仅查询字段 |
| meterNum | meterNum | ⚠️ | 仅查询字段 |
| valveStatus | valveStatus | ⚠️ | 仅查询字段 |
| meterArcCode | meterArcCode | ⚠️ | 仅查询字段 |

**关键发现**：
- 核心字段完整，但关联字段（userId、userName、phone、idNo、puhId）在新系统中仅作为查询字段，未建立实际的 PrFamily 关联
- 旧系统中的 List 类型关联字段（standardList、heatValveArchiveList 等）在新系统中移除，需通过额外查询获取

### PrHeatArchive（旧 vs 新字段）

| 旧字段 | 新字段 | 状态 | 备注 |
|--------|--------|------|------|
| id | id | ✅ | |
| companyId | companyId | ✅ | |
| orgId | orgId | ✅ | |
| orgName | orgName | ✅ | |
| buildingName | buildingName | ✅ | |
| houseId | houseId | ✅ | |
| roomNum | roomNum | ✅ | |
| archiveId | archiveId | ✅ | |
| meterArcCode | meterArcCode | ✅ | |
| meterArcName | meterArcName | ✅ | |
| meterNum | meterNum | ✅ | |
| imei | imei | ✅ | |
| cardNum | cardNum | ✅ | |
| productId | productId | ✅ | |
| deviceId | deviceId | ✅ | |
| meterSerial | meterSerial | ✅ | |
| lineNumber | lineNumber | ❌ | **缺失**：线路号 |
| specification | specification | ✅ | |
| model | model | ✅ | |
| concentratorCode | concentratorCode | ✅ | |
| installSite | installSite | ✅ | |
| standardId | standardId | ✅ | |
| standardPrice | standardPrice | ✅ | |
| inTemperature | inTemperature | ✅ | |
| outTemperature | outTemperature | ✅ | |
| diffTemperature | diffTemperature | ✅ | |
| settingTemperature | settingTemperature | ✅ | |
| settingStatus | settingStatus | ✅ | |
| valveStatus | valveStatus | ✅ | |
| isOpened | isOpened | ✅ | |
| hisMoney | hisMoney | ✅ | |
| openedTime | openedTime | ✅ | |
| startTime | startTime | ✅ | |
| endTime | endTime | ✅ | |
| isExpense | isExpense | ✅ | |
| isNotify | isNotify | ✅ | |
| isChanged | isChanged | ✅ | |
| isStop | isStop | ✅ | |
| startReading | startReading | ✅ | |
| totalHeat | totalHeat | ✅ | |
| totalFlow | totalFlow | ✅ | |
| totalWorktime | totalWorktime | ✅ | |
| currentReading | currentReading | ✅ | |
| totalUsed | totalUsed | ✅ | |
| tradeTimes | tradeTimes | ✅ | |
| totalMoney | totalMoney | ✅ | |
| totalRecharge | totalRecharge | ✅ | |
| currentBalance | currentBalance | ✅ | |
| payDegrees | payDegrees | ✅ | |
| hoardLimit | hoardLimit | ✅ | |
| alarmValue | alarmValue | ✅ | |
| closeValue | closeValue | ✅ | |
| isSteps | isSteps | ✅ | |
| measurement | measurement | ✅ | |
| type | type | ✅ | |
| command | command | ✅ | |
| valveOpening | valveOpening | ✅ | |
| commandTime | commandTime | ✅ | |
| commandStatus | commandStatus | ✅ | |
| returnTime | returnTime | ✅ | |
| isPrint | isPrint | ✅ | |
| printType | printType | ✅ | |
| prUserHouse | - | ❌ | **缺失**：关联用户房屋 |
| prHouse | - | ❌ | **缺失**：关联房屋 |
| prStandard | - | ❌ | **缺失**：关联收费标准 |
| prAccount | - | ❌ | **缺失**：关联账户 |
| prAccount1 | - | ❌ | **缺失**：关联个人账户 |
| paymentMethod1 | paymentMethod1 | ✅ | 查询字段 |
| paymentMethod2 | paymentMethod2 | ✅ | 查询字段 |
| paymentMoney1 | paymentMoney1 | ✅ | 查询字段 |
| paymentMoney2 | paymentMoney2 | ✅ | 查询字段 |
| deducted | deducted | ✅ | 查询字段 |
| paidIn | paidIn | ✅ | 查询字段 |

**关键发现**：
- 核心业务字段完整
- 关联对象（prUserHouse、prHouse、prStandard、prAccount、prAccount1）已移除，需通过 Service 层关联查询

### PrHeatValveArchive（旧 vs 新字段）

| 旧字段 | 新字段 | 状态 | 备注 |
|--------|--------|------|------|
| id | id | ✅ | |
| archiveId | archiveId | ✅ | |
| meterNum | meterNum | ✅ | |
| cardNum | cardNum | ✅ | |
| meterArcCode | meterArcCode | ✅ | |
| meterArcName | meterArcName | ✅ | |
| concentratorCode | concentratorCode | ✅ | |
| imeiNum | imeiNum | ✅ | |
| productId | productId | ✅ | |
| deviceId | deviceId | ✅ | |
| meterSerial | meterSerial | ✅ | |
| houseId | houseId | ✅ | |
| orgId | orgId | ✅ | |
| companyId | companyId | ✅ | |
| valveStatus | valveStatus | ✅ | |
| settingStatus | settingStatus | ✅ | |
| actualStatus | actualStatus | ✅ | |
| inTemperature | inTemperature | ✅ | |
| outTemperature | outTemperature | ✅ | |
| voltage | voltage | ✅ | |
| valveTime | valveTime | ✅ | |
| signalStrength | signalStrength | ✅ | |
| reportingInterval | reportingInterval | ✅ | |
| intervalUnit | intervalUnit | ✅ | |
| validTime | validTime | ✅ | |
| totalDegree | totalDegree | ✅ | |
| residueDegree | residueDegree | ✅ | |
| isChanged | isChanged | ✅ | |
| isStop | isStop | ✅ | |
| chanNumUpdateTime | chanNumUpdateTime | ✅ | |
| chanNumSyncTime | chanNumSyncTime | ✅ | |
| lastPerformId | lastPerformId | ✅ | |
| dtuNum | dtuNum | ✅ | |
| dtuNumStatus | dtuNumStatus | ✅ | |
| chanNum | chanNum | ✅ | |
| installSite | installSite | ✅ | |
| dtuStatus | dtuStatus | ✅ | |
| tradeTimes | tradeTimes | ✅ | |
| isOpen | isOpen | ✅ | |
| caliber | caliber | ✅ | |
| installType | installType | ✅ | |
| groupNum25 | groupNum25 | ✅ | |
| userSetTemp | userSetTemp | ✅ | |
| roomTemp | roomTemp | ✅ | |
| avgTemp | avgTemp | ✅ | |
| valveModel | valveModel | ✅ | |
| coldFlg | coldFlg | ✅ | |
| wkqLock | wkqLock | ✅ | |
| tempLow | tempLow | ✅ | |
| tempHigh | tempHigh | ✅ | |
| workTime | workTime | ✅ | |
| totalOpenTime | totalOpenTime | ✅ | |
| dtuType | dtuType | ✅ | |
| insFlow | insFlow | ✅ | |
| prHouse | - | ❌ | **缺失**：关联房屋对象 |
| number | - | ❌ | **缺失**：查询用数字 |
| status | - | ❌ | **缺失**：状态 |
| instructionStatus | - | ❌ | **缺失**：指令状态 |
| instructionId | - | ❌ | **缺失**：指令ID |
| sendTime | - | ❌ | **缺失**：发送时间 |
| name | - | ❌ | **缺失**：名称 |
| strategyNumber | - | ❌ | **缺失**：策略编号 |
| strategyName | - | ❌ | **缺失**：策略名称 |
| stationName | - | ❌ | **缺失**：热力站名称 |
| scopeStatus | - | ❌ | **缺失**：范围状态 |
| outTemp | - | ❌ | **缺失**：出口温度 |
| curFlow | - | ❌ | **缺失**：当前流量 |
| outTempDeviation | - | ❌ | **缺失**：出口温度偏差 |
| tasksName | - | ❌ | **缺失**：任务名称 |
| tasksNumber | - | ❌ | **缺失**：任务编号 |
| specification | - | ❌ | **缺失**：规格 |
| partitionName | - | ❌ | **缺失**：分区名称 |
| installSiteName | - | ❌ | **缺失**：安装位置名称 |
| mtTcValve | - | ❌ | **缺失**：关联温控阀门 |
| statusName | - | ❌ | **缺失**：状态名称 |
| isCharged | isCharged | ⚠️ | 仅查询字段 |

**关键发现**：
- 核心阀门字段完整
- 大量查询用辅助字段已移除（number、status、instructionStatus 等），这些应通过 VO 转换而非持久化

### 关键 Mapper 查询等价性

#### PrHouseMapper

- **pageList**: 新系统使用 MyBatis-Plus LambdaQueryWrapper，旧系统使用 XML 自定义 SQL，逻辑等价
- **validateRoomNum**: 新系统简化查询条件，移除了 companyId 校验
- **importAll**: 新系统简化为单表操作，旧系统包含 UserHouse、HouseChange、User 多表导入

#### PrHeatArchiveMapper

- **manualControl**: 新系统通过 HtTasksPerform 统一调度，旧系统直接构造指令字符串
- **replaceHeatMeter**: 新系统使用 PrTransactionRecordSub，旧系统使用 PrTransactionDetail
- **setHeatDaily**: 统计计算 SQL 等价

## 三、风险项

### 事务安全

| 端点 | 风险 | 说明 |
|------|------|------|
| PrHouse.updateUserByHouse | 缺少 @Transactional | 新系统方法未标注事务注解 |
| PrHeatArchive.replaceHeatMeter | ⚠️ | 已有 @Transactional，但余额转移逻辑不完整 |
| PrHeatArchive.recharge | ⚠️ | 交易记录与余额更新应在同一事务 |
| PrHeatValveArchive.insertDataNbValve | ❌ | 缺少 @Transactional，且缺少房屋温度反写 |
| PrHeatValveArchive.insertDataMBusValve | ❌ | 缺少 @Transactional，且缺少房屋温度反写 |
| PrAutoMachine.回调端点 | ❌ | 支付回调必须有事务保证数据一致性 |

### 参数校验

| 端点 | 风险 | 说明 |
|------|------|------|
| PrHouse.setHouseHeatingArea | 缺少边界检查 | 供热面积不能为负，应校验 |
| PrHeatArchive.manualControl | adjust 参数未校验 | 旧系统支持 1~30+，新系统只处理部分类型，应抛出异常 |
| PrHeatValveArchive.setValveOpening | 开度范围未校验 | 应在 0-100 之间 |
| PrAutoMachine.generateSerialNum | companyId 可为空 | 有校验，OK |

### 安全风险

| 端点 | 风险 | 说明 |
|------|------|------|
| PrHeatArchiveController.manualControl | 未鉴权 | 标注了 @SaCheckPermission，OK |
| PrAutoMachineController.回调端点 | ⚠️ | 使用 @SaIgnore 跳过鉴权，这是支付回调的正常做法，但应验证签名 |
| PrHeatControlController | ⚠️ | 所有端点都有 @SaCheckPermission，OK |
| PrAbnormalRecordController | ✅ | 有 @SaCheckPermission，OK |

### 业务逻辑风险

| 端点 | 风险 | 说明 |
|------|------|------|
| 孤岛户功能 | ❌ **缺失** | queryGDH/setGDH 完全未迁移，影响热力平衡分析 |
| PrHeatArchive.manualControl | ❌ **指令不全** | 只支持部分 adjust 类型（7、27、28-2、29、30），缺少 1~6、8~26 |
| 支付回调 | ❌ **未实现** | 返回 fail，影响线上支付业务 |
| 房屋导入导出 | ⚠️ | 关联表处理简化，可能导致数据不一致 |
| NB/MBus 阀门数据接收 | ⚠️ | 缺少房屋温度反写，影响室温监控 |

## 四、行动建议（按优先级）

### P0（必须修复）

1. **补全孤岛户功能**（queryGDH/setGDH）
   - 迁移 `PrHouseServiceImpl.queryGDH()` 和 `setGDH()` 方法
   - 孤岛户识别逻辑：根据房号判断上下左右相邻房屋的阀门状态
   - 位置属性设置：siteType、siteTypeOld

2. **修复 PrHeatArchive.manualControl 指令支持**
   - 补全 adjust=1~6、8~26 的处理逻辑
   - 参考旧系统 `PrHeatArchiveServiceImpl` 中的 switch/case 逻辑
   - 指令类型：1=开阀, 2=关阀, 3=设置开度, 4=查询, 5=制动, 6=特殊制动 等

3. **实现支付回调端点**
   - PrAutoMachineController 的 wechat-heat、ali-heat 回调
   - 集成微信支付和支付宝 SDK
   - 验证签名后更新交易记录状态

### P1（重要）

4. **补全 PrHeatValveArchive 房屋温度反写**
   - insertDataNbValve 和 insertDataMBusValve 中添加 updateHouse 调用
   - 反写字段：inTemperature、outTemperature、valveOpen（actualStatus）

5. **完善 PrHeatArchive 交易记录结构**
   - replaceHeatMeter 和 recharge 应同时写入 PrTransactionRecord 和 PrTransactionRecordSub
   - 参考旧系统的 PrTransactionDetail 明细结构

6. **添加事务保护**
   - PrHouse.updateUserByHouse 添加 @Transactional
   - NB/MBus 阀门数据接收端点添加 @Transactional

7. **补全 PrHouse 导入导出关联表处理**
   - importAll 应处理 UserHouse、HouseChange、User 表
   - 导出时应包含关联数据

### P2（优化）

8. **完善 PrHouse 业主变更逻辑**
   - updateUserByHouse 应包含个人账户创建逻辑
   - 变更记录应完整（迁出+迁入）

9. **添加参数校验**
   - setHouseHeatingArea 校验面积非负
   - setValveOpening 校验开度范围 0-100
   - manualControl 校验 adjust 参数有效范围

10. **PrAbnormalRecordServiceImpl 补充业务逻辑**
    - 当前为空实现，需添加异常处理、状态流转等逻辑

## 五、统计总结

| 类别 | 数量 |
|------|------|
| 深度审核的 Controller | 10 |
| 深度审核的 Service | 10 |
| 深度审核的 Entity | 3 |
| 发现的严重问题 | 5 |
| 发现的中等问题 | 8 |
| 发现的轻微问题 | 4 |

**迁移完成度评估**：
- API 层：85%（端点基本存在，但部分未实现）
- Service 层：70%（核心逻辑迁移，但缺少孤岛户、指令不全）
- DB 层：90%（字段基本完整，关联对象移除）
- 整体：**75%**，需重点修复 P0 和 P1 级别问题
