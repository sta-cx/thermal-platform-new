# 项目清理与重命名 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 RuoYi-Vue-Plus 基座项目重命名为 SDKJ 品牌（org.dromara → org.sdkj），裁剪不需要的模块，精简数据库初始化脚本，仅保留必要的用户/角色/菜单数据。

**Architecture:** 三个独立阶段：(1) 模块裁剪删除不需要的代码，(2) 全局包名/路径名重命名，(3) 数据库脚本精简。每个阶段独立可验证，阶段间顺序执行。

**Tech Stack:** Spring Boot 3.5.12, Java 17, MyBatis-Plus 3.5.16, MySQL, RuoYi-Vue-Plus 5.6.0 → 重命名为 SDKJ Thermal Platform

---

## 变更范围总览

### 模块裁剪决策

| 模块 | 保留? | 理由 |
|------|-------|------|
| `ruoyi-admin` | **保留** | 启动入口 |
| `ruoyi-common/*` (24个子模块) | **保留** | 核心基础设施 |
| `ruoyi-system` | **保留** | 用户/角色/菜单/部门等系统管理 |
| `ruoyi-meter` | **保留** | 仪表模块（业务核心） |
| `ruoyi-thermal` | **保留** | 热力调控+收费模块（业务核心） |
| `ruoyi-job` | **保留** | Quartz定时任务（收费/调控依赖） |
| `ruoyi-generator` | **删除** | 代码生成器，生产不需要 |
| `ruoyi-workflow` | **删除** | Warm-Flow工作流，当前业务不需要 |
| `ruoyi-demo` | **删除** | 演示代码，生产不需要 |
| `ruoyi-extend/ruoyi-monitor-admin` | **删除** | 独立监控服务，当前不需要 |
| `ruoyi-extend/ruoyi-snailjob-server` | **删除** | SnailJob服务器，当前用Quartz |
| `ruoyi-extend` (父模块) | **删除** | 所有子模块都删除后父模块也删除 |

### 重命名映射

| 原文 | 替换为 | 范围 |
|------|--------|------|
| `org.dromara` | `org.sdkj` | 所有Java package, import, XML namespace, pom.xml groupId |
| `ruoyi-` | `sdkj-` | 模块目录名, artifactId, pom.xml module引用 |
| `DromaraApplication` | `SdkjApplication` | 主类文件名和内容 |
| `RuoYi-Vue-Plus` | `SDKJ Thermal Platform` | banner.txt, springdoc title |

### 数据精简策略

保留以下表的初始化数据（INSERT语句）：
- `sys_tenant` — 租户默认
- `sys_menu` — 菜单（含thermal/meter自定义菜单）
- `sys_role` — 角色
- `sys_dept` — 部门
- `sys_post` — 岗位
- `sys_user` — 用户（admin等默认用户）
- `sys_user_role` — 用户角色关联
- `sys_role_menu` — 角色菜单关联
- `sys_role_dept` — 角色部门关联
- `sys_user_post` — 用户岗位关联
- `sys_dict_type` + `sys_dict_data` — 字典
- `sys_config` — 系统配置
- `sys_oss_config` — OSS配置
- `sys_client` — 客户端配置

删除以下表的初始化数据：
- `flow_*` — 工作流表数据
- `sys_job*` — SnailJob数据
- 所有 demo 相关数据

---

## 文件结构总览

### Task 1-2: 模块裁剪
- Delete: `ruoyi-modules/ruoyi-generator/`
- Delete: `ruoyi-modules/ruoyi-workflow/`
- Delete: `ruoyi-modules/ruoyi-demo/`
- Delete: `ruoyi-extend/` (entire directory)
- Modify: `ruoyi-modules/pom.xml` (remove module declarations)
- Modify: `ruoyi-admin/pom.xml` (remove dependencies)
- Modify: `pom.xml` (remove ruoyi-extend module)

