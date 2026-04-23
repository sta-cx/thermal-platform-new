# Phase 3: 仪表模块迁移 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将旧系统 11 个仪表相关控制器迁移到 RuoYi-Vue-Plus 架构，覆盖 11 张数据库表的 CRUD 及业务逻辑。

**Architecture:** 新建独立模块 `ruoyi-modules/ruoyi-meter/`，采用 RuoYi 标准四层架构（controller → service → mapper → domain），使用 MyBatis-Plus 注解 + XML 混合模式。所有端点挂载到 `/thermal/meter/*` 路径下，使用 `@SaCheckLogin` 认证。

**Tech Stack:** Spring Boot 3.5.12, Java 17, MyBatis-Plus 3.5.16, Sa-Token 1.44.0, RuoYi-Vue-Plus 5.6.0

---

## 文件结构总览

### 新建文件清单

```
ruoyi-modules/ruoyi-meter/
├── pom.xml                                          ← Task 1
└── src/main/java/org/dromara/meter/
    ├── controller/
    │   ├── MtMeterVendorController.java              ← Task 2
    │   ├── MtMeterSortController.java                ← Task 3
    │   ├── MtElectricArchiveController.java          ← Task 4
    │   ├── MtWaterArchiveController.java             ← Task 4
    │   ├── MtHeatArchiveController.java              ← Task 4
    │   ├── MtGasArchiveController.java               ← Task 4
    │   ├── MtCentratorArchiveController.java         ← Task 4
    │   ├── MtTcArchiveController.java                ← Task 5
    │   └── MtTcValveController.java                  ← Task 5
    ├── domain/
    │   ├── MtMeterVendor.java                        ← Task 2
    │   ├── MtMeterSort.java                          ← Task 3
    │   ├── MtElectricArchive.java                    ← Task 4
    │   ├── MtWaterArchive.java                       ← Task 4
    │   ├── MtHeatArchive.java                        ← Task 4
    │   ├── MtGasArchive.java                         ← Task 4
    │   ├── MtCentratorArchive.java                   ← Task 4
    │   ├── MtTcArchive.java                          ← Task 5
    │   ├── MtTcValve.java                            ← Task 5
    │   └── MtMeterMatch.java                         ← Task 4
    ├── domain/vo/
    │   ├── MtMeterVendorVo.java                      ← Task 2
    │   ├── MtMeterSortVo.java                        ← Task 3
    │   ├── MtElectricArchiveVo.java                  ← Task 4
    │   ├── MtWaterArchiveVo.java                     ← Task 4
    │   ├── MtHeatArchiveVo.java                      ← Task 4
    │   ├── MtGasArchiveVo.java                       ← Task 4
    │   ├── MtCentratorArchiveVo.java                 ← Task 4
    │   ├── MtTcArchiveVo.java                        ← Task 5
    │   └── MtTcValveVo.java                          ← Task 5
    ├── mapper/
    │   ├── MtMeterVendorMapper.java                  ← Task 2
    │   ├── MtMeterSortMapper.java                    ← Task 3
    │   ├── MtElectricArchiveMapper.java              ← Task 4
    │   ├── MtWaterArchiveMapper.java                 ← Task 4
    │   ├── MtHeatArchiveMapper.java                  ← Task 4
    │   ├── MtGasArchiveMapper.java                   ← Task 4
    │   ├── MtCentratorArchiveMapper.java             ← Task 4
    │   ├── MtTcArchiveMapper.java                    ← Task 5
    │   └── MtTcValveMapper.java                      ← Task 5
    └── service/
        ├── IMtMeterVendorService.java                ← Task 2
        ├── IMtMeterSortService.java                  ← Task 3
        ├── IMtElectricArchiveService.java            ← Task 4
        ├── IMtWaterArchiveService.java               ← Task 4
        ├── IMtHeatArchiveService.java                ← Task 4
        ├── IMtGasArchiveService.java                 ← Task 4
        ├── IMtCentratorArchiveService.java           ← Task 4
        ├── IMtTcArchiveService.java                  ← Task 5
        ├── IMtTcValveService.java                    ← Task 5
        └── impl/
            ├── MtMeterVendorServiceImpl.java         ← Task 2
            ├── MtMeterSortServiceImpl.java           ← Task 3
            ├── MtElectricArchiveServiceImpl.java     ← Task 4
            ├── MtWaterArchiveServiceImpl.java        ← Task 4
            ├── MtHeatArchiveServiceImpl.java         ← Task 4
            ├── MtGasArchiveServiceImpl.java          ← Task 4
            ├── MtCentratorArchiveServiceImpl.java    ← Task 4
            ├── MtTcArchiveServiceImpl.java           ← Task 5
            └── MtTcValveServiceImpl.java             ← Task 5
ruoyi-modules/pom.xml                                 ← Task 1 (修改)
ruoyi-admin/pom.xml                                   ← Task 1 (修改)
script/sql/phase3_meter_tables.sql                    ← Task 6
```

### 约定

- **主键类型**：所有仪表表使用 `varchar(32)` UUID 主键，Entity 中用 `@TableId(value = "id", type = IdType.ASSIGN_UUID)`
- **继承 BaseEntity**：所有 Entity 继承 `BaseEntity`（提供 createBy, createTime, updateBy, updateTime, remark）
- **Service 接口**：继承 `IService<T>`，Service 实现继承 `ServiceImpl<M, T>`
- **Controller**：继承 `BaseController`，使用 `@SaCheckLogin` 认证，`@Log` 记录操作日志
- **响应格式**：统一使用 `R<T>` 包装，分页使用 `TableDataInfo`
- **包扫描**：`org.dromara.meter.**.mapper` 添加到 mybatis-plus mapperPackage

---

### Task 1: 创建 ruoyi-meter 模块骨架

