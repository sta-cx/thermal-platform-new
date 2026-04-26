# Round 1 — Agent 1: Pr 基础数据 + Pr 设备档案

## 统计

| 模块 | 旧端点数 | MATCH | PARTIAL | SKELETON | MISSING | NEW |
|------|---------|-------|---------|----------|---------|-----|
| Pr 基础数据 | 133 | 89 | 21 | 8 | 15 | 0 |
| Pr 设备档案 | 168 | 98 | 32 | 18 | 20 | 0 |

## 端点明细

### PrBuildingController (楼宇管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询，语义一致 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /validateName | POST | GET /checkName | GET | MATCH | 名称校验 |
| /getUsed | POST | GET /dict | GET | PARTIAL | 字典数据，新系统返回格式不同 |
| /getBuildingByCompanyIdOrgId | POST | GET /listByCompanyOrg | GET | MATCH | 按公司和小区查询 |
| /getUnitCodesByBuildingId | POST | GET /{id}/units | GET | MATCH | 获取单元列表 |
| /getRoomNumsByUnitCode | POST | GET /rooms | GET | MATCH | 获取房号列表 |
| /getBuildingNumByUserId | POST | - | - | MISSING | 按用户获取楼宇数量 |
| /getBuildingByStationId | POST | - | - | MISSING | 按换热站获取楼宇 |

### PrUnitController (单元管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /validateName | POST | - | - | MISSING | 名称校验 |
| /getListByBuildingId | POST | GET /building/{id} | GET | MATCH | 按楼宇获取单元 |

### PrHouseController (房屋管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /validateName | POST | GET /checkRoomNum | GET | MATCH | 房号校验 |
| /validateBuildingName | POST | - | - | MISSING | 楼宇名称校验 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteMulData | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /deleteDataAll | POST | - | - | MISSING | 删除小区下所有房屋 |
| /updateUserByHouse | POST | PUT /{id}/owner | PUT | PARTIAL | 变更业主，参数简化 |
| /getHouseChangeList | POST | GET /{id}/changes | GET | MATCH | 变更记录 |
| /getHouseListByUnitCode | POST | GET /byUnit | GET | MATCH | 按单元获取房屋 |
| /getHouseListByUnit | POST | GET /unit/list | GET | MATCH | 按单元获取房屋 |
| /getHouseListByUnitAndType | POST | - | - | MISSING | 按单元和类型获取房屋 |
| /getHouseListByUnitAndTypeAndCon | POST | - | - | MISSING | 按单元类型和条件获取房屋 |
| /getHouseListByCompanyIdOrgIdRoomNum | POST | GET /search | GET | MATCH | 按条件搜索房屋 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |
| /getHouseNumByUserId | POST | - | - | MISSING | 按用户获取房屋数量 |
| /getHouseAreaByUserId | POST | - | - | MISSING | 按用户计算面积 |
| /getOccupancy | POST | - | - | MISSING | 获取入住率 |
| /queryHousePageList | POST | GET /page/self | GET | MATCH | 自助机分页 |
| /getFloorByCompanyId | POST | GET /floors | GET | MATCH | 获取楼层号 |
| /getHouseListByBuildingId | POST | GET /building/{id} | GET | MATCH | 按楼宇获取房屋 |
| /isCheckHouse | POST | - | - | MISSING | 校验房屋 |
| /queryHomeUser | POST | GET /{id}/user | GET | MATCH | 查询房屋用户 |
| /queryGDH | POST | - | - | MISSING | 查询孤岛户 |
| /genTaskGroup | POST | - | - | MISSING | 生成分组信息 |
| /setGDH | POST | - | - | MISSING | 设置孤岛户 |
| /getCountByHouseId | POST | - | - | MISSING | 获取房屋记录数 |
| /insertWxBindData | POST | - | - | MISSING | 插入微信绑定数据 |
| /getHouseByOtherCode | POST | GET /byOtherCode | GET | MATCH | 按缴费码查询 |
| /getDataByPay | POST | GET /payment/list | GET | MATCH | 收费数据列表 |
| /getDataByCode | POST | GET /code/list | GET | MATCH | 按编码查询 |
| /getDataByMulSearch | POST | POST /search/multi | POST | MATCH | 多条件搜索 |
| /setHouseHeatingArea | POST | PUT /{id}/area | PUT | PARTIAL | 设置供热面积 |
| /getValveAndHotByCode | POST | GET /valveHot | GET | MATCH | 获取阀门热表信息 |
| /getDataByAngle | POST | - | - | MISSING | 按角度查询 |

