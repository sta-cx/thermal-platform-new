# Round 2 — Agent 2: Pr 收费运维 + Agent 代理管理 Service/DB 层深度审核

## 执行摘要

本报告深入对比分析了 Pr 收费运维模块和 Agent 代理管理模块在新旧系统之间的 Service 层业务逻辑和 DB 层数据结构。

**关键发现：**
- **PrExpense/Account 核心收费逻辑**: 新系统简化了大量复杂业务逻辑，部分关键功能待实现
- **事务安全性**: 新系统缺少部分关键的事务保护措施
- **Reconciliation 对账模块**: 新系统使用内存存储，旧系统有完整的数据库持久化
- **Agent 权限控制**: 新系统缺少角色菜单管理功能

---

## 一、Service 层对比

### 1.1 PrExpenseService (费用明细管理)

#### 核心业务逻辑对比

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **费用生成规则** | 支持 6 种生成规则（月对月、自然月、固定期限、按年、按季、按半年） | 仅实现简单月度生成 | 🔴 HIGH |
| **阶梯单价计算** | 支持建筑面积/使用面积/楼层三种阶梯类型 | 占位实现（TODO Phase 5b） | 🔴 HIGH |
| **金额公式计算** | 动态 SQL 公式计算 | 占位实现 | 🔴 HIGH |
| **优惠/免收/报停/复供/退费** | 完整业务逻辑 + 操作日志 | 简化版，无日志记录 | 🟡 MEDIUM |
| **滞纳金计算** | 支持四种计算方式（清算、结算、指定、数据恢复） | 未实现 | 🔴 HIGH |
| **费用精度处理** | 四舍五入/进位/截位三种模式 | 简化处理 | 🟡 MEDIUM |

#### 关键代码差异分析

**旧系统 - insertDatall（批量生成费用）：**
```java
// 旧系统：支持 6 种生成规则
if ("1".equals(prHouseExpense.getPrStandard().getGenerateRule())) {
    // 月对月
} else if ("2".equals(prStandard.getGenerateRule())) {
    // 自然月
} else if ("3".equals(prStandard.getGenerateRule())) {
    // 固定期限
} else if ("4".equals(prStandard.getGenerateRule())) {
    // 按年（12个月）
} else if ("5".equals(prStandard.getGenerateRule())) {
    // 按季（3个月）
} else if ("6".equals(prStandard.getGenerateRule())) {
    // 按半年（6个月）
}
```

**新系统 - insertData（生成费用）：**
```java
// 新系统：仅实现简单月度生成
for (int i = 0; i < months; i++) {
    PrExpense e = new PrExpense();
    // ... 简化逻辑
}
```

#### 风险标注

1. **🔴 RED - 费用生成规则缺失**
   - 影响：无法正确生成按年/按季/按半年的费用
   - 旧系统代码行数：~470 行
   - 新系统代码行数：~120 行
   - 建议：参考旧系统 `PrExpenseServiceImpl.insertDatall()` 完整实现

2. **🔴 RED - 阶梯单价计算未实现**
   - 旧系统：`setStandardPriceJzmj()`, `setStandardPriceSymj()`, `setStandardPriceLc()`
   - 新系统：`updateStepPrice()` 为占位实现
   - 影响：费用计算不准确

3. **🟡 YELLOW - 操作日志缺失**
   - 旧系统：每次操作都生成日志（`insertExpenseLog`, `insertExpenseLogMs`, 等）
   - 新系统：无日志记录
   - 影响：审计追踪能力下降

---

### 1.2 PrAccountService (个人账户管理)

#### 核心业务逻辑对比

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **开户** | synchronized 保护 + 自动创建交易记录 + 账户流水 | 无并发保护 | 🔴 HIGH |
| **充值** | synchronized 保护 + 完整事务流程 | 简化实现 | 🟡 MEDIUM |
| **退费** | 完整事务 + 交易记录 + 账户流水 | 简化实现 | 🟡 MEDIUM |
| **转存** | 原子性操作（转出+转入） | 简化实现 | 🟡 MEDIUM |
| **押金管理** | 完整功能 | 骨架实现（返回占位错误） | 🔴 HIGH |
| **导入功能** | Excel 解析 + 校验 + 临时表 | 简化实现 | 🟡 MEDIUM |