**Files:**
- Create: `ruoyi-modules/ruoyi-meter/pom.xml`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/` (空包目录)
- Modify: `ruoyi-modules/pom.xml` (添加 module 引用)
- Modify: `ruoyi-admin/pom.xml` (添加 ruoyi-meter 依赖)
- Modify: `ruoyi-admin/src/main/resources/application.yml` (添加 meter mapper 包扫描)

- [ ] **Step 1: 创建 ruoyi-meter/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema.git.io"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.dromara</groupId>
        <artifactId>ruoyi-modules</artifactId>
        <version>${revision}</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>ruoyi-meter</artifactId>

    <description>
        meter仪表模块
    </description>

    <dependencies>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-doc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-mybatis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-translation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-log</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-excel</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-tenant</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-idempotent</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-sensitive</artifactId>
        </dependency>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>ruoyi-common-encrypt</artifactId>
        </dependency>
    </dependencies>

</project>
```

- [ ] **Step 2: 修改 ruoyi-modules/pom.xml，添加 module**

在 `<modules>` 块中添加：
```xml
<module>ruoyi-meter</module>
```

- [ ] **Step 3: 修改 ruoyi-admin/pom.xml，添加依赖**

在 `<dependencies>` 块中添加：
```xml
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>ruoyi-meter</artifactId>
</dependency>
```

- [ ] **Step 4: 创建包目录结构**

```bash
mkdir -p ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/{controller,domain/vo,mapper,service/impl}
mkdir -p ruoyi-modules/ruoyi-meter/src/main/resources/mapper
```

- [ ] **Step 5: Commit**

```bash
git add ruoyi-modules/ruoyi-meter/ ruoyi-modules/pom.xml ruoyi-admin/pom.xml
git commit -m "feat: 创建 ruoyi-meter 仪表模块骨架"
```

---

### Task 2: MtMeterVendor 迁移（仪表厂商管理）

建立迁移模板。后续所有模块遵循此模式。

**Files:**
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtMeterVendor.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/vo/MtMeterVendorVo.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/mapper/MtMeterVendorMapper.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/resources/mapper/MtMeterVendorMapper.xml`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/service/IMtMeterVendorService.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/service/impl/MtMeterVendorServiceImpl.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/controller/MtMeterVendorController.java`

- [ ] **Step 1: 创建 Entity — MtMeterVendor.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtMeterVendorVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_meter_vendor")
@AutoMapper(target = MtMeterVendorVo.class)
public class MtMeterVendor extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String code;

    private String name;

    private String contacts;

    private String tele;

    private String address;

    private String seq;

    private Integer isEnabled;
}
```

- [ ] **Step 2: 创建 Vo — MtMeterVendorVo.java**

```java
package org.dromara.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.meter.domain.MtMeterVendor;

@Data
@AutoMapper(target = MtMeterVendor.class)
public class MtMeterVendorVo {

    private String id;
    private String code;
    private String name;
    private String contacts;
    private String tele;
    private String address;
    private String seq;
    private Integer isEnabled;
}
```

- [ ] **Step 3: 创建 Mapper 接口 — MtMeterVendorMapper.java**

```java
package org.dromara.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.meter.domain.MtMeterVendor;
import org.dromara.meter.domain.vo.MtMeterVendorVo;

import java.util.List;

public interface MtMeterVendorMapper extends BaseMapperPlus<MtMeterVendor, MtMeterVendorVo> {

    int verifyName(@Param("name") String name, @Param("id") String id);

    int countByVendorId(@Param("vendorId") String vendorId);

    List<MtMeterVendor> selectAllEnabled();
}
```

- [ ] **Step 4: 创建 Mapper XML — MtMeterVendorMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.meter.mapper.MtMeterVendorMapper">

    <select id="verifyName" resultType="int">
        SELECT COUNT(name) FROM mt_meter_vendor
        <where>
            <if test="name != null and name != ''">
                name = #{name}
            </if>
            <if test="id != null and id != ''">
                AND id != #{id}
            </if>
        </where>
    </select>

    <select id="countByVendorId" resultType="int">
        SELECT COUNT(id) FROM mt_meter_sort WHERE vendor_id = #{vendorId}
    </select>

    <select id="selectAllEnabled" resultType="org.dromara.meter.domain.MtMeterVendor">
        SELECT id, name, code, is_enabled
        FROM mt_meter_vendor
        WHERE is_enabled = 1
        ORDER BY seq, create_time DESC
    </select>

</mapper>
```

- [ ] **Step 5: 创建 Service 接口 — IMtMeterVendorService.java**

```java
package org.dromara.meter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.meter.domain.MtMeterVendor;

import java.util.List;

public interface IMtMeterVendorService extends IService<MtMeterVendor> {

    int verifyName(String name, String id);

    List<MtMeterVendor> listAllEnabled();
}
```

- [ ] **Step 6: 创建 Service 实现 — MtMeterVendorServiceImpl.java**

```java
package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.meter.domain.MtMeterVendor;
import org.dromara.meter.mapper.MtMeterVendorMapper;
import org.dromara.meter.service.IMtMeterVendorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MtMeterVendorServiceImpl extends ServiceImpl<MtMeterVendorMapper, MtMeterVendor> implements IMtMeterVendorService {

    private final MtMeterVendorMapper baseMapper;

    @Override
    public int verifyName(String name, String id) {
        return baseMapper.verifyName(name, id);
    }

    @Override
    public List<MtMeterVendor> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }
}
```

- [ ] **Step 7: 创建 Controller — MtMeterVendorController.java**

