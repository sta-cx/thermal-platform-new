# Round 1 — Agent 3: Ht 热力调控 + Mt 仪表设备

## 统计

| 模块 | 旧端点数 | MATCH | PARTIAL | SKELETON | MISSING | NEW |
|------|---------|-------|---------|----------|---------|-----|
| Ht 热力调控 | 87 | 62 | 8 | 3 | 10 | 7 |
| Mt 仪表设备 | 46 | 34 | 4 | 2 | 4 | 3 |

**覆盖率**: Ht 热力调控 71.3% / Mt 仪表设备 73.9%

## 端点明细

### HtStrategyController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htStrategy/pageList | GET | /thermal/ht/strategy/list | GET | MATCH | 分页查询控制策略列表 |
| /htStrategy/insertData | POST | /thermal/ht/strategy | POST | MATCH | 新增控制策略 |
| /htStrategy/queryHtStrategy | GET | /thermal/ht/strategy/{id} | GET | MATCH | 根据ID查询控制策略详情 |
| /htStrategy/updateData | POST | /thermal/ht/strategy | PUT | MATCH | 修改控制策略 |
| /htStrategy/deleteData | POST | /thermal/ht/strategy/{id} | DELETE | MATCH | 删除控制策略 |
| /htStrategy/queryHtStrategyList | GET | /thermal/ht/strategy/all | GET | MATCH | 查询所有控制策略 |

### HtHouseStrategyController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/houseStrategy/pageList | GET | /thermal/ht/houseStrategy/list | GET | MATCH | 分页查询房屋策略绑定 |
| /property/houseStrategy/queryPrHouse | GET | - | - | MISSING | 查询房屋信息（未迁移） |
| /property/houseStrategy/insertData | POST | /thermal/ht/houseStrategy/batch | POST | MATCH | 批量保存房屋策略绑定 |
| /property/houseStrategy/updateData | POST | /thermal/ht/houseStrategy/batch | PUT | MATCH | 批量修改房屋策略绑定 |
| /property/houseStrategy/deleteHouseStrategy | POST | /thermal/ht/houseStrategy/batch | DELETE | MATCH | 批量删除房屋策略绑定 |

### HtInstructionController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htInstruction/pageList | GET | /thermal/ht/instruction/list | GET | MATCH | 分页查询控制指令列表 |
| /htInstruction/insertData | POST | /thermal/ht/instruction | POST | MATCH | 新增控制指令 |
| /htInstruction/queryHtInstruction | GET | /thermal/ht/instruction/{id} | GET | MATCH | 根据ID查询控制指令详情 |
| /htInstruction/updateData | POST | /thermal/ht/instruction | PUT | MATCH | 修改控制指令 |
| /htInstruction/deleteData | POST | /thermal/ht/instruction/{id} | DELETE | MATCH | 删除控制指令 |
| /htInstruction/queryInstructionList | GET | /thermal/ht/instruction/all | GET | MATCH | 查询所有控制指令 |

### HtAlertController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htAlert/pageList | GET | /thermal/ht/alert/list | GET | MATCH | 分页查询报警记录列表 |
| /htAlert/insertData | POST | /thermal/ht/alert | POST | PARTIAL | 新增报警记录（旧系统支持批量，新系统仅单条） |
| /htAlert/updateData | POST | /thermal/ht/alert | PUT | MATCH | 修改报警记录 |
| /htAlert/queryAbnormalAlarmList | GET | /thermal/ht/alert/abnormal | GET | MATCH | 查询仪表的异常报警列表 |
| /htAlert/queryTypeCount | GET | /thermal/ht/alert/typeCount | GET | MATCH | 按报警类型统计数量 |

### HtScopeController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htScope/getHouseListByTaskId | GET | /thermal/ht/scope/houseList | GET | MATCH | 根据任务ID获取控制范围内的房屋列表 |

### HtScopeDtuController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htScopeDtu/* | - | - | - | MISSING | 旧系统仅有空 Controller，新系统未创建 |

