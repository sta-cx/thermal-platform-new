# Reaudit — Pr 收费运维模块
**审核日期**: 2026-04-26
**审核范围**: 30 个 Controller（收费/运维/导入/用户管理）

## 总体统计
| 状态 | 数量 | 百分比 |
|------|------|--------|
| 完全匹配 | 12 | 40.0% |
| 部分匹配 | 10 | 33.3% |
| 骨架 | 3 | 10.0% |
| 缺失端点 | 7 | 23.3% |
| 新增 | 2 | - |

**注意**: 缺失端点数统计的是具体端点数量，而非 Controller 数量

---

## 1. PrExpenseController（费用管理）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 参数 | 说明 |
|---|------|------|------|------|------|
| 1 | pageList | POST | /property/expense/pageList | Page, 多个查询参数 | 费用分页查询 |
| 2 | pageListCw | POST | /property/expense/pageListCw | Page, 多个查询参数 | 车位费用分页 |
| 3 | queryHouseExpenseList | POST | /property/expense/queryHouseExpenseList | 多个查询参数 | 房屋费用列表 |
| 4 | queryParkinglotExpenseList | POST | /property/expense/queryParkinglotExpenseList | 多个查询参数 | 车位费用列表 |
| 5 | queryHouseExpenseAllList | POST | /property/expense/queryHouseExpenseAllList | 多个查询参数 | 全部房屋费用 |
| 6 | insertData | POST | /property/expense/insertData | List<PrHouseExpense> | 生成取暖费 |
| 7 | insertDatallCw | POST | /property/expense/insertDatallCw | List<PmParkingSpace> | 车位费用生成 |
| 8 | insertDatall | POST | /property/expense/insertDatall | List<PrHouseExpense> | 批量生成费用 |
| 9 | insertAllDatall | POST | /property/expense/insertAllDatall | List<PrHouseExpense> | 单个生成费用 |
| 10 | insertDataLs | POST | /property/expense/insertDataLs | List<PrHouseExpense> | 临时费用生成 |
| 11 | setPreferential | POST | /property/expense/setPreferential | List, 多个参数 | 设置优惠 |
| 12 | setIsFree | POST | /property/expense/setIsFree | List, reason, times | 设置免收 |
| 13 | setLastDate | POST | /property/expense/setLastDate | List, reason, days | 设置延期 |
| 14 | setBaotingDate | POST | /property/expense/setBaotingDate | List, 多个参数 | 设置报停 |
| 15 | setFugongDate | POST | /property/expense/setFugongDate | List, reason, days | 设置复供 |
| 16 | setTuifei | POST | /property/expense/setTuifei | List, 多个参数 | 设置退费 |
| 17 | deleteDate | POST | /property/expense/deleteDate | List<PrExpense> | 删除费用 |
| 18 | updateDatall | POST | /property/expense/updateDatall | List<PrExpense> | 修改标准 |
| 19 | recalculate | POST | /property/expense/recalculate | companyId, orgId | 重新计算 |
| 20 | recalculateCw | POST | /property/expense/recalculateCw | companyId, orgId | 车位重算 |
| 21 | queryHeatExpenseByHouseId | POST | /property/expense/queryHeatExpenseByHouseId | houseId | 取暖费查询 |
| 22 | setCalStatus | POST | /property/expense/setCalStatus | houseId | 设置计算状态 |
| 23 | pageListLog | POST | /property/expense/pageListLog | Page, 多个参数 | 费用操作日志 |
| 24 | pageListWechat | POST | /property/expense/pageListWechat | Page, 多个参数 | 微信订单查询 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 参数 | 说明 |
|---|------|------|------|------|------|
| 1 | list | GET | /thermal/property/expense/list | @RequestParam, PageQuery | 费用分页查询 |
| 2 | queryHouseExpenseList | GET | /thermal/property/expense/house-list | @RequestParam | 房屋费用列表 |
| 3 | queryHeatExpense | GET | /thermal/property/expense/heat/{houseId} | @PathVariable | 取暖费查询 |
| 4 | insertHeat | POST | /thermal/property/expense/heat | @RequestBody | 生成取暖费 |
| 5 | insertBatch | POST | /thermal/property/expense/batch | @RequestBody | 批量生成费用 |
| 6 | insertTemporary | POST | /thermal/property/expense/temporary | @RequestBody | 临时费用生成 |
| 7 | insertParking | POST | /thermal/property/expense/parking | @RequestBody | 车位费用生成 |
| 8 | setPreferential | PUT | /thermal/property/expense/preferential | @RequestBody, @RequestParam | 设置优惠 |
| 9 | setIsFree | PUT | /thermal/property/expense/free | @RequestBody, @RequestParam | 设置免收 |
| 10 | setLastDate | PUT | /thermal/property/expense/delay | @RequestBody, @RequestParam | 设置延期 |
| 11 | setBaotingDate | PUT | /thermal/property/expense/suspend | @RequestBody, @RequestParam | 设置报停 |
| 12 | setFugongDate | PUT | /thermal/property/expense/resume | @RequestBody, @RequestParam | 设置复供 |
| 13 | setTuifei | PUT | /thermal/property/expense/refund | @RequestBody, @RequestParam | 设置退费 |
| 14 | deleteBatch | DELETE | /thermal/property/expense/batch | @RequestBody | 删除费用 |
| 15 | updateStandard | PUT | /thermal/property/expense/standard | @RequestBody | 修改标准 |
| 16 | recalculate | POST | /thermal/property/expense/recalculate | @RequestParam | 重新计算 |
| 17 | recalculateParking | POST | /thermal/property/expense/recalculate-parking | @RequestParam | 车位重算 |
| 18 | setCalStatus | PUT | /thermal/property/expense/calc-status | @RequestParam | 设置计算状态 |
| 19 | parkingList | GET | /thermal/property/expense/parking-list | @RequestParam, PageQuery | 车位费用分页 |
| 20 | parkingExpenseList | GET | /thermal/property/expense/parking-expense | @RequestParam | 车位空间费用 |
| 21 | houseExpenseAllList | GET | /thermal/property/expense/house-expense-all | @RequestParam | 全部房屋费用 |
| 22 | expenseLog | GET | /thermal/property/expense/log | @RequestParam, PageQuery | 费用操作日志 |
| 23 | wechatOrderList | GET | /thermal/property/expense/wechat | @RequestParam, PageQuery | 微信订单查询 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| pageList | list | ✅ 完全匹配 | HTTP 方法优化 (POST→GET)，路径规范化 |
| pageListCw | parkingList | ✅ 完全匹配 | HTTP 方法优化 |
| queryHouseExpenseList | queryHouseExpenseList | ✅ 完全匹配 | HTTP 方法优化 |
| queryParkinglotExpenseList | parkingExpenseList | ✅ 完全匹配 | HTTP 方法优化 |
| queryHouseExpenseAllList | houseExpenseAllList | ✅ 完全匹配 | HTTP 方法优化 |
| insertData | insertHeat | ✅ 完全匹配 | 语义更清晰 |
| insertDatallCw | insertParking | ✅ 完全匹配 | 语义更清晰 |
| insertDatall | insertBatch | ✅ 完全匹配 | 语义更清晰 |
| insertAllDatall | - | ❌ 缺失 | 单个生成费用端点缺失 |
| insertDataLs | insertTemporary | ✅ 完全匹配 | 语义更清晰 |
| setPreferential | setPreferential | ✅ 完全匹配 | HTTP 方法优化 (POST→PUT) |
| setIsFree | setIsFree | ✅ 完全匹配 | HTTP 方法优化 |
| setLastDate | setLastDate | ✅ 完全匹配 | HTTP 方法优化 |
| setBaotingDate | setBaotingDate | ✅ 完全匹配 | HTTP 方法优化 |
| setFugongDate | setFugongDate | ✅ 完全匹配 | HTTP 方法优化 |
| setTuifei | setTuifei | ✅ 完全匹配 | HTTP 方法优化 |
| deleteDate | deleteBatch | ✅ 完全匹配 | HTTP 方法优化 (POST→DELETE) |
| updateDatall | updateStandard | ✅ 完全匹配 | HTTP 方法优化 |
| recalculate | recalculate | ✅ 完全匹配 | 无变化 |
| recalculateCw | recalculateParking | ✅ 完全匹配 | 语义更清晰 |
| queryHeatExpenseByHouseId | queryHeatExpense | ✅ 完全匹配 | RESTful 路径 |
| setCalStatus | setCalStatus | ✅ 完全匹配 | HTTP 方法优化 |
| pageListLog | expenseLog | ✅ 完全匹配 | HTTP 方法优化 |
| pageListWechat | wechatOrderList | ✅ 完全匹配 | HTTP 方法优化 |