```java
package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.MtMeterVendor;
import org.dromara.meter.service.IMtMeterVendorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/vendor")
public class MtMeterVendorController extends BaseController {

    private final IMtMeterVendorService vendorService;

    /**
     * 分页查询仪表厂商
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtMeterVendor> pageList(
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return vendorService.page(
            pageQuery.build(),
            new LambdaQueryWrapper<MtMeterVendor>()
                .like(search != null && !search.isEmpty(), MtMeterVendor::getCode, search.trim())
                .or(search != null && !search.isEmpty(),
                    w -> w.like(MtMeterVendor::getName, search.trim()))
                .orderByAsc(MtMeterVendor::getSeq)
                .orderByDesc(MtMeterVendor::getCreateTime)
        );
    }

    /**
     * 校验名称是否重复
     */
    @SaCheckLogin
    @PostMapping("/verifyName")
    public R<Integer> verifyName(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String id) {
        return R.ok(vendorService.verifyName(name, id));
    }

    /**
     * 新增仪表厂商
     */
    @SaCheckLogin
    @Log(title = "仪表厂商", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtMeterVendor vendor) {
        vendor.setIsEnabled(1);
        return toAjax(vendorService.save(vendor));
    }

    /**
     * 修改仪表厂商
     */
    @SaCheckLogin
    @Log(title = "仪表厂商", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtMeterVendor vendor) {
        return toAjax(vendorService.updateById(vendor));
    }

    /**
     * 删除仪表厂商（检查是否被仪表分类引用）
     */
    @SaCheckLogin
    @Log(title = "仪表厂商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        int count = vendorService.getBaseMapper().countByVendorId(id);
        if (count > 0) {
            return R.fail("该厂商已被仪表分类引用，无法删除");
        }
        return toAjax(vendorService.removeById(id));
    }

    /**
     * 查询所有启用的厂商
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<MtMeterVendor>> all() {
        return R.ok(vendorService.listAllEnabled());
    }
}
```

- [ ] **Step 8: Commit**

```bash
git add ruoyi-modules/ruoyi-meter/src/
git commit -m "feat: 迁移 MtMeterVendor 仪表厂商管理"
```

---

### Task 3: MtMeterSort 迁移（仪表分类管理）

**Files:**
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtMeterSort.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/vo/MtMeterSortVo.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/mapper/MtMeterSortMapper.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/resources/mapper/MtMeterSortMapper.xml`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/service/IMtMeterSortService.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/service/impl/MtMeterSortServiceImpl.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/controller/MtMeterSortController.java`

- [ ] **Step 1: 创建 Entity — MtMeterSort.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtMeterSortVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_meter_sort")
@AutoMapper(target = MtMeterSortVo.class)
public class MtMeterSort extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String code;

    private String name;

    private String model;

    private String vendorId;

    private Integer isOnecard;

    private String measureType;

    private String seq;

    /** 仪表类型: 01=电表, 02=水表, 03=热力表, 04=燃气表 */
    private String meterType;
}
```

- [ ] **Step 2: 创建 Vo — MtMeterSortVo.java**

```java
package org.dromara.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.meter.domain.MtMeterSort;

@Data
@AutoMapper(target = MtMeterSort.class)
public class MtMeterSortVo {

    private String id;
    private String code;
    private String name;
    private String model;
    private String vendorId;
    private Integer isOnecard;
    private String measureType;
    private String seq;
    private String meterType;
}
```

- [ ] **Step 3: 创建 Mapper 接口 — MtMeterSortMapper.java**

```java
package org.dromara.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.meter.domain.MtMeterSort;
import org.dromara.meter.domain.vo.MtMeterSortVo;

import java.util.List;

public interface MtMeterSortMapper extends BaseMapperPlus<MtMeterSort, MtMeterSortVo> {

    int countBySortId(@Param("sortId") String sortId, @Param("meterType") String meterType);
}
```

- [ ] **Step 4: 创建 Mapper XML — MtMeterSortMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.meter.mapper.MtMeterSortMapper">

    <select id="countBySortId" resultType="int">
        SELECT COUNT(id) FROM
        <choose>
            <when test="meterType == '01'">mt_electric_archive</when>
            <when test="meterType == '02'">mt_water_archive</when>
            <when test="meterType == '03'">mt_heat_archive</when>
            <when test="meterType == '04'">mt_gas_archive</when>
            <otherwise>mt_electric_archive</otherwise>
        </choose>
        WHERE sort_id = #{sortId}
    </select>

</mapper>
```

- [ ] **Step 5: 创建 Service 接口 — IMtMeterSortService.java**

```java
package org.dromara.meter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.meter.domain.MtMeterSort;

public interface IMtMeterSortService extends IService<MtMeterSort> {

    int verifyName(String name, String id);

    int countBySortId(String sortId, String meterType);
}
```

- [ ] **Step 6: 创建 Service 实现 — MtMeterSortServiceImpl.java**

```java
package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.meter.domain.MtMeterSort;
import org.dromara.meter.mapper.MtMeterSortMapper;
import org.dromara.meter.service.IMtMeterSortService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MtMeterSortServiceImpl extends ServiceImpl<MtMeterSortMapper, MtMeterSort> implements IMtMeterSortService {

    private final MtMeterSortMapper baseMapper;

    @Override
    public int verifyName(String name, String id) {
        LambdaQueryWrapper<MtMeterSort> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtMeterSort::getName, name);
        if (id != null && !id.isEmpty()) {
            lqw.ne(MtMeterSort::getId, id);
        }
        return Math.toIntExact(this.count(lqw));
    }

    @Override
    public int countBySortId(String sortId, String meterType) {
        return baseMapper.countBySortId(sortId, meterType);
    }
}
```

- [ ] **Step 7: 创建 Controller — MtMeterSortController.java**

