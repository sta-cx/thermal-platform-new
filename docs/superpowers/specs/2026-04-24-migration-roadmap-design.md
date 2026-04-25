# 迁移路线图设计文档

**日期**: 2026-04-24
**主题**: thermal-balance-backend → thermal-platform-new 全量迁移路线图
**前置依据**: 架构审核总报告（2026-04-24-architecture-review-report.md）

## 核心迁移原则

1. **不逐行复刻**：在保证业务逻辑等价的前提下，优先采用新系统架构模式（MyBatis-Plus、Bo/Vo 分离、MapStruct、RuoYi 基类），以更简洁高效的方式实现相同功能
2. **严格优先级**：P0/P1 修复 → 架构规范化 → 核心业务迁移 → 辅助功能 → 清理收尾
3. **每阶段验证**：每个批次完成后执行 `mvn compile` + `mvn spring-boot:run -pl sdkj-admin`，确认编译通过且启动无异常
4. **Bo/Vo 先行**：新迁移的模块必须遵循 Bo/Vo 分离模式，Controller 不直接使用 Entity

## 总体结构

```
Stage 0 (1-2h)     Stage 1 (2-3d)      Stage 2 (15-20d)     Stage 3 (5-8d)    Stage 4 (2-3d)
┌──────────┐      ┌──────────────┐     ┌────────────────┐   ┌──────────────┐   ┌──────────┐
│ P0/P1    │      │ Bo 类补全    │     │ 核心业务迁移    │   │ 辅助功能迁移  │   │ 清理收尾  │
│ 代码修复  │  →   │ Permission   │  →  │ 房屋+配表+抄表  │→  │ 巡检/导入/微信 │→  │ 边缘/Quartz│
│ 8 项     │      │ 架构规范化    │     │ ~15 模块        │   │ ~15 模块     │   │ ~10 模块  │
└──────────┘      └──────────────┘     └────────────────┘   └──────────────┘   └──────────┘
     │                   │                    │                    │                  │
  compile+run        compile+run          compile+run          compile+run       compile+run
```

---

## Stage 0：P0/P1 代码修复（1-2 小时）

| # | 文件 | 修复内容 |
|---|------|----------|
| 0.1 | `HtTasksController.java:87` | `"com.thermal.job.ControlJob"` → `"org.sdkj.job.ControlJob"` |
| 0.2 | `PrAutoMachineController.java:111-126` | 回调端点添加 `@SaIgnore` + 签名校验占位 |
| 0.3 | `PrAutoMachineController.java` | 整个 Controller 添加 `@Deprecated` + 从 Swagger 文档隐藏 |
| 0.4 | `application.yml:104` | `jwt-secret-key` 改为 `${JWT_SECRET_KEY:sdkj2024}` |
| 0.5 | `application.yml:182-185` | RSA 密钥改为 `${RSA_PRIVATE_KEY:...}` / `${RSA_PUBLIC_KEY:...}` |
| 0.6 | `HeatMeterControl.java:45-51` | `new HttpEntity<>(payload, headers)` 修复 HTTP 头未附加 |
| 0.7 | `HtTasksController.java` edit/remove | 添加 jobStatus 运行状态校验 |
| 0.8 | `PrUseCardLogServiceImpl.java:32` | `changeValveStatus()` 添加 `@Transactional(rollbackFor = Exception.class)` |

**验证**：`mvn compile` + 启动无 ClassNotFoundException 或 Bean 创建失败。

---

## Stage 1：Bo 类补全 + @SaCheckPermission（2-3 天）

### 1.1 Bo 类创建（36 个）

**sdkj-meter（10 个）**：MtMeterVendorBo、MtMeterSortBo、MtElectricArchiveBo、MtWaterArchiveBo、MtHeatArchiveBo、MtGasArchiveBo、MtCentratorArchiveBo、MtTcArchiveBo、MtTcValveBo、MtFormulaFileBo

**sdkj-thermal（26 个）**：HtTasksBo、HtTasksPerformBo、HtStrategyBo、HtStrategySubBo、HtHouseStrategyBo、HtInstructionBo、HtAlertBo、HtRepairBo、HtScopeBo、PrAccountBalanceBo、PrBillingNotesBo、PrExpenseBo、PrExpenseItemBo、PrHouseExpenseBo、PrHouseBo、PrOptionsBo、PrOptionsHeatBo、PrPrintTemplateBo、PrStandardBo、PrStandardPriceBo、PrTransactionRecordBo、PrTransactionRecordSubBo、PrUseCardLogBo、PrUserBo、PmParkingSpaceBo、SingleChargeBo

**Bo 类规范**：
- 放在 `domain/bo/` 包下
- 只包含写操作需要的字段（去掉 createBy/createTime/updateBy/updateTime/tenantId 等框架字段）
- 添加 `@NotBlank`/`@NotNull` 校验注解
- 使用 `@AutoMapper(target = Entity.class)` 映射
- Controller 的 `@RequestBody` 从 Entity 改为 Bo