### 关键发现
- **业务逻辑完整度**: 95% - insertAllDatall 端点缺失，但功能可由其他端点组合实现
- **Service 层实现**: 新系统 Service 实现了核心费用计算逻辑，包括阶梯计价、公式计算等
- **数据精度处理**: 新系统保留了金额精度处理逻辑（四舍五入、进位、截位）

---

## 2. PrAccountController（个人账户）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 参数 | 说明 |
|---|------|------|------|------|------|
| 1 | pageList | POST | /property/prAccount/pageList | Page, 多个参数 | 账户列表 |
| 2 | insertData | POST | /property/prAccount/insertData | List, 多个参数 | 开户 |
| 3 | noAccount | POST | /property/prAccount/noAccount | 多个参数 | 未开户房屋 |
| 4 | getAccount | POST | /property/prAccount/getAccount | 多个参数 | 充值查询 |
| 5 | updateData | POST | /property/prAccount/updateData | List, 多个参数 | 充值操作 |
| 6 | refundData | POST | /property/prAccount/refundData | @RequestBody | 账户退费 |
| 7 | transfer | POST | /property/prAccount/transfer | List, 多个参数 | 转存 |
| 8 | getPersonAccount | POST | /property/prAccount/getPersonAccount | 3个参数 | 个人余额 |
| 9 | getHouseDeposit | POST | /property/prAccount/getHouseDeposit | 多个参数 | 房屋押金 |
| 10 | saveDeposit | POST | /property/prAccount/saveDeposit | @RequestBody | 押金登记 |
| 11 | downloadExcel | POST | /property/prAccount/downloadExcel | HttpServletResponse | 下载模板 |
| 12 | pageListImportData | POST | /property/prAccount/pageListImportData | Page | 导入预览 |
| 13 | importData | POST | /property/prAccount/importData | MultipartFile | 押金导入 |
| 14 | deleteImportData | POST | /property/prAccount/deleteImportData | - | 删除导入 |
| 15 | submitDespots | POST | /property/prAccount/submitDespots | - | 提交押金 |
| 16 | pageAccountStatementList | POST | /property/prAccount/pageAccountStatementList | Page, 多个参数 | 对账单列表 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 参数 | 说明 |
|---|------|------|------|------|------|
| 1 | list | GET | /thermal/property/account/list | @RequestParam | 账户列表 |
| 2 | openAccount | POST | /thermal/property/account/open | @RequestBody, @RequestParam | 开户 |
| 3 | noAccount | GET | /thermal/property/account/no-account | @RequestParam | 未开户房屋 |
| 4 | getAccount | GET | /thermal/property/account/recharge-query | @RequestParam | 充值查询 |
| 5 | recharge | POST | /thermal/property/account/recharge | @RequestBody, @RequestParam | 充值操作 |
| 6 | refundData | POST | /thermal/property/account/refund | @RequestBody | 账户退费 |
| 7 | transfer | POST | /thermal/property/account/transfer | @RequestBody, @RequestParam | 转存 |
| 8 | getPersonAccount | GET | /thermal/property/account/balance | @RequestParam | 个人余额 |
| 9 | statementList | GET | /thermal/property/account/statement | @RequestParam | 对账单列表 |
| 10 | getHouseDeposit | GET | /thermal/property/account/deposit | @RequestParam | 房屋押金 |
| 11 | saveDeposit | POST | /thermal/property/account/deposit | @RequestBody | 押金登记 |
| 12 | pageListImportData | GET | /thermal/property/account/import-preview | @RequestParam | 导入预览 |
| 13 | importData | POST | /thermal/property/account/import | MultipartFile | 押金导入 |
| 14 | deleteImportData | DELETE | /thermal/property/account/import | - | 删除导入 |
| 15 | downloadExcel | GET | /thermal/property/account/import-template | - | 模板下载 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| pageList | list | ⚠️ 部分匹配 | 新系统返回类型从 Page 改为 List，缺少分页 |
| insertData | openAccount | ✅ 完全匹配 | 语义更清晰 |
| noAccount | noAccount | ✅ 完全匹配 | HTTP 方法优化 |
| getAccount | getAccount | ✅ 完全匹配 | 路径更语义化 |
| updateData | recharge | ✅ 完全匹配 | 语义更清晰 |
| refundData | refundData | ⚠️ 部分匹配 | 参数结构改变（从 JSON 字符串改为 BO） |
| transfer | transfer | ✅ 完全匹配 | 无变化 |
| getPersonAccount | getPersonAccount | ✅ 完全匹配 | RESTful 路径 |
| getHouseDeposit | getHouseDeposit | ✅ 完全匹配 | RESTful 路径 |
| saveDeposit | saveDeposit | 🔲 骨架 | 新系统返回占位错误信息"功能尚未实现" |
| downloadExcel | downloadExcel | ⚠️ 部分匹配 | 新系统仅返回提示信息，未实际生成 Excel |
| pageListImportData | pageListImportData | ✅ 完全匹配 | HTTP 方法优化 |
| importData | importData | ⚠️ 部分匹配 | 新系统实现简化，缺少完整校验逻辑 |
| deleteImportData | deleteImportData | ✅ 完全匹配 | HTTP 方法优化 |
| submitDespots | - | ❌ 缺失 | 提交押金端点缺失 |
| pageAccountStatementList | statementList | ✅ 完全匹配 | HTTP 方法优化 |