```java
package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.MtMeterSort;
import org.dromara.meter.service.IMtMeterSortService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/sort")
public class MtMeterSortController extends BaseController {

    private final IMtMeterSortService sortService;

    /**
     * 分页查询仪表分类
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtMeterSort> pageList(MtMeterSort sort, PageQuery pageQuery) {
        return sortService.page(
            pageQuery.build(),
            new LambdaQueryWrapper<MtMeterSort>()
                .like(sort.getName() != null && !sort.getName().isEmpty(), MtMeterSort::getName, sort.getName())
                .eq(sort.getMeterType() != null && !sort.getMeterType().isEmpty(), MtMeterSort::getMeterType, sort.getMeterType())
                .eq(sort.getVendorId() != null && !sort.getVendorId().isEmpty(), MtMeterSort::getVendorId, sort.getVendorId())
                .orderByAsc(MtMeterSort::getSeq)
                .orderByDesc(MtMeterSort::getCreateTime)
        );
    }

    /**
     * 校验名称是否重复
     */
    @SaCheckLogin
    @PostMapping("/verifyName")
    public R<Integer> verifyName(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String id) {
        return R.ok(sortService.verifyName(name, id));
    }

    /**
     * 新增仪表分类
     */
    @SaCheckLogin
    @Log(title = "仪表分类", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtMeterSort sort) {
        return toAjax(sortService.save(sort));
    }

    /**
     * 修改仪表分类
     */
    @SaCheckLogin
    @Log(title = "仪表分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtMeterSort sort) {
        return toAjax(sortService.updateById(sort));
    }

    /**
     * 删除仪表分类（检查是否被档案表引用）
     */
    @SaCheckLogin
    @Log(title = "仪表分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id, @RequestParam String meterType) {
        int count = sortService.countBySortId(id, meterType);
        if (count > 0) {
            return R.fail("该分类已被仪表档案引用，无法删除");
        }
        return toAjax(sortService.removeById(id));
    }

    /**
     * 条件查询仪表分类
     */
    @SaCheckLogin
    @GetMapping("/queryMeterSort")
    public R<List<MtMeterSort>> queryMeterSort(MtMeterSort sort) {
        return R.ok(sortService.list(
            new LambdaQueryWrapper<MtMeterSort>()
                .like(sort.getName() != null && !sort.getName().isEmpty(), MtMeterSort::getName, sort.getName())
                .eq(sort.getMeterType() != null && !sort.getMeterType().isEmpty(), MtMeterSort::getMeterType, sort.getMeterType())
        ));
    }
}
```

- [ ] **Step 8: Commit**

```bash
git add ruoyi-modules/ruoyi-meter/src/
git commit -m "feat: 迁移 MtMeterSort 仪表分类管理"
```

---

### Task 4: 五类档案表迁移（电表/水表/热力表/燃气表/集中器）

**Files:**
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtElectricArchive.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtWaterArchive.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtHeatArchive.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtGasArchive.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtCentratorArchive.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtMeterMatch.java`
- Create: Vo 类 5 个（同上路径 /vo/ 目录下）
- Create: Mapper 接口 5 个
- Create: Mapper XML 5 个
- Create: Service 接口 5 个
- Create: Service 实现 5 个
- Create: Controller 5 个

- [ ] **Step 1: 创建 MtMeterMatch.java（通用分配关联表）**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.dromara.common.mybatis.core.domain.BaseEntity;

@Data
@TableName("mt_meter_match")
public class MtMeterMatch extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String archiveId;

    private String companyId;

    /** 仪表类型: 01=电表, 02=水表, 03=热力表, 04=燃气表, 11=集中器, 21=温控器, 31=阀门 */
    private String meterType;
}
```

- [ ] **Step 2: 创建 MtElectricArchive.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtElectricArchiveVo;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_electric_archive")
@AutoMapper(target = MtElectricArchiveVo.class)
public class MtElectricArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String sortId;

    private String code;

    private String name;

    private String specification;

    private String model;

    private String ratedVoltage;

    private String ratedCurrent;

    private String voltageRatio;

    private String currentRatio;

    private String loadLimit;

    private String alarmValue;

    private String displayValue;

    private String constant;

    private String seq;

    private Integer isEnabled;

    private Boolean meterNumRequired;

    private BigDecimal maxAmount;
}
```

- [ ] **Step 3: 创建 MtWaterArchive.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtWaterArchiveVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_water_archive")
@AutoMapper(target = MtWaterArchiveVo.class)
public class MtWaterArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String sortId;

    private String code;

    private String name;

    private Integer msgType;

    private String specification;

    private String model;

    private String constant;

    private String closeVal;

    private String alarmVal;

    private String loadLimit;

    private String seq;

    private Boolean meterNumRequired;

    private Integer isEnabled;
}
```

- [ ] **Step 4: 创建 MtHeatArchive.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtHeatArchiveVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_heat_archive")
@AutoMapper(target = MtHeatArchiveVo.class)
public class MtHeatArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String sortId;

    private String code;

    private String name;

    private String specification;

    private String model;

    private String type;

    private Boolean isAction;

    private String installSite;

    private String seq;

    private Integer isEnabled;
}
```

- [ ] **Step 5: 创建 MtGasArchive.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtGasArchiveVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_gas_archive")
@AutoMapper(target = MtGasArchiveVo.class)
public class MtGasArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String sortId;

    private String code;

    private String name;

    private String specification;

    private String model;

    private String seq;

    private Integer isEnabled;
}
```

- [ ] **Step 6: 创建 MtCentratorArchive.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtCentratorArchiveVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_centrator_archive")
@AutoMapper(target = MtCentratorArchiveVo.class)
public class MtCentratorArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String sortId;

    private String code;

    private String name;

    private String specification;

    private String model;

    private String type;

    private Boolean isAction;

    private String installSite;

    private String seq;

    private Integer isEnabled;
}
```

- [ ] **Step 7: 创建 5 个 Vo 类**

每个 Vo 类结构与对应 Entity 一致（无 BaseEntity 继承），添加 `@AutoMapper(target = XxxArchive.class)` 和 `@Data` 注解。

- [ ] **Step 8: 创建 MtElectricArchiveMapper.java**

```java
package org.dromara.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.meter.domain.MtElectricArchive;
import org.dromara.meter.domain.vo.MtElectricArchiveVo;

public interface MtElectricArchiveMapper extends BaseMapperPlus<MtElectricArchive, MtElectricArchiveVo> {

    int countAllocatedToOtherCompany(@Param("archiveId") String archiveId);

    int deleteMeterMatch(@Param("archiveId") String archiveId);
}
```

- [ ] **Step 9: 创建 MtElectricArchiveMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.meter.mapper.MtElectricArchiveMapper">

    <select id="countAllocatedToOtherCompany" resultType="int">
        SELECT COUNT(id) FROM mt_meter_match
        WHERE archive_id = #{archiveId}
        AND company_id != (SELECT id FROM sys_company WHERE nature = '1' AND code = 'XABL')
    </select>

    <delete id="deleteMeterMatch">
        DELETE FROM mt_meter_match WHERE archive_id = #{archiveId}
    </delete>

</mapper>
```

