# 代码审查修复实施计划

> **For agentic workers:** 使用 superpowers:subagent-driven-development 或 superpowers:executing-plans 执行此计划。

**Goal:** 修复代码审查发现的 2 个 Critical + 3 个 Important + 4 个 Minor 问题，确保迁移代码质量达标。

**Architecture:** 修改已有文件，遵循新系统的四层模式（BaseEntity/BaseController/BaseMapperPlus/@AutoMapper）。使用 3 个并行 Agent 分组修复。

**Tech Stack:** Spring Boot 3.5, MyBatis-Plus 3.5, Sa-Token 1.44, BCrypt (Spring Security Crypto)

---

## 文件结构

### 需修改的文件
```
sdkj-modules/sdkj-thermal/src/main/java/org/sdkj/thermal/
├── domain/
│   ├── AgCompany.java          ← 加 BaseEntity + @AutoMapper
│   ├── AgUser.java             ← 加 BaseEntity + @AutoMapper
│   ├── AgRole.java             ← 加 BaseEntity + @AutoMapper
│   ├── AgProperty.java         ← 改表名 + 重写为关联表实体
│   ├── AgPropertyMenu.java     ← 加 BaseEntity + @AutoMapper
├── controller/
│   ├── AgCompanyController.java ← 加 extends BaseController + toAjax()
│   ├── PrInspectionRecordController.java ← 改 PageQuery/TableDataInfo
├── service/impl/
│   ├── AgCompanyServiceImpl.java   ← 补 insertData 完整逻辑 + TODO
│   ├── AgUserServiceImpl.java      ← MD5 → BCrypt
│   ├── AgPropertyServiceImpl.java  ← 补 bindProperty 实现
```

---

## Task 1: 修复 Agent Domain 层 — BaseEntity + @AutoMapper

**Files:**
- Modify: `domain/AgCompany.java`
- Modify: `domain/AgUser.java`
- Modify: `domain/AgRole.java`
- Modify: `domain/AgPropertyMenu.java`

**Changes for each file:**
1. `implements Serializable` → `extends BaseEntity`
2. 添加 `@EqualsAndHashCode(callSuper = true)`
3. 添加 `@AutoMapper(target = XxxVo.class)`
4. 移除 BaseEntity 中已有的字段（createBy/createTime/updateBy/updateTime）
5. 移除 `import java.io.Serializable`

**AgCompany.java 修改后：**
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_company")
@AutoMapper(target = AgCompanyVo.class)
public class AgCompany extends BaseEntity {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    // ... 保留业务字段，删除 createBy/createTime/updateBy/updateTime
}
```

**AgUser.java 修改后：**
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@AutoMapper(target = AgUserVo.class)
public class AgUser extends BaseEntity {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    // ... 保留业务字段，删除 createBy/createTime/updateBy/updateTime/isDeleted
    // isDeleted 字段由 BaseEntity 的逻辑删除处理
}
```

**AgRole.java 修改后：**
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@AutoMapper(target = AgRoleVo.class)
public class AgRole extends BaseEntity {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    // ... 保留业务字段，删除 createBy/createTime/updateBy/updateTime
}
```

**AgPropertyMenu.java 修改后：**
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ag_property_menu")
@AutoMapper(target = AgPropertyMenuVo.class)
public class AgPropertyMenu extends BaseEntity {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    private String companyId;
    private String menuId;
}
```

- [ ] **Step 1:** 修改 AgCompany.java
- [ ] **Step 2:** 修改 AgUser.java
- [ ] **Step 3:** 修改 AgRole.java
- [ ] **Step 4:** 修改 AgPropertyMenu.java
- [ ] **Step 5:** 编译验证 `mvn clean compile -pl sdkj-modules/sdkj-thermal -am`
- [ ] **Step 6:** Commit: `fix(agent): Agent Domain 继承 BaseEntity + @AutoMapper`

---

## Task 2: 修复 AgProperty — 改为关联表映射

**Files:**
- Modify: `domain/AgProperty.java`
- Modify: `mapper/AgPropertyMapper.java`
- Modify: `service/impl/AgPropertyServiceImpl.java`
- Modify: `resources/mapper/AgPropertyMapper.xml`

**Problem:** AgProperty 当前映射 `sys_company` 表，与 AgCompany 冲突。应该是代理商-物业关联实体。

