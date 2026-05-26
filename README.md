# SDKJ Thermal Platform Backend

SDKJ 智慧供热综合管理平台新后端，基于 RuoYi-Vue-Plus 5.6.0 改造。当前代码包名为 `org.sdkj`，模块前缀为 `sdkj-`，打包为单体多模块 JAR。

## 当前技术栈

| 项 | 事实 |
|----|------|
| Java | 17 |
| Spring Boot | 3.5.14 |
| Sa-Token | 1.44.0 |
| MyBatis-Plus | 3.5.16 |
| dynamic-datasource | 4.3.1 |
| Spring AI | 1.0.7 |
| 打包方式 | JAR，启动模块 `sdkj-admin` |

## 模块

```text
sdkj-admin/                 启动入口与配置
sdkj-common/                24 个公共模块
sdkj-modules/sdkj-system/   系统管理
sdkj-modules/sdkj-meter/    仪表管理
sdkj-modules/sdkj-thermal/  热力调控 + 物业收费
sdkj-modules/sdkj-ai/       AI 旁注、助手、RAG 与 Tool Calling 基建
script/sql/                 数据库脚本
```

## 运行

```bash
mvn clean compile
mvn clean package -DskipTests
mvn spring-boot:run -pl sdkj-admin
```

默认端口为 `8080`，Spring profiles 在 `sdkj-admin/src/main/resources/application.yml` 中为 `dev,thermal`。

## 数据源与租户

系统采用独立库模式：

- `master`：`ry-vue`，保存系统表、租户、菜单、用户等。
- `tenant_{id}`：租户业务库，保存 `thermal`、`meter` 等业务表。
- `sqlserver`：IoT/外部系统读取。
- `legacy`：旧系统 `rltk_pro` 迁移读取。

`tenant.enable: false` 表示关闭 `tenant_id` 行级 SQL 过滤；租户隔离依靠动态数据源切换完成。

## 测试现状

`sdkj-ai` 已有 BM25 和中文预处理相关 JUnit 单测。其余模块测试覆盖仍少，常规验证以编译、启动和接口手测为主。

## 进一步文档

- `CLAUDE.md`：后端架构、请求链路、AI 模块、数据库和开发约定。
- `../docs/documentation-audit-summary-2026W21.md`：项目文档审计和事实校正。