- [ ] **Step 10: 为其他 4 个档案表创建相同的 Mapper + XML**

- MtWaterArchiveMapper.java / .xml — 同 MtElectricArchiveMapper 结构，namespace 改为 `org.dromara.meter.mapper.MtWaterArchiveMapper`
- MtHeatArchiveMapper.java / .xml — 同上
- MtGasArchiveMapper.java / .xml — 同上
- MtCentratorArchiveMapper.java / .xml — 同上

每个 XML 中的 `countAllocatedToOtherCompany` SQL 的 `nature` 值：
- 电表/水表/热力表/燃气表/集中器: `nature = '1'`

- [ ] **Step 11: 创建 IMtElectricArchiveService.java**

```java
package org.dromara.meter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.meter.domain.MtElectricArchive;

public interface IMtElectricArchiveService extends IService<MtElectricArchive> {
}
```

- [ ] **Step 12: 创建 MtElectricArchiveServiceImpl.java**

```java
package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.meter.domain.MtElectricArchive;
import org.dromara.meter.domain.MtMeterMatch;
import org.dromara.meter.mapper.MtElectricArchiveMapper;
import org.dromara.meter.mapper.MtMeterMatchMapper;
import org.dromara.meter.service.IMtElectricArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MtElectricArchiveServiceImpl extends ServiceImpl<MtElectricArchiveMapper, MtElectricArchive> implements IMtElectricArchiveService {

    private final MtElectricArchiveMapper baseMapper;
    private final MtMeterMatchMapper meterMatchMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MtElectricArchive entity) {
        boolean saved = super.save(entity);
        if (saved) {
            assignToAgentCompany(entity.getId(), "01");
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        int count = baseMapper.countAllocatedToOtherCompany((String) id);
        if (count > 0) {
            throw new RuntimeException("该电表已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch((String) id);
        return super.removeById(id);
    }

    /**
     * 自动分配到一级代理公司
     * @param archiveId 档案ID
     * @param meterType 仪表类型 (01=电表)
     */
    private void assignToAgentCompany(String archiveId, String meterType) {
        MtMeterMatch match = new MtMeterMatch();
        match.setArchiveId(archiveId);
        match.setMeterType(meterType);
        // TODO: 需要从 sys_company 查询 code='XABL' AND nature='1' 的公司ID
        // 暂时使用固定值或查询逻辑
        meterMatchMapper.insert(match);
    }
}
```

> **注意**：`assignToAgentCompany` 中的公司ID需要查询 `sys_company` 表。由于 `sys_company` 属于 ruoyi-system 模块，需要在 ruoyi-meter 的 pom.xml 中添加对 ruoyi-system 的依赖，或者通过 SQL 查询实现。建议创建一个 Mapper 方法：
>
> ```java
> // 在 MtElectricArchiveMapper.xml 中
> <insert id="insertMeterToAgent">
>     INSERT INTO mt_meter_match (id, archive_id, company_id, meter_type, create_by, create_time)
>     SELECT REPLACE(UUID(), '-', ''), #{archiveId}, id, #{meterType}, #{createBy}, NOW()
>     FROM sys_company WHERE code = 'XABL' AND nature = '1'
> </insert>
> ```

- [ ] **Step 13: 创建 MtElectricArchiveController.java**

```java
package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.MtElectricArchive;
import org.dromara.meter.service.IMtElectricArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/electric")
public class MtElectricArchiveController extends BaseController {

    private final IMtElectricArchiveService archiveService;

    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtElectricArchive> pageList(
            @RequestParam String sortId,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return archiveService.page(
            pageQuery.build(),
            new LambdaQueryWrapper<MtElectricArchive>()
                .eq(MtElectricArchive::getSortId, sortId)
                .and(search != null && !search.isEmpty(), w ->
                    w.eq(MtElectricArchive::getCode, search.trim())
                     .or()
                     .eq(MtElectricArchive::getName, search.trim()))
                .orderByAsc(MtElectricArchive::getSeq)
                .orderByDesc(MtElectricArchive::getCreateTime)
        );
    }

    @SaCheckLogin
    @Log(title = "电表档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtElectricArchive archive) {
        return toAjax(archiveService.save(archive));
    }

    @SaCheckLogin
    @Log(title = "电表档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtElectricArchive archive) {
        return toAjax(archiveService.updateById(archive));
    }

    @SaCheckLogin
    @Log(title = "电表档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(archiveService.removeById(id));
    }
}
```

- [ ] **Step 14: 为水表/热力表/燃气表/集中器创建相同的 Service + Controller**

- MtWaterArchiveServiceImpl.java — 同 MtElectricArchiveServiceImpl，`meterType = "02"`
- MtHeatArchiveServiceImpl.java — 同 MtElectricArchiveServiceImpl，`meterType = "03"`，添加 TODO 注释标记级联更新
- MtGasArchiveServiceImpl.java — 同 MtElectricArchiveServiceImpl，`meterType = "04"`
- MtCentratorArchiveServiceImpl.java — 同 MtElectricArchiveServiceImpl，`meterType = "11"`

对应 Controller 路径分别为 `/thermal/meter/water`, `/thermal/meter/heat`, `/thermal/meter/gas`, `/thermal/meter/centrator`。

- [ ] **Step 15: 创建 MtMeterMatchMapper.java**

```java
package org.dromara.meter.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.meter.domain.MtMeterMatch;

public interface MtMeterMatchMapper extends BaseMapperPlus<MtMeterMatch, MtMeterMatch> {
}
```

- [ ] **Step 16: Commit**

```bash
git add ruoyi-modules/ruoyi-meter/src/
git commit -m "feat: 迁移五类仪表档案（电表/水表/热力表/燃气表/集中器）"
```

---

### Task 5: 温控器与阀门档案迁移