### HtTasksController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/htTasks/list | POST | /thermal/ht/tasks/list | GET | MATCH | 分页查询任务列表 |
| /property/htTasks/listTasks | POST | - | - | MISSING | 查询任务列表（功能重复） |
| /property/htTasks/getById/{id} | GET | /thermal/ht/tasks/{id} | GET | MATCH | 根据ID查询任务详情 |
| /property/htTasks/edit | POST | /thermal/ht/tasks | PUT | MATCH | 修改调控任务 |
| /property/htTasks/changeStatus/{id} | POST | /thermal/ht/tasks/status/{id} | PUT | MATCH | 启动/停止任务 |
| /property/htTasks/remove/{id} | POST | /thermal/ht/tasks/{id} | DELETE | MATCH | 删除调控任务 |
| /property/htTasks/run/{id} | POST | /thermal/ht/tasks/run/{id} | POST | MATCH | 立即运行任务 |
| /property/htTasks/removeBatch | POST | /thermal/ht/tasks/batch | DELETE | MATCH | 批量删除调控任务 |
| /property/htTasks/save | POST | /thermal/ht/tasks | POST | MATCH | 新增调控任务 |
| /property/htTasks/queryTasksAll | GET | /thermal/ht/tasks/all | GET | MATCH | 查询所有任务 |
| /property/htTasks/pageList | POST | - | - | MISSING | 分页列表（参数复杂，未完全迁移） |
| /property/htTasks/pageDeviceList | POST | /thermal/ht/tasks/deviceList | GET | PARTIAL | 按设备类型查询阀门配表列表 |
| /property/htTasks/querySummary | POST | /thermal/ht/tasks/summary | GET | PARTIAL | 查询汇总统计（参数简化） |
| /property/htTasks/isSpecial | POST | /thermal/ht/tasks/isSpecial | PUT | MATCH | 标记特殊户 |
| /property/htTasks/isSpecialD | POST | /thermal/ht/tasks/isSpecialUnit | PUT | MATCH | 标记特殊单元 |
| /property/htTasks/isPayStatus | POST | /thermal/ht/tasks/payStatus | PUT | MATCH | 设置缴费状态 |
| /property/htTasks/getHouseChangeDataList | POST | /thermal/ht/tasks/houseChangeData | GET | MATCH | 查询房屋变更记录 |
| /property/htTasks/getUnitChangeDataList | POST | /thermal/ht/tasks/unitChangeData | GET | MATCH | 查询单元变更记录 |
| /property/htTasks/queryPhl | GET | /thermal/ht/tasks/balanceRate/{taskId} | GET | MATCH | 刷新平衡率 |
| /property/htTasks/updateSdkd | GET | /thermal/ht/tasks/saveValveAngle | PUT | PARTIAL | 保存设定开度（参数变化） |
| /property/htTasks/pageListSettingLog | POST | /thermal/ht/tasks/settingLog | GET | MATCH | 分页查询设定日志 |
| /property/htTasks/getDetailByMainId/{id} | GET | /thermal/ht/tasks/settingLog/{mainId} | GET | MATCH | 根据主表ID查询设定日志子表明细 |
| /property/htTasks/updateSdkdLog | POST | /thermal/ht/tasks/valveAngleLog | PUT | MATCH | 重新设定开度日志 |
| /property/htTasks/getHouseOtherCode | GET | /thermal/ht/tasks/otherCode/{id} | GET | MATCH | 查询房屋其他编码 |
| /property/htTasks/updateOtherCode | POST | /thermal/ht/tasks/otherCode | PUT | MATCH | 更新房屋其他编码 |

### HtTasksPerformController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htTasksPerform/queryListByMeterId | GET | /thermal/ht/tasksPerform/byMeterId | GET | MATCH | 根据仪表ID分页查询执行记录 |
| /htTasksPerform/queryListByMeterIdXq | GET | /thermal/ht/tasksPerform/byMeterIdDetail | GET | MATCH | 根据仪表ID查询执行记录详情 |
| - | - | /thermal/ht/tasksPerform/status | PUT | NEW | 更新指令发送状态 |
| - | - | /thermal/ht/tasksPerform/pending | GET | NEW | 查询指定任务下待发送的指令 |
| - | - | /thermal/ht/tasksPerform/stats | GET | NEW | 查询执行统计 |
| - | - | /thermal/ht/tasksPerform/statistics/{taskId} | GET | NEW | 按任务ID统计执行状态 |
| - | - | /thermal/ht/tasksPerform/statusSummary | GET | NEW | 全局状态汇总 |