### Task 3-6: 包名重命名（按模块分发子任务）
所有 `ruoyi-modules/*/src/main/java/` 下的目录和文件需要：
1. 目录从 `org/dromara/xxx` 移动到 `org/sdkj/xxx`
2. 所有 `.java` 文件中 `package org.dromara` → `package org.sdkj`
3. 所有 `.java` 文件中 `import org.dromara` → `import org.sdkj`
4. 所有 Mapper XML 中 `namespace="org.dromara` → `namespace="org.sdkj`
5. 所有 `pom.xml` 中 `<groupId>org.dromara</groupId>` → `<groupId>org.sdkj</groupId>`
6. 所有 `pom.xml` 中 `ruoyi-xxx` artifactId → `sdkj-xxx`
7. `application.yml` 中相关配置更新

### Task 7: 数据库脚本精简
- Modify: `script/sql/ry_vue_5.X.sql` (保留必要INSERT，删除工作流/demo相关)
- 创建精简版初始化脚本

---

### Task 1: 删除不需要的业务模块

**Files:**
- Delete: `ruoyi-modules/ruoyi-generator/` (代码生成器)
- Delete: `ruoyi-modules/ruoyi-workflow/` (工作流)
- Delete: `ruoyi-modules/ruoyi-demo/` (演示模块)

- [ ] **Step 1: 删除 ruoyi-generator 模块**

```bash
rm -rf ruoyi-modules/ruoyi-generator/
```

- [ ] **Step 2: 删除 ruoyi-workflow 模块**

```bash
rm -rf ruoyi-modules/ruoyi-workflow/
```

- [ ] **Step 3: 删除 ruoyi-demo 模块**

```bash
rm -rf ruoyi-modules/ruoyi-demo/
```

- [ ] **Step 4: 修改 ruoyi-modules/pom.xml，移除模块声明**

当前 `<modules>` 块包含 7 个模块，修改为只保留 4 个：

```xml
<modules>
    <module>ruoyi-system</module>
    <module>ruoyi-job</module>
    <module>ruoyi-meter</module>
    <module>ruoyi-thermal</module>
</modules>
```

- [ ] **Step 5: 修改 ruoyi-admin/pom.xml，移除依赖**

删除以下依赖块：

```xml
<!-- 删除 ruoyi-generator 依赖 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>ruoyi-generator</artifactId>
</dependency>

<!-- 删除 ruoyi-workflow 依赖 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>ruoyi-workflow</artifactId>
</dependency>

<!-- 删除 ruoyi-demo 依赖 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>ruoyi-demo</artifactId>
</dependency>
```

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "refactor: 删除 generator/workflow/demo 模块"
```

---

### Task 2: 删除 ruoyi-extend 扩展模块

**Files:**
- Delete: `ruoyi-extend/` (整个目录，含 ruoyi-monitor-admin 和 ruoyi-snailjob-server)
- Modify: `pom.xml` (根pom移除 ruoyi-extend 模块)
- Modify: `ruoyi-admin/pom.xml` (如有对 extend 子模块的依赖)

- [ ] **Step 1: 删除 ruoyi-extend 目录**

```bash
rm -rf ruoyi-extend/
```

- [ ] **Step 2: 修改根 pom.xml，移除 ruoyi-extend 模块**

在 `<modules>` 块中删除：
```xml
<module>ruoyi-extend</module>
```

修改后根 pom 的 modules 应为：
```xml
<modules>
    <module>ruoyi-admin</module>
    <module>ruoyi-common</module>
    <module>ruoyi-modules</module>
</modules>
```

- [ ] **Step 3: 检查 ruoyi-admin/pom.xml 是否有 extend 子模块依赖并删除**

检查并删除以下依赖（如果存在）：
```xml
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>ruoyi-monitor-admin</artifactId>
</dependency>
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>ruoyi-snailjob-server</artifactId>
</dependency>
```

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "refactor: 删除 ruoyi-extend 扩展模块"
```

---

### Task 3: 根 pom.xml 和 ruoyi-common 模块重命名

