# 迁移补齐实施计划

> **For agentic workers:** 使用 superpowers:subagent-driven-development 或 superpowers:executing-plans 执行此计划。

**Goal:** 补齐审核报告中发现的所有缺失模块和端点，完成从 thermal-balance-backend 到 thermal-platform-new 的完整迁移。

**Architecture:** 遵循新系统已建立的四层架构模式（Controller → Service → Mapper → Domain），使用 BO/VO 分层、Sa-Token 权限、@Log 审计日志、RESTful API 设计。每个缺失模块直接参照旧系统源码实现。

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus 3.5 (BaseMapperPlus), Sa-Token 1.44, MapStruct-Plus (@AutoMapper), Lombok

---

## 新系统代码模式（所有 Agent 必须遵循）

### Controller 模式
```java
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/xxx/yyy")
public class XxxController extends BaseController {
    private final IXxxService xxxService;

    @SaCheckPermission("thermal:xxx:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<XxxVo> list(PageQuery pageQuery) { ... }

    @SaCheckPermission("thermal:xxx:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<XxxVo> getById(@PathVariable String id) { ... }

    @SaCheckPermission("thermal:xxx:add")
    @SaCheckLogin
    @Log(title = "XXX", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody XxxBo bo) {
        Xxx entity = MapstructUtils.convert(bo, Xxx.class);
        return toAjax(xxxService.save(entity));
    }

    @SaCheckPermission("thermal:xxx:edit")
    @SaCheckLogin
    @Log(title = "XXX", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody XxxBo bo) { ... }

    @SaCheckPermission("thermal:xxx:remove")
    @SaCheckLogin
    @Log(title = "XXX", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) { ... }
}
```

### Domain 模式
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("table_name")
@AutoMapper(target = XxxVo.class)
public class Xxx extends BaseEntity {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    // 字段...
}
```

### VO/BO 模式
- VO: `@AutoMapper(target = Xxx.class)` + 字段与 Domain 一致
- BO: `@AutoMapper(target = Xxx.class, reverseConvertGenerate = false)` + @Validated 校验注解

### Mapper 模式
```java
public interface XxxMapper extends BaseMapperPlus<Xxx, XxxVo> {}
```

### Service 模式
- 接口: `extends IService<Xxx>` + 自定义方法（返回 Vo 类型）
- 实现: `extends ServiceImpl<XxxMapper, Xxx>` + `@Service` + `@RequiredArgsConstructor`

### 关键依赖
- `org.sdkj.common.core.domain.R`
- `org.sdkj.common.mybatis.core.page.PageQuery` / `TableDataInfo`
- `org.sdkj.common.mybatis.core.mapper.BaseMapperPlus`
- `org.sdkj.common.mybatis.core.domain.BaseEntity`
- `org.sdkj.common.web.core.BaseController`
- `org.sdkj.common.core.utils.MapstructUtils`
- `cn.dev33.satoken.annotation.SaCheckLogin` / `SaCheckPermission`

---

## Phase 1: Agent 代理管理模块（最高优先级，完全未迁移）

### Task 1.1: 读取旧系统全部 Agent 相关源码

**Files:**
- 读取: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentCompanyController.java`
- 读取: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentUserController.java`
- 读取: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentRoleController.java`
- 读取: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentMeterController.java`
- 读取: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentPropertyController.java`
- 读取: `D:\chonggou\thermal-balance-backend\src\main\java\com\thermal\controller\AgentPropertyMenuController.java`
- 读取对应 Service/ServiceImpl、Entity、Mapper

- [ ] **Step 1:** 读取所有旧系统 Agent 模块的 Controller + ServiceImpl + Entity + Mapper
- [ ] **Step 2:** 分析每个 Controller 的端点清单和业务逻辑
- [ ] **Step 3:** 确认新系统中是否有部分已存在（AgAutoVersion, AgReaderParam 已有）

### Task 1.2: 实现 AgentCompany 模块

**Files:**
- Create: `sdkj-thermal/.../controller/AgCompanyController.java`
- Create: `sdkj-thermal/.../service/IAgCompanyService.java`
- Create: `sdkj-thermal/.../service/impl/AgCompanyServiceImpl.java`
- Create: `sdkj-thermal/.../mapper/AgCompanyMapper.java`
- 注意: Agent Company 复用 SysCompany 实体，无需新 Domain

- [ ] **Step 1:** 创建 AgCompanyController（pageList, getDataById, insertData, updateData, deleteData, verifyTele, verifyCode, verifyName, startUsing, endUsing）
- [ ] **Step 2:** 创建 IAgCompanyService + AgCompanyServiceImpl
- [ ] **Step 3:** 创建 AgCompanyMapper

### Task 1.3: 实现 AgentUser 模块

**Files:**
- Create: `sdkj-thermal/.../controller/AgUserController.java`
- Create: `sdkj-thermal/.../service/IAgUserService.java`
- Create: `sdkj-thermal/.../service/impl/AgUserServiceImpl.java`
- Create: `sdkj-thermal/.../mapper/AgUserMapper.java`

- [ ] **Step 1:** 创建 AgUserController（pageList, checkTele, insertData, updateData, deleteData, startUsing, endUsing, saveUserRole）
- [ ] **Step 2:** 创建 IAgUserService + AgUserServiceImpl
- [ ] **Step 3:** 创建 AgUserMapper

### Task 1.4: 实现 AgentRole 模块