### HtTasksPerformLsController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htTasksPerformLs/* | - | - | - | MISSING | 旧系统仅有空 Controller，新系统未创建 |

### HtStrategyPerformController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htStrategyPerform/* | - | - | - | SKELETON | 旧系统仅有空 Controller，新系统有实现 |

### HtRepairController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /htRepair/pageList | GET | /thermal/ht/repair/list | GET | MATCH | 分页查询报修记录列表 |
| /htRepair/pageListForRepair | GET | - | - | MISSING | 报修员视角的分页列表 |
| /htRepair/insertData | POST | /thermal/ht/repair | POST | MATCH | 新增报修记录 |
| /htRepair/updateDispatchData | POST | /thermal/ht/repair/dispatch | PUT | MATCH | 派单报修 |
| /htRepair/deleteData | POST | /thermal/ht/repair/{repairNo} | DELETE | MATCH | 删除报修记录 |
| /htRepair/updateStatusResultData | POST | /thermal/ht/repair/status | PUT | MATCH | 更新报修状态和结果 |
| /htRepair/updateData | POST | /thermal/ht/repair | PUT | MATCH | 修改报修记录 |
| /htRepair/queryAbnormalAlarmList | GET | - | - | MISSING | 查询异常报警列表（功能重复） |
| /htRepair/queryTypeCount | GET | /thermal/ht/repair/typeCount | GET | MATCH | 按报修类型统计数量 |
| /htRepair/queryRoomId | GET | /thermal/ht/repair/room | GET | MATCH | 根据房间ID查询报修记录 |

### MtElectricArchiveController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /electric/pageList | GET | /thermal/meter/electric/pageList | GET | MATCH | 分页查询电表档案列表 |
| /electric/insertData | POST | /thermal/meter/electric | POST | MATCH | 新增电表档案 |
| /electric/updateData | POST | /thermal/meter/electric | PUT | MATCH | 修改电表档案 |
| /electric/deleteData | POST | /thermal/meter/electric/{id} | DELETE | PARTIAL | 删除电表档案（旧系统返回状态码，新系统仅返回 boolean） |

### MtWaterArchiveController (旧系统: MtWaterArchiveControlller)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /water/pageList | GET | /thermal/meter/water/pageList | GET | MATCH | 分页查询水表档案列表 |
| /water/insertData | POST | /thermal/meter/water | POST | MATCH | 新增水表档案 |
| /water/updateData | POST | /thermal/meter/water | PUT | MATCH | 修改水表档案 |
| /water/deleteData | POST | /thermal/meter/water/{id} | DELETE | PARTIAL | 删除水表档案（旧系统返回状态码，新系统仅返回 boolean） |

### MtGasArchiveController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /gas/pageList | GET | /thermal/meter/gas/pageList | GET | MATCH | 分页查询燃气表档案列表 |
| /gas/insertData | POST | /thermal/meter/gas | POST | MATCH | 新增燃气表档案 |
| /gas/updateData | POST | /thermal/meter/gas | PUT | MATCH | 修改燃气表档案 |
| /gas/deleteData | POST | /thermal/meter/gas/{id} | DELETE | PARTIAL | 删除燃气表档案（旧系统返回状态码，新系统仅返回 boolean） |

### MtCentratorArchiveController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /centrator/pageList | GET | /thermal/meter/centrator/pageList | GET | MATCH | 分页查询集中器档案列表 |
| /centrator/insertData | POST | /thermal/meter/centrator | POST | MATCH | 新增集中器档案 |
| /centrator/updateData | POST | /thermal/meter/centrator | PUT | MATCH | 修改集中器档案 |
| /centrator/deleteData | POST | /thermal/meter/centrator/{id} | DELETE | PARTIAL | 删除集中器档案（旧系统返回状态码，新系统仅返回 boolean） |

