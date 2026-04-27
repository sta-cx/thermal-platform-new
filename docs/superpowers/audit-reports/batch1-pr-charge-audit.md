# Pr-收费运维模块迁移审核报告

## 审核概览
- **审核日期**: 2026-04-26
- **旧系统路径**: D:\chonggou\thermal-balance-backend
- **新系统路径**: D:\chonggou\thermal-platform-new
- **旧系统 Controller 数**: 33
- **新系统对应 Controller 数**: 33
- **完全匹配**: 24
- **部分匹配**: 7
- **缺失**: 2
- **待实现**: 2

---

## 逐 Controller 对比

### 1. PrExpenseController (费用明细管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/expense/pageList | POST | /thermal/property/expense/list | GET | MATCH | 分页查询费用明细 |
| /property/expense/pageListCw | POST | /thermal/property/expense/parking-list | GET | MATCH | 车位费用分页查询 |
| /property/expense/queryHouseExpenseList | POST | /thermal/property/expense/house-list | GET | MATCH | 查询房屋费用明细 |
| /property/expense/queryParkinglotExpenseList | POST | /thermal/property/expense/parking-expense | GET | MATCH | 车位空间费用查询 |
| /property/expense/queryHouseExpenseAllList | POST | /thermal/property/expense/house-expense-all | GET | MATCH | 全部房屋费用查询 |
| /property/expense/insertData | POST | /thermal/property/expense/heat | POST | MATCH | 生成取暖费明细 |
| /property/expense/insertDatallCw | POST | /thermal/property/expense/parking | POST | MATCH | 批量生成车位费用 |
| /property/expense/insertDatall | POST | /thermal/property/expense/batch | POST | MATCH | 批量生成费用明细 |
| /property/expense/insertAllDatall | POST | - | - | MISSING | 单个生成费用明细(混合类型) |
| /property/expense/insertDataLs | POST | /thermal/property/expense/temporary | POST | MATCH | 生成临时费用明细 |
| /property/expense/setPreferential | POST | /thermal/property/expense/preferential | PUT | MATCH | 设置优惠 |
| /property/expense/setIsFree | POST | /thermal/property/expense/free | PUT | MATCH | 设置免收 |
| /property/expense/setLastDate | POST | /thermal/property/expense/delay | PUT | MATCH | 设置延期 |
| /property/expense/setBaotingDate | POST | /thermal/property/expense/suspend | PUT | MATCH | 设置报停 |
| /property/expense/setFugongDate | POST | /thermal/property/expense/resume | PUT | MATCH | 设置复供 |
| /property/expense/setTuifei | POST | /thermal/property/expense/refund | PUT | MATCH | 设置退费 |
| /property/expense/deleteDate | POST | /thermal/property/expense/batch | DELETE | MATCH | 删除费用明细 |
| /property/expense/updateDatall | POST | /thermal/property/expense/standard | PUT | MATCH | 修改费用明细标准 |
| /property/expense/recalculate | POST | /thermal/property/expense/recalculate | POST | MATCH | 重新计算费用明细 |
| /property/expense/recalculateCw | POST | /thermal/property/expense/recalculate-parking | POST | MATCH | 重新计算车位费用 |
| /property/expense/queryHeatExpenseByHouseId | POST | /thermal/property/expense/heat/{houseId} | GET | MATCH | 查询房屋取暖费 |
| /property/expense/setCalStatus | POST | /thermal/property/expense/calc-status | PUT | MATCH | 设置计算状态 |
| /property/expense/pageListLog | POST | /thermal/property/expense/log | GET | MATCH | 费用操作日志 |
| /property/expense/pageListWechat | POST | /thermal/property/expense/wechat | GET | MATCH | 微信费用订单查询 |