**Files:**
- Create: `sdkj-thermal/.../controller/AgRoleController.java`
- Create: `sdkj-thermal/.../service/IAgRoleService.java`
- Create: `sdkj-thermal/.../service/impl/AgRoleServiceImpl.java`
- Create: `sdkj-thermal/.../mapper/AgRoleMapper.java`

- [ ] **Step 1:** 创建 AgRoleController + Service + Mapper

### Task 1.5: 实现 AgentProperty + AgentPropertyMenu 模块

**Files:**
- Create: `sdkj-thermal/.../controller/AgPropertyController.java`
- Create: `sdkj-thermal/.../controller/AgPropertyMenuController.java`
- 对应 Service/Mapper

- [ ] **Step 1:** 创建 AgPropertyController + Service + Mapper
- [ ] **Step 2:** 创建 AgPropertyMenuController + Service + Mapper

### Task 1.6: 实现 AgentMeter 模块

**Files:**
- Create: `sdkj-thermal/.../controller/AgentMeterController.java` (注意: 审计报告显示新系统已有 AgentMeterController 在 sdkj-meter 模块)

- [ ] **Step 1:** 确认新系统 sdkj-meter 中是否已有 AgentMeterController
- [ ] **Step 2:** 如已有，补充缺失端点；如没有，创建完整模块

### Task 1.7: Phase 1 编译验证

- [ ] **Step 1:** `mvn clean compile -pl sdkj-modules/sdkj-thermal`
- [ ] **Step 2:** 修复编译错误
- [ ] **Step 3:** `git add` + `git commit`

---

## Phase 2: 运维管理模块（巡检/维修/排班/通知）

### Task 2.1: 实现巡检管理（PrInspectionPerson + PrInspectionPlan + PrInspectionRecord）

- [ ] 读取旧系统 3 个 Controller + ServiceImpl
- [ ] 确认新系统 Domain 是否已存在
- [ ] 创建 Controller + Service + Mapper（如 Domain 已存在则复用）
- [ ] 编译验证

### Task 2.2: 实现维修管理（PrRepairPerson + PrRepairRecord）

- [ ] 读取旧系统源码，创建对应模块
- [ ] 编译验证

### Task 2.3: 实现排班管理（PrScheduling）

- [ ] 创建 PrScheduling 四层模块

### Task 2.4: 实现通知管理（PrNotice）

- [ ] 创建 PrNotice 四层模块

### Task 2.5: Phase 2 编译验证 + Commit

- [ ] `mvn clean compile`
- [ ] `git commit`

---

## Phase 3: 数据导入模块（8 个 PrImport Controller）

### Task 3.1: 实现基础数据导入（PrImportBasicData）

- [ ] 创建导入 Controller，复用 EasyExcel 工具类

### Task 3.2: 实现仪表/阀门导入（PrImportHeat, PrImportUnitHeat, PrImportValve, PrImportUnitValve, PrImportHeatTemp）

- [ ] 创建 5 个导入 Controller

### Task 3.3: 实现历史导入 + 记录（PrImportHistory, PrImportRecord, PrImportAuthorizationCode）

- [ ] 创建 3 个 Controller

### Task 3.4: Phase 3 编译验证 + Commit

---

## Phase 4: 微信/支付模块核心逻辑补齐

### Task 4.1: 集成微信 SDK + 实现授权登录

- [ ] 添加 weixin-java-miniapp 依赖
- [ ] 实现 WechatAuthController 业务逻辑

### Task 4.2: 实现微信支付 + 回调

- [ ] 实现 WechatPayController 支付/回调/退款
- [ ] 添加签名验证

### Task 4.3: 实现对账 + 小程序用户/媒体

- [ ] 实现 ReconciliationController 对账逻辑
- [ ] 补齐 WxMaUserController / WxMaMediaController

### Task 4.4: Phase 4 编译验证 + Commit

---

## Phase 5: Pr-设备档案骨架填充 + 缺失端点

### Task 5.1: 填充 PrHeatControl 骨架

- [ ] 实现 IoT 控制指令下发逻辑

### Task 5.2: 填充 PrAutoMachine + PrAbnormalRecord 骨架

- [ ] 实现自动抄表机管理
- [ ] 实现异常记录管理

### Task 5.3: 补齐设备档案缺失端点

- [ ] 补充导入导出、数据同步端点

### Task 5.4: Phase 5 编译验证 + Commit

---

## Phase 6: PrHouse 缺失端点 + 其他零散补齐

### Task 6.1: PrHouse 缺失端点（~20 个）

- [ ] 实现导入导出、孤岛户管理、微信绑定、高级查询等

### Task 6.2: PrBuilding/PrHouseChange/PrOptionsHeat/PrPrintTemplate 缺失端点

- [ ] 补齐各 Controller 的个别缺失端点

### Task 6.3: Ht 模块缺失端点

- [ ] HtScopeDtuController（DTU 范围管理）
- [ ] HtTasksPerformLsController（任务执行历史）
- [ ] HtTasksController 缺失端点

### Task 6.4: Phase 6 编译验证 + 最终 Commit

---

## 执行约束

- 每批最多 3 个并行 Agent
- 每个 Agent 负责一个 Phase 中的若干 Task
- 每个 Phase 完成后必须编译验证通过
- 所有新代码遵循本文档定义的模式
- 旧系统源码是业务逻辑的唯一参考