#### 事务安全性分析

**旧系统 - 开户操作（synchronized）：**
```java
@Override
public synchronized boolean insertData(List<PrHouse> prHouses, ...) {
    // 1. 去重处理（同一用户多房屋合并）
    HashMap<String, Integer> map = new HashMap();
    // 2. 批量插入账户
    prAccountMapper.insertList(prAccounts);
    // 3. 插入交易记录主表
    prAccountMapper.insertTransactionRecord(prTransactionRecords);
    // 4. 插入交易记录子表
    prAccountMapper.insertTransactionDetail(prTransactionDetails);
    // 5. 插入账户流水
    prAccountMapper.insertAccountStatement(prAccountStatements);
}
```

**新系统 - 开户操作：**
```java
@Override
@Transactional(rollbackFor = Exception.class)
public boolean insertData(List<String> houseIds, ...) {
    // 无 synchronized 保护
    // 简化实现，缺少账户流水记录
}
```

#### 风险标注

1. **🔴 RED - 并发安全缺失**
   - 旧系统使用 `synchronized` 保证开户操作的原子性
   - 新系统仅依赖 `@Transactional`，可能存在并发问题
   - 建议：添加分布式锁或乐观锁

2. **🔴 RED - 押金管理未实现**
   - 旧系统：`saveDeposit()` 完整实现（~100 行）
   - 新系统：返回占位错误 "功能待实现"
   - 影响：押金相关业务完全不可用

---

### 1.3 PrTransactionRecordService (交易记录管理)

#### 核心业务逻辑对比

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **撤销账单** | 检查卡表状态 + 回滚账户 + 更新费用状态 | 简化实现 | 🟡 MEDIUM |
| **作废账单** | 检查状态 + 更新 | 简化实现 | 🟢 LOW |
| **统计报表** | 9 种复杂统计（物业/水/电/综合/等） | 8 种统计 | 🟢 LOW |
| **写卡相关** | 完整的写卡日志查询 | 部分实现 | 🟡 MEDIUM |

#### 关键业务逻辑差异

**旧系统 - revocation（撤销账单）：**
```java
public boolean revocation(PrTransactionRecord record, String reason, Date date1) {
    List<PrTransactionDetail> details = mapper.getDetails(record.getId());
    if (details.get(0).getFeeId() != null) {
        // 1. 账单改成删除状态
        mapper.delete(record.getId(), reason, date1, SecurityUtils.getUser().getId());
        // 2. 交易子表改成删除状态
        mapper.deleteDetail(record.getId(), date1);
        // 3. 回滚个人账户金额
        if (record.getPaymentBalance().compareTo(BigDecimal.ZERO) > 0) {
            mapper.updatePersonAccount(prAccount);
            // 4. 增加账单流水
            mapper.insertAccountStatement(prAccountStatement);
        }
        // 5. 收费明细改为未支付状态
        mapper.updateExpense(details, SecurityUtils.getUser().getId());
    }
    // ... 卡表处理逻辑
}
```

**新系统 - revocation（撤销账单）：**
```java
@Override
@Transactional(rollbackFor = Exception.class)
public boolean revocation(String recordId) {
    int rows = baseMapper.revokeRecord(recordId);
    if (rows == 0) {
        throw new RuntimeException("撤销失败：记录不存在、已撤销/作废，或已跨月");
    }
    return true;
}
```

#### 风险标注

1. **🟡 YELLOW - 撤销逻辑简化**
   - 旧系统：完整的账户回滚 + 流水记录 + 费用状态更新
   - 新系统：直接数据库更新
   - 建议：补充账户余额回滚逻辑

---

### 1.4 SingleChargeService (单笔收费)

#### 核心业务逻辑对比

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **费用精度处理** | 复杂的精度计算（四舍五入/进位/截位） | 简化实现 | 🟡 MEDIUM |
| **收费操作** | 完整事务 + 账户扣费 + 交易记录 | 基本实现 | 🟢 LOW |
| **周期选择** | 复杂的周期计算逻辑 | 使用 Redis 缓存 | 🟢 LOW |