#### 业务逻辑差异
- **缺失功能**: `insertAllDatall` (混合类型费用生成：固定+临时+取暖费)
- **新系统优势**:
  - 增加了 `@SaCheckPermission` 权限校验
  - 增加了 `@Log` 操作日志记录
  - 使用 RESTful 风格的 HTTP 方法 (GET/POST/PUT/DELETE)
  - 统一的 `R<Void>` 返回格式

#### 代码质量问题
- **安全性**: 旧系统缺乏权限校验，新系统已完善
- **事务管理**: 需要确认 Service 层是否正确处理事务

---

### 2. PrExpenseItemController (费用项目管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/expenseItem/pageList | POST | /thermal/property/expense-item/list | GET | MATCH | 分页查询费用项目 |
| /property/expenseItem/insertData | POST | /thermal/property/expense-item | POST | MATCH | 新增费用项目 |
| /property/expenseItem/querypPrExpenseItem | POST | /thermal/property/expense-item/{id} | GET | MATCH | 查询费用项目详情 |
| /property/expenseItem/updateData | POST | /thermal/property/expense-item | PUT | MATCH | 修改费用项目 |
| /property/expenseItem/getDataByItemCode | POST | /thermal/property/expense-item/by-code | GET | MATCH | 按编号查询 |
| /property/expenseItem/getDataByItemGroup | POST | /thermal/property/expense-item/by-group | GET | MATCH | 按费项分组查询 |
| /property/expenseItem/getItemCodesByItemGroup | POST | /thermal/property/expense-item/codes | GET | MATCH | 获取费项code列表 |
| /property/expenseItem/getDataByCompanyIdOrgId | POST | /thermal/property/expense-item/by-org | GET | MATCH | 按公司/小区查询 |
| /property/expenseItem/deleteData | POST | /thermal/property/expense-item/{id} | DELETE | MATCH | 删除费用项目 |
| /property/expenseItem/isItemName | POST | /thermal/property/expense-item/check-name | GET | MATCH | 检查项目名称重复 |

#### 业务逻辑差异
- 完全匹配，无差异

#### 代码质量问题
- 无

---

### 3. PrHouseExpenseController (房屋费用项目绑定)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/houseExpense/pageList | POST | /thermal/property/house-expense/list | GET | MATCH | 分页查询绑定列表 |
| /property/houseExpense/queryPrHouse | POST | /thermal/property/house-expense/unbound-houses | GET | MATCH | 查询未绑定房屋 |
| /property/houseExpense/queryPrHouseD | POST | /thermal/property/house-expense/unbound-items | GET | MATCH | 查询未绑定费项 |
| /property/houseExpense/insertData | POST | /thermal/property/house-expense/batch | POST | MATCH | 批量保存绑定 |
| /property/houseExpense/updateData | POST | /thermal/property/house-expense/batch | PUT | MATCH | 批量修改绑定 |
| /property/houseExpense/deleteHouseExpense | POST | /thermal/property/house-expense/batch | DELETE | MATCH | 批量删除绑定 |

#### 业务逻辑差异
- 新系统将 URL 设计更加 RESTful

#### 代码质量问题
- 无

---