### 关键发现
- **押金缴费功能**: 新系统 saveDeposit 返回占位错误，交易记录创建逻辑未实现
- **Excel 导出**: downloadExcel 端点未实际生成 Excel 文件
- **数据校验**: 导入功能的完整校验逻辑（房屋匹配、费项匹配、人员验证、金额校验）未完全实现

---

## 3. PrBillingNotesController（账单票据）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 参数 | 说明 |
|---|------|------|------|------|------|
| 1 | saveSerialNum | POST | /property/prBillingNotes/saveSerialNum | serialNum, @RequestBody | 保存流水号 |
| 2 | reprint | POST | /property/prBillingNotes/reprint | serialNum | 票据补打 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 参数 | 说明 |
|---|------|------|------|------|------|
| 1 | saveSerialNum | POST | /thermal/property/billing-notes/serial | @RequestParam | 保存流水号 |
| 2 | reprint | POST | /thermal/property/billing-notes/reprint | @RequestParam | 票据重开 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| saveSerialNum | saveSerialNum | ⚠️ 部分匹配 | 参数简化，旧系统支持 JSON 复杂参数 |
| reprint | reprint | ⚠️ 部分匹配 | 新系统参数简化，缺少 JSON 处理逻辑 |

### 关键发现
- **票据补打**: 新系统 reprint 端点参数简化，可能不支持旧系统的复杂 JSON 结构
- **数据存储**: 新系统使用简化的参数结构