**Files:**
- Modify: `pom.xml` (根pom: groupId, artifactId references)
- Modify: `ruoyi-common/pom.xml` (BOM module)
- Modify: `ruoyi-common/*/pom.xml` (24个子模块的pom)

- [ ] **Step 1: 修改根 pom.xml 的 groupId 和 artifactId**

```xml
<!-- 修改前 -->
<groupId>org.dromara</groupId>
<artifactId>ruoyi-vue-plus</artifactId>

<!-- 修改后 -->
<groupId>org.sdkj</groupId>
<artifactId>sdkj-thermal-platform</artifactId>
```

同时修改 `<name>` 和 `<description>`：
```xml
<name>SDKJ Thermal Platform</name>
<description>SDKJ智慧供热综合管理平台</description>
```

- [ ] **Step 2: 修改 ruoyi-common/pom.xml 的 groupId**

将文件中所有 `<groupId>org.dromara</groupId>` 替换为 `<groupId>org.sdkj</groupId>`。

将 `<artifactId>ruoyi-common-bom</artifactId>` 替换为 `<artifactId>sdkj-common-bom</artifactId>`。

- [ ] **Step 3: 修改 ruoyi-common 每个子模块的 pom.xml**

对以下每个目录下的 pom.xml 执行相同替换：
`ruoyi-common-core`, `ruoyi-common-doc`, `ruoyi-common-encrypt`, `ruoyi-common-excel`,
`ruoyi-common-idempotent`, `ruoyi-common-job`, `ruoyi-common-json`, `ruoyi-common-log`,
`ruoyi-common-mail`, `ruoyi-common-mybatis`, `ruoyi-common-oss`, `ruoyi-common-ratelimiter`,
`ruoyi-common-redis`, `ruoyi-common-satoken`, `ruoyi-common-security`, `ruoyi-common-sensitive`,
`ruoyi-common-sms`, `ruoyi-common-social`, `ruoyi-common-sse`, `ruoyi-common-tenant`,
`ruoyi-common-translation`, `ruoyi-common-web`, `ruoyi-common-websocket`

每个 pom.xml 中：
1. `<groupId>org.dromara</groupId>` → `<groupId>org.sdkj</groupId>`
2. `<artifactId>ruoyi-common-xxx</artifactId>` → `<artifactId>sdkj-common-xxx</artifactId>`
3. parent 中的 `<groupId>org.dromara</groupId>` → `<groupId>org.sdkj</groupId>`
4. parent 中的 `<artifactId>ruoyi-common-bom</artifactId>` → `<artifactId>sdkj-common-bom</artifactId>`

- [ ] **Step 4: 重命名 ruoyi-common 子模块目录**

```bash
cd ruoyi-common
for dir in ruoyi-common-*; do
    newname=$(echo "$dir" | sed 's/ruoyi-/sdkj-/')
    mv "$dir" "$newname"
done
```

- [ ] **Step 5: 更新 ruoyi-common/pom.xml 中的 module 声明**

```xml
<modules>
    <module>sdkj-common-core</module>
    <module>sdkj-common-doc</module>
    <module>sdkj-common-encrypt</module>
    <!-- ... 所有 ruoyi-common-* 改为 sdkj-common-* -->
</modules>
```

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "refactor: 重命名 ruoyi-common 模块为 sdkj-common"
```

---

### Task 4: ruoyi-admin, ruoyi-system, ruoyi-job 模块重命名

**Files:**
- Modify/Rename: `ruoyi-admin/` (目录重命名 + 文件内容)
- Modify/Rename: `ruoyi-modules/ruoyi-system/` → `ruoyi-modules/sdkj-system/`
- Modify/Rename: `ruoyi-modules/ruoyi-job/` → `ruoyi-modules/sdkj-job/`

- [ ] **Step 1: 重命名 ruoyi-admin 主类**

```bash
mv ruoyi-admin/src/main/java/org/dromara/DromaraApplication.java ruoyi-admin/src/main/java/org/dromara/SdkjApplication.java
mv ruoyi-admin/src/main/java/org/dromara/DromaraServletInitializer.java ruoyi-admin/src/main/java/org/dromara/SdkjServletInitializer.java
```

修改 `SdkjApplication.java` 内容：

```java
package org.sdkj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SdkjApplication {
    public static void main(String[] args) {
        SpringApplication.run(SdkjApplication.class, args);
        System.out.println("SDKJ智慧供热平台启动成功");
    }
}
```

- [ ] **Step 2: 重命名 ruoyi-admin 目录结构**

```bash
mv ruoyi-admin/src/main/java/org/dromara ruoyi-admin/src/main/java/org/sdkj
```

- [ ] **Step 3: 批量替换 ruoyi-admin 下所有 Java 文件的 package 和 import**

在 `ruoyi-admin/src/main/java/` 下所有 `.java` 文件：
1. `package org.dromara` → `package org.sdkj`
2. `import org.dromara` → `import org.sdkj`

- [ ] **Step 4: 修改 ruoyi-admin/pom.xml**

```xml
<!-- parent -->
<parent>
    <groupId>org.sdkj</groupId>
    <artifactId>sdkj-thermal-platform</artifactId>
    <version>${revision}</version>