### 1.2 @SaCheckPermission 补全

**sdkj-meter 权限映射**：
- MtMeterVendorController → `thermal:meter:vendor`
- MtMeterSortController → `thermal:meter:sort`
- MtElectricArchiveController → `thermal:meter:electric`
- MtWaterArchiveController → `thermal:meter:water`
- MtHeatArchiveController → `thermal:meter:heat`
- MtGasArchiveController → `thermal:meter:gas`
- MtCentratorArchiveController → `thermal:meter:centrator`
- MtTcArchiveController → `thermal:meter:tc`
- MtTcValveController → `thermal:meter:valve`
- MtFormulaFileController → `thermal:meter:formula`
- AgentMeterController → `thermal:meter:agent`

**sdkj-thermal 权限映射**：
- Ht* 系列 → `thermal:ht:*`
- Pr* 系列 → `thermal:property:*`
- PrHeatControlController → `thermal:ht:control`（硬件操作独立权限）

**操作权限后缀**：`:list`、`:query`、`:add`、`:edit`、`:remove`

**验证**：编译通过 + 默认账号登录验证 Bo 参数接收和权限拦截。

---

## Stage 2：核心业务迁移（15-20 天）

### 批次 2A：房屋/楼宇/单元/组织（3-5 天）

| # | 模块 | Entity | 关键端点 | 依赖 |
|---|------|--------|---------|------|
| 2A.1 | PrBuildingController | 2 | 楼宇 CRUD、层级查询 | 无 |
| 2A.2 | PrFamilyController | 1 | 家庭成员 CRUD | PrHouse |
| 2A.3 | PrHouseController | 5 | 房屋 CRUD、导入导出、关联设置 | PrBuilding |
| 2A.4 | PrCompanyController | 4 | 组织机构树 | PrBuilding |

**路由前缀**：`/thermal/property/building`、`/thermal/property/house`、`/thermal/property/family`、`/thermal/property/company`

**SQL 脚本**：`script/sql/phase6_house_tables.sql`

### 批次 2B：配表九模块（8-10 天）

| # | 模块 | Entity | 核心能力 |
|---|------|--------|---------|
| 2B.1 | PrHeatArchiveController | 13 | 房屋热表配表、巡测、充值 |
| 2B.2 | PrHeatValveArchiveController | 9 | 户间阀门（SDNB、写卡、开/关/开度） |
| 2B.3 | PrHeatCommandValveArchiveController | 12 | 户间控制阀门（NB/MBus、电信/移动平台） |
| 2B.4 | PrHeatHotArchiveController | 10 | 房屋热量表配表 |
| 2B.5 | PrHeatCommandUnitValveArchiveController | 10 | 单元控制阀门 |
| 2B.6 | PrHeatDtuArchiveController | 8 | DTU 采集器配表 |
| 2B.7 | PrHeatUnitHotArchiveController | 7 | 单元热表配表 |
| 2B.8 | PrHeatUnitValveArchiveController | 7 | 单元阀门配表 |
| 2B.9 | PrHeatTempArchiveController | 5 | 温采器配表 |

**迁移要点**：
- 抽取通用导入导出框架（替代老系统 PrImportHandler），减少 9 个模块的重复代码
- 设备同步到电信采集平台的逻辑封装为独立 Service
- 阀门控制指令复用 HtTasksPerform 任务执行体系
- 路由前缀：`/thermal/ht/heat-archive`、`/thermal/ht/valve-archive` 等

**SQL 脚本**：`script/sql/phase6_archive_tables.sql`

### 批次 2C：日/月/抄表（2-3 天）

| # | 模块 | Entity | 核心能力 |
|---|------|--------|---------|
| 2C.1 | PrHeatDailyController | 6 | 热表日表生成与查询 |
| 2C.2 | PrHeatMonthController | 5 | 热表月表生成与查询 |
| 2C.3 | PrHeatReadingController | 7 | 实时/历史/温度/阀门抄表数据 |

**SQL 脚本**：`script/sql/phase6_reading_tables.sql`

### 批次 2D：已迁移模块增强（1-2 天）

补充等价报告中 ⚠️ 部分等价的端点（均为老系统原有功能）：

| # | 内容 | 来源 |
|---|------|------|
| 2D.1 | HtTasksController `list` 扩展查询参数（3 → 20+） | 等价报告 ⚠️ |
| 2D.2 | HtTasksController 补充 `deviceList`、`houseChangeData`、`unitChangeData` 端点 | 等价报告 ⚠️ |
| 2D.3 | PrAccountController 补充押金管理、数据导入、Excel 导出 | 等价报告 ⚠️ |
| 2D.4 | PrTransactionRecordController 补充退费、水电气查询、月度统计 | 等价报告 ⚠️ |
| 2D.5 | PrExpenseController 补充车位费用、批量生成、微信费用查询 | 等价报告 ⚠️ |