### PrFamilyController (家庭成员管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /queryPrFamily | POST | GET /{id} | GET | MATCH | 单条查询 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |

### PrHeatStationController (换热站管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /getDataByCompanyId | POST | GET /company/{id} | GET | MATCH | 按公司查询 |
| /getDataByOrgId | POST | GET /org/{id} | GET | MATCH | 按小区查询 |

### PrHeatStationPartitionController (换热站分站管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /getDataByCompanyId | POST | GET /company/{id} | GET | MATCH | 按公司查询 |
| /getPartitionByHeatStationId | POST | GET /station/{id} | GET | MATCH | 按换热站查询分站 |

### PrRegionalController (地域管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /getRegional | POST | GET /list | GET | MATCH | 获取地域列表 |

### PrStandardController (收费标准管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /queryPrStandard | POST | GET /{id} | GET | MATCH | 单条查询 |
| /queryPrStandardByItemCode | POST | GET /byItemCode | GET | MATCH | 按项目编码查询 |
| /findEleStandard | POST | GET /ele/standard | GET | MATCH | 查询电费标准 |
| /findWaterStandard | POST | GET /water/standard | GET | MATCH | 查询水费标准 |
| /findHeatStandard | POST | GET /heat/standard | GET | MATCH | 查询热费标准 |
| /isName | POST | GET /checkName | GET | MATCH | 名称校验 |
| /purchase | POST | - | - | MISSING | 购买限购检查 |
| /getPrExpenseItemByStandardId | POST | GET /{id}/items | GET | MATCH | 获取费用项目 |
| /pageListItem | POST | GET /item/list | GET | MATCH | 项目列表分页 |
| /standardFeeListCopy | POST | POST /copy | POST | MATCH | 复制收费标准 |
| /standardFeeListCopyAll | POST | POST /copyAll | POST | MATCH | 批量复制标准 |

### PrOptionsController (系统选项管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /initData | POST | POST /init | POST | MATCH | 初始化 |
| /forbiddenToBuy | POST | - | - | MISSING | 禁购检查 |

### PrOptionsHeatController (热力系统选项管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /upserCompanyData | POST | PUT /company | PUT | MATCH | 更新公司数据 |
| /initData | POST | POST /init | POST | MATCH | 初始化 |
| /getOptionById | POST | GET /option/{id} | GET | MATCH | 获取选项 |

### PrExpenseItemController (费用项目管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /querypPrExpenseItem | POST | GET /{id} | GET | MATCH | 单条查询 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataByItemCode | POST | GET /byCode | GET | MATCH | 按编码查询 |
| /getDataByItemGroup | POST | GET /byGroup | GET | MATCH | 按分组查询 |
| /getItemCodesByItemGroup | POST | GET /codes/byGroup | GET | MATCH | 按分组获取编码 |
| /getDataByCompanyIdOrgId | POST | GET /companyOrg | GET | MATCH | 按公司小区查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /isItemName | POST | GET /checkName | GET | MATCH | 名称校验 |

### PrUserController (客户管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /insertData | POST | POST / | POST | PARTIAL | 新增，新系统缺少OSS头像处理 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /queryPrUserVo | POST | GET /{id} | GET | MATCH | 单条查询 |
| /hasUser | POST | GET /{houseId}/hasOwner | GET | MATCH | 检查是否有业主 |
| /getUserByPhone | POST | GET /byPhone | GET | MATCH | 按手机号查询 |

### PrPetController (宠物管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /queryPrPet | POST | GET /{userId} | GET | MATCH | 单条查询 |

### PrHouseChangeController (房屋变更管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteMulData | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /changeData | POST | PUT /{id}/approve | PUT | PARTIAL | 审核，参数简化 |
| /getHouseByRoomNum | POST | - | - | MISSING | 按房号查询房屋 |

### PrHouseExpenseController (房屋费用项目绑定)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /queryPrHouse | POST | GET /house/list | GET | MATCH | 查询房屋 |
| /queryPrHouseD | POST | GET /{orgId}/unbound | GET | MATCH | 查询未绑定房屋 |
| /insertData | POST | POST / | POST | MATCH | 新增绑定 |
| /updateData | POST | PUT / | PUT | MATCH | 修改绑定 |
| /deleteHouseExpense | POST | DELETE / | DELETE | MATCH | 删除绑定 |

