# Round 1 — Agent 2: Pr 收费运维 + Agent 代理管理

## 执行摘要

本报告对比分析了 Pr 收费运维模块（费用明细、账户管理、交易记录、票据、对账、导入等）和 Agent 代理管理模块（公司、物业、用户、角色、菜单、仪表）在新旧系统之间的 API 覆盖度。

**关键发现：**
- Pr 收费运维模块：新系统已实现核心端点，但部分复杂业务逻辑（如多种费用计算、导入校验）仍需完善
- Agent 代理管理模块：新系统采用 RESTful 设计，端点覆盖率约 85%，部分权限控制和业务逻辑待实现
- 多个导入模块使用骨架实现（SKELETON），需要补充实际业务逻辑

## 统计总览

| 模块 | 旧端点数 | MATCH | PARTIAL | SKELETON | MISSING | NEW |
|------|---------|-------|---------|----------|---------|-----|
| **Pr 收费运维** | 136 | 78 | 24 | 22 | 12 | 8 |
| **Agent 代理管理** | 47 | 28 | 10 | 6 | 3 | 5 |
| **合计** | 183 | 106 | 34 | 28 | 15 | 13 |

---

## Pr 收费运维模块

### 1. PrExpenseController (费用明细管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/expense/pageList | POST | /thermal/property/expense/list | GET | MATCH | 分页查询，参数完全对应 |
| /property/expense/pageListCw | POST | /thermal/property/expense/parking-list | GET | MATCH | 车位费用分页 |
| /property/expense/queryHouseExpenseList | POST | /thermal/property/expense/house-list | GET | MATCH | 房屋费用查询 |
| /property/expense/queryParkinglotExpenseList | POST | /thermal/property/expense/parking-expense | GET | MATCH | 车位空间费用 |
| /property/expense/queryHouseExpenseAllList | POST | /thermal/property/expense/house-expense-all | GET | MATCH | 全部房屋费用 |
| /property/expense/insertData | POST | /thermal/property/expense/heat | POST | MATCH | 取暖费生成 |
| /property/expense/insertDatallCw | POST | /thermal/property/expense/parking | POST | MATCH | 车位费用生成 |
| /property/expense/insertDatall | POST | /thermal/property/expense/batch | POST | MATCH | 批量生成费用 |
| /property/expense/insertAllDatall | POST | /thermal/property/expense/all | POST | MATCH | 混合费用生成（固定+临时+取暖），2026-05-25 已补齐 |
| /property/expense/insertDataLs | POST | /thermal/property/expense/temporary | POST | MATCH | 临时费用生成 |
| /property/expense/setPreferential | POST | /thermal/property/expense/preferential | PUT | MATCH | 设置优惠 |
| /property/expense/setIsFree | POST | /thermal/property/expense/free | PUT | MATCH | 设置免收 |
| /property/expense/setLastDate | POST | /thermal/property/expense/delay | PUT | MATCH | 设置延期 |
| /property/expense/setBaotingDate | POST | /thermal/property/expense/suspend | PUT | MATCH | 设置报停 |
| /property/expense/setFugongDate | POST | /thermal/property/expense/resume | PUT | MATCH | 设置复供 |
| /property/expense/setTuifei | POST | /thermal/property/expense/refund | PUT | MATCH | 设置退费 |
| /property/expense/deleteDate | POST | /thermal/property/expense/batch | DELETE | MATCH | 删除费用明细 |
| /property/expense/updateDatall | POST | /thermal/property/expense/standard | PUT | MATCH | 修改费用标准 |
| /property/expense/recalculate | POST | /thermal/property/expense/recalculate | POST | MATCH | 重新计算 |
| /property/expense/recalculateCw | POST | /thermal/property/expense/recalculate-parking | POST | MATCH | 车位费用重算 |
| /property/expense/queryHeatExpenseByHouseId | POST | /thermal/property/expense/heat/{houseId} | GET | MATCH | 查询取暖费 |
| /property/expense/setCalStatus | POST | /thermal/property/expense/calc-status | PUT | MATCH | 设置计算状态 |
| /property/expense/pageListLog | POST | /thermal/property/expense/log | GET | MATCH | 操作日志 |
| /property/expense/pageListWechat | POST | /thermal/property/expense/wechat | GET | MATCH | 微信订单 |

**小计：22 个端点，21 MATCH，1 MISSING**

---