</parent>

<!-- artifactId -->
<artifactId>sdkj-admin</artifactId>

<!-- dependencies: 所有 ruoyi-xxx 改为 sdkj-xxx -->
<dependency>
    <groupId>org.sdkj</groupId>
    <artifactId>sdkj-system</artifactId>
</dependency>
<dependency>
    <groupId>org.sdkj</groupId>
    <artifactId>sdkj-job</artifactId>
</dependency>
<dependency>
    <groupId>org.sdkj</groupId>
    <artifactId>sdkj-meter</artifactId>
</dependency>
<dependency>
    <groupId>org.sdkj</groupId>
    <artifactId>sdkj-thermal</artifactId>
</dependency>
<!-- sdkj-common-* 依赖同上 -->
```

- [ ] **Step 5: 重命名 ruoyi-system 目录**

```bash
mv ruoyi-modules/ruoyi-system ruoyi-modules/sdkj-system
mv ruoyi-modules/sdkj-system/src/main/java/org/dromara/system ruoyi-modules/sdkj-system/src/main/java/org/sdkj/system
```

- [ ] **Step 6: 批量替换 sdkj-system 下所有文件**

所有 `.java` 文件：`org.dromara.system` → `org.sdkj.system`，`import org.dromara` → `import org.sdkj`
所有 Mapper XML：`namespace="org.dromara.system` → `namespace="org.sdkj.system`
pom.xml：`org.dromara` → `org.sdkj`，`ruoyi-system` → `sdkj-system`

- [ ] **Step 7: 重命名 ruoyi-job 目录**

```bash
mv ruoyi-modules/ruoyi-job ruoyi-modules/sdkj-job
mv ruoyi-modules/sdkj-job/src/main/java/org/dromara/job ruoyi-modules/sdkj-job/src/main/java/org/sdkj/job
```

- [ ] **Step 8: 批量替换 sdkj-job 下所有文件**

所有 `.java` 文件：`org.dromara.job` → `org.sdkj.job`
所有 Mapper XML：`namespace="org.dromara.job` → `namespace="org.sdkj.job`
pom.xml：`org.dromara` → `org.sdkj`，`ruoyi-job` → `sdkj-job`

- [ ] **Step 9: 更新 ruoyi-modules/pom.xml**

```xml
<modules>
    <module>sdkj-system</module>
    <module>sdkj-job</module>
    <module>sdkj-meter</module>
    <module>sdkj-thermal</module>