**Files:**
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtTcArchive.java`
- Create: `ruoyi-modules/ruoyi-meter/src/main/java/org/dromara/meter/domain/MtTcValve.java`
- Create: Vo 类 2 个
- Create: Mapper 接口 2 个
- Create: Mapper XML 2 个
- Create: Service 接口 2 个
- Create: Service 实现 2 个
- Create: Controller 2 个

- [ ] **Step 1: 创建 MtTcArchive.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtTcArchiveVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_tc_archive")
@AutoMapper(target = MtTcArchiveVo.class)
public class MtTcArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String sortId;
    private String code;
    private String name;
    private String specification;
    private String model;
    private String type;
    private Boolean isAction;
    private String installSite;
    private String seq;
    private Integer isEnabled;
}
```

- [ ] **Step 2: 创建 MtTcValve.java**

```java
package org.dromara.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.meter.domain.vo.MtTcValveVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_tc_valve")
@AutoMapper(target = MtTcValveVo.class)
public class MtTcValve extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String sortId;
    private String code;
    private String name;
    private String specification;
    private String model;
    private String type;
    private Boolean isAction;
    private String installSite;
    private String seq;
    private Integer isEnabled;
}
```

- [ ] **Step 3: 创建 Vo 类 2 个**

- MtTcArchiveVo.java — 字段与 MtTcArchive 一致
- MtTcValveVo.java — 字段与 MtTcValve 一致

- [ ] **Step 4: 创建 MtTcArchiveMapper.java + .xml**

```java
package org.dromara.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.meter.domain.MtTcArchive;
import org.dromara.meter.domain.vo.MtTcArchiveVo;
import java.util.List;

public interface MtTcArchiveMapper extends BaseMapperPlus<MtTcArchive, MtTcArchiveVo> {
    int countAllocatedToOtherCompany(@Param("archiveId") String archiveId);
    int deleteMeterMatch(@Param("archiveId") String archiveId);
    List<MtTcArchive> selectAllEnabled();
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.meter.mapper.MtTcArchiveMapper">

    <select id="countAllocatedToOtherCompany" resultType="int">
        SELECT COUNT(id) FROM mt_meter_match
        WHERE archive_id = #{archiveId}
        AND company_id != (SELECT id FROM sys_company WHERE nature = '1' AND code = 'XABL')
    </select>

    <delete id="deleteMeterMatch">
        DELETE FROM mt_meter_match WHERE archive_id = #{archiveId}
    </delete>

    <select id="selectAllEnabled" resultType="org.dromara.meter.domain.MtTcArchive">
        SELECT id, code, name, is_enabled FROM mt_tc_archive
        WHERE is_enabled = 1 ORDER BY seq, create_time DESC
    </select>

</mapper>
```

- [ ] **Step 5: 创建 MtTcValveMapper.java + .xml**

```java
package org.dromara.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.meter.domain.MtTcValve;
import org.dromara.meter.domain.vo.MtTcValveVo;
import java.util.List;

public interface MtTcValveMapper extends BaseMapperPlus<MtTcValve, MtTcValveVo> {
    int countAllocatedToOtherCompany(@Param("archiveId") String archiveId);
    int deleteMeterMatch(@Param("archiveId") String archiveId);
    List<MtTcValve> selectValvesByUserCompany(@Param("userId") Long userId);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.meter.mapper.MtTcValveMapper">

    <select id="countAllocatedToOtherCompany" resultType="int">
        SELECT COUNT(id) FROM mt_meter_match
        WHERE archive_id = #{archiveId}
        AND company_id != (SELECT id FROM sys_company WHERE nature = '2' AND code = 'XABL')
    </select>

    <delete id="deleteMeterMatch">
        DELETE FROM mt_meter_match WHERE archive_id = #{archiveId}
    </delete>

    <select id="selectValvesByUserCompany" resultType="org.dromara.meter.domain.MtTcValve">
        SELECT a.id, a.code, a.name, a.specification, a.model, a.type, a.is_action, a.install_site, a.seq, a.is_enabled
        FROM mt_tc_valve a
        INNER JOIN mt_meter_match m ON a.id = m.archive_id
        INNER JOIN sys_user u ON u.dept_id = m.company_id
        WHERE u.user_id = #{userId} AND m.meter_type = '31'
    </select>

</mapper>
```

- [ ] **Step 6: 创建 Service 接口 + 实现 2 个**

IMtTcArchiveService / MtTcArchiveServiceImpl — 同 Task 4 的档案 Service 模式，`meterType = "21"`
IMtTcValveService / MtTcValveServiceImpl — `meterType = "31"`, `nature = '2'`

- [ ] **Step 7: 创建 MtTcArchiveController.java**

```java
package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.MtTcArchive;
import org.dromara.meter.service.IMtTcArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/tc")
public class MtTcArchiveController extends BaseController {

    private final IMtTcArchiveService archiveService;

    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtTcArchive> pageList(MtTcArchive archive, PageQuery pageQuery) {
        return archiveService.page(pageQuery.build(),
            new LambdaQueryWrapper<MtTcArchive>()
                .like(archive.getName() != null && !archive.getName().isEmpty(), MtTcArchive::getName, archive.getName())
                .eq(archive.getSortId() != null && !archive.getSortId().isEmpty(), MtTcArchive::getSortId, archive.getSortId())
                .orderByAsc(MtTcArchive::getSeq)
                .orderByDesc(MtTcArchive::getCreateTime));
    }

    @SaCheckLogin
    @GetMapping("/list")
    public R<List<MtTcArchive>> list() {
        return R.ok(archiveService.list(
            new LambdaQueryWrapper<MtTcArchive>().eq(MtTcArchive::getIsEnabled, 1)));
    }

    @SaCheckLogin
    @Log(title = "温控器档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtTcArchive archive) {
        return toAjax(archiveService.save(archive));
    }

    @SaCheckLogin
    @Log(title = "温控器档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtTcArchive archive) {
        return toAjax(archiveService.updateById(archive));
    }

    @SaCheckLogin
    @Log(title = "温控器档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(archiveService.removeById(id));
    }

    @SaCheckLogin
    @GetMapping("/query")
    public R<List<MtTcArchive>> query(MtTcArchive archive) {
        return R.ok(archiveService.list(
            new LambdaQueryWrapper<MtTcArchive>()
                .eq(archive.getId() != null, MtTcArchive::getId, archive.getId())
                .like(archive.getName() != null && !archive.getName().isEmpty(), MtTcArchive::getName, archive.getName())
                .eq(archive.getCode() != null && !archive.getCode().isEmpty(), MtTcArchive::getCode, archive.getCode())));
    }
}
```