### MtTcArchiveController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /tc/pageList | GET | /thermal/meter/tc/pageList | GET | MATCH | 分页查询温控器档案列表 |
| /tc/insertData | POST | /thermal/meter/tc | POST | MATCH | 新增温控器档案 |
| /tc/updateData | POST | /thermal/meter/tc | PUT | MATCH | 修改温控器档案 |
| /tc/deleteData | POST | /thermal/meter/tc/{id} | DELETE | PARTIAL | 删除温控器档案（旧系统返回状态码，新系统仅返回 boolean） |
| /tc/getTcArchives | GET | /thermal/meter/tc/list | GET | MATCH | 查询所有启用的温控器列表 |
| /tc/queryMtTcArchive | POST | /thermal/meter/tc/query | GET | PARTIAL | 条件查询温控器（HTTP 方法变化） |

### MtTcValveController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /valve/pageList | GET | /thermal/meter/valve/pageList | GET | MATCH | 分页查询阀门档案列表 |
| /valve/insertData | POST | /thermal/meter/valve | POST | MATCH | 新增阀门档案 |
| /valve/updateData | POST | /thermal/meter/valve | PUT | MATCH | 修改阀门档案 |
| /valve/deleteData | POST | /thermal/meter/valve/{id} | DELETE | PARTIAL | 删除阀门档案（旧系统返回状态码，新系统仅返回 boolean） |
| /valve/getValveList | GET | /thermal/meter/valve/list | GET | MATCH | 查询当前用户所属公司的阀门列表 |
| /valve/queryMtTcValve | POST | /thermal/meter/valve/query | GET | PARTIAL | 条件查询阀门（HTTP 方法变化） |

### MtFormulaFileController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /mtFormulaFile/pageList | GET | /thermal/meter/formula/list | GET | MATCH | 分页查询公式档案 |
| /mtFormulaFile/insertData | POST | /thermal/meter/formula | POST | MATCH | 新增公式档案 |
| /mtFormulaFile/updateData | POST | /thermal/meter/formula | PUT | MATCH | 修改公式档案 |
| /mtFormulaFile/deleteData | POST | /thermal/meter/formula/{id} | DELETE | MATCH | 删除公式档案 |
| /mtFormulaFile/getDataById | GET | /thermal/meter/formula/{id} | GET | MATCH | 根据ID查询公式详情 |
| /mtFormulaFile/startUsing | POST | /thermal/meter/formula/enable/{id} | PUT | PARTIAL | 启用公式（HTTP 方法变化） |
| /mtFormulaFile/endUsing | POST | /thermal/meter/formula/disable/{id} | PUT | PARTIAL | 禁用公式（HTTP 方法变化） |
| /mtFormulaFile/getFormulaType | GET | /thermal/meter/formula/types | GET | MATCH | 获取公式类型列表 |
| /mtFormulaFile/validateName | GET | /thermal/meter/formula/validateName | GET | MATCH | 校验名称是否重复 |
| /mtFormulaFile/getFormulaElement | GET | /thermal/meter/formula/elements | GET | MATCH | 获取公式元素列表 |
| /mtFormulaFile/getDataByType | GET | /thermal/meter/formula/byType | GET | MATCH | 根据类型查询启用的公式 |

### MtMeterVendorController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /meterVendor/pageList | GET | /thermal/meter/vendor/list | GET | MATCH | 分页查询仪表厂商列表 |
| /meterVendor/verifyName | GET | /thermal/meter/vendor/verifyName | GET | MATCH | 校验厂商名称是否重复 |
| /meterVendor/insertData | POST | /thermal/meter/vendor | POST | MATCH | 新增仪表厂商 |
| /meterVendor/updateData | POST | /thermal/meter/vendor | PUT | MATCH | 修改仪表厂商 |
| /meterVendor/deleteData | POST | /thermal/meter/vendor/{id} | DELETE | MATCH | 删除仪表厂商 |
| /meterVendor/allVendor | GET | /thermal/meter/vendor/all | GET | MATCH | 查询所有启用的仪表厂商 |