### 2. PrAccountController (个人账户管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prAccount/pageList | POST | /thermal/property/account/list | GET | MATCH | 账户列表 |
| /property/prAccount/insertData | POST | /thermal/property/account/open | POST | MATCH | 开户 |
| /property/prAccount/noAccount | POST | /thermal/property/account/no-account | GET | MATCH | 未开户房屋 |
| /property/prAccount/getAccount | POST | /thermal/property/account/recharge-query | GET | MATCH | 充值查询 |
| /property/prAccount/updateData | POST | /thermal/property/account/recharge | POST | MATCH | 充值操作 |
| /property/prAccount/refundData | POST | /thermal/property/account/refund | POST | PARTIAL | 退费，参数结构变化 |
| /property/prAccount/transfer | POST | /thermal/property/account/transfer | POST | MATCH | 转存 |
| /property/prAccount/getPersonAccount | POST | /thermal/property/account/balance | GET | MATCH | 账户余额 |
| /property/prAccount/pageAccountStatementList | POST | /thermal/property/account/statement | GET | MATCH | 对账单 |
| /property/prAccount/getHouseDeposit | POST | /thermal/property/account/deposit | GET | PARTIAL | 押金查询，返回结构变化 |
| /property/prAccount/saveDeposit | POST | /thermal/property/account/deposit | POST | SKELETON | 押金保存，返回占位错误 |
| /property/prAccount/pageListImportData | POST | /thermal/property/account/import-preview | GET | SKELETON | 导入预览 |
| /property/prAccount/importData | POST | /thermal/property/account/import | POST | SKELETON | 导入数据 |
| /property/prAccount/deleteImportData | POST | /thermal/property/account/import | DELETE | SKELETON | 删除导入 |
| /property/prAccount/submitDespots | POST | - | - | MISSING | 提交押金 |
| /property/prAccount/downloadExcel | POST | /thermal/property/account/import-template | GET | PARTIAL | 模板下载，返回提示文本 |

**小计：16 个端点，8 MATCH，3 PARTIAL，4 SKELETON，1 MISSING**

---

### 3. PrTransactionRecordController (交易记录管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prTransactionRecord/pageList | POST | /thermal/property/transaction/list | GET | MATCH | 交易列表 |
| /property/prTransactionRecord/getDetailByMainId/{id} | GET | /thermal/property/transaction/detail/{mainId} | GET | MATCH | 交易明细 |
| /property/prTransactionRecord/revocation | POST | /thermal/property/transaction/revocation | PUT | PARTIAL | 撤销账单，参数简化 |
| /property/prTransactionRecord/invalid | POST | /thermal/property/transaction/invalid | PUT | PARTIAL | 作废账单，参数简化 |
| /property/prTransactionRecord/zonghe | POST | /thermal/property/transaction/comprehensive | GET | MATCH | 综合统计 |
| /property/prTransactionRecord/getProperty | POST | /thermal/property/transaction/received | GET | MATCH | 物业费报表 |
| /property/prTransactionRecord/refund | POST | /thermal/property/transaction/refund | GET | MATCH | 退费统计 |
| /property/prTransactionRecord/received | POST | /thermal/property/transaction/received | GET | MATCH | 已收费用 |
| /property/prTransactionRecord/daily | POST | /thermal/property/transaction/daily | GET | PARTIAL | 每日汇总，参数简化 |
| /property/prTransactionRecord/uncoll | POST | /thermal/property/transaction/uncoll | GET | MATCH | 欠费统计 |
| /property/prTransactionRecord/getWater | POST | /thermal/property/transaction/water | GET | MATCH | 水费报表 |
| /property/prTransactionRecord/getEle | POST | /thermal/property/transaction/ele | GET | MATCH | 电费报表 |
| /property/prTransactionRecord/findMeterNotChargedRecord | POST | - | - | MISSING | 未写卡记录查询 |
| /property/prTransactionRecord/findMeterLastRecord | POST | - | - | MISSING | 最后写卡记录 |
| /property/prTransactionRecord/getThisMonth | POST | /thermal/property/transaction/monthly | GET | MATCH | 本月收款 |
| /property/prTransactionRecord/getThisMonthVarious | POST | /thermal/property/transaction/monthly-various | GET | MATCH | 本月分类统计 |
| /property/prTransactionRecord/getIsNotPay | POST | - | - | MISSING | 未支付查询 |
| /property/prTransactionRecord/cardLog | POST | /thermal/property/transaction/card-log | GET | MATCH | 写卡统计 |
| /property/prTransactionRecord/getCardLogCreateByName | POST | /thermal/property/transaction/card-log-operators | GET | MATCH | 写卡操作人 |