</modules>
```

- [ ] **Step 10: Commit**

```bash
git add -A
git commit -m "refactor: 重命名 admin/system/job 模块为 sdkj"
```

---

### Task 5: sdkj-meter 和 sdkj-thermal 模块重命名

**Files:**
- Modify/Rename: `ruoyi-modules/ruoyi-meter/` → `ruoyi-modules/sdkj-meter/`
- Modify/Rename: `ruoyi-modules/ruoyi-thermal/` → `ruoyi-modules/sdkj-thermal/`

- [ ] **Step 1: 重命名 sdkj-meter 目录**

```bash
mv ruoyi-modules/ruoyi-meter ruoyi-modules/sdkj-meter
mv ruoyi-modules/sdkj-meter/src/main/java/org/dromara/meter ruoyi-modules/sdkj-meter/src/main/java/org/sdkj/meter
```

- [ ] **Step 2: 批量替换 sdkj-meter 下所有文件**

所有 `.java` 文件：`org.dromara.meter` → `org.sdkj.meter`，`import org.dromara` → `import org.sdkj`
所有 Mapper XML：`namespace="org.dromara.meter` → `namespace="org.sdkj.meter`
pom.xml：`org.dromara` → `org.sdkj`，`ruoyi-meter` → `sdkj-meter`

- [ ] **Step 3: 重命名 sdkj-thermal 目录**

```bash
mv ruoyi-modules/ruoyi-thermal ruoyi-modules/sdkj-thermal
mv ruoyi-modules/sdkj-thermal/src/main/java/org/dromara/thermal ruoyi-modules/sdkj-thermal/src/main/java/org/sdkj/thermal
```

- [ ] **Step 4: 批量替换 sdkj-thermal 下所有文件**

所有 `.java` 文件：`org.dromara.thermal` → `org.sdkj.thermal`，`import org.dromara` → `import org.sdkj`
所有 Mapper XML：`namespace="org.dromara.thermal` → `namespace="org.sdkj.thermal`
pom.xml：`org.dromara` → `org.sdkj`，`ruoyi-thermal` → `sdkj-thermal`

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "refactor: 重命名 meter/thermal 模块为 sdkj"
```

---

### Task 6: 配置文件和 ruoyi-common Java 代码重命名

**Files:**
- Rename: `ruoyi-common/*/src/main/java/org/dromara/` → `org/sdkj/` (24个子模块)
- Modify: `ruoyi-admin/src/main/resources/application.yml`
- Modify: `ruoyi-admin/src/main/resources/application-dev.yml`
- Modify: `ruoyi-admin/src/main/resources/banner.txt`

- [ ] **Step 1: 重命名 ruoyi-common 所有 Java 源码目录**

```bash
for mod in ruoyi-common/sdkj-common-*/; do
    if [ -d "${mod}src/main/java/org/dromara" ]; then
        dirname=$(basename "$mod" | sed 's/sdkj-common-//')
        mv "${mod}src/main/java/org/dromara/common/${dirname}" "${mod}src/main/java/org/sdkj/common/${dirname}" 2>/dev/null || true
        mkdir -p "${mod}src/main/java/org/sdkj"
        mv "${mod}src/main/java/org/dromara"/* "${mod}src/main/java/org/sdkj/" 2>/dev/null || true
        rm -rf "${mod}src/main/java/org/dromara"
    fi
done
```

- [ ] **Step 2: 批量替换 ruoyi-common 所有 Java 文件**

在所有 `ruoyi-common/` 下的 `.java` 文件：
1. `package org.dromara` → `package org.sdkj`
2. `import org.dromara` → `import org.sdkj`

- [ ] **Step 3: 批量替换 ruoyi-common 所有 pom.xml 中的引用**

在 `ruoyi-common/` 下所有 `pom.xml` 中：
1. `<parent><groupId>org.dromara</groupId>` → `<parent><groupId>org.sdkj</groupId>`
2. `<groupId>org.dromara</groupId>` → `<groupId>org.sdkj</groupId>`
3. `ruoyi-common-` → `sdkj-common-` (artifactId 和 module)

- [ ] **Step 4: 修改 application.yml**

修改以下行：