### MtMeterSortController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /meterSort/pageList | GET | /thermal/meter/sort/pageList | GET | MATCH | 分页查询仪表分类 |
| /meterSort/verifyName | GET | /thermal/meter/sort/verifyName | POST | PARTIAL | 校验名称是否重复（HTTP 方法变化） |
| /meterSort/insertData | POST | /thermal/meter/sort | POST | MATCH | 新增仪表分类 |
| /meterSort/updateData | POST | /thermal/meter/sort | PUT | MATCH | 修改仪表分类 |
| /meterSort/deleteData | POST | /thermal/meter/sort/{id} | DELETE | MATCH | 删除仪表分类 |
| /meterSort/queryMeterSort | GET | /thermal/meter/sort/queryMeterSort | GET | MATCH | 条件查询仪表分类 |

### MtHeatArchiveController

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /heat/pageList | GET | /thermal/meter/heat/pageList | GET | MATCH | 分页查询热力表档案列表 |
| /heat/insertData | POST | /thermal/meter/heat | POST | MATCH | 新增热力表档案 |
| /heat/updateData | POST | /thermal/meter/heat | PUT | MATCH | 修改热力表档案 |
| /heat/deleteData | POST | /thermal/meter/heat/{id} | DELETE | PARTIAL | 删除热力表档案（旧系统有级联更新逻辑，新系统未实现） |
| /heat/getHeatList | GET | /thermal/meter/heat/list | GET | MATCH | 获取所有热力表列表 |
| /heat/queryMtHeatArchive | POST | /thermal/meter/heat/query | POST | MATCH | 按条件查询热力表 |

## 重要发现

### 1. 缺失的 Controller

- **HtScopeDtuController**: 旧系统仅有空 Controller，新系统未创建
- **HtTasksPerformLsController**: 旧系统仅有空 Controller，新系统未创建

### 2. 缺失的端点

#### Ht 热力调控
1. `/property/houseStrategy/queryPrHouse` — 查询房屋信息
2. `/property/htTasks/listTasks` — 查询任务列表（功能重复）
3. `/property/htTasks/pageList` — 分页列表（参数复杂）
4. `/htRepair/pageListForRepair` — 报修员视角的分页列表
5. `/htRepair/queryAbnormalAlarmList` — 查询异常报警列表（功能重复）

#### Mt 仪表设备
1. 删除档案时返回状态码的逻辑（旧系统返回 1/0/-1，新系统仅返回 boolean）

### 3. HTTP 方法变化

以下端点从 POST 变为 GET/PUT，可能影响前端调用：
- `/mtFormulaFile/startUsing` → PUT `/thermal/meter/formula/enable/{id}`
- `/mtFormulaFile/endUsing` → PUT `/thermal/meter/formula/disable/{id}`
- `/meterSort/verifyName` → POST `/thermal/meter/sort/verifyName`
- `/tc/queryMtTcArchive` → GET `/thermal/meter/tc/query`
- `/valve/queryMtTcValve` → GET `/thermal/meter/valve/query`

### 4. 新增端点

#### HtTasksPerformController（新系统扩展）
- `PUT /thermal/ht/tasksPerform/status` — 更新指令发送状态
- `GET /thermal/ht/tasksPerform/pending` — 查询待发送指令
- `GET /thermal/ht/tasksPerform/stats` — 查询执行统计
- `GET /thermal/ht/tasksPerform/statistics/{taskId}` — 按任务ID统计
- `GET /thermal/ht/tasksPerform/statusSummary` — 全局状态汇总

### 5. 命名修正

旧系统 `MtWaterArchiveControlller`（拼写错误，双 l）在新系统中修正为 `MtWaterArchiveController`

## 建议

1. **高优先级**: 补充缺失的查询端点，特别是 `/property/houseStrategy/queryPrHouse`
2. **中优先级**: 确认 HTTP 方法变化是否影响前端调用
3. **低优先级**: 评估空 Controller 是否需要保留