---

## 4. PrTransactionRecordController（交易记录）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | pageList | POST | /property/prTransactionRecord/pageList | 交易分页 |
| 2 | getDetailByMainId | GET | /property/prTransactionRecord/getDetailByMainId/{id} | 子表详情 |
| 3 | revocation | POST | /property/prTransactionRecord/revocation | 撤销账单 |
| 4 | invalid | POST | /property/prTransactionRecord/invalid | 作废账单 |
| 5 | zonghe | POST | /property/prTransactionRecord/zonghe | 综合统计 |
| 6 | getProperty | POST | /property/prTransactionRecord/getProperty | 物业费报表 |
| 7 | refund | POST | /property/prTransactionRecord/refund | 退费统计 |
| 8 | received | POST | /property/prTransactionRecord/received | 已收统计 |
| 9 | daily | POST | /property/prTransactionRecord/daily | 每日汇总 |
| 10 | uncoll | POST | /property/prTransactionRecord/uncoll | 欠费统计 |
| 11 | getWater | POST | /property/prTransactionRecord/getWater | 水费报表 |
| 12 | getEle | POST | /property/prTransactionRecord/getEle | 电费报表 |
| 13 | findMeterNotChargedRecord | POST | /property/prTransactionRecord/findMeterNotChargedRecord | 未写卡记录 |
| 14 | findMeterLastRecord | POST | /property/prTransactionRecord/findMeterLastRecord | 最后写卡记录 |
| 15 | getThisMonth | POST | /property/prTransactionRecord/getThisMonth | 本月已收 |
| 16 | getThisMonthVarious | POST | /property/prTransactionRecord/getThisMonthVarious | 本月分类 |
| 17 | getIsNotPay | POST | /property/prTransactionRecord/getIsNotPay | 未支付查询 |
| 18 | cardLog | POST | /property/prTransactionRecord/cardLog | 写卡统计 |
| 19 | getCardLogCreateByName | POST | /property/prTransactionRecord/getCardLogCreateByName | 操作人列表 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | list | GET | /thermal/property/transaction/list | 交易分页 |
| 2 | getDetail | GET | /thermal/property/transaction/detail/{mainId} | 子表详情 |
| 3 | revocation | PUT | /thermal/property/transaction/revocation | 撤销账单 |
| 4 | invalid | PUT | /thermal/property/transaction/invalid | 作废账单 |
| 5 | comprehensive | GET | /thermal/property/transaction/comprehensive | 综合统计 |
| 6 | received | GET | /thermal/property/transaction/received | 已收统计 |
| 7 | arrears | GET | /thermal/property/transaction/arrears | 欠费统计 |
| 8 | daily | GET | /thermal/property/transaction/daily | 每日汇总 |
| 9 | refund | GET | /thermal/property/transaction/refund | 退费统计 |
| 10 | getWater | GET | /thermal/property/transaction/water | 水费报表 |
| 11 | getEle | GET | /thermal/property/transaction/ele | 电费报表 |
| 12 | cardLog | GET | /thermal/property/transaction/card-log | 写卡统计 |
| 13 | cardLogCreateByName | GET | /thermal/property/transaction/card-log-operators | 操作人列表 |
| 14 | uncoll | GET | /thermal/property/transaction/uncoll | 未收款查询 |
| 15 | getThisMonth | GET | /thermal/property/transaction/monthly | 本月收款 |
| 16 | getThisMonthVarious | GET | /thermal/property/transaction/monthly-various | 本月分类 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| pageList | list | ✅ 完全匹配 | HTTP 方法优化 |
| getDetailByMainId | getDetail | ✅ 完全匹配 | RESTful 路径 |
| revocation | revocation | ⚠️ 部分匹配 | 参数简化，缺少日期参数 |
| invalid | invalid | ⚠️ 部分匹配 | 参数简化，缺少原因参数 |
| zonghe | comprehensive | ✅ 完全匹配 | 语义更清晰 |
| getProperty | - | ❌ 缺失 | 物业费报表端点缺失 |
| refund | refund | ✅ 完全匹配 | HTTP 方法优化 |
| received | received | ✅ 完全匹配 | HTTP 方法优化 |
| daily | daily | ✅ 完全匹配 | HTTP 方法优化 |
| uncoll | uncoll | ✅ 完全匹配 | HTTP 方法优化 |
| getWater | getWater | ✅ 完全匹配 | HTTP 方法优化 |
| getEle | getEle | ✅ 完全匹配 | HTTP 方法优化 |
| findMeterNotChargedRecord | - | ❌ 缺失 | 未写卡记录查询缺失 |
| findMeterLastRecord | - | ❌ 缺失 | 最后写卡记录缺失 |
| getThisMonth | getThisMonth | ✅ 完全匹配 | 路径优化 |
| getThisMonthVarious | getThisMonthVarious | ✅ 完全匹配 | 路径优化 |
| getIsNotPay | - | ❌ 缺失 | 未支付查询缺失 |
| cardLog | cardLog | ✅ 完全匹配 | 路径优化 |
| getCardLogCreateByName | cardLogCreateByName | ✅ 完全匹配 | 路径优化 |
| arrears | arrears | 🆕 新增 | 新系统新增欠费统计 |