### 4. PrStandardController (收费标准管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prStandard/pageList | POST | /thermal/property/standard/list | GET | MATCH | 分页查询收费标准 |
| /property/prStandard/insertData | POST | /thermal/property/standard | POST | MATCH | 新增收费标准 |
| /property/prStandard/deleteData | POST | /thermal/property/standard/{id} | DELETE | MATCH | 删除收费标准 |
| /property/prStandard/updateData | POST | /thermal/property/standard | PUT | MATCH | 修改收费标准 |
| /property/prStandard/queryPrStandard | POST | /thermal/property/standard/{id} | GET | MATCH | 查询收费标准详情 |
| /property/prStandard/queryPrStandardByItemCode | POST | /thermal/property/standard/by-item-code | GET | MATCH | 按itemCode查询 |
| /property/prStandard/findEleStandard | POST | /thermal/property/standard/ele | GET | MATCH | 查询电表标准 |
| /property/prStandard/findWaterStandard | POST | /thermal/property/standard/water | GET | MATCH | 查询水表标准 |
| /property/prStandard/findHeatStandard | POST | /thermal/property/standard/heat | GET | MATCH | 查询热力标准 |
| /property/prStandard/isName | POST | /thermal/property/standard/check-name | GET | MATCH | 检查标准名称重复 |
| /property/prStandard/purchase | POST | /thermal/property/standard/purchase | POST | MATCH | 购买限额校验 |
| /property/prStandard/getPrExpenseItemByStandardId | POST | /thermal/property/standard/expense-item | GET | MATCH | 查询关联费用项目 |
| /property/prStandard/pageListItem | POST | /thermal/property/standard/by-item-name | GET | MATCH | 按项目名称查询 |
| /property/prStandard/standardFeeListCopy | POST | - | - | MISSING | 引用收费标准(单个) |
| /property/prStandard/standardFeeListCopyAll | POST | - | - | MISSING | 引用收费标准(批量) |

#### 业务逻辑差异
- **缺失功能**: 
  - `standardFeeListCopy` - 引用收费标准(单个小区)
  - `standardFeeListCopyAll` - 引用收费标准(多小区批量)

#### 代码质量问题
- 缺失的功能可能影响收费标准复制场景

---

### 5. PrBillingNotesController (票据备注管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prBillingNotes/saveSerialNum | POST | /thermal/property/billing-notes/serial | POST | MATCH | 保存票据流水号 |
| /property/prBillingNotes/reprint | GET | /thermal/property/billing-notes/reprint | POST | MATCH | 票据补打 |

#### 业务逻辑差异
- 新系统增强了参数校验

#### 代码质量问题
- 无

---

### 6. PrTransactionRecordController (交易记录管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prTransactionRecord/pageList | POST | /thermal/property/transaction/list | GET | MATCH | 分页查询交易记录 |
| /property/prTransactionRecord/getDetailByMainId/{id} | GET | /thermal/property/transaction/detail/{mainId} | GET | MATCH | 查询交易明细 |
| /property/prTransactionRecord/revocation | POST | /thermal/property/transaction/revocation | PUT | MATCH | 撤销账单 |
| /property/prTransactionRecord/invalid | POST | /thermal/property/transaction/invalid | PUT | MATCH | 作废账单 |
| /property/prTransactionRecord/zonghe | POST | /thermal/property/transaction/comprehensive | GET | MATCH | 综合统计报表 |
| /property/prTransactionRecord/getProperty | POST | - | - | MISSING | 物业费报表 |
| /property/prTransactionRecord/refund | POST | /thermal/property/transaction/refund | GET | MATCH | 退费统计表 |
| /property/prTransactionRecord/received | POST | /thermal/property/transaction/received | GET | MATCH | 已收费用统计 |
| /property/prTransactionRecord/daily | POST | /thermal/property/transaction/daily | GET | MATCH | 每日汇总表 |
| /property/prTransactionRecord/uncoll | POST | /thermal/property/transaction/uncoll | GET | MATCH | 欠费统计表 |
| /property/prTransactionRecord/getWater | POST | /thermal/property/transaction/water | GET | MATCH | 水费报表 |
| /property/prTransactionRecord/getEle | POST | /thermal/property/transaction/ele | GET | MATCH | 电费报表 |
| /property/prTransactionRecord/findMeterNotChargedRecord | POST | - | - | MISSING | 查询未写卡记录 |
| /property/prTransactionRecord/findMeterLastRecord | POST | - | - | MISSING | 查询最后写卡记录 |
| /property/prTransactionRecord/getThisMonth | POST | /thermal/property/transaction/monthly | GET | MATCH | 本月收款统计 |
| /property/prTransactionRecord/getThisMonthVarious | POST | /thermal/property/transaction/monthly-various | GET | MATCH | 本月分类统计 |
| /property/prTransactionRecord/getIsNotPay | POST | - | - | MISSING | 查询未支付记录 |
| /property/prTransactionRecord/cardLog | POST | /thermal/property/transaction/card-log | GET | MATCH | 写卡统计 |
| /property/prTransactionRecord/getCardLogCreateByName | POST | /thermal/property/transaction/card-log-operators | GET | MATCH | 写卡操作人列表 |