**AgProperty.java 修改后：**
```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ag_company_property")
@AutoMapper(target = AgPropertyVo.class)
public class AgProperty extends BaseEntity {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /** 代理商公司ID */
    private String agentCompanyId;
    /** 物业公司ID */
    private String propertyCompanyId;
    /** 物业公司名称（冗余/查询用） */
    @TableField(exist = false)
    private String propertyName;
    /** 物业公司编码 */
    @TableField(exist = false)
    private String propertyCode;
}
```

注意：AgPropertyVo 也要相应调整字段。AgPropertyMapper.xml 中的 JOIN 查询关联 sys_company 获取物业名称。

**AgPropertyServiceImpl.bindProperty() 修复：**
```java
@Override
@Transactional(rollbackFor = Exception.class)
public boolean bindProperty(AgPropertyBo propertyBo) {
    AgProperty entity = new AgProperty();
    entity.setAgentCompanyId(propertyBo.getAgentCompanyId());
    entity.setPropertyCompanyId(propertyBo.getPropertyCompanyId());
    return save(entity);
}
```

- [ ] **Step 1:** 重写 AgProperty.java
- [ ] **Step 2:** 更新 AgPropertyVo.java 对应字段
- [ ] **Step 3:** 更新 AgPropertyBo.java 对应字段
- [ ] **Step 4:** 修复 AgPropertyServiceImpl.bindProperty()
- [ ] **Step 5:** 更新 AgPropertyMapper.xml 查询逻辑
- [ ] **Step 6:** 编译验证
- [ ] **Step 7:** Commit: `fix(agent): AgProperty 改为关联表映射`

---

## Task 3: 修复密码加密 MD5 → BCrypt

**Files:**
- Modify: `service/impl/AgUserServiceImpl.java`

**Changes:**
将 `SecureUtil.md5(...)` 替换为 BCrypt 加密。

```java
// 旧代码
user.setUserPwd(SecureUtil.md5(user.getUserPwd().trim()));

// 新代码
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

// 在 insertUser 和 updateUser 中:
if (StrUtil.isNotBlank(userBo.getUserPwd())) {
    user.setUserPwd(PASSWORD_ENCODER.encode(userBo.getUserPwd().trim()));
}
```

注意：如果数据库中已有 MD5 密码的用户，需要考虑兼容策略。但由于这是全新迁移，可以直接用 BCrypt。

- [ ] **Step 1:** 修改 AgUserServiceImpl.java 密码加密方式
- [ ] **Step 2:** 移除 `import cn.hutool.crypto.SecureUtil`
- [ ] **Step 3:** 编译验证
- [ ] **Step 4:** Commit: `fix(agent): 密码加密 MD5 → BCrypt`

---

## Task 4: 修复 AgCompanyController — extends BaseController

**Files:**
- Modify: `controller/AgCompanyController.java`

**Changes:**
1. 添加 `extends BaseController`
2. 手动判断 `? R.ok() : R.fail()` → `toAjax()`
3. `list()` 返回类型保持 `R<List<TreeNode>>` 不变（toAjax 不适用此方法）

```java
public class AgCompanyController extends BaseController {

    // add 方法：
    return toAjax(companyService.insertCompany(companyBo));

    // edit 方法：
    return toAjax(companyService.updateCompany(companyBo));

    // remove 方法：
    return toAjax(companyService.deleteCompany(id));

    // enable 方法：
    return toAjax(companyService.enableCompany(id));

    // disable 方法：
    return toAjax(companyService.disableCompany(id));
}
```

- [ ] **Step 1:** 修改 AgCompanyController.java
- [ ] **Step 2:** 编译验证
- [ ] **Step 3:** Commit: `fix(agent): AgCompanyController 继承 BaseController`

---

## Task 5: 修复 PrInspectionRecordController — PageQuery/TableDataInfo

**Files:**
- Modify: `controller/PrInspectionRecordController.java`
- Modify: `service/IPrInspectionRecordService.java` (如果方法签名需要变)
- Modify: `service/impl/PrInspectionRecordServiceImpl.java` (如果需要)