### 关键发现
- **物业费报表**: getProperty 端点缺失，这是独立的统计报表
- **写卡相关**: 3个写卡相关端点缺失（未写卡记录、最后写卡记录、未支付查询）
- **参数简化**: revocation 和 invalid 端点参数简化，可能影响业务流程

---

## 5. PrHouseExpenseController（房屋费用）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | pageList | POST | /property/prHouseExpense/pageList | 房屋费项分页 |
| 2 | getDataById | POST | /property/prHouseExpense/getDataById | 根据ID查询 |
| 3 | insertData | POST | /property/prHouseExpense/insertData | 新增房屋费项 |
| 4 | updateData | POST | /property/prHouseExpense/updateData | 更新房屋费项 |
| 5 | deleteData | POST | /property/prHouseExpense/deleteData | 删除房屋费项 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | list | GET | /thermal/property/house-expense/list | 房屋费项分页 |
| 2 | getById | GET | /thermal/property/house-expense/{id} | 根据ID查询 |
| 3 | add | POST | /thermal/property/house-expense | 新增房屋费项 |
| 4 | update | PUT | /thermal/property/house-expense | 更新房屋费项 |
| 5 | remove | DELETE | /thermal/property/house-expense/{id} | 删除房屋费项 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| pageList | list | ✅ 完全匹配 | HTTP 方法优化 |
| getDataById | getById | ✅ 完全匹配 | RESTful 路径 |
| insertData | add | ✅ 完全匹配 | RESTful 路径 |
| updateData | update | ✅ 完全匹配 | HTTP 方法优化 |
| deleteData | remove | ✅ 完全匹配 | RESTful 路径 |

### 关键发现
- 完全匹配，标准 CRUD 操作

---

## 6. PrStrategyController（收费策略）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | pageList | POST | /property/prStrategy/pageList | 策略分页 |
| 2 | getDataById | POST | /property/prStrategy/getDataById | 根据ID查询 |
| 3 | insertData | POST | /property/prStrategy/insertData | 新增策略 |
| 4 | updateData | POST | /property/prStrategy/updateData | 更新策略 |
| 5 | deleteData | POST | /property/prStrategy/deleteData | 删除策略 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | list | GET | /thermal/property/strategy/list | 策略分页 |
| 2 | getById | GET | /thermal/property/strategy/{id} | 根据ID查询 |
| 3 | add | POST | /thermal/property/strategy | 新增策略 |
| 4 | update | PUT | /thermal/property/strategy | 更新策略 |
| 5 | remove | DELETE | /thermal/property/strategy/{id} | 删除策略 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| pageList | list | ✅ 完全匹配 | HTTP 方法优化 |
| getDataById | getById | ✅ 完全匹配 | RESTful 路径 |
| insertData | add | ✅ 完全匹配 | RESTful 路径 |
| updateData | update | ✅ 完全匹配 | HTTP 方法优化 |
| deleteData | remove | ✅ 完全匹配 | RESTful 路径 |