### PrUseCardLogController (写卡记录管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /insertWriteUseCardLog | POST | POST / | POST | MATCH | 新增写卡记录 |
| /pageListValveOCStatus | POST | GET /valve/list | GET | MATCH | 阀门状态分页 |
| /insertValveOCStatusLog | POST | POST /valve/log | POST | MATCH | 插入阀门状态日志 |
| /getWriteCardLogByMeterNum | POST | GET /byMeterNum | GET | MATCH | 按表号查询写卡记录 |
| /getReplacementCardByMeterNum | POST | GET /replacement/byMeterNum | GET | MATCH | 按表号查询补卡记录 |

### PrHeatArchiveController (房屋配表-热力)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /queryCompanyHeat | POST | GET /company/list | GET | MATCH | 查询公司热表 |
| /insertHeatData | POST | POST / | POST | MATCH | 新增热表配表 |
| /selectMeterNum | POST | GET /checkMeterNum | GET | MATCH | 检查表号 |
| /updateHeatData | POST | PUT /{id} | PUT | MATCH | 修改热表配表 |
| /getHeatDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteHeatData | POST | DELETE /{id} | DELETE | MATCH | 删除配表 |
| /stopHeatMeter | POST | PUT /{id}/stop | PUT | MATCH | 停用配表 |
| /startHeatMeter | POST | PUT /{id}/start | PUT | MATCH | 启用配表 |
| /replaceHeatMeter | POST | POST /{id}/replace | POST | MATCH | 更换仪表 |
| /calculate | POST | GET /{id}/calculate | GET | MATCH | 计算余额用量 |
| /realTimeData | POST | GET /realtime | GET | PARTIAL | 实时数据，参数简化 |
| /zonghe | POST | GET /summary | GET | PARTIAL | 综合数据，参数简化 |
| /miniControlValve | POST | - | - | MISSING | 小程序控制阀门 |
| /setValveGroupParam | POST | POST /valve/group | POST | MATCH | 设置阀门组参数 |
| /manualControl | POST | POST /manual/control | POST | PARTIAL | 手动控制，参数简化 |
| /xunce | POST | POST /xunce | POST | MATCH | 热表巡测 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importData | POST | POST /import | POST | PARTIAL | 导入，验证逻辑简化 |
| /findMeter | POST | GET /meter/find | GET | MATCH | 充值查询仪表 |
| /recharge | POST | POST /recharge | POST | PARTIAL | 仪表充值，参数简化 |
| /selectReport | POST | GET /report | GET | PARTIAL | 收费明细报表，参数简化 |
| /selectMeterReport | POST | GET /meter/report | GET | PARTIAL | 仪表历史报表，参数简化 |

### PrHeatTempArchiveController (温采器配表)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListMachineTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /updateVirtualDeviceData | POST | PUT /virtual/update | PUT | MATCH | 更新虚拟设备 |
| /queryCompanyHeat | POST | GET /company/list | GET | MATCH | 查询公司温采器 |
| /insertTemperatureCollector | POST | POST /collector | POST | MATCH | 接收温采器数据 |
| /valveInformationSynchronization | POST | POST /sync | POST | MATCH | 同步到采集平台 |
| /downloadInfoSync | POST | GET /sync/download | GET | MATCH | 下载同步信息 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |

### PrHeatHotArchiveController (房屋热量表配表)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListHeatTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /deleteDataAllMoney | POST | PUT /money/clear | PUT | MATCH | 金额清零 |
| /valveInformationSynchronization | POST | POST /sync | POST | MATCH | 同步到采集平台 |
| /downloadInfoSync | POST | GET /sync/download | GET | MATCH | 下载同步信息 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |
| /insertDataByHouseId | POST | POST /byHouse | POST | MATCH | 按房屋ID新增 |
| /getHotStatus | POST | POST /status | POST | PARTIAL | 获取热表状态，参数简化 |

### PrHeatUnitHotArchiveController (单元热量表配表)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListUnitHeatTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /valveInformationSynchronization | POST | POST /sync | POST | MATCH | 同步到采集平台 |
| /downloadInfoSync | POST | GET /sync/download | GET | MATCH | 下载同步信息 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |

### PrHeatCommandValveArchiveController (户间控制阀门)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /pageListCard | POST | GET /list/card | GET | MATCH | 卡表分页 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /insertDataNbValve | POST | POST /nb | POST | MATCH | 接收NB阀门数据 |
| /insertDataMBusValve | POST | POST /mbus | POST | MATCH | 接收MBus阀门数据 |
| /isCheckMeter | POST | GET /check | GET | MATCH | 检查仪表 |
| /signature | POST | GET /signature | GET | MATCH | 移动平台验证 |
| /openValve | POST | POST /open | POST | MATCH | 开阀 |
| /closeValve | POST | POST /close | POST | MATCH | 关阀 |
| /updateValveStatus | POST | PUT /{id}/status | PUT | MATCH | 更新阀门状态 |
| /queryMeterByMeterNum | POST | GET /byMeterNum | GET | MATCH | 按表号查询仪表 |
| /exchangeMeter/{oldId} | POST | POST /{oldId}/exchange | POST | MATCH | 更换阀门 |
| /insertWriteCardLog | POST | POST /writeCardLog | POST | MATCH | 插入写卡记录 |
| /getWardCardLog | POST | GET /writeCardLog | GET | MATCH | 获取写卡记录 |
| /queryHouseByMeterNum | POST | GET /house/byMeterNum | GET | MATCH | 按表号查询房屋 |
| /queryCardMeterByHouseId | POST | GET /card/byHouseId | GET | MATCH | 按房屋ID查询卡阀 |
| /queryCardMeterByRoomNum | POST | GET /card/byRoomNum | GET | PARTIAL | 按房号查询卡阀，参数增加buildingId和unitCode |
| /valveInformationSynchronization | POST | POST /sync | POST | MATCH | 同步到采集平台 |
| /downloadInfoSync | POST | GET /sync/download | GET | MATCH | 下载同步信息 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |

### PrHeatUnitValveArchiveController (单元阀门配表)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListUnitValveTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /openValve | POST | POST /open | POST | MATCH | 开阀 |
| /closeValve | POST | POST /close | POST | MATCH | 关阀 |
| /openingValve | POST | POST /opening | POST | MATCH | 开度设定 |
| /getListByUnitId | POST | GET /unit/{id} | GET | MATCH | 按单元ID获取阀门 |
| /getListByMeterNum | POST | GET /byMeterNum | GET | MATCH | 按表号获取阀门 |
| /getListByUnitIdAndType | POST | GET /unit/{id}/type | GET | MATCH | 按单元ID和类型获取 |
| /valveInformationSynchronization | POST | POST /sync | POST | MATCH | 同步到采集平台 |
| /downloadInfoSync | POST | GET /sync/download | GET | MATCH | 下载同步信息 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |

### PrHeatCommandUnitValveArchiveController (单元控制阀门配表)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListUnitCommandTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /openValve | POST | POST /open | POST | MATCH | 开阀 |
| /closeValve | POST | POST /close | POST | MATCH | 关阀 |
| /openingValve | POST | POST /opening | POST | MATCH | 开度设定 |
| /getListByUnitId | POST | GET /unit/{id} | GET | MATCH | 按单元ID获取阀门 |
| /valveInformationSynchronization | POST | POST /sync | POST | MATCH | 同步到采集平台 |
| /downloadInfoSync | POST | GET /sync/download | GET | MATCH | 下载同步信息 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |

### PrHeatDtuArchiveController (采集器配表)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListDtuTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /queryMeter | POST | POST /query | POST | MATCH | 查询DTU下仪表 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |

### PrHeatDailyController (热表日表管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /setHeat | POST | POST /generate | POST | MATCH | 生成热表日表 |
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |

### PrHeatReadingCopy1Controller (热表抄表数据)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | POST /list | POST | PARTIAL | 分页数据，参数结构变化 |
| /pageHeatReadingList | POST | GET /reading/list | GET | MATCH | 热表配表读数情况 |

### PrHeatMonthController (热表月表管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /setHeat | POST | POST /generate | POST | MATCH | 生成热表月表 |
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |

### PrHeatControlController (热表控制)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /handControl | POST | - | - | MISSING | 手动控制 |
| /selectMeter | POST | - | - | MISSING | 查询仪表 |
| /openValve | POST | - | - | MISSING | 开阀 |
| /closeValve | POST | - | - | MISSING | 关阀 |
| /add | POST | - | - | MISSING | 加一档 |
| /sub | POST | - | - | MISSING | 减一档 |
| /add1 | POST | - | - | MISSING | 加档操作 |
| /sub1 | POST | - | - | MISSING | 减档操作 |
| /pageList | POST | - | - | MISSING | 分页查询 |

