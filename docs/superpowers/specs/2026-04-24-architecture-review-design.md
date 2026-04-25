# 架构迁移审核设计文档

**日期**: 2026-04-24
**主题**: 旧系统（thermal-balance-backend）→ 新系统（thermal-platform-new）全面审核

## 概述

对已完成架构迁移的智慧供热平台进行全面审核，涵盖迁移缺口分析、代码质量审核、功能等价验证三个维度。

## 审核架构

三个 Agent 并行执行，独立输出报告，最终由 Orchestrator 汇总。

```
Orchestrator → [Gap Analyst] ──┐
            → [Code Auditor] ──┤→ 汇总总报告
            → [Equivalence Tester]─┘
```

---

## Agent 1: Gap Analyst（迁移缺口分析）

### 输入源
- 老系统: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\`
- 老系统: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\entity\`
- 老系统: `D:\chonggou\thermal-balance-backend\src\main\resources\mapper\`
- 新系统: `D:\chonggou\thermal-platform-new\sdkj-modules\**\controller\`
- 新系统: `D:\chonggou\thermal-platform-new\script\sql\`

### 执行步骤
1. 扫描老系统所有 controller，按业务域分组
2. 扫描新系统所有 controller，建立已迁移清单
3. 对比差异，生成未迁移模块列表
4. 对每个未迁移模块评估：
   - **复杂度**: 通过 Entity 数量、依赖关系判断
   - **业务优先级**: 核心业务 > 辅助功能 > 边缘功能
   - **迁移工作量**: 小（< 1天）/ 中（1-3天）/ 大（> 3天）

### 业务域分类
| 域 | 前缀 | 说明 |
|---|------|------|
| 系统管理 | `Sys*` | 用户/角色/菜单/部门/字典/租户 |
| 仪表管理 | `Mt*` | 电表/水表/热力表/燃气表/集中器/温控器 |
| 热力调控 | `Ht*` | 策略/任务/指令/报警/报修 |
| 物业收费 | `Pr*` (charge-related) | 收费标准/费用/交易/账户 |
| 房屋管理 | `Pr*` (building-related) | 房屋/楼栋/单元/家庭 |
| 供热数据 | `PrHeat*` | 热量采集/日/月/站/阀门 |
| 导入导出 | `PrImport*` | 基础数据/热量/阀门/历史记录 |
| 巡检维修 | `PrInspection*`, `PrRepair*` | 巡检计划/记录/人员 |
| 微信集成 | `Wechat*`, `Wx*` | 支付/认证/小程序 |
| 停车管理 | `Pm*` | 车位/道闸/停车场 |
| 商业推广 | `Bn*` | 商户/商品/推广 |
| 自动化 | `Ag*` | 自动版本/设备 |

### 输出格式
```
迁移缺口清单:
| 序号 | 模块名 | 业务域 | Entity数 | 复杂度 | 优先级 | 工作量 | 依赖关系 | 说明 |
|------|--------|--------|----------|--------|--------|--------|----------|------|
```

---

## Agent 2: Code Auditor（架构质量审核）

### 输入源
- 新系统全部 Java 源码: `D:\chonggou\thermal-platform-new\sdkj-modules\`
- 目标架构规范: `D:\chonggou\thermal-platform-new\CLAUDE.md`

### 审核维度

1. **包结构规范**
   - controller/service/service.impl/mapper/domain/domain.vo 是否齐全且对齐
   - 是否存在错误的包路径（如残留 `com.thermal`、`org.dromara`）

2. **MyBatis-Plus 模式**
   - Mapper 是否继承 `BaseMapperPlus`
   - 是否正确声明 `@AutoMapper` 类型转换
   - 是否正确使用 `@TableField`、`@TableId` 注解

3. **事务管理**
   - 所有写操作（增删改）是否包裹 `@Transactional(rollbackFor = Exception.class)`
   - 是否存在遗漏的回滚场景

4. **租户隔离**
   - 非系统表是否正确标记 `@TenantLine`
   - 租户排除表是否合理

5. **安全审计**
   - SQL 注入风险（拼接 SQL、动态表名）
   - 越权访问（缺少 `@SaCheckLogin`、`@SaCheckPermission`）
   - 敏感信息泄露（密码、密钥硬编码）
   - XSS 防护（用户输入过滤）

6. **残留旧代码**
   - `com.thermal` 包引用
   - JPA/QueryDSL 残留模式（`@Entity`、`@Repository`）
   - 旧 Spring Boot 2.x API 残留

7. **代码异味**
   - 空方法/空实现
   - TODO/FIXME 标记
   - 硬编码常量、魔法值
   - 未使用的 import

8. **BO/VO 模式**
   - 是否正确分离 Request(Bo)/Response(Vo) 对象
   - MapStruct 转换是否正确配置
   - Controller 是否直接使用 Entity（应避免）

### 问题分级
| 级别 | 含义 | 处理要求 |
|------|------|----------|
| P0 | 阻塞 - 功能缺失或安全漏洞 | 必须立即修复 |
| P1 | 严重 - 可能导致运行时错误 | 本轮迭代修复 |
| P2 | 建议 - 违反架构规范 | 计划内修复 |
| P3 | 优化 - 代码风格/可读性 | 择机修复 |

### 输出格式
```
代码质量报告:
| 模块 | 文件 | 问题级别 | 问题描述 | 建议修复方案 |
|------|------|----------|----------|-------------|
```

---

## Agent 3: Equivalence Tester（功能等价验证）

### 输入源
- 老系统全部 controller: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\`
- 新系统全部 controller: `D:\chonggou\thermal-platform-new\sdkj-modules\**\controller\`
- 老系统 service 实现: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\service\`
- 新系统 service 实现: `D:\chonggou\thermal-platform-new\sdkj-modules\**\service\`

### 执行步骤
1. 提取老系统所有 API 端点（`@RequestMapping`、`@GetMapping` 等），建立路由表
2. 提取新系统所有 API 端点，建立路由表
3. 逐端点对比匹配
4. 重点业务逻辑深度对比：
   - 分页查询逻辑（数据库分页 vs 内存分页）
   - 多表 JOIN 查询
   - 事务操作（批量写入、级联更新）
   - 权限校验注解（`@SaCheckPermission`）
   - 参数校验（`@Validated`、`@NotBlank`）

### 判定标准
| 状态 | 条件 |
|------|------|
| ✅ 等价 | 路径匹配、参数完整、核心逻辑一致、权限校验存在 |
| ⚠️ 部分等价 | 路径匹配但缺少部分参数/逻辑，或权限校验不完整 |
| ❌ 缺失 | 老系统有但新系统完全没有对应端点 |
| 🔴 有差异 | 路径匹配但行为不一致（如返回值结构不同、业务逻辑改变） |

### 输出格式
```
功能等价对比:
| 老系统端点 | 新系统端点 | 状态 | 差异项 | 风险等级 |
|-----------|-----------|------|--------|---------|
```

---

## 汇总总报告结构

三个 Agent 完成后，Orchestrator 生成总报告：

```
# 架构迁移审核总报告

## 一、执行摘要
- 审核范围
- 关键发现
- 风险等级分布

## 二、迁移缺口清单（来自 Gap Analyst）
- 未迁移模块清单（按优先级排序）
- 迁移路线图建议

## 三、代码质量报告（来自 Code Auditor）
- 问题统计（按级别/模块）
- Top 5 关键问题
- 架构规范遵守度

## 四、功能等价验证（来自 Equivalence Tester）
- 端点覆盖率统计
- 差异项详细列表
- 高风险差异

## 五、行动建议
- 按优先级排序的后续任务清单
- 工作量估算
```

## 输出位置

`docs/superpowers/specs/2026-04-24-architecture-review-report.md`