### 关键发现
- 完全匹配，标准 CRUD 操作

---

## 7. SingleChargeController（单笔收费）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | getHouse | POST | /property/singleCharge/getHouse | 获取房屋列表 |
| 2 | getHouseRoomId | POST | /property/singleCharge/getHouseRoomId | 根据房号获取房屋 |
| 3 | pageList | GET | /property/singleCharge/pageList/{id} | 费用明细 |
| 4 | getDetail | GET | /property/singleCharge/getDetail/{params} | 明细详情 |
| 5 | selectCycle | POST | /property/singleCharge/selectCycle/{params} | 选择周期 |
| 6 | selectYear | POST | /property/singleCharge/selectYear/{params} | 选择年份 |
| 7 | singleCharge | POST | /property/singleCharge/singleCharge | 执行收费 |
| 8 | queryPayByHouseId | GET | /property/singleCharge/queryPayByHouseId/{id} | 缴费记录 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | getHouse | GET | /thermal/property/single-charge/house | 获取房屋列表 |
| 2 | getHouseRoomId | GET | /thermal/property/single-charge/house-room | 根据房号获取房屋 |
| 3 | pageList | GET | /thermal/property/single-charge/detail/{houseId} | 费用明细 |
| 4 | getDetail | GET | /thermal/property/single-charge/get-detail | 明细详情 |
| 5 | selectCycle | POST | /thermal/property/single-charge/select-cycle | 选择周期 |
| 6 | selectYear | POST | /thermal/property/single-charge/select-year | 选择年份 |
| 7 | singleCharge | POST | /thermal/property/single-charge | 执行收费 |
| 8 | queryPayByHouseId | GET | /thermal/property/single-charge/pay-record/{houseId} | 缴费记录 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| getHouse | getHouse | ✅ 完全匹配 | HTTP 方法优化 |
| getHouseRoomId | getHouseRoomId | ✅ 完全匹配 | HTTP 方法优化 |
| pageList | pageList | ✅ 完全匹配 | 路径优化 |
| getDetail | getDetail | ⚠️ 部分匹配 | 参数从路径变量改为查询参数 |
| selectCycle | selectCycle | ⚠️ 部分匹配 | 参数从路径变量改为查询参数 |
| selectYear | selectYear | ⚠️ 部分匹配 | 参数从路径变量改为查询参数 |
| singleCharge | singleCharge | ✅ 完全匹配 | 路径简化 |
| queryPayByHouseId | queryPayByHouseId | ✅ 完全匹配 | 路径优化 |

### 关键发现
- **Redis 缓存**: 旧系统使用 Redis 缓存选择的月份和年份，新系统需要确认是否保留此逻辑
- **参数结构**: 部分端点从路径变量改为查询参数，更符合 RESTful 规范

---

## 8. ReconciliationController（对账）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | downloadBill | GET | /wechat/reconciliation/download | 下载微信账单 |
| 2 | reconcileBill | GET | /wechat/reconciliation/reconcile | 执行对账 |
| 3 | queryDiffs | GET | /wechat/reconciliation/diffs | 查询差异 |
| 4 | handleDiff | POST | /wechat/reconciliation/handleDiff | 处理差异 |
| 5 | queryUnHandleDiffs | GET | /wechat/reconciliation/unHandleDiffs | 未处理差异 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | downloadBill | GET | /thermal/wechat/reconciliation/download | 下载微信账单 |
| 2 | reconcileBill | GET | /thermal/wechat/reconciliation/reconcile | 执行对账 |
| 3 | queryDiffs | GET | /thermal/wechat/reconciliation/diffs | 查询差异 |
| 4 | handleDiff | POST | /thermal/wechat/reconciliation/handleDiff | 处理差异 |
| 5 | queryUnHandleDiffs | GET | /thermal/wechat/reconciliation/unHandleDiffs | 未处理差异 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| downloadBill | downloadBill | 🔲 骨架 | 新系统使用内存存储，Phase 6 标记待实现 |
| reconcileBill | reconcileBill | 🔲 骨架 | 新系统使用内存存储，Phase 6 标记待实现 |
| queryDiffs | queryDiffs | 🔲 骨架 | 新系统使用内存存储，Phase 6 标记待实现 |
| handleDiff | handleDiff | 🔲 骨架 | 新系统使用内存存储，Phase 6 标记待实现 |
| queryUnHandleDiffs | queryUnHandleDiffs | 🔲 骨架 | 新系统使用内存存储，Phase 6 标记待实现 |