**Changes:**
```java
// 旧代码
@GetMapping("/list")
public R<Page<PrInspectionRecord>> list(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String orgId,
        @RequestParam(required = false) String companyId,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize) {
    Page<PrInspectionRecord> page = new Page<>(pageNum, pageSize);
    // ...
    return R.ok(inspectionRecordService.page(page, lqw));
}

// 新代码
@GetMapping("/list")
public TableDataInfo<PrInspectionRecord> list(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String orgId,
        @RequestParam(required = false) String companyId,
        PageQuery pageQuery) {
    LambdaQueryWrapper<PrInspectionRecord> lqw = new LambdaQueryWrapper<>();
    lqw.eq(StrUtil.isNotBlank(companyId), PrInspectionRecord::getCompanyId, companyId);
    lqw.eq(StrUtil.isNotBlank(orgId), PrInspectionRecord::getOrgId, orgId);
    lqw.and(StrUtil.isNotBlank(search), wrapper -> wrapper
        .like(PrInspectionRecord::getPersonName, search)
        .or().like(PrInspectionRecord::getEquipmentName, search)
        .or().like(PrInspectionRecord::getContent, search));
    lqw.orderByDesc(PrInspectionRecord::getCreateTime);
    Page<PrInspectionRecord> result = inspectionRecordService.page(pageQuery.build(), lqw);
    return TableDataInfo.build(result);
}
```

- [ ] **Step 1:** 修改 PrInspectionRecordController.java
- [ ] **Step 2:** 编译验证
- [ ] **Step 3:** Commit: `fix(ops): 巡检记录改用 PageQuery/TableDataInfo 分页`

---

## Task 6: 补全 AgCompanyServiceImpl.insertCompany 完整逻辑

**Files:**
- Modify: `service/impl/AgCompanyServiceImpl.java`

**Changes:**
旧系统创建公司时同步创建：管理员用户 + 角色 + 菜单权限 + 用户角色关联。当前只创建了角色。

```java
@Override
@Transactional(rollbackFor = Exception.class)
public boolean insertCompany(AgCompanyBo companyBo) {
    AgCompany company = new AgCompany();
    BeanUtils.copyProperties(companyBo, company);
    boolean saved = save(company);
    if (!saved) return false;

    // 1. 创建超管角色
    AgRole role = new AgRole();
    role.setName("代理商超管");
    role.setIdentifying("ROLE_SUPER_AGENT");
    role.setNature("1");
    role.setCompanyId(company.getId());
    role.setIsSuper(1);
    roleMapper.insert(role);

    // 2. 创建管理员用户
    AgUser adminUser = new AgUser();
    adminUser.setUserName(company.getTele());
    adminUser.setRealName(company.getPrincipal());
    adminUser.setPhone(company.getTele());
    adminUser.setCompanyId(company.getId());
    adminUser.setIsSuper(1);
    adminUser.setIsRealname(1);
    adminUser.setIsDeleted("0");
    adminUser.setIsEnabled(1);
    adminUser.setUserPwd(PASSWORD_ENCODER.encode("123456"));
    userMapper.insert(adminUser);

    // 3. 关联用户与角色
    userMapper.saveUserRole(adminUser.getId(), new String[]{role.getId()});

    return true;
}
```

需要注入 `AgUserMapper` 和 `BCryptPasswordEncoder`。

- [ ] **Step 1:** 修改 AgCompanyServiceImpl.java
- [ ] **Step 2:** 实现 updateAgAdminUserStatus()
- [ ] **Step 3:** 编译验证
- [ ] **Step 4:** Commit: `fix(agent): 补全代理商创建时同步创建管理员用户逻辑`

---

## Task 7: 修复 PrInspectionRecordController 接收 BO 而非 Entity

**Files:**
- Modify: `controller/PrInspectionRecordController.java`

**Changes:**
```java
// 旧代码
public R<Void> add(@Validated @RequestBody PrInspectionRecord record) {
    return toAjax(inspectionRecordService.save(record));
}

// 新代码
public R<Void> add(@Validated @RequestBody PrInspectionRecordBo bo) {
    PrInspectionRecord record = MapstructUtils.convert(bo, PrInspectionRecord.class);
    return toAjax(inspectionRecordService.save(record));
}
```

需要在 Mapper XML 中增加 `import org.sdkj.common.core.utils.MapstructUtils;`

- [ ] **Step 1:** 修改 PrInspectionRecordController add/update 方法
- [ ] **Step 2:** 编译验证
- [ ] **Step 3:** Commit: `fix(ops): 巡检记录使用 BO 接收参数`

---

## Task 8: 最终编译验证 + Commit

- [ ] **Step 1:** `mvn clean compile -pl sdkj-modules/sdkj-thermal -am` 全量编译
- [ ] **Step 2:** `mvn clean compile` 全项目编译（包含 sdkj-meter, sdkj-system）
- [ ] **Step 3:** 检查无残留 TODO（`grep -r TODO sdkj-modules/sdkj-thermal/src --include="*.java" -c`）
- [ ] **Step 4:** 最终 commit: `fix: 代码审查修复 — BaseEntity/BCrypt/关联表/分页规范化`