#### 风险标注

1. **🟡 YELLOW - 费用精度计算简化**
   - 旧系统：`round()` 方法实现逐步四舍五入
   - 新系统：未完整实现
   - 影响：费用计算可能有精度差异

---

### 1.5 ReconciliationService (对账管理)

#### 核心业务逻辑对比

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **账单下载** | 调用微信 API + 文件保存 + 数据库持久化 | 内存存储 | 🔴 HIGH |
| **对账执行** | 完整对账算法 + 差异记录持久化 | 内存存储 | 🔴 HIGH |
| **差异处理** | 数据库记录 + 状态更新 | 内存存储 | 🔴 HIGH |
| **定时对账** | @Scheduled 定时任务 | 未实现 | 🔴 HIGH |

#### 架构差异

**旧系统 - 数据库持久化架构：**
```
pr_wechat_bill（账单表）
    ↓
pr_reconciliation_diff（差异表）
    ↓
pr_transaction_record（本地交易表）
```

**新系统 - 内存存储架构：**
```
ConcurrentHashMap<String, Map<String, Object>> DIFF_STORE
ConcurrentHashMap<String, List<Map<String, String>>> BILL_STORE
```

#### 风险标注

1. **🔴 RED - 数据持久化缺失**
   - 新系统使用内存存储，服务重启后数据丢失
   - 旧系统有完整的数据库表设计
   - 建议：Phase 6 必须实现数据库持久化

2. **🔴 RED - 定时对账缺失**
   - 旧系统：`@Scheduled(cron = "0 0 3 * * ?")` 每天凌晨 3 点自动对账
   - 新系统：未实现
   - 影响：需要手动触发对账

---

### 1.6 Agent 模块 Service

#### 1.6.1 AgCompanyService (代理商公司)

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **新增公司** | 创建超管用户 + 创建角色 + 分配全部菜单 | 创建超管用户 + 创建角色 | 🟡 MEDIUM |
| **启用/禁用** | 更新公司状态 + 同步更新管理员状态 | 基本实现 | 🟢 LOW |
| **校验功能** | 手机号/编码/名称唯一性校验 | 基本实现 | 🟢 LOW |

#### 风险标注

1. **🟡 YELLOW - 菜单权限分配缺失**
   - 旧系统：`agentRoleMenuMapper.insertData(uuid)` 分配全部菜单
   - 新系统：未实现菜单分配
   - 影响：新创建的代理商无法访问任何菜单

#### 1.6.2 AgPropertyService (代理物业管理)

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **物业绑定** | 完整功能 | 基本绑定/解绑 | 🟢 LOW |
| **菜单分配** | 完整的权限控制 | 未实现 | 🔴 HIGH |
| **审核/启用状态** | 完整功能 | 未实现 | 🔴 HIGH |
| **自助机管理** | 完整功能 | 未实现 | 🔴 HIGH |
| **仪表分配** | 完整功能 | 未实现 | 🔴 HIGH |

#### 风险标注

1. **🔴 RED - 物业菜单管理缺失**
   - 旧系统：`menuInsertData()` 完整实现，包括删除旧关联 + 插入新关联 + 清除权限缓存
   - 新系统：未实现
   - 影响：无法为物业分配菜单权限

2. **🔴 RED - 自助机和仪表分配缺失**
   - 旧系统：完整的自助机档案和仪表分配功能
   - 新系统：完全未实现
   - 影响：代理商无法管理自助机和仪表

#### 1.6.3 AgUserService (代理商用户)

| 功能点 | 旧系统实现 | 新系统实现 | 风险等级 |
|--------|-----------|-----------|----------|
| **用户管理** | 完整 CRUD + 角色分配 | 完整 CRUD + 角色分配 | 🟢 LOW |
| **启用/禁用** | 完整功能 | 完整功能 | 🟢 LOW |
| **密码加密** | 自定义加密 | BCrypt 加密 | 🟢 IMPROVED |

#### 风险标注