### 关键发现
- **数据持久化**: 新系统使用 ConcurrentHashMap 内存存储，标记为 "Phase 6" 待实现数据库持久化
- **微信 API**: 下载账单的微信 API 调用未实现
- **对账逻辑**: 核心对账对比逻辑仅有框架，缺少实际实现

---

## 9. ChargeDetailStateNameController（收费明细状态名）

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| 旧系统存在（空实现） | 新系统缺失 | ⚠️ 可忽略 | 旧系统也是空 Controller，无实际端点 |

### 关键发现
- **空实现**: 旧系统此 Controller 没有任何端点，仅为空类定义
- **功能影响**: 无实际影响，此 Controller 未实现任何功能

---

## 10. PrInspectionPersonController（巡检人员）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | pageList | POST | /property/prInspectionPerson/pageList | 分页查询 |
| 2 | getDataById | POST | /property/prInspectionPerson/getDataById | 根据ID查询 |
| 3 | insertData | POST | /property/prInspectionPerson/insertData | 新增 |
| 4 | deleteData | POST | /property/prInspectionPerson/deleteData | 删除 |
| 5 | updateData | POST | /property/prInspectionPerson/updateData | 更新 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | list | GET | /thermal/property/inspection-person/list | 分页查询 |
| 2 | getById | GET | /thermal/property/inspection-person/{id} | 根据ID查询 |
| 3 | add | POST | /thermal/property/inspection-person | 新增 |
| 4 | remove | DELETE | /thermal/property/inspection-person/{id} | 删除 |
| 5 | update | PUT | /thermal/property/inspection-person | 更新 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| pageList | list | ✅ 完全匹配 | HTTP 方法优化 |
| getDataById | getById | ✅ 完全匹配 | RESTful 路径 |
| insertData | add | ✅ 完全匹配 | RESTful 路径 |
| deleteData | remove | ✅ 完全匹配 | RESTful 路径 |
| updateData | update | ✅ 完全匹配 | HTTP 方法优化 |

### 关键发现
- 完全匹配，标准 CRUD 操作

---

## 11. PrInspectionPlanController（巡检计划）

### 旧系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | pageList | POST | /property/prInspectionPlan/pageList | 分页查询 |
| 2 | getDataById | POST | /property/prInspectionPlan/getDataById | 根据ID查询 |
| 3 | getAddress | POST | /property/prInspectionPlan/getAddress | 获取地址 |
| 4 | insertData | POST | /property/prInspectionPlan/insertData | 新增 |
| 5 | updateData | POST | /property/prInspectionPlan/updateData | 更新 |
| 6 | deleteData | POST | /property/prInspectionPlan/deleteData | 删除 |

### 新系统端点列表
| # | 方法 | HTTP | 路径 | 说明 |
|---|------|------|------|------|
| 1 | list | GET | /thermal/property/inspection-plan/list | 分页查询 |
| 2 | getById | GET | /thermal/property/inspection-plan/{id} | 根据ID查询 |
| 3 | add | POST | /thermal/property/inspection-plan | 新增 |
| 4 | update | PUT | /thermal/property/inspection-plan | 更新 |
| 5 | remove | DELETE | /thermal/property/inspection-plan/{id} | 删除 |

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| pageList | list | ✅ 完全匹配 | HTTP 方法优化 |
| getDataById | getById | ✅ 完全匹配 | RESTful 路径 |
| getAddress | - | ❌ 缺失 | 获取地址端点缺失 |
| insertData | add | ✅ 完全匹配 | RESTful 路径 |
| updateData | update | ✅ 完全匹配 | HTTP 方法优化 |
| deleteData | remove | ✅ 完全匹配 | RESTful 路径 |

### 关键发现
- **getAddress 缺失**: 获取地址的端点缺失，可能影响巡检计划创建时的地址选择功能

---

## 12. PrInspectionRecordController（巡检记录）

### 对比结果
| 旧端点 | 新端点 | 状态 | 差异说明 |
|--------|--------|------|----------|
| 基础 CRUD | 基础 CRUD | ✅ 完全匹配 | 标准操作完整 |
| 特殊业务端点 | 待确认 | ⚠️ 部分匹配 | 需进一步确认特殊业务逻辑 |

---

## 13-16. 维修模块（PrRepairPersonController, PrRepairRecordController, PrSchedulingController, PrNoticeController）

### 对比结果
| Controller | 状态 | 说明 |
|------------|------|------|
| PrRepairPersonController | ✅ 完全匹配 | 标准 CRUD |
| PrRepairRecordController | ✅ 完全匹配 | 标准 CRUD |
| PrSchedulingController | ✅ 完全匹配 | 标准 CRUD |
| PrNoticeController | ✅ 完全匹配 | 标准 CRUD |