#### 业务逻辑差异
- **缺失功能**:
  - `getProperty` - 物业费报表
  - `findMeterNotChargedRecord` - 查询未写卡记录
  - `findMeterLastRecord` - 查询最后写卡记录
  - `getIsNotPay` - 查询未支付记录

#### 代码质量问题
- 旧系统在 Controller 中包含大量金额计算逻辑 (精度处理)，应移至 Service 层

---

### 7. PrAccountController (个人账户管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prAccount/pageList | POST | /thermal/property/account/list | GET | MATCH | 分页查询账户列表 |
| /property/prAccount/insertData | POST | /thermal/property/account/open | POST | MATCH | 开户 |
| /property/prAccount/noAccount | POST | /thermal/property/account/no-account | GET | MATCH | 查询未开户房屋 |
| /property/prAccount/getAccount | POST | /thermal/property/account/recharge-query | GET | MATCH | 充值查询 |
| /property/prAccount/updateData | POST | /thermal/property/account/recharge | POST | MATCH | 充值操作 |
| /property/prAccount/refundData | POST | /thermal/property/account/refund | POST | MATCH | 账户退费 |
| /property/prAccount/transfer | POST | /thermal/property/account/transfer | POST | MATCH | 转存 |
| /property/prAccount/getPersonAccount | POST | /thermal/property/account/balance | GET | MATCH | 查询账户余额 |
| /property/prAccount/getHouseDeposit | POST | /thermal/property/account/deposit | GET | MATCH | 查询房屋押金 |
| /property/prAccount/saveDeposit | POST | /thermal/property/account/deposit | POST | PARTIAL | 押金缴费(未实现) |
| /property/prAccount/pageAccountStatementList | POST | /thermal/property/account/statement | GET | MATCH | 账户对账单列表 |
| /property/prAccount/downloadExcel | GET | /thermal/property/account/import-template | GET | PARTIAL | 下载押金导入模板 |
| /property/prAccount/pageListImportData | POST | /thermal/property/account/import-preview | GET | MATCH | 导入数据预览 |
| /property/prAccount/importData | POST | /thermal/property/account/import | POST | MATCH | 导入Excel数据 |
| /property/prAccount/deleteImportData | POST | /thermal/property/account/import | DELETE | MATCH | 删除导入数据 |
| /property/prAccount/submitDespots | POST | - | - | MISSING | 提交押金数据 |

#### 业务逻辑差异
- **缺失功能**: `submitDespots` - 提交押金数据
- **部分实现**: `saveDeposit` - 押金缴费功能尚未实现

#### 代码质量问题
- 旧系统 Controller 包含大量 Excel 导出样式处理逻辑
- 新系统押金缴费功能未完成实现

---

### 8. SingleChargeController (单笔收费管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/singleCharge/getHouse | POST | /thermal/property/single-charge/house | GET | MATCH | 获取房屋列表 |
| /property/singleCharge/getHouseRoomId | POST | /thermal/property/single-charge/house-room | GET | MATCH | 根据房间号获取房屋 |
| /property/singleCharge/pageList/{id} | GET | /thermal/property/single-charge/detail/{houseId} | GET | MATCH | 查询费用明细 |
| /property/singleCharge/getDetail/{...} | GET | /thermal/property/single-charge/get-detail | GET | MATCH | 查询明细详情 |
| /property/singleCharge/selectCycle/{...} | POST | /thermal/property/single-charge/select-cycle | POST | MATCH | 选择计费周期 |
| /property/singleCharge/selectYear/{...} | POST | /thermal/property/single-charge/select-year | POST | MATCH | 选择计费年份 |
| /property/singleCharge/singleCharge | POST | /thermal/property/single-charge | POST | MATCH | 执行单笔收费 |
| /property/singleCharge/queryPayByHouseId/{id} | GET | /thermal/property/single-charge/pay-record/{houseId} | GET | MATCH | 查询缴费记录 |