1. **🟢 IMPROVED - 密码加密改进**
   - 旧系统：`InitPasswordUtil.InitPasswordUtil("123456")`
   - 新系统：`BCrypt.hashpw("123456")`
   - 评价：安全性提升

---

## 二、DB 层对比

### 2.1 Entity 字段对比

#### PrExpense (费用明细)

| 字段名 | 旧系统类型 | 新系统类型 | 差异说明 |
|--------|-----------|-----------|----------|
| id | String | String | ✅ 一致 |
| houseId | String | String | ✅ 一致 |
| itemGroup | String | String | ✅ 一致 |
| money | BigDecimal(0.0000) | BigDecimal | ⚠️ 精度定义差异 |
| standardPrice | BigDecimal(0.000000) | BigDecimal | ⚠️ 精度定义差异 |
| maxPrice | BigDecimal(0.000000) | BigDecimal(max_price) | ⚠️ 字段名差异 |
| preferential | BigDecimal(0.0000) | BigDecimal | ⚠️ 精度定义差异 |
| finalMoney | BigDecimal(0.0000) | BigDecimal | ⚠️ 精度定义差异 |

**风险标注：**
- 🟡 **MEDIUM** - 数据库字段精度定义可能影响金额计算精度

#### 关联对象差异

**旧系统：**
```java
private PrUserHouse prUserHouse;
private PrHouse prHouse;
private PrExpenseItem prExpenseItem;
```

**新系统：**
```java
// 无关联对象，使用 @TableField(exist = false) 查询字段
private String roomNum;
private String orgName;
```

**风险标注：**
- 🟢 **LOW** - 新系统使用扁平化设计，减少 N+1 查询问题

---

### 2.2 Mapper 方法对比

#### PrExpenseMapper

| 方法 | 旧系统 | 新系统 | 差异 |
|------|--------|--------|------|
| **费用生成** | `insertExpense()` | 使用 `saveBatch()` | ✅ 等价 |
| **阶梯单价** | `setStandardPriceJzmj()` 等 3 个方法 | `updateStepPrice()` 占位 | 🔴 功能缺失 |
| **金额计算** | `updateReceivable()` | `updateMoney()` 占位 | 🔴 功能缺失 |
| **优惠设置** | `setPreferentialBl()` + `setPreferentialJe()` | Service 层实现 | ✅ 等价 |
| **免收设置** | `setIsFree()` | Service 层实现 | ✅ 等价 |
| **报停/复供** | `setBaotingDateBl()` + `setFugongDate()` | Service 层实现 | 🟡 逻辑简化 |
| **滞纳金** | 4 种计算方法 | 未实现 | 🔴 功能缺失 |

#### Mapper XML 对比

**旧系统 - updateReceivable（金额公式计算）：**
```xml
<update id="updateReceivable">
    UPDATE pr_expense
    SET receivable = ${moneyFormula}
    WHERE id = #{id}
</update>
```

**新系统 - updateFormula（占位实现）：**
```xml
<update id="updateFormula">
    <!-- TODO: Phase 5b 完整实现 - 公式计算逻辑 -->
    UPDATE pr_expense SET receivable = money
    WHERE company_id = #{companyId} AND org_id = #{orgId} AND del_flag = '0'
</update>
```

**风险标注：**
- 🔴 **HIGH** - 动态 SQL 公式计算完全缺失

---

## 三、高风险问题汇总

### 3.1 数据一致性风险

| 问题 | 影响 | 优先级 |
|------|------|--------|
| 费用生成规则缺失（仅实现月度） | 无法生成按年/按季费用 | P0 |
| 阶梯单价计算未实现 | 费用计算不准确 | P0 |
| 滞纳金计算未实现 | 无法计算滞纳金 | P1 |
| 开户操作无并发保护 | 可能出现重复开户 | P0 |
| 对账数据内存存储 | 服务重启数据丢失 | P0 |

### 3.2 业务完整性风险

| 问题 | 影响 | 优先级 |
|------|------|--------|
| 押金管理未实现 | 押金业务完全不可用 | P0 |
| 导入模块大面积缺失 | 数据迁移功能受限 | P1 |
| Agent 菜单管理缺失 | 权限控制不完整 | P1 |
| 操作日志缺失 | 审计追踪能力下降 | P2 |