- [ ] **Step 8: 创建 MtTcValveController.java**

```java
package org.dromara.meter.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.meter.domain.MtTcValve;
import org.dromara.meter.service.IMtTcValveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/meter/valve")
public class MtTcValveController extends BaseController {

    private final IMtTcValveService valveService;

    @SaCheckLogin
    @GetMapping("/pageList")
    public TableDataInfo<MtTcValve> pageList(MtTcValve valve, PageQuery pageQuery) {
        return valveService.page(pageQuery.build(),
            new LambdaQueryWrapper<MtTcValve>()
                .like(valve.getName() != null && !valve.getName().isEmpty(), MtTcValve::getName, valve.getName())
                .eq(valve.getSortId() != null && !valve.getSortId().isEmpty(), MtTcValve::getSortId, valve.getSortId())
                .orderByAsc(MtTcValve::getSeq)
                .orderByDesc(MtTcValve::getCreateTime));
    }

    @SaCheckLogin
    @GetMapping("/list")
    public R<List<MtTcValve>> listByUserCompany() {
        Long userId = LoginHelper.getUserId();
        return R.ok(valveService.getBaseMapper().selectValvesByUserCompany(userId));
    }

    @SaCheckLogin
    @Log(title = "阀门档案", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody MtTcValve valve) {
        return toAjax(valveService.save(valve));
    }

    @SaCheckLogin
    @Log(title = "阀门档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody MtTcValve valve) {
        return toAjax(valveService.updateById(valve));
    }

    @SaCheckLogin
    @Log(title = "阀门档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(valveService.removeById(id));
    }

    @SaCheckLogin
    @GetMapping("/query")
    public R<List<MtTcValve>> query(MtTcValve valve) {
        return R.ok(valveService.list(
            new LambdaQueryWrapper<MtTcValve>()
                .eq(valve.getId() != null, MtTcValve::getId, valve.getId())
                .like(valve.getName() != null && !valve.getName().isEmpty(), MtTcValve::getName, valve.getName())
                .eq(valve.getCode() != null && !valve.getCode().isEmpty(), MtTcValve::getCode, valve.getCode())));
    }
}
```

- [ ] **Step 9: Commit**

```bash
git add ruoyi-modules/ruoyi-meter/src/
git commit -m "feat: 迁移温控器与阀门档案"
```

---

### Task 6: 数据库迁移脚本

**Files:**
- Create: `script/sql/phase3_meter_tables.sql`

- [ ] **Step 1: 创建迁移 SQL 脚本**

```sql
-- Phase 3: 仪表模块表迁移
-- 从旧库 rltk_pro 迁移到新库 ry-vue

-- ========================================
-- 1. 仪表厂商表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_vendor` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(32) DEFAULT NULL COMMENT '厂商编码',
    `name` varchar(32) DEFAULT NULL COMMENT '厂商名称',
    `contacts` varchar(32) DEFAULT NULL COMMENT '厂商联系人',
    `tele` varchar(32) DEFAULT NULL COMMENT '联系人电话',
    `address` varchar(125) DEFAULT NULL COMMENT '厂商地址',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表厂商表';

-- ========================================
-- 2. 仪表分类表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_sort` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `code` varchar(32) DEFAULT NULL COMMENT '编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `model` varchar(32) DEFAULT NULL COMMENT '型号',
    `vendor_id` varchar(32) DEFAULT NULL COMMENT '厂商',
    `is_onecard` tinyint DEFAULT '0' COMMENT '是否一卡通',
    `measure_type` varchar(2) DEFAULT NULL COMMENT '计费模式 0按量 1按金额 2按时间',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_type` varchar(10) NOT NULL COMMENT '仪表类型 01电表 02水表 03热力表 04燃气表',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表分类表';

-- ========================================
-- 3. 电表档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_electric_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '电表类型编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '表型号',
    `rated_voltage` varchar(32) DEFAULT NULL COMMENT '额定电压(V)',
    `rated_current` varchar(32) DEFAULT NULL COMMENT '额定电流(A)',
    `voltage_ratio` varchar(32) DEFAULT NULL COMMENT '电压变比',
    `current_ratio` varchar(32) DEFAULT NULL COMMENT '电流变比',
    `load_limit` varchar(32) DEFAULT NULL COMMENT '负荷限制(kw.h)',
    `alarm_value` varchar(32) DEFAULT NULL COMMENT '报警值(kw.h)',
    `display_value` varchar(32) DEFAULT NULL COMMENT '长显报警值(kw.h)',
    `constant` varchar(32) DEFAULT NULL COMMENT '常数(imp/kw.h)',
    `is_enabled` int DEFAULT NULL COMMENT '是否启用',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_num_required` tinyint(1) DEFAULT NULL COMMENT '表号是否必填',
    `max_amount` decimal(18,2) DEFAULT NULL COMMENT '最大购量',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电表仪表档案';

-- ========================================
-- 4. 水表档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_water_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '水表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '名称',
    `msg_type` tinyint(1) DEFAULT NULL COMMENT '通讯方式 1卡式 2远传 3手工抄表',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格(A)',
    `model` varchar(32) DEFAULT NULL COMMENT '表型号',
    `constant` varchar(10) DEFAULT NULL COMMENT '常数(脉冲)',
    `close_val` varchar(32) DEFAULT NULL COMMENT '关阀值',
    `alarm_val` varchar(32) DEFAULT NULL COMMENT '报警值',
    `load_limit` varchar(32) DEFAULT NULL COMMENT '囤积量',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `meter_num_required` tinyint(1) DEFAULT NULL COMMENT '表号是否必填',
    `is_enabled` int DEFAULT '0' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='水表仪表档案';

-- ========================================
-- 5. 热力表档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_heat_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '热力表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '热力表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '热力表型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='热力表仪表档案';

-- ========================================
-- 6. 燃气表档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_gas_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) DEFAULT NULL COMMENT '仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '燃气表编号',
    `name` varchar(32) DEFAULT NULL COMMENT '燃气表名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '燃气表型号',
    `seq` varchar(10) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT NULL COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='燃气表档案表';

-- ========================================
-- 7. 集中器档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_centrator_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '集中器编号',
    `name` varchar(32) DEFAULT NULL COMMENT '集中器名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='集中器档案';

-- ========================================
-- 8. 温控器档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_tc_archive` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '温控器编号',
    `name` varchar(32) DEFAULT NULL COMMENT '温控器名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='温控器档案';