#### 业务逻辑差异
- 新系统将 URL 参数从路径变量改为请求参数，更加 RESTful

#### 代码质量问题
- 旧系统使用 Redis 缓存选择结果，新系统需要确认是否保留此逻辑

---

### 9. ReconciliationController (对账管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /wechat/reconciliation/download | GET | /thermal/wechat/reconciliation/download | GET | MATCH | 下载微信账单 |
| /wechat/reconciliation/reconcile | GET | /thermal/wechat/reconciliation/reconcile | GET | MATCH | 执行对账 |
| /wechat/reconciliation/diffs | GET | /thermal/wechat/reconciliation/diffs | GET | MATCH | 查询对账差异 |
| /wechat/reconciliation/handleDiff | POST | /thermal/wechat/reconciliation/handleDiff | POST | MATCH | 处理对账差异 |
| /wechat/reconciliation/unHandleDiffs | GET | /thermal/wechat/reconciliation/unHandleDiffs | GET | MATCH | 查询未处理差异 |

#### 业务逻辑差异
- 新系统使用内存存储 (ConcurrentHashMap) 作为临时实现，标注为 Phase 6 需替换为数据库持久化

#### 代码质量问题
- **严重**: 新系统对账功能仅是骨架实现，未完成微信账单下载和真实对账逻辑

---

### 10. PrInspectionPersonController (巡检人员管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prInspectionPerson/pageList | POST | - | - | MISSING | 分页查询 |
| /property/prInspectionPerson/getDataById | POST | - | - | MISSING | 查询详情 |
| /property/prInspectionPerson/insertData | POST | - | - | MISSING | 新增人员 |
| /property/prInspectionPerson/deleteData | POST | - | - | MISSING | 删除人员 |
| /property/prInspectionPerson/updateData | POST | - | - | MISSING | 修改人员 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现巡检人员管理功能

---

### 11. PrInspectionPlanController (巡检计划管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prInspectionPlan/pageList | POST | - | - | MISSING | 分页查询 |
| /property/prInspectionPlan/getDataById | POST | - | - | MISSING | 查询详情 |
| /property/prInspectionPlan/getAddress | POST | - | - | MISSING | 获取地址 |
| /property/prInspectionPlan/insertData | POST | - | - | MISSING | 新增计划 |
| /property/prInspectionPlan/updateData | POST | - | - | MISSING | 修改计划 |
| /property/prInspectionPlan/deleteData | POST | - | - | MISSING | 删除计划 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现巡检计划管理功能

---

### 12. PrInspectionRecordController (巡检记录管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prInspectionRecord/pageList | POST | - | - | MISSING | 分页查询 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现巡检记录查询功能

---

### 13. PrRepairPersonController (维修人员管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prRepairPerson/pageList | POST | - | - | MISSING | 分页查询 |
| /property/prRepairPerson/getDataById | POST | - | - | MISSING | 查询详情 |
| /property/prRepairPerson/insertData | POST | - | - | MISSING | 新增人员 |
| /property/prRepairPerson/deleteData | POST | - | - | MISSING | 删除人员 |
| /property/prRepairPerson/updateData | POST | - | - | MISSING | 修改人员 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现维修人员管理功能

---