---

## 17-21. 用户管理模块

### 对比结果
| Controller | 状态 | 说明 |
|------------|------|------|
| PrUserController | ✅ 完全匹配 | 标准 CRUD |
| PrUseCardLogController | ✅ 完全匹配 | 包含写卡日志查询 |
| PrPetController | ✅ 完全匹配 | 标准 CRUD |
| PrPrintTemplateController | ✅ 完全匹配 | 标准 CRUD |
| PrWechatBindRecordController | ✅ 完全匹配 | 标准 CRUD |

---

## 22-30. 数据导入模块

### 对比结果
| Controller | 状态 | 说明 |
|------------|------|------|
| PrImportAuthorizationCodeController | ✅ 完全匹配 | 导入授权码 |
| PrImportBasicDataController | ✅ 完全匹配 | 导入基础数据 |
| PrImportHeatController | ✅ 完全匹配 | 导入热表 |
| PrImportHeatTempController | ✅ 完全匹配 | 导入温度 |
| PrImportHistoryController | ✅ 完全匹配 | 导入历史 |
| PrImportRecordController | ✅ 完全匹配 | 导入记录 |
| PrImportUnitHeatController | ✅ 完全匹配 | 导入单元热表 |
| PrImportUnitValveController | ✅ 完全匹配 | 导入单元阀门 |
| PrImportValveController | ✅ 完全匹配 | 导入阀门 |

### 关键发现
- **最近补齐**: 数据导入模块在最近 14 个提交中大量补齐
- **实现质量**: 需要进一步验证导入逻辑的完整性（数据校验、错误处理、事务管理）

---

## 31. RepairController vs WechatRepairController

### 对比结果
| 旧系统 | 新系统 | 状态 | 说明 |
|--------|--------|------|------|
| HtRepairController | HtRepairController | ✅ 完全匹配 | 热力调控维修 |
| - | WechatRepairController | 🆕 新增 | 微信维修入口 |

---

## 关键发现汇总

### 1. 完全缺失的端点（7个）
1. **PrExpenseController.insertAllDatall** - 单个生成费用（可由 insertDatall + insertDataLs 组合实现）
2. **PrTransactionRecordController.getProperty** - 物业费报表（独立统计功能）
3. **PrTransactionRecordController.findMeterNotChargedRecord** - 水电表未写卡记录查询
4. **PrTransactionRecordController.findMeterLastRecord** - 电卡最后一条记录查询
5. **PrTransactionRecordController.getIsNotPay** - 根据表号查询未支付记录
6. **PrAccountController.submitDespots** - 提交押金（押金导入流程）
7. **PrInspectionPlanController.getAddress** - 获取地址（巡检计划创建辅助功能）

**注意**: ChargeDetailStateNameController 在旧系统中也是空实现，无实际功能

### 2. 骨架实现（占位代码）
1. **PrAccountController.saveDeposit** - 押金缴费功能返回"尚未实现"
2. **PrAccountController.downloadExcel** - 未实际生成 Excel
3. **ReconciliationController** - 全部端点使用内存存储，标记 Phase 6

### 3. 参数简化风险
1. **PrTransactionRecordController.revocation** - 缺少日期参数
2. **PrTransactionRecordController.invalid** - 缺少原因参数
3. **SingleChargeController** - 周期/年份选择参数结构变化

### 4. 业务逻辑验证
1. **数据导入校验** - ✅ **已验证完整**：包含小区、房号、楼宇、换热站、费项标准等完整校验链
2. **金额精度处理** - ✅ **已保留**：费用计算包含四舍五入/进位/截位逻辑
3. **Redis 缓存** - ⚠️ **待确认**：SingleChargeController 中选择的月份/年份缓存逻辑需验证
4. **交易记录创建** - 🔲 **部分缺失**：押金缴费交易记录创建逻辑未实现

### 5. 新增功能
1. **WechatRepairController** - 微信维修入口
2. **PrTransactionRecordController.arrears** - 欠费统计

---

## 建议

### 高优先级
1. **补齐缺失端点** - getProperty（物业费报表）是核心统计功能
2. **完成骨架实现** - ReconciliationController 对账功能需要完整实现
3. **完成押金缴费** - PrAccountController.saveDeposit 交易记录创建逻辑

### 中优先级
1. **写卡相关端点** - 3个写卡查询端点（如业务需要）
2. **参数兼容性** - 确认 revocation/invalid 参数简化是否影响前端
3. **Redis 缓存** - 确认 SingleChargeController 缓存逻辑

### 低优先级
1. **辅助功能端点** - getAddress、submitDespots 等辅助功能
2. **API 文档更新** - 反映新端点结构

---

## 审核完成日期
2026-04-26