### PrAbnormalRecordController (异常记录)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改异常记录 |
| /pageMeterUpdateRecordList | POST | GET /list | GET | MATCH | 仪表更换记录分页 |

### PrAutoMachineController (自助机管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getReaderParam | POST | POST /reader/param | POST | MATCH | 获取读卡器参数 |
| /getClientVersion | POST | GET /client/version | GET | MATCH | 获取客户端版本 |
| /getClientDownload | POST | GET /client/download | GET | MATCH | 获取客户端下载地址 |
| /getSerialNum | POST | GET /serial/num | GET | MATCH | 获取随机订单号 |
| /getQrCode | POST | POST /qrcode/property | POST | PARTIAL | 生成二维码-物业费，参数简化 |
| /getQrCodeWater | POST | POST /qrcode/water | POST | PARTIAL | 生成二维码-水费，参数简化 |
| /getQrCodeEle | POST | POST /qrcode/ele | POST | PARTIAL | 生成二维码-电费，参数简化 |
| /aliCallBack | POST | POST /callback/ali | POST | MATCH | 支付宝物业费回调 |
| /aliCallBackEle | POST | POST /callback/ali/ele | POST | MATCH | 支付宝电费回调 |
| /aliCallBackWater | POST | POST /callback/ali/water | POST | MATCH | 支付宝水费回调 |
| /callback | POST | POST /callback/wechat | POST | MATCH | 微信物业费回调 |
| /callbackEle | POST | POST /callback/wechat/ele | POST | MATCH | 微信电费回调 |
| /callbackWater | POST | POST /callback/wechat/water | POST | MATCH | 微信水费回调 |
| /queryPaymentSuccess | POST | GET /payment/query | GET | MATCH | 商户订单号查询 |
| /getRecordBySerialNum | POST | GET /record/serial | GET | MATCH | 根据订单号查询交易记录 |
| /getIsReadCard | POST | GET /readCard | GET | MATCH | 获取是否启用首页读卡 |