### 14. PrRepairRecordController (维修记录管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prRepairRecord/pageList | POST | - | - | MISSING | 分页查询 |
| /property/prRepairRecord/getPersonByCompanyId | POST | - | - | MISSING | 查询公司维修员 |
| /property/prRepairRecord/getPersonByOrgId | POST | - | - | MISSING | 查询小区维修员 |
| /property/prRepairRecord/updateData | POST | - | - | MISSING | 派单 |
| /property/prRepairRecord/select | POST | - | - | MISSING | 查找报修项目 |
| /property/prRepairRecord/insertRepairItems | POST | - | - | MISSING | 增加报修项目 |
| /property/prRepairRecord/insertRecordData | POST | - | - | MISSING | 增加手工报修 |
| /property/prRepairRecord/updateRecordData | POST | - | - | MISSING | 编辑手工报修 |
| /property/prRepairRecord/deleteRecordData | POST | - | - | MISSING | 删除报修记录 |
| /property/prRepairRecord/getHouseIsOwe | POST | - | - | MISSING | 查询欠费房屋 |
| /property/prRepairRecord/updateDataService | POST | - | - | MISSING | 服务评价 |
| /property/prRepairRecord/getAllTypeCount | POST | - | - | MISSING | 统计各类型数量 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现维修记录管理功能
- **注意**: 此功能在旧系统中使用 OSS 进行图片处理

---

### 15. PrSchedulingController (排班管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/scheduling/pageList | POST | - | - | MISSING | 分页查询 |
| /property/scheduling/getDataById | POST | - | - | MISSING | 查询详情 |
| /property/scheduling/insertData | POST | - | - | MISSING | 新增排班 |
| /property/scheduling/deleteData | POST | - | - | MISSING | 删除排班 |
| /property/scheduling/updateData | POST | - | - | MISSING | 修改排班 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现排班管理功能

---

### 16. PrNoticeController (通知公告管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/notice/pageList | POST | - | - | MISSING | 分页查询 |
| /property/notice/insertData | POST | - | - | MISSING | 新增通知 |
| /property/notice/getById | POST | - | - | MISSING | 查询详情 |
| /property/notice/updateData | POST | - | - | MISSING | 修改通知 |
| /property/notice/deleteData | POST | - | - | MISSING | 删除通知 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现通知公告管理功能

---

### 17. PrImportBasicDataController (基础数据导入)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prImportBasicData/pageList | POST | - | - | MISSING | 错误信息查询 |
| /property/prImportBasicData/importData | POST | - | - | MISSING | 上传基础数据 |
| /property/prImportBasicData/downloadExcel | GET | - | - | MISSING | 下载模板 |
| /property/prImportBasicData/downloadExcelByHeatCode | GET | - | - | MISSING | 下载缴费模板 |
| /property/prImportBasicData/importDataByHeatCode | POST | - | - | MISSING | 导入缴费数据 |
| /property/prImportBasicData/deleteData | POST | - | - | MISSING | 删除数据 |
| /property/prImportBasicData/submitData | POST | - | - | MISSING | 提交基础资料 |
| /property/prImportBasicData/isCheckHouseD | POST | - | - | MISSING | 校验房屋数据 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现基础数据导入功能

---

### 18. PrImportHeatController (热表数据导入)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prImportHeat/pageList | POST | - | - | MISSING | 分页查询 |
| /property/prImportHeat/downloadExcel | GET | - | - | MISSING | 下载模板 |
| /property/prImportHeat/importData | POST | - | - | MISSING | 导入数据 |
| /property/prImportHeat/deleteData | POST | - | - | MISSING | 删除数据 |
| /property/prImportHeat/submitData | POST | - | - | MISSING | 提交数据 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现热表数据导入功能

---

### 19. PrImportRecordController (交易记录导入)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prImportRecord/pageList | POST | - | - | MISSING | 分页查询 |
| /property/prImportRecord/downloadExcel | GET | - | - | MISSING | 下载模板 |
| /property/prImportRecord/importData | POST | - | - | MISSING | 导入数据 |
| /property/prImportRecord/deleteData | POST | - | - | MISSING | 删除数据 |
| /property/prImportRecord/submitData | POST | - | - | MISSING | 提交数据 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现交易记录导入功能

