# Plan D: Agent 代理商模块迁移实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将旧系统的代理商管理模块（8 个未迁移 Controller）完整迁移到新系统

**Architecture:** 创建独立的 `sdkj-agent` 模块，或放入 `sdkj-thermal` 模块中（需先决策）。解决 `sys_company.nature` 与 `tenant_id` 多租户策略的映射问题。

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus, Sa-Token, RuoYi-Vue-Plus 多租户

**前置条件:** Plan A (P0 阻塞性修复) 已完成

---

## 核心决策（实施前必须确定）

### D1: 模块归属

| 方案 | 说明 | 优缺点 |
|------|------|--------|
| A. 创建 sdkj-agent | 独立模块，职责清晰 | 依赖 sdkj-system 和 sdkj-meter，模块间调用 |
| B. 放入 sdkj-thermal | 与物业/收费模块在一起 | 减少模块数量，但 thermal 模块已经很大 |
| C. 放入 sdkj-system | 代理商本质是用户/角色/公司的扩展 | 污染系统模块，不利于升级 |

**推荐方案 B**：代理商管理本质上是物业管理的上层（代理商管理物业公司），放在 sdkj-thermal 中最符合业务语义。

### D2: 多租户映射策略

| 旧系统概念 | 新系统对应 | 映射方式 |
|-----------|-----------|---------|
| `sys_company.nature='1'`（代理商） | `sys_tenant` | 代理商 = 租户 |
| `sys_company.nature='2'`（物业公司） | `sys_tenant` | 物业公司 = 子租户或平级租户 |
| `sys_company.parent_id`（层级关系） | 需新增字段 | 在 `sys_tenant` 表加 `parent_id` |

---

## 修复清单（来自审核报告 04-agent-management-audit.md）

### 未迁移 Controller（8 个）

| Controller | 功能 | 优先级 |
|-----------|------|--------|
| AgentCompanyController | 代理商公司管理（树形结构） | P0 |
| AgentUserController | 代理商员工管理 | P0 |
| AgentRoleController | 代理商角色管理 | P0 |
| AgentPropertyController | 物业公司管理（代理商旗下） | P1 |
| AgentPropertyMenuController | 物业公司菜单分配 | P1 |
| AccessCodeController | 仪表编码获取 | P2 |
| AgAutoVersionController | 自助机版本管理 | P2 |
| AgReaderParamController | 自助机读卡器参数 | P2 |

---

## Task 0: 架构决策和准备工作

- [ ] **Step 1: 确定模块归属方案（A/B/C）**

与产品/技术负责人确认代理商模块放在哪里。

- [ ] **Step 2: 确定多租户映射策略**

确认 `sys_company.nature` 与 `tenant_id` 的映射关系。

- [ ] **Step 3: 评估 sys_tenant 表是否需要添加 parent_id 字段**

如果代理商-物业需要层级关系，需修改 `sys_tenant` 表结构。

---

## Task 1: 迁移 AgentCompanyController（代理商公司管理）

**Files:**
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/controller/AgentCompanyController.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/IAgentCompanyService.java`
- Create: `sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/service/impl/AgentCompanyServiceImpl.java`

- [ ] **Step 1: 读取旧系统完整实现**

读取:
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/controller/AgentCompanyController.java`
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/service/impl/AgentCompanyServiceImpl.java`
- `D:/chonggou/thermal-balance-backend/src/main/java/com/thermal/mapper/AgentCompanyMapper.java`

- [ ] **Step 2: 适配多租户模型**

将旧系统的 `sys_company.nature='1'` 逻辑改为基于 `sys_tenant` 的查询。核心 CRUD 方法：
- 树形公司列表
- 公司详情
- 公司新增/修改
- 手机号/编码/名称唯一性校验
- 启用/禁用

- [ ] **Step 3: 编译验证并提交**

```bash
git commit -m "feat(agent): migrate AgentCompanyController with tenant adaptation"
```

---

## Task 2: 迁移 AgentUserController（代理商员工管理）

**Files:**
- Create: Controller / Service / Mapper

- [ ] **Step 1: 读取旧系统实现**
- [ ] **Step 2: 适配新系统用户管理模型**
- [ ] **Step 3: 编译验证并提交**

---

## Task 3: 迁移 AgentRoleController（代理商角色管理）

**Files:**
- Create: Controller / Service / Mapper

- [ ] **Step 1: 读取旧系统实现**
- [ ] **Step 2: 适配 Sa-Token 权限体系**
- [ ] **Step 3: 编译验证并提交**

---

## Task 4: 迁移 AgentPropertyController（物业公司管理）

**Files:**
- Create: Controller / Service / Mapper

- [ ] **Step 1: 读取旧系统实现**
- [ ] **Step 2: 适配代理商-物业层级关系**
- [ ] **Step 3: 编译验证并提交**

---

## Task 5: 迁移 AgentPropertyMenuController（物业菜单分配）

- [ ] **Step 1: 读取旧系统实现**
- [ ] **Step 2: 适配新系统菜单权限体系**
- [ ] **Step 3: 编译验证并提交**

---

## Task 6: 迁移 AccessCodeController（仪表编码获取）

- [ ] **Step 1: 读取旧系统实现**
- [ ] **Step 2: 整合到 sdkj-meter 模块的服务层**
- [ ] **Step 3: 编译验证并提交**

---

## Task 7: 迁移 AgAutoVersionController 和 AgReaderParamController**

- [ ] **Step 1: 创建 ag_auto_version 和 ag_reader_param 表**
- [ ] **Step 2: 迁移 Controller 和 Service**
- [ ] **Step 3: 编译验证并提交**