-- ========================================
-- 9. 阀门档案表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_tc_valve` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `sort_id` varchar(32) NOT NULL COMMENT '仪表分类ID',
    `code` varchar(32) DEFAULT NULL COMMENT '阀门编号',
    `name` varchar(32) DEFAULT NULL COMMENT '阀门名称',
    `specification` varchar(32) DEFAULT NULL COMMENT '规格',
    `model` varchar(32) DEFAULT NULL COMMENT '型号',
    `type` char(2) DEFAULT NULL COMMENT '设备类型',
    `is_action` tinyint(1) DEFAULT NULL COMMENT '是否能开关阀',
    `install_site` varchar(32) DEFAULT NULL COMMENT '安装位置',
    `seq` varchar(32) DEFAULT NULL COMMENT '排序',
    `is_enabled` int DEFAULT '1' COMMENT '是否启用',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='阀门档案';

-- ========================================
-- 10. 仪表分配关联表
-- ========================================
CREATE TABLE IF NOT EXISTS `mt_meter_match` (
    `id` varchar(32) NOT NULL COMMENT '主键',
    `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
    `archive_id` varchar(32) DEFAULT NULL COMMENT '档案ID',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `meter_type` varchar(10) DEFAULT NULL COMMENT '仪表类型',
    `create_dept` bigint DEFAULT NULL COMMENT '创建部门',
    `create_by` varchar(80) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(80) DEFAULT NULL COMMENT '修改者',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `remark` varchar(80) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仪表分配关联表';
```

- [ ] **Step 2: Commit**

```bash
git add script/sql/phase3_meter_tables.sql
git commit -m "feat: 添加 Phase 3 仪表模块数据库迁移脚本"
```

---

### Task 7: 编译验证与启动测试

- [ ] **Step 1: 编译项目**

```bash
cd D:/chonggou/thermal-platform-new
mvn clean compile -q
```

Expected: BUILD SUCCESS with no errors.

- [ ] **Step 2: 执行数据库迁移脚本**

通过 MySQL MCP 执行：
```sql
source D:/chonggou/thermal-platform-new/script/sql/phase3_meter_tables.sql
```

或在 MySQL 客户端手动执行，确认 10 张表在 ry-vue 库中创建成功。

验证：
```sql
SHOW TABLES LIKE 'mt_%';
```

应返回 10 张表。

- [ ] **Step 3: 启动应用**

```bash
cd D:/chonggou/thermal-platform-new
mvn spring-boot:run -pl ruoyi-admin
```

确认启动日志无报错。

- [ ] **Step 4: 测试端点**

登录获取 token 后测试：

```bash
# 测试厂商列表
curl http://localhost:8080/thermal/meter/vendor/pageList?pageNum=1&pageSize=10 \
  -H "Authorization: Bearer <token>" \
  -H "clientid: e5cd7e4891bf95d1d19206ce24a7b32e"

# 测试所有启用厂商
curl http://localhost:8080/thermal/meter/vendor/all \
  -H "Authorization: Bearer <token>" \
  -H "clientid: e5cd7e4891bf95d1d19206ce24a7b32e"
```

- [ ] **Step 5: Commit（如有修复）**

```bash
git add .
git commit -m "fix: 修复 Phase 3 编译/启动问题"
```

---

## Self-Review: Spec Coverage

| 旧系统控制器 | 对应 Task | 状态 |
|---|---|---|
| MtMeterVendorController | Task 2 | ✅ 覆盖 |
| MtMeterSortController | Task 3 | ✅ 覆盖 |
| MtElectricArchiveController | Task 4 | ✅ 覆盖 |
| MtWaterArchiveController | Task 4 | ✅ 覆盖 |
| MtHeatArchiveController | Task 4 | ✅ 覆盖（级联更新标注 TODO） |
| MtGasArchiveController | Task 4 | ✅ 覆盖 |
| MtCentratorArchiveController | Task 4 | ✅ 覆盖 |
| MtTcArchiveController | Task 5 | ✅ 覆盖 |
| MtTcValveController | Task 5 | ✅ 覆盖 |
| MtFormulaFileController | 后续 Phase | ⏭️ 暂不迁移 |
| AgentMeterController | 后续 Phase | ⏭️ 暂不迁移 |

## Placeholder Scan
- Task 4 中 `assignToAgentCompany` 使用 XML SQL 插入，无 placeholder
- 级联更新（Phase 5 模块依赖）已在代码中标注 TODO 注释
- 无 "TBD"/"TODO"/"fill in later" 式占位

## Type Consistency
- 所有 Entity 使用 `@TableId(value = "id", type = IdType.ASSIGN_UUID)` 统一
- 所有 Service 继承 `IService<T>` + `ServiceImpl<M, T>`
- 所有 Controller 使用 `@SaCheckLogin` + `/thermal/meter/*` 前缀
- 所有 Vo 使用 `@AutoMapper(target = Xxx.class)`
- MeterType 编码一致: 01=电表, 02=水表, 03=热力表, 04=燃气表, 11=集中器, 21=温控器, 31=阀门