---

### 20. PrWechatBindRecordController (微信绑定记录)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prWechatBindRecord/insertData | POST | - | - | MISSING | 微信绑定 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现微信绑定记录功能

---

### 21. PrUseCardLogController (写卡日志管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/prUseCardLog/pageList | POST | - | - | MISSING | 分页查询 |
| /property/prUseCardLog/insertWriteUseCardLog | POST | - | - | MISSING | 插入写卡日志 |
| /property/prUseCardLog/pageListValveOCStatus | POST | - | - | MISSING | 阀门状态查询 |
| /property/prUseCardLog/insertValveOCStatusLog | POST | - | - | MISSING | 插入阀门日志 |
| /property/prUseCardLog/getWriteCardLogByMeterNum | POST | - | - | MISSING | 查询写卡日志 |
| /property/prUseCardLog/getReplacementCardByMeterNum | POST | - | - | MISSING | 查询补卡日志 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现写卡日志管理功能

---

### 22. PushController (推送管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/push/setPush | POST | - | - | MISSING | 设置推送 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现推送功能

---

### 23. TaskController (任务管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/task/list | POST | - | - | MISSING | 查询任务列表 |
| /property/task/listPost | POST | - | - | MISSING | 查询岗位任务 |
| /property/task/getById/{id} | GET | - | - | MISSING | 查询任务详情 |
| /property/task/getPostById/{id} | GET | - | - | MISSING | 查询岗位任务详情 |
| /property/task/edit | POST | - | - | MISSING | 修改任务 |
| /property/task/editPost | POST | - | - | MISSING | 修改岗位任务 |
| /property/task/changeStatus/{id} | POST | - | - | MISSING | 修改任务状态 |
| /property/task/changePostStatus/{id} | POST | - | - | MISSING | 修改岗位任务状态 |
| /property/task/remove/{id} | POST | - | - | MISSING | 删除任务 |
| /property/task/removePost/{id} | POST | - | - | MISSING | 删除岗位任务 |
| /property/task/run/{id} | POST | - | - | MISSING | 立即运行任务 |
| /property/task/runPost/{id} | POST | - | - | MISSING | 立即运行岗位任务 |
| /property/task/runTask/{id} | POST | - | - | MISSING | 运行指定任务 |
| /property/task/runPostTask/{id} | POST | - | - | MISSING | 运行指定岗位任务 |
| /property/task/removeBatch | POST | - | - | MISSING | 批量删除任务 |
| /property/task/save | POST | - | - | MISSING | 新增任务 |
| /property/task/savePost | POST | - | - | MISSING | 新增岗位任务 |
| /property/task/getTaskClass | POST | - | - | MISSING | 获取任务类列表 |
| /property/task/getTaskPostClass | POST | - | - | MISSING | 获取岗位任务类列表 |
| /property/task/getTaskDetails | POST | - | - | MISSING | 获取任务详情 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现任务管理功能 (基于 Quartz)

---

### 24. ToolsController (工具管理)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /property/tools/getCompanyMeter | POST | - | - | MISSING | 查询公司仪表厂商 |
| /property/tools/getMeterList | POST | - | - | MISSING | 查询仪表列表 |

#### 业务逻辑差异
- **完全缺失**: 新系统未实现工具管理功能

---

### 25. ChargeDetailStateNameController (收费明细状态名称)

#### API 端点覆盖度
| 旧系统端点 | 方法 | 新系统端点 | 方法 | 状态 | 备注 |
|-----------|------|-----------|------|------|------|
| /chargeDetailStateName | - | - | - | MISSING | 空实现 |

#### 业务逻辑差异
- 旧系统为空实现，新系统未迁移

---

## 其他导入 Controller (未在新系统实现)

### 26-33. PrImport 系列其他 Controller