```yaml
# line 36: logging level
logging:
  level:
    org.sdkj: @logging.level@

# line 149: mapper package
mybatis-plus:
  mapperPackage: org.sdkj.**.mapper
  typeAliasesPackage: org.sdkj.**.domain

# line 194-195: springdoc
springdoc:
  info:
    title: '标题：SDKJ智慧供热综合管理平台_接口文档'
    description: '描述：智慧供热管理平台'
    url: ''

# line 205-215: group-configs scan packages
  group-configs:
    - group: 1.仪表模块
      packages-to-scan: org.sdkj.meter
    - group: 2.系统模块
      packages-to-scan: org.sdkj.system
    - group: 3.热力调控模块
      packages-to-scan: org.sdkj.thermal
    - group: 4.定时任务模块
      packages-to-scan: org.sdkj.job

# line 258-269: warm-flow — 删除整个 warm-flow 配置块（工作流模块已删除）
```

- [ ] **Step 5: 修改 application-thermal.yml**

将所有 `org.dromara` 替换为 `org.sdkj`。

- [ ] **Step 6: 修改 banner.txt**

```
  _______  __   __  _______  _______  _______
 |  _    ||  | |  ||       ||       ||       |
 | |_|   ||  |_|  ||    ___||_     _||   _   |
 |       ||       ||   |___   |   |  |  | |  |
 |  _   | |_     _||    ___|  |   |  |  |_|  |
 | |_|   |  |   |  |   |___   |   |  |       |
 |_______|  |___|  |_______|  |___|  |_______|

 SDKJ Thermal Platform v${project.version}
```

- [ ] **Step 7: 删除 workflow 相关配置**

删除 `application.yml` 中的 warm-flow 配置块（lines 258-269）。
删除 `tenant.excludes` 中的 `flow_spel`。
删除 `security.excludes` 中的 `/warm-flow-ui/config`。

- [ ] **Step 8: 修改 ruoyi-admin/pom.xml 中的 artifactId 引用**

所有 `<artifactId>ruoyi-xxx</artifactId>` → `<artifactId>sdkj-xxx</artifactId>`。
所有 `<groupId>org.dromara</groupId>` → `<groupId>org.sdkj</groupId>`。

- [ ] **Step 9: 编译验证**

```bash
cd D:/chonggou/thermal-platform-new
mvn clean compile 2>&1 | tail -20
```

Expected: BUILD SUCCESS

- [ ] **Step 10: Commit**

```bash
git add -A
git commit -m "refactor: 完成 org.dromara → org.sdkj 全局重命名"
```

---

### Task 7: 数据库初始化脚本精简

**Files:**
- Create: `script/sql/sdkj-init.sql` (精简版初始化脚本)

- [ ] **Step 1: 创建精简版初始化脚本**

从 `ry_vue_5.X.sql` 中提取以下部分：
1. 所有 CREATE TABLE 语句（完整保留表结构）
2. 仅保留必要的 INSERT 语句

创建新文件 `script/sql/sdkj-init.sql`，包含：

```sql
-- ========================================
-- SDKJ Thermal Platform 初始化脚本
-- 仅包含必要的系统初始化数据
-- ========================================

-- 1. 保留所有 CREATE TABLE 语句（从 ry_vue_5.X.sql 复制）
-- ... 所有建表语句 ...

-- 2. 保留租户默认数据
INSERT INTO sys_tenant VALUES (...);

-- 3. 保留默认部门
INSERT INTO sys_dept VALUES (...);

-- 4. 保留默认岗位
INSERT INTO sys_post VALUES (...);

-- 5. 保留默认角色
INSERT INTO sys_role VALUES (...);

-- 6. 保留默认用户（admin等）
INSERT INTO sys_user VALUES (...);

-- 7. 保留用户角色关联
INSERT INTO sys_user_role VALUES (...);

-- 8. 保留用户岗位关联
INSERT INTO sys_user_post VALUES (...);

-- 9. 保留角色部门关联
INSERT INTO sys_role_dept VALUES (...);

-- 10. 保留角色菜单关联
INSERT INTO sys_role_menu VALUES (...);

-- 11. 保留菜单数据（含 thermal/meter 自定义菜单）
-- 包含所有 sys_menu INSERT，含 Phase 5 迁移的 31 条菜单

-- 12. 保留字典数据
INSERT INTO sys_dict_type VALUES (...);
INSERT INTO sys_dict_data VALUES (...);

-- 13. 保留系统配置
INSERT INTO sys_config VALUES (...);

-- 14. 保留客户端配置
INSERT INTO sys_client VALUES (...);

-- 15. 保留 OSS 配置
INSERT INTO sys_oss_config VALUES (...);

-- 16. 删除以下内容（不生成INSERT）：
--    - flow_* 工作流数据
--    - sys_job* 定时任务数据
--    - demo 相关数据
--    - warm-flow 流程定义
```