**验证**：每批次 `mvn compile` + 启动验证。

---

## Stage 3：辅助功能迁移（5-8 天）

### 批次 3A：热力站 + 巡检 + 报修（2-3 天）

| # | 模块 | Entity | 端点 |
|---|------|--------|------|
| 3A.1 | PrHeatStationController | 2 | 8 |
| 3A.2 | PrHeatStationPartitionController | 3 | 8 |
| 3A.3 | PrInspectionPlanController | 2 | 7 |
| 3A.4 | PrInspectionPersonController | 1 | 6 |
| 3A.5 | PrInspectionRecordController | 3 | 1 |
| 3A.6 | PrRepairRecordController | 3 | 12 |
| 3A.7 | PrRepairPersonController | 1 | 6 |
| 3A.8 | RepairController | 1 | 8 |

### 批次 3B：导入模块群（2-3 天）

先抽取通用导入框架，再逐个实现。

| # | 模块 | 说明 |
|---|------|------|
| 3B.1 | PrImportBasicDataController | 基础数据导入 |
| 3B.2 | PrImportHeatController | 热表数据导入 |
| 3B.3 | PrImportHeatTempController | 温采器数据导入 |
| 3B.4 | PrImportUnitHeatController | 单元热表导入 |
| 3B.5 | PrImportUnitValveController | 单元阀门导入 |
| 3B.6 | PrImportValveController | 阀门数据导入 |
| 3B.7 | PrImportHistoryController | 导入历史 |
| 3B.8 | PrImportRecordController | 导入记录管理 |

### 批次 3C：微信生态 + 对账（1-2 天）

| # | 模块 | 说明 |
|---|------|------|
| 3C.1 | WechatAuthController | 微信用户认证/绑定 |
| 3C.2 | WechatPayController | 微信支付/退款 |
| 3C.3 | WxPortalController | 微信公众号消息推送 |
| 3C.4 | WxMaUserController | 小程序用户 |
| 3C.5 | WxMaMediaController | 小程序媒体 |
| 3C.6 | ReconciliationController | 微信对账 |

**验证**：每批次 `mvn compile` + 启动验证。

---

## Stage 4：清理收尾（2-3 天）

### 批次 4A：边缘功能补全（1 天）

| # | 模块 | Entity | 说明 |
|---|------|--------|------|
| 4A.1 | PrNoticeController | 1 | 通知公告 |
| 4A.2 | PrSchedulingController | 1 | 排班管理 |
| 4A.3 | PrPetController | 1 | 宠物管理 |
| 4A.4 | PrAbnormalRecordController | 1 | 异常记录 |
| 4A.5 | PrRegionalController | 3 | 区域数据统计 |
| 4A.6 | PrStrategyController | 1 | 物业策略 |
| 4A.7 | AreaController | 4 | 省市区街道（可用第三方库替代） |

### 批次 4B：Quartz 任务调度集成（1 天）

| # | 内容 |
|---|------|
| 4B.1 | sdkj-job 模块 Quartz 配置和 Job 管理实现 |
| 4B.2 | ControlJob 实现调控任务执行 |
| 4B.3 | ThermalJob/ThermalJobManager 补充调度逻辑 |

### 批次 4C：架构清理（0.5 天）

| # | 内容 |
|---|------|
| 4C.1 | 补充 `@TableLogic` 软删除注解（25 个 Entity） |
| 4C.2 | 提取魔法值为常量/枚举 |
| 4C.3 | 删除或实现空 Controller |
| 4C.4 | sdkj-job BillDto 包名 `entity` → `domain` |
| 4C.5 | AuthController 欢迎消息更新 |

### 批次 4D：最终验证（0.5 天）

- 全量 `mvn compile` + 启动验证
- 确认所有模块注册到 Spring 容器
- 菜单权限数据与 `sdkj-init.sql` 同步
- 交叉检查等价报告 🔴 有差异项是否已处理

---

## 不需要迁移的模块

以下老系统模块已被新系统架构替代，无需迁移：

| 老模块 | 替代方案 |
|--------|----------|
| AgentCompany/Property/Role/User | 新系统多租户架构（tenant_id） |
| MenuController/RoleController/PrRoleController | SysMenuController/SysRoleController |
| SysCompanyController/SysUserController | SysDeptController/SysUserController |
| SysDictController | SysDictDataController + SysDictTypeController |
| SaOAuth2ServerController | Sa-Token 认证 |
| PropertyMenuController | 新系统权限模型 |
| ChargeDetailStateNameController | 空控制器 |

## 附录

| 文档 | 路径 |
|------|------|
| 架构审核总报告 | `docs/superpowers/specs/2026-04-24-architecture-review-report.md` |
| 代码审计报告 | `AUDIT_REPORT.md` |
| 等价验证报告 | `docs/equivalence-test-report.md` |
| 审核设计文档 | `docs/superpowers/specs/2026-04-24-architecture-review-design.md` |