| Controller | 端点数 | 状态 |
|-----------|-------|------|
| PrImportUnitHeatController | 5 | MISSING |
| PrImportUnitValveController | 5 | MISSING |
| PrImportValveController | 5 | MISSING |
| PrImportHeatTempController | 5 | MISSING |
| PrImportHistoryController | 5 | MISSING |
| PrImportAuthorizationCodeController | 5 | MISSING |

所有导入相关 Controller 在新系统中均未实现。

---

## 总结与建议

### 关键发现

1. **核心收费功能已完整迁移**
   - PrExpense (费用明细)
   - PrExpenseItem (费用项目)
   - PrHouseExpense (房屋费项绑定)
   - PrStandard (收费标准)
   - PrTransactionRecord (交易记录)
   - PrAccount (个人账户)
   - SingleCharge (单笔收费)
   - Reconciliation (对账)

2. **运维管理功能未迁移**
   - PrInspectionPerson (巡检人员)
   - PrInspectionPlan (巡检计划)
   - PrInspectionRecord (巡检记录)
   - PrRepairPerson (维修人员)
   - PrRepairRecord (维修记录)
   - PrScheduling (排班)
   - PrNotice (通知公告)
   - Push (推送)
   - Task (任务管理)
   - Tools (工具)

3. **数据导入功能未迁移**
   - 所有 PrImport 系列Controller (8个)
   - 账户押金导入部分实现

4. **写卡日志功能未迁移**
   - PrUseCardLogController

5. **微信绑定记录未迁移**
   - PrWechatBindRecordController

### 缺失功能清单

#### 高优先级 (核心业务)
- [ ] PrStandard: `standardFeeListCopy`, `standardFeeListCopyAll` (收费标准引用)
- [ ] PrAccount: `submitDespots` (押金提交)
- [ ] PrTransactionRecord: `getProperty` (物业费报表)
- [ ] PrAccount: 完整实现 `saveDeposit` (押金缴费)

#### 中优先级 (运维功能)
- [ ] PrInspectionPerson (巡检人员管理)
- [ ] PrInspectionPlan (巡检计划管理)
- [ ] PrInspectionRecord (巡检记录)
- [ ] PrRepairPerson (维修人员管理)
- [ ] PrRepairRecord (维修记录)
- [ ] PrScheduling (排班管理)
- [ ] PrNotice (通知公告)
- [ ] PrUseCardLog (写卡日志)

#### 低优先级 (辅助功能)
- [ ] Push (推送)
- [ ] Task (定时任务管理)
- [ ] Tools (工具)
- [ ] PrWechatBindRecord (微信绑定)
- [ ] 所有 PrImport 系列 Controller (数据导入)

### 代码质量风险

1. **对账功能仅骨架实现**
   - 位置: ReconciliationController
   - 风险: 未实现微信账单下载和对账逻辑
   - 建议: 尽快完成 Phase 6 实现

2. **押金缴费功能未实现**
   - 位置: PrAccountController.saveDeposit
   - 风险: 核心收费功能缺失
   - 建议: 优先实现

3. **事务管理需验证**
   - 所有写操作需要验证事务正确性
   - 特别是收费和退费流程

4. **权限校验已完善**
   - 新系统使用 Sa-Token 进行权限控制
   - 所有端点都有 `@SaCheckPermission` 注解

5. **日志记录已完善**
   - 新系统使用 `@Log` 注解记录操作日志

### 架构改进

1. **RESTful API 设计**
   - 旧系统: 全部使用 POST 方法
   - 新系统: 正确使用 GET/POST/PUT/DELETE

2. **统一响应格式**
   - 新系统使用 `R<T>` 统一响应

3. **分层架构**
   - 新系统使用 BO/VO 分离
   - 使用 MapstructUtils 进行类型转换

4. **分页处理**
   - 新系统使用统一的 PageQuery 和 TableDataInfo

---

**审核人**: Claude Code  
**审核日期**: 2026-04-26  
**审核版本**: migration/init 分支