- [ ] **Step 2: 保留 Phase 3/4/5 自定义表结构**

将以下脚本中的 CREATE TABLE 语句合并到 `sdkj-init.sql`：
- `phase3_meter_tables.sql` — mt_* 表
- `phase4_thermal_tables.sql` — ht_* 表（不存在的）
- `phase5_property_charging_tables.sql` — pr_*/pm_* 表

- [ ] **Step 3: 清理旧的 SQL 脚本**

删除或归档不再需要的 SQL 文件：
- `ry_vue_5.X.sql` — 保留备份，不再作为主初始化脚本
- Oracle/PostgreSQL/SQL Server 变体 — 保留备份
- `ry_workflow.sql` — 删除（工作流已移除）
- `ry_job.sql` — 保留（Quartz依赖）

- [ ] **Step 4: Commit**

```bash
git add script/sql/sdkj-init.sql
git commit -m "feat: 创建精简版数据库初始化脚本"
```

---

### Task 8: 最终验证

- [ ] **Step 1: 全局搜索确认无残留 dromara/ruoyi 引用**

```bash
# Java 文件
grep -rn "org\.dromara" ruoyi-admin/src ruoyi-common ruoyi-modules --include="*.java" | head -20
# 应返回空

# XML 文件
grep -rn "org\.dromara" ruoyi-admin/src ruoyi-common ruoyi-modules --include="*.xml" | head -20
# 应返回空

# pom.xml
grep -rn "org\.dromara" pom.xml ruoyi-common ruoyi-modules ruoyi-admin --include="pom.xml" | head -20
# 应返回空

# 目录结构
find . -type d -name "dromara" | head -20
# 应返回空
```

- [ ] **Step 2: 完整编译**

```bash
cd D:/chonggou/thermal-platform-new
mvn clean compile 2>&1 | tail -20
```

Expected: BUILD SUCCESS, zero errors

- [ ] **Step 3: 打包验证**

```bash
mvn clean package -DskipTests 2>&1 | tail -10
```

Expected: BUILD SUCCESS

- [ ] **Step 4: 验证模块目录**

```bash
ls ruoyi-modules/
# 应只包含: sdkj-system sdkj-job sdkj-meter sdkj-thermal

ls ruoyi-common/
# 应只包含: sdkj-common-* (24个子模块)

ls ruoyi-extend/
# 应不存在
```

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "chore: 项目清理与重命名最终验证"
```

---

## Self-Review

### Spec coverage
| 需求 | Task | 状态 |
|------|------|------|
| 删除不需要的模块 | Task 1-2 | 覆盖 |
| org.dromara → org.sdkj | Task 3-6 | 覆盖 |
| ruoyi- → sdkj- | Task 3-6 | 覆盖 |
| 配置文件更新 | Task 6 | 覆盖 |
| 数据库脚本精简 | Task 7 | 覆盖 |
| 编译验证 | Task 8 | 覆盖 |

### Placeholder scan
- 无 TBD/TODO/fill-in 模式
- 所有代码步骤包含实际内容
- SQL INSERT 语句需要从原始脚本提取，Task 7 中已明确说明提取策略

### Type consistency
- 包名映射一致: `org.dromara.*` → `org.sdkj.*`
- 模块名映射一致: `ruoyi-xxx` → `sdkj-xxx`
- pom.xml 中的 groupId/artifactId 全局一致
- Mapper XML namespace 全局一致