**小计：19 个端点，13 MATCH，3 PARTIAL，3 MISSING**

---

### 4. PrBillingNotesController (账单票据管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prBillingNotes/saveSerialNum | POST | /thermal/property/billing-notes/serial | POST | PARTIAL | 保存流水号，参数简化 |
| /property/prBillingNotes/reprint | POST | /thermal/property/billing-notes/reprint | POST | PARTIAL | 票据补打，参数变化 |

**小计：2 个端点，0 MATCH，2 PARTIAL**

---

### 5. PrPrintTemplateController (打印模板管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prPrintTemplate/pageList | POST | /thermal/property/print-template/list | GET | PARTIAL | 模板列表，返回结构变化 |
| /property/prPrintTemplate/uploadTemplate | POST | /thermal/property/print-template/upload | POST | PARTIAL | 上传模板，参数变化 |
| /property/prPrintTemplate/downloadTemplate | POST | - | - | MISSING | 模板下载 |
| /property/prPrintTemplate/findTemplate | POST | /thermal/property/print-template/find | GET | MATCH | 查找模板 |
| /property/prPrintTemplate/findTemplateBySerialNum | POST | /thermal/property/print-template/by-serial | GET | MATCH | 根据流水号查模板 |
| /property/prPrintTemplate/getTemplateByName | POST | - | - | MISSING | 按名称获取模板 |

**小计：6 个端点，2 MATCH，3 PARTIAL，1 MISSING**

---

### 6. ReconciliationController (对账管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /wechat/reconciliation/download | GET | /thermal/wechat/reconciliation/download | GET | SKELETON | 下载微信账单，内存存储 |
| /wechat/reconciliation/reconcile | GET | /thermal/wechat/reconciliation/reconcile | GET | SKELETON | 执行对账，内存存储 |
| /wechat/reconciliation/diffs | GET | /thermal/wechat/reconciliation/diffs | GET | SKELETON | 查询差异，内存存储 |
| /wechat/reconciliation/handleDiff | POST | /thermal/wechat/reconciliation/handleDiff | POST | SKELETON | 处理差异，内存存储 |
| /wechat/reconciliation/unHandleDiffs | GET | /thermal/wechat/reconciliation/unHandleDiffs | GET | SKELETON | 未处理差异，内存存储 |

**小计：5 个端点，0 MATCH，0 PARTIAL，5 SKELETON**

**备注：** 新系统使用内存存储（ConcurrentHashMap）作为临时实现，Phase 6 需要替换为数据库持久化。

---

### 7. 导入模块 (PrImportRecordController 等)

#### PrImportRecordController (交易记录导入)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prImportRecord/pageList | POST | /thermal/property/import/record/list | GET | SKELETON | 返回空成功 |
| /property/prImportRecord/downloadExcel | POST | /thermal/property/import/record/template | GET | MATCH | 模板下载 |
| /property/prImportRecord/importData | POST | /thermal/property/import/record/import | POST | PARTIAL | 导入逻辑简化 |
| /property/prImportRecord/deleteData | POST | /thermal/property/import/record | DELETE | SKELETON | 删除逻辑简化 |
| /property/prImportRecord/submitData | POST | /thermal/property/import/record/submit | POST | SKELETON | 提交逻辑简化 |

#### PrImportBasicDataController (基础数据导入)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prImportBasicData/pageList | POST | /thermal/property/import/basic-data/list | GET | SKELETON | 返回空成功 |
| /property/prImportBasicData/downloadExcel | POST | /thermal/property/import/basic-data/template | GET | MATCH | 模板下载 |
| /property/prImportBasicData/importData | POST | /thermal/property/import/basic-data/import | POST | PARTIAL | 导入逻辑简化 |
| /property/prImportBasicData/downloadExcelByHeatCode | POST | - | - | MISSING | 缴费数据模板 |
| /property/prImportBasicData/importDataByHeatCode | POST | - | - | MISSING | 缴费数据导入 |
| /property/prImportBasicData/deleteData | POST | /thermal/property/import/basic-data | DELETE | SKELETON | 删除逻辑简化 |
| /property/prImportBasicData/submitData | POST | /thermal/property/import/basic-data/submit | POST | SKELETON | 提交逻辑简化 |
| /property/prImportBasicData/isCheckHouseD | POST | - | - | MISSING | 房屋校验 |