### 3.3 架构设计风险

| 问题 | 影响 | 优先级 |
|------|------|--------|
| Reconciliation 使用内存存储 | 不符合生产环境要求 | P0 |
| 定时对账任务缺失 | 需要手动触发对账 | P1 |
| 事务边界简化 | 部分场景可能数据不一致 | P2 |

---

## 四、行动建议

### 4.1 短期修复（P0 - 2 周内）

1. **实现费用生成规则**
   - 参考旧系统 `PrExpenseServiceImpl.insertDatall()` 第 233-457 行
   - 补充按年/按季/按半年的生成逻辑

2. **实现阶梯单价计算**
   - 参考旧系统 `updateStepPrice()` 和 `setStandardPriceJzmj()` 等方法
   - 实现建筑面积/使用面积/楼层三种阶梯类型

3. **实现开户并发保护**
   - 添加分布式锁或数据库唯一约束
   - 确保同一用户不会重复开户

4. **实现 Reconciliation 数据持久化**
   - 创建 `pr_wechat_bill` 和 `pr_reconciliation_diff` 表
   - 参考旧系统 `ReconciliationServiceImpl` 完整实现

### 4.2 中期完善（P1 - 1 个月内）

1. **实现滞纳金计算**
   - 参考旧系统 4 种计算方式
   - 实现 `updateLatefeeQs/Js/Zd/SJHC` 方法

2. **实现押金管理**
   - 参考旧系统 `saveDeposit()` 方法
   - 补充完整的押金业务逻辑

3. **实现 Agent 菜单管理**
   - 参考旧系统 `AgentPropertyServiceImpl.menuInsertData()`
   - 实现物业菜单分配功能

4. **实现定时对账任务**
   - 添加 `@Scheduled` 注解
   - 每天凌晨 3 点自动对账

### 4.3 长期优化（P2 - 持续改进）

1. **补充操作日志**
   - 费用优惠/免收/报停/复供/退费操作记录日志
   - 便于审计追踪

2. **完善导入模块**
   - 实现复杂校验逻辑
   - 补充 36 个缺失的导入端点

3. **优化事务边界**
   - 评估当前事务设置是否满足业务需求
   - 必要时添加事务隔离级别控制

---

## 五、总结

### 5.1 整体评估

- **PrExpense/Account 核心收费逻辑**: 🟡 **部分迁移** - 基本框架完成，复杂业务逻辑待补充
- **Reconciliation 对账模块**: 🔴 **骨架实现** - 需要完整重写
- **Agent 代理管理**: 🟡 **部分迁移** - 基础功能完成，权限控制待完善
- **SingleCharge 单笔收费**: 🟢 **基本完成** - 核心功能已实现

### 5.2 迁移完成度

| 模块 | 完成度 | 备注 |
|------|--------|------|
| PrExpenseService | 60% | 缺少复杂生成规则和计算逻辑 |
| PrAccountService | 70% | 缺少并发保护和押金管理 |
| PrTransactionRecordService | 80% | 基本功能完整 |
| SingleChargeService | 85% | 核心功能完整 |
| ReconciliationService | 20% | 需要完整重写 |
| AgCompanyService | 85% | 基本功能完整 |
| AgPropertyService | 40% | 缺少菜单和资源管理 |
| AgUserService | 90% | 功能完整 |

### 5.3 建议优先级

**P0（必须修复）：**
1. 费用生成规则
2. 阶梯单价计算
3. 开户并发保护
4. Reconciliation 数据持久化

**P1（尽快完善）：**
1. 滞纳金计算
2. 押金管理
3. Agent 菜单管理
4. 定时对账任务

**P2（持续改进）：**
1. 操作日志
2. 导入模块
3. 事务优化

---

**报告生成时间：** 2026-04-26
**Agent：** Agent 2
**审核范围：** Pr 收费运维 + Agent 代理管理 Service/DB 层
**下一步：** Agent 3 进行 Ht 热力调控 + Mt 仪表设备模块深度审核