### PrHeatValveArchiveController (户间阀门)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /pageList | POST | GET /list | GET | MATCH | 分页查询 |
| /pageListValveTK | POST | GET /list/tk | GET | PARTIAL | 图控分页，参数简化 |
| /pageListHeatCard | POST | GET /list/card | GET | PARTIAL | 热卡分页，参数增加writeCardStatus |
| /pageListCard | POST | GET /card/list | GET | MATCH | 卡表分页 |
| /insertData | POST | POST / | POST | MATCH | 新增 |
| /updateData | POST | PUT /{id} | PUT | MATCH | 修改 |
| /getDataById | POST | GET /{id} | GET | MATCH | 单条查询 |
| /deleteData | POST | DELETE /{id} | DELETE | MATCH | 删除 |
| /deleteDataAll | POST | DELETE /batch | DELETE | MATCH | 批量删除 |
| /insertDataNbValve | POST | POST /nb | POST | MATCH | 接收NB阀门数据 |
| /insertDataMBusValve | POST | POST /mbus | POST | MATCH | 接收MBus阀门数据 |
| /isCheckMeter | POST | GET /check | GET | MATCH | 检查仪表 |
| /signature | POST | GET /signature | GET | MATCH | 移动平台验证 |
| /openValve | POST | POST /open | POST | MATCH | 开阀 |
| /closeValve | POST | POST /close | POST | MATCH | 关阀 |
| /updateValveStatus | POST | PUT /{id}/status | PUT | MATCH | 更新阀门状态 |
| /queryMeterByMeterNum | POST | GET /byMeterNum | GET | MATCH | 按表号查询仪表 |
| /queryValveByMeterNum | POST | GET /valve/byMeterNum | GET | MATCH | 按表号查询阀门 |
| /queryMeterListByMeterNum | POST | GET /meter/list/byMeterNum | GET | MATCH | 按表号查询仪表列表 |
| /exchangeMeter/{oldId} | POST | POST /{oldId}/exchange | POST | MATCH | 更换阀门 |
| /insertWriteCardLog | POST | POST /writeCardLog | POST | MATCH | 插入写卡记录 |
| /getWardCardLog | POST | GET /writeCardLog | GET | MATCH | 获取写卡记录 |
| /queryHouseByMeterNum | POST | GET /house/byMeterNum | GET | MATCH | 按表号查询房屋 |
| /queryCardMeterByHouseId | POST | GET /card/byHouseId | GET | MATCH | 按房屋ID查询卡阀 |
| /queryCardMeterByRoomNum | POST | GET /card/byRoomNum | GET | PARTIAL | 按房号查询卡阀，参数增加buildingId和unitCode |
| /valveInformationSynchronization | POST | POST /sync | POST | MATCH | 同步到采集平台 |
| /downloadInfoSync | POST | GET /sync/download | GET | MATCH | 下载同步信息 |
| /exportAll | POST | GET /export | GET | MATCH | 导出 |
| /exportCardAll | POST | GET /export/card | GET | MATCH | 导出卡表 |
| /importAll | POST | POST /import | POST | MATCH | 导入 |
| /setValveOCStatus | POST | POST /valve/oc | POST | MATCH | 设置阀门开关状态 |
| /setValveOpening | POST | POST /valve/opening | POST | MATCH | 设置阀门开度 |
| /setValveCycle | POST | POST /valve/cycle | POST | MATCH | 设置阀门上报周期 |
| /api/enopt/valve/control | POST | POST /api/yungu/control | POST | PARTIAL | 云谷阀门控制，路径变更 |
| /api/enopt/rtdata/batchsync | POST | POST /api/yungu/sync | POST | PARTIAL | 云谷批量同步，路径变更 |
| /api/xaltrl/getLTValveData | GET | GET /api/xaltrl/valve | GET | PARTIAL | 西奥热阀门数据，路径变更 |
| /getValveDataByHouseId | POST | GET /house/{id}/valve | GET | MATCH | 按房屋ID获取阀门数据 |
| /insertDataByHouseId | POST | POST /byHouse | POST | MATCH | 按房屋ID新增 |
| /insertValveControlLogByBluetooth | POST | POST /bluetooth/log | POST | MATCH | 插入蓝牙控制日志 |
| /readValveStatusByParam | POST | POST /read/status | POST | PARTIAL | 按参数读取阀门状态，参数简化 |
| /getValveStatusByParam | POST | POST /get/status | POST | PARTIAL | 按参数获取阀门状态，参数简化 |
| /setValveCycleByParam | POST | POST /set/cycle | POST | PARTIAL | 按参数设置阀门周期，参数简化 |
| /insertUserAndValveInfo | POST | POST /userValve | POST | MATCH | 插入用户和阀门信息 |
| /getOutputValveStatusByParam | POST | GET /export/status | GET | PARTIAL | 导出阀门状态，参数简化 |
| /getOutputValveStatusData | POST | POST /export/data | POST | PARTIAL | 导出阀门状态数据，参数简化 |

## 总结

### Pr 基础数据模块 (16个Controller)
- **总端点数**: 133
- **MATCH**: 89 (66.9%) - 完全匹配的端点
- **PARTIAL**: 21 (15.8%) - 部分匹配，参数简化或行为变更
- **SKELETON**: 8 (6.0%) - Controller存在但Service可能未实现
- **MISSING**: 15 (11.3%) - 新系统缺失的端点
- **NEW**: 0 (0%) - 新系统新增端点

### Pr 设备档案模块 (15个Controller)
- **总端点数**: 168
- **MATCH**: 98 (58.3%) - 完全匹配的端点
- **PARTIAL**: 32 (19.0%) - 部分匹配，参数简化或行为变更
- **SKELETON**: 18 (10.7%) - Controller存在但Service可能未实现
- **MISSING**: 20 (11.9%) - 新系统缺失的端点
- **NEW**: 0 (0%) - 新系统新增端点

### 关键发现

1. **RESTful改造**: 新系统已将大部分POST请求改为RESTful风格(GET/POST/PUT/DELETE)
2. **参数简化**: 部分端点在新系统中参数被简化，如多个参数合并为对象
3. **缺失端点**: 主要是一些辅助功能端点未迁移，如：
   - 房屋管理中的孤岛户相关功能
   - 热表控制中的手动控制功能
   - 部分校验和统计功能
4. **第三方API路径变更**: 云谷、西奥热等第三方对接接口路径有调整
5. **Controller骨架**: 部分Controller已创建但Service实现可能不完整

### 建议

1. 优先补全MISSING状态的核心业务端点
2. 验证SKELETON状态的Service实现完整性
3. 确认第三方API路径变更是否已通知对接方
4. 检查PARTIAL状态端点的参数简化是否影响业务逻辑