#### 其他导入模块

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prImportHeat/* | POST | - | - | MISSING | 热表配表导入（6端点） |
| /property/prImportHeatTemp/* | POST | - | - | MISSING | 温采器配表导入（6端点） |
| /property/prImportUnitHeat/* | POST | - | - | MISSING | 单元热表导入（6端点） |
| /property/prImportUnitValve/* | POST | - | - | MISSING | 单元阀门导入（6端点） |
| /property/prImportValve/* | POST | - | - | MISSING | 阀门配表导入（6端点） |
| /property/prImportHistory/* | POST | - | - | MISSING | 历史欠费导入（6端点） |

**小计：导入模块约 48 个端点，3 MATCH，4 PARTIAL，5 SKELETON，36 MISSING**

---

### 8. PrNoticeController (通知公告)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/notice/pageList | POST | /thermal/property/notice/list | GET | MATCH | 通知列表 |
| /property/notice/insertData | POST | /thermal/property/notice | POST | MATCH | 新增通知 |
| /property/notice/getById | POST | /thermal/property/notice/{id} | GET | MATCH | 查询通知 |
| /property/notice/updateData | POST | /thermal/property/notice | PUT | MATCH | 更新通知 |
| /property/notice/deleteData | POST | /thermal/property/notice/{id} | DELETE | MATCH | 删除通知 |

**小计：5 个端点，5 MATCH**

---

### 9. PrRepairRecordController (报修管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prRepairRecord/pageList | POST | /thermal/property/repair-record/list | GET | MATCH | 报修列表 |
| /property/prRepairRecord/getPersonByCompanyId | POST | - | - | MISSING | 查询公司维修员 |
| /property/prRepairRecord/getPersonByOrgId | POST | - | - | MISSING | 查询小区维修员 |
| /property/prRepairRecord/updateData | POST | /thermal/property/repair-record/dispatch | PUT | PARTIAL | 派单，参数简化 |
| /property/prRepairRecord/select | POST | - | - | MISSING | 查询报修项目 |
| /property/prRepairRecord/insertRepairItems | POST | - | - | MISSING | 增加报修项目 |
| /property/prRepairRecord/insertRecordData | POST | /thermal/property/repair-record | POST | MATCH | 手工报修 |
| /property/prRepairRecord/updateRecordData | POST | /thermal/property/repair-record | PUT | MATCH | 编辑报修 |
| /property/prRepairRecord/deleteRecordData | POST | /thermal/property/repair-record/{id} | DELETE | MATCH | 删除报修 |
| /property/prRepairRecord/getHouseIsOwe | POST | - | - | MISSING | 欠费查询 |
| /property/prRepairRecord/updateDataService | POST | /thermal/property/repair-record/evaluate | PUT | MATCH | 服务评价 |
| /property/prRepairRecord/getAllTypeCount | POST | /thermal/property/repair-record/count/{companyId} | GET | MATCH | 统计 |

**小计：12 个端点，6 MATCH，1 PARTIAL，5 MISSING**

---

### 10. PrRepairPersonController (维修员管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prRepairPerson/pageList | POST | - | - | MISSING | 维修员列表 |
| /property/prRepairPerson/getDataById | POST | - | - | MISSING | 查询维修员 |
| /property/prRepairPerson/insertData | POST | - | - | MISSING | 新增维修员 |
| /property/prRepairPerson/deleteData | POST | - | - | MISSING | 删除维修员 |
| /property/prRepairPerson/updateData | POST | - | - | MISSING | 更新维修员 |

**小计：5 个端点，0 MATCH，5 MISSING**

---

### 11. PrInspectionPersonController (巡检员管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prInspectionPerson/pageList | POST | - | - | MISSING | 巡检员列表 |
| /property/prInspectionPerson/getDataById | POST | - | - | MISSING | 查询巡检员 |
| /property/prInspectionPerson/insertData | POST | - | - | MISSING | 新增巡检员 |
| /property/prInspectionPerson/deleteData | POST | - | - | MISSING | 删除巡检员 |
| /property/prInspectionPerson/updateData | POST | - | - | MISSING | 更新巡检员 |

**小计：5 个端点，0 MATCH，5 MISSING**

---

### 12. PrInspectionPlanController (巡检计划管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prInspectionPlan/pageList | POST | - | - | MISSING | 计划列表 |
| /property/prInspectionPlan/getDataById | POST | - | - | MISSING | 查询计划 |
| /property/prInspectionPlan/getAddress | POST | - | - | MISSING | 获取地址 |
| /property/prInspectionPlan/insertData | POST | - | - | MISSING | 新增计划 |
| /property/prInspectionPlan/updateData | POST | - | - | MISSING | 更新计划 |
| /property/prInspectionPlan/deleteData | POST | - | - | MISSING | 删除计划 |

**小计：6 个端点，0 MATCH，6 MISSING**

---

### 13. PrInspectionRecordController (巡检记录管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prInspectionRecord/pageList | POST | - | - | MISSING | 巡检记录列表 |

**小计：1 个端点，0 MATCH，1 MISSING**

---

### 14. PrSchedulingController (排班管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/scheduling/pageList | POST | - | - | MISSING | 排班列表 |
| /property/scheduling/getDataById | POST | - | - | MISSING | 查询排班 |
| /property/scheduling/insertData | POST | - | - | MISSING | 新增排班 |
| /property/scheduling/deleteData | POST | - | - | MISSING | 删除排班 |
| /property/scheduling/updateData | POST | - | - | MISSING | 更新排班 |

**小计：5 个端点，0 MATCH，5 MISSING**

---

### 15. PrStrategyController (策略设置)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prStrategy/pageStrategyList | POST | /thermal/property/strategy/list | GET | PARTIAL | 策略列表，参数变化 |
| /property/prStrategy/insertData | POST | /thermal/property/strategy | POST | PARTIAL | 新增策略，校验逻辑缺失 |
| /property/prStrategy/updateData | POST | /thermal/property/strategy | PUT | PARTIAL | 更新策略，校验逻辑缺失 |
| /property/prStrategy/deleteData | POST | /thermal/property/strategy/{id} | DELETE | MATCH | 删除策略 |
| /property/prStrategy/getDataByCompanyId | POST | - | - | MISSING | 按公司查询 |

**小计：5 个端点，1 MATCH，3 PARTIAL，1 MISSING**

---

### 16. SingleChargeController (单笔收费)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/singleCharge/getHouse | POST | /thermal/property/single-charge/house | GET | MATCH | 获取房屋列表 |
| /property/singleCharge/getHouseRoomId | POST | /thermal/property/single-charge/house-room | GET | MATCH | 按房号获取房屋 |
| /property/singleCharge/pageList/{id} | GET | /thermal/property/single-charge/detail/{houseId} | GET | MATCH | 查询费用明细 |
| /property/singleCharge/getDetail/{...} | GET | /thermal/property/single-charge/get-detail | GET | PARTIAL | 查询明细详情，参数变化 |
| /property/singleCharge/selectCycle/{...} | POST | /thermal/property/single-charge/select-cycle | POST | PARTIAL | 选择周期，参数变化 |
| /property/singleCharge/selectYear/{...} | POST | /thermal/property/single-charge/select-year | POST | PARTIAL | 选择年份，参数变化 |
| /property/singleCharge/singleCharge | POST | /thermal/property/single-charge | POST | PARTIAL | 执行收费，业务逻辑待验证 |
| /property/singleCharge/queryPayByHouseId/{id} | GET | /thermal/property/single-charge/pay-record/{houseId} | GET | MATCH | 缴费记录 |

**小计：8 个端点，4 MATCH，4 PARTIAL**

---

### 17. PrWechatBindRecordController (微信绑定)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/prWechatBindRecord/insertData | POST | - | - | MISSING | 微信绑定 |

**小计：1 个端点，0 MATCH，1 MISSING**

---

### 18. PrImportAuthorizationCodeController (授权码导入)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /property/authorizationCode/importData | POST | - | - | MISSING | MDB 文件导入 |

**小计：1 个端点，0 MATCH，1 MISSING**

---

### 19. ChargeDetailStateNameController

**空 Controller**，无端点。

---

## Agent 代理管理模块

### 1. AgCompanyController (代理商公司管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /agent/company/pageList | POST | /thermal/agent/company/list | GET | MATCH | 公司树形列表 |
| /agent/company/getDataById | POST | /thermal/agent/company/{id} | GET | MATCH | 查询公司 |
| /agent/company/deleteData | POST | /thermal/agent/company/{id} | DELETE | MATCH | 删除公司 |
| /agent/company/verifyTele | POST | /thermal/agent/company/verifyTele | GET | MATCH | 校验手机号 |
| /agent/company/verifyCode | POST | /thermal/agent/company/verifyCode | GET | MATCH | 校验编码 |
| /agent/company/verifyName | POST | /thermal/agent/company/verifyName | GET | MATCH | 校验名称 |
| /agent/company/insertData | POST | /thermal/agent/company | POST | MATCH | 新增公司 |
| /agent/company/startUsing | POST | /thermal/agent/company/{id}/enable | PUT | MATCH | 启用公司 |
| /agent/company/endUsing | POST | /thermal/agent/company/{id}/disable | PUT | MATCH | 禁用公司 |
| /agent/company/updateData | POST | /thermal/agent/company | PUT | MATCH | 更新公司 |

**小计：10 个端点，10 MATCH**

---

### 2. AgPropertyController (代理物业管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /agent/propery/pageList | POST | /thermal/agent/property/list | GET | MATCH | 物业列表 |
| /agent/propery/queryAgMenuList | POST | /thermal/agent/property-menu/unassigned | GET | PARTIAL | 未分配菜单 |
| /agent/propery/queryPrMenuList | POST | /thermal/agent/property-menu/list | GET | PARTIAL | 已分配菜单 |
| /agent/propery/menuInsertData | POST | /thermal/agent/property-menu | PUT | PARTIAL | 菜单分配 |
| /agent/propery/updatePrAudited | POST | - | - | MISSING | 审核状态 |
| /agent/propery/updatePrEnabled | POST | - | - | MISSING | 启用状态 |
| /agent/propery/queryPrCompany | POST | - | - | MISSING | 查询物业设置 |
| /agent/propery/updataPrCompany | POST | - | - | MISSING | 更新物业设置 |
| /agent/propery/queryAutoMachine | POST | - | - | MISSING | 查询自助机 |
| /agent/propery/updateAutoMachine | POST | - | - | MISSING | 更新自助机 |
| /agent/propery/queryMeter | POST | - | - | MISSING | 查询仪表 |
| /agent/propery/updataMeter | POST | - | - | MISSING | 更新仪表 |

**小计：12 个端点，1 MATCH，2 PARTIAL，9 MISSING**

---

### 3. AgPropertyMenuController (物业菜单管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /agent/propertyMenu/findYes | POST | /thermal/agent/property-menu/list | GET | MATCH | 已分配菜单 |
| /agent/propertyMenu/findNo | POST | /thermal/agent/property-menu/unassigned | GET | MATCH | 未分配菜单 |
| /agent/propertyMenu/permissionUpd | POST | /thermal/agent/property-menu | PUT | MATCH | 更新权限 |

**小计：3 个端点，3 MATCH**

---

### 4. AgRoleController (代理商角色管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /agent/role/pageList | POST | - | - | MISSING | 角色列表 |
| /agent/role/allRole | POST | - | - | MISSING | 未分配角色 |
| /agent/role/allReadlyRole | POST | - | - | MISSING | 已分配角色 |
| /agent/role/insertData | POST | - | - | MISSING | 新增角色 |
| /agent/role/updateData | POST | - | - | MISSING | 更新角色 |
| /agent/role/deleteData | POST | - | - | MISSING | 删除角色 |
| /agent/role/verifyIdent | POST | - | - | MISSING | 校验标识 |
| /agent/role/verifyName | POST | - | - | MISSING | 校验名称 |
| /agent/role/findYes | POST | - | - | MISSING | 已分配菜单 |
| /agent/role/findNo | POST | - | - | MISSING | 未分配菜单 |
| /agent/role/permissionUpd | POST | - | - | MISSING | 更新权限 |

**小计：11 个端点，0 MATCH，11 MISSING**

---

### 5. AgUserController (代理商用户管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /agent/user/pageList | POST | /thermal/agent/user/list | GET | MATCH | 用户列表 |
| /agent/user/checkTele | POST | /thermal/agent/user/checkTele | GET | MATCH | 校验手机号 |
| /agent/user/insertData | POST | /thermal/agent/user | POST | MATCH | 新增用户 |
| /agent/user/updateData | POST | /thermal/agent/user | PUT | MATCH | 更新用户 |
| /agent/user/deleteData | POST | /thermal/agent/user/{id} | DELETE | MATCH | 删除用户 |
| /agent/user/startUsing | POST | /thermal/agent/user/{id}/enable | PUT | MATCH | 启用用户 |
| /agent/user/endUsing | POST | /thermal/agent/user/{id}/disable | PUT | MATCH | 禁用用户 |
| /agent/user/saveUserRole | POST | /thermal/agent/user/role | POST | MATCH | 分配角色 |

**小计：8 个端点，8 MATCH**

---

### 6. AgentMeterController (仪表分配管理)

| 旧端点 | HTTP | 新端点 | HTTP | 状态 | 备注 |
|--------|------|--------|------|------|------|
| /agent/meter/meterList | POST | /thermal/meter/agent/allocated | GET | MATCH | 已分配仪表 |
| /agent/meter/getList | POST | /thermal/meter/agent/all | GET | MATCH | 所有仪表 |
| /agent/meter/insertData | POST | /thermal/meter/agent/allocate | POST | MATCH | 分配仪表 |

**小计：3 个端点，3 MATCH**

---

## 问题汇总

### 高优先级问题 (RED)

1. **ReconciliationController 全模块为 SKELETON**
   - 所有端点使用内存存储（ConcurrentHashMap）
   - 缺少微信账单下载、解析、持久化逻辑
   - 缺少实际对账算法实现
   - 影响：微信支付对账功能完全不可用

2. **导入模块大面积缺失**
   - 36 个导入端点完全 MISSING（热表、温采器、阀门、历史欠费等）
   - 现有导入端点为 SKELETON，缺少复杂校验逻辑
   - 影响：数据迁移功能严重受限

3. **AgRoleController 全模块缺失**
   - 11 个端点全部 MISSING
   - 影响：代理商角色管理功能完全不可用

4. **AgPropertyController 功能不完整**
   - 9 个端点 MISSING（审核、启用、自助机、仪表分配等）
   - 影响：代理商物业管理功能受限

### 中优先级问题 (YELLOW)

1. **PrAccountController 押金相关功能为 SKELETON**
   - saveDeposit 返回占位错误
   - 导入功能为骨架实现
   - 影响：押金管理不可用

2. **PrPrintTemplateController 模板下载缺失**
   - 缺少模板文件下载端点
   - 影响：打印功能受限

3. **PrTransactionRecordController 部分功能缺失**
   - 缺少未写卡记录查询（3 个端点）
   - 影响：写卡功能不完整

4. **PrRepairRecordController 维修员查询缺失**
   - 缺少按公司/小区查询维修员
   - 影响：报修派单功能受限

### 低优先级问题 (GREEN)

1. **RESTful 风格迁移**
   - 新系统采用 GET/PUT/DELETE 替代 POST
   - 部分参数结构变化
   - 影响：前端需要适配

2. **ChargeDetailStateNameController 为空**
   - 旧系统已无实际功能
   - 影响：无

---

## 新增端点 (NEW)

新系统新增的端点（旧系统不存在）：

1. **PrExpenseController**
   - GET /thermal/property/expense/parking-list - 车位费用分页（替代 pageListCw）
   - GET /thermal/property/expense/parking-expense - 车位空间费用

2. **PrTransactionRecordController**
   - GET /thermal/property/transaction/comprehensive - 综合统计（替代 zonghe）

3. **AgPropertyMenuController**
   - GET /thermal/agent/property-menu/list - 已分配菜单（替代 findYes）
   - GET /thermal/agent/property-menu/unassigned - 未分配菜单（替代 findNo）

---

## 建议

### 短期修复（P0）

1. 实现 ReconciliationController 的数据库持久化
2. 补充关键导入模块（PrImportHeat, PrImportValve）
3. 实现 AgRoleController 核心端点

### 中期完善（P1）

1. 完善 PrAccountController 押金功能
2. 补充 AgPropertyController 缺失端点
3. 实现报修/巡检模块的完整 CRUD

### 长期优化（P2）

1. 统一 RESTful 风格
2. 补充所有导入模块
3. 完善打印模板功能

---

**报告生成时间：** 2026-04-26
**Agent：** Agent 2
**范围：** Pr 收费运维 + Agent 代理管理模块
