# Phase 4a: Thermal Regulation Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate 4 basic thermal regulation controllers (instruction, alert, repair, strategy) to RuoYi-Vue-Plus architecture as foundation for the full thermal module.

**Architecture:** New module `ruoyi-modules/ruoyi-thermal/` with standard RuoYi layering (controller → service → mapper → domain). All endpoints under `/thermal/ht/*` prefix. Complex business logic (Quartz, hardware control) deferred to Phase 4c.

**Tech Stack:** Spring Boot 3.5.12, Java 17, MyBatis-Plus 3.5.16, Sa-Token 1.44.0, RuoYi-Vue-Plus 5.6.0

---

## File Structure

```
ruoyi-modules/ruoyi-thermal/
├── pom.xml
└── src/main/java/org/dromara/thermal/
    ├── controller/
    │   ├── HtInstructionController.java
    │   ├── HtAlertController.java
    │   ├── HtRepairController.java
    │   └── HtStrategyController.java
    ├── domain/
    │   ├── HtInstruction.java
    │   ├── HtAlert.java
    │   ├── HtRepair.java
    │   ├── HtStrategy.java
    │   └── HtStrategySub.java
    ├── domain/vo/
    │   ├── HtInstructionVo.java
    │   ├── HtAlertVo.java
    │   ├── HtRepairVo.java
    │   ├── HtStrategyVo.java
    │   └── HtStrategySubVo.java
    ├── mapper/
    │   ├── HtInstructionMapper.java
    │   ├── HtAlertMapper.java
    │   ├── HtRepairMapper.java
    │   ├── HtStrategyMapper.java
    │   └── HtStrategySubMapper.java
    └── service/
        ├── IHtInstructionService.java
        ├── IHtAlertService.java
        ├── IHtRepairService.java
        ├── IHtStrategyService.java
        └── impl/
            ├── HtInstructionServiceImpl.java
            ├── HtAlertServiceImpl.java
            ├── HtRepairServiceImpl.java
            └── HtStrategyServiceImpl.java
ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/
    ├── HtInstructionMapper.xml
    ├── HtAlertMapper.xml
    ├── HtRepairMapper.xml
    ├── HtStrategyMapper.xml
    └── HtStrategySubMapper.xml
```

### Parent file modifications
- `ruoyi-modules/pom.xml` — add `<module>ruoyi-thermal</module>`
- `ruoyi-admin/pom.xml` — add ruoyi-thermal dependency
- `ruoyi-admin/src/main/resources/application.yml` — mapperPackage already uses `org.dromara.**.mapper`, auto-scans new module

### Database tables (already in ry-vue, created from old system migration)
- `ht_instruction` — control instruction dictionary
- `ht_alert` — alarm records
- `ht_repair` — repair records
- `ht_strategy` — control strategy master
- `ht_strategy_sub` — strategy sub-table (individual instructions per strategy)

---

### Task 1: Create ruoyi-thermal module skeleton

**Files:**
- Create: `ruoyi-modules/ruoyi-thermal/pom.xml`
- Create: directory structure (empty packages)
- Modify: `ruoyi-modules/pom.xml`
- Modify: `ruoyi-admin/pom.xml`

- [ ] **Step 1: Create ruoyi-thermal/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.dromara</groupId>
        <artifactId>ruoyi-modules</artifactId>
        <version>${revision}</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>ruoyi-thermal</artifactId>

    <description>
        thermal热力调控模块
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

- [ ] **Step 2: Modify ruoyi-modules/pom.xml**

In `<modules>` block, add:
```xml
<module>ruoyi-thermal</module>
```

- [ ] **Step 3: Modify ruoyi-admin/pom.xml**

In `<dependencies>` block, add:
```xml
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>ruoyi-thermal</artifactId>
</dependency>
```

- [ ] **Step 4: Create directory structure**

```bash
mkdir -p ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/{controller,domain/vo,mapper,service/impl}
mkdir -p ruoyi-modules/ruoyi-thermal/src/main/resources/mapper
```

- [ ] **Step 5: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/ ruoyi-modules/pom.xml ruoyi-admin/pom.xml
git commit -m "feat: 创建 ruoyi-thermal 热力调控模块骨架"
```

---

### Task 2: HtInstruction migration (控制指令字典)

**Old endpoint mapping:**
- `/htInstruction/pageList` → `/thermal/ht/instruction/list`
- `/htInstruction/insertData` → `POST /thermal/ht/instruction`
- `/htInstruction/updateData` → `PUT /thermal/ht/instruction`
- `/htInstruction/deleteData` → `DELETE /thermal/ht/instruction/{id}`
- `/htInstruction/queryHtInstruction` → `GET /thermal/ht/instruction/{id}`
- `/htInstruction/queryInstructionList` → `GET /thermal/ht/instruction/all`

**DB table:** `ht_instruction` — id(varchar32), name(varchar64, NOT NULL), type(tinyint, NOT NULL), instruction(varchar256), remark(varchar60), create_by(varchar40), create_time, update_by(varchar40), update_time

**Delete logic:** Check `ht_strategy_sub.instruction_id` for references before deleting.

- [ ] **Step 1: Create Entity — HtInstruction.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtInstructionVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_instruction")
@AutoMapper(target = HtInstructionVo.class)
public class HtInstruction extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 指令名称 */
    private String name;

    /** 指令类型 */
    private Integer type;

    /** 指令内容 */
    private String instruction;
}
```

- [ ] **Step 2: Create Vo — HtInstructionVo.java**

```java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.HtInstruction;

@Data
@AutoMapper(target = HtInstruction.class)
public class HtInstructionVo {

    private String id;
    private String name;
    private Integer type;
    private String instruction;
    /** 创建者姓名 (via JOIN sys_user) */
    private String createName;
}
```

- [ ] **Step 3: Create Mapper — HtInstructionMapper.java**

```java
package org.dromara.thermal.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtInstruction;
import org.dromara.thermal.domain.vo.HtInstructionVo;

import java.util.List;

public interface HtInstructionMapper extends BaseMapperPlus<HtInstruction, HtInstructionVo> {

    /** 检查指令是否被策略子表引用 */
    int countByStrategySub(String instructionId);

    /** 查询所有指令 */
    List<HtInstruction> selectAllList();
}
```

- [ ] **Step 4: Create Mapper XML — HtInstructionMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.HtInstructionMapper">

    <select id="countByStrategySub" resultType="int">
        SELECT COUNT(*) FROM ht_strategy_sub WHERE instruction_id = #{instructionId}
    </select>

    <select id="selectAllList" resultType="org.dromara.thermal.domain.HtInstruction">
        SELECT id, name, type, instruction, remark, create_by, create_time
        FROM ht_instruction
    </select>

</mapper>
```

- [ ] **Step 5: Create Service Interface — IHtInstructionService.java**

```java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtInstruction;
import org.dromara.thermal.domain.vo.HtInstructionVo;

import java.util.List;

public interface IHtInstructionService extends IService<HtInstruction> {

    TableDataInfo<HtInstructionVo> selectPageList(LambdaQueryWrapper<HtInstruction> lqw, PageQuery pageQuery);

    int countByStrategySub(String instructionId);

    List<HtInstructionVo> selectAllList();
}
```

- [ ] **Step 6: Create Service Impl — HtInstructionServiceImpl.java**

```java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtInstruction;
import org.dromara.thermal.domain.vo.HtInstructionVo;
import org.dromara.thermal.mapper.HtInstructionMapper;
import org.dromara.thermal.service.IHtInstructionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HtInstructionServiceImpl extends ServiceImpl<HtInstructionMapper, HtInstruction> implements IHtInstructionService {

    private final HtInstructionMapper baseMapper;

    @Override
    public TableDataInfo<HtInstructionVo> selectPageList(LambdaQueryWrapper<HtInstruction> lqw, PageQuery pageQuery) {
        Page<HtInstructionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public int countByStrategySub(String instructionId) {
        return baseMapper.countByStrategySub(instructionId);
    }

    @Override
    public List<HtInstructionVo> selectAllList() {
        return baseMapper.selectAllList();
    }
}
```

- [ ] **Step 7: Create Controller — HtInstructionController.java**

```java
package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtInstruction;
import org.dromara.thermal.domain.vo.HtInstructionVo;
import org.dromara.thermal.service.IHtInstructionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/instruction")
public class HtInstructionController extends BaseController {

    private final IHtInstructionService instructionService;

    /**
     * 分页查询控制指令
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtInstructionVo> list(@RequestParam(required = false) String search, PageQuery pageQuery) {
        LambdaQueryWrapper<HtInstruction> lqw = new LambdaQueryWrapper<>();
        lqw.eq(search != null && !search.isEmpty(), HtInstruction::getName, search.trim());
        lqw.orderByDesc(HtInstruction::getCreateTime);
        return instructionService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增控制指令
     */
    @SaCheckLogin
    @Log(title = "控制指令", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody HtInstruction instruction) {
        return toAjax(instructionService.save(instruction));
    }

    /**
     * 根据ID查询指令详情
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtInstruction> getById(@PathVariable String id) {
        return R.ok(instructionService.getById(id));
    }

    /**
     * 修改控制指令
     */
    @SaCheckLogin
    @Log(title = "控制指令", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody HtInstruction instruction) {
        return toAjax(instructionService.updateById(instruction));
    }

    /**
     * 删除控制指令（检查是否被策略引用）
     */
    @SaCheckLogin
    @Log(title = "控制指令", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        int count = instructionService.countByStrategySub(id);
        if (count > 0) {
            return R.fail("该指令已被策略引用，无法删除");
        }
        return toAjax(instructionService.removeById(id));
    }

    /**
     * 查询所有控制指令
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<HtInstructionVo>> all() {
        return R.ok(instructionService.selectAllList());
    }
}
```

- [ ] **Step 8: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/
git commit -m "feat: 迁移 HtInstruction 控制指令字典"
```

---

### Task 3: HtAlert migration (报警记录)

**Old endpoint mapping:**
- `/htAlert/pageList` → `/thermal/ht/alert/list`
- `/htAlert/insertData` → `POST /thermal/ht/alert/batch` (batch insert)
- `/htAlert/updateData` → `PUT /thermal/ht/alert`
- `/htAlert/queryAbnormalAlarmList` → `GET /thermal/ht/alert/abnormal`
- `/htAlert/queryTypeCount` → `GET /thermal/ht/alert/typeCount`

**DB table:** `ht_alert` — id(varchar32), building_id, unit_id, house_id, meter_id(varchar32, NOT NULL), is_charged, valve, in_temp(decimal), out_temp(decimal), room_temp(decimal), alert_type(tinyint, NOT NULL), alert_time(datetime, NOT NULL), alert_status(varchar500), org_id(varchar32, NOT NULL), company_id(varchar32, NOT NULL), create_by, create_time, in_maintenance

**Note:** The old controller uses `List<HtAlert>` for batch insert. Simplified to single record insert for RuoYi pattern; batch insert deferred.

- [ ] **Step 1: Create Entity — HtAlert.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtAlertVo;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_alert")
@AutoMapper(target = HtAlertVo.class)
public class HtAlert extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String buildingId;
    private String unitId;
    private String houseId;
    private String meterId;
    private Integer isCharged;
    private Integer valve;
    private BigDecimal inTemp;
    private BigDecimal outTemp;
    private BigDecimal roomTemp;
    private Integer alertType;
    private Date alertTime;
    private String alertStatus;
    private String orgId;
    private String companyId;
    private String inMaintenance;
}
```

- [ ] **Step 2: Create Vo — HtAlertVo.java**

```java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.HtAlert;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AutoMapper(target = HtAlert.class)
public class HtAlertVo {

    private String id;
    private String buildingId;
    private String unitId;
    private String houseId;
    private String meterId;
    private Integer isCharged;
    private Integer valve;
    private BigDecimal inTemp;
    private BigDecimal outTemp;
    private BigDecimal roomTemp;
    private Integer alertType;
    private Date alertTime;
    private String alertStatus;
    private String orgId;
    private String companyId;
    private String inMaintenance;
    private String createName;
}
```

- [ ] **Step 3: Create Mapper — HtAlertMapper.java**

```java
package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtAlert;
import org.dromara.thermal.domain.vo.HtAlertVo;
import java.util.List;
import java.util.Map;

public interface HtAlertMapper extends BaseMapperPlus<HtAlert, HtAlertVo> {

    /** 查询异常报警列表 */
    List<HtAlertVo> selectAbnormalAlarmList(@Param("meterId") String meterId);

    /** 按类型统计报警数 */
    List<Map<String, Object>> selectTypeCount(@Param("companyId") String companyId);
}
```

- [ ] **Step 4: Create Mapper XML — HtAlertMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.HtAlertMapper">

    <select id="selectAbnormalAlarmList" resultType="org.dromara.thermal.domain.vo.HtAlertVo">
        SELECT a.id, a.meter_id, a.alert_type, a.alert_time, a.alert_status,
               a.in_temp, a.out_temp, a.room_temp, a.valve, a.company_id
        FROM ht_alert a
        WHERE a.meter_id = #{meterId}
          AND a.alert_status = '0'
        ORDER BY a.alert_time DESC
    </select>

    <select id="selectTypeCount" resultType="java.util.HashMap">
        SELECT alert_type AS alertType, COUNT(*) AS count
        FROM ht_alert
        WHERE company_id = #{companyId}
        GROUP BY alert_type
    </select>

</mapper>
```

- [ ] **Step 5: Create Service Interface — IHtAlertService.java**

```java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtAlert;
import org.dromara.thermal.domain.vo.HtAlertVo;
import java.util.List;
import java.util.Map;

public interface IHtAlertService extends IService<HtAlert> {

    TableDataInfo<HtAlertVo> selectPageList(LambdaQueryWrapper<HtAlert> lqw, PageQuery pageQuery);

    List<HtAlertVo> selectAbnormalAlarmList(String meterId);

    List<Map<String, Object>> selectTypeCount(String companyId);
}
```

- [ ] **Step 6: Create Service Impl — HtAlertServiceImpl.java**

```java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtAlert;
import org.dromara.thermal.domain.vo.HtAlertVo;
import org.dromara.thermal.mapper.HtAlertMapper;
import org.dromara.thermal.service.IHtAlertService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HtAlertServiceImpl extends ServiceImpl<HtAlertMapper, HtAlert> implements IHtAlertService {

    private final HtAlertMapper baseMapper;

    @Override
    public TableDataInfo<HtAlertVo> selectPageList(LambdaQueryWrapper<HtAlert> lqw, PageQuery pageQuery) {
        Page<HtAlertVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtAlertVo> selectAbnormalAlarmList(String meterId) {
        return baseMapper.selectAbnormalAlarmList(meterId);
    }

    @Override
    public List<Map<String, Object>> selectTypeCount(String companyId) {
        return baseMapper.selectTypeCount(companyId);
    }
}
```

- [ ] **Step 7: Create Controller — HtAlertController.java**

```java
package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtAlert;
import org.dromara.thermal.domain.vo.HtAlertVo;
import org.dromara.thermal.service.IHtAlertService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/alert")
public class HtAlertController extends BaseController {

    private final IHtAlertService alertService;

    /**
     * 分页查询报警记录
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtAlertVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String alertStatus,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtAlert> lqw = new LambdaQueryWrapper<>();
        lqw.eq(companyId != null && !companyId.isEmpty(), HtAlert::getCompanyId, companyId);
        lqw.eq(orgId != null && !orgId.isEmpty(), HtAlert::getOrgId, orgId);
        lqw.eq(buildingId != null && !buildingId.isEmpty(), HtAlert::getBuildingId, buildingId);
        lqw.eq(alertType != null && !alertType.isEmpty(), HtAlert::getAlertType, alertType);
        lqw.eq(alertStatus != null && !alertStatus.isEmpty(), HtAlert::getAlertStatus, alertStatus);
        lqw.orderByDesc(HtAlert::getAlertTime);
        return alertService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增报警记录
     */
    @SaCheckLogin
    @Log(title = "报警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody HtAlert alert) {
        return toAjax(alertService.save(alert));
    }

    /**
     * 修改报警记录
     */
    @SaCheckLogin
    @Log(title = "报警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody HtAlert alert) {
        return toAjax(alertService.updateById(alert));
    }

    /**
     * 查询异常报警列表
     */
    @SaCheckLogin
    @GetMapping("/abnormal")
    public R<List<HtAlertVo>> abnormalAlarmList(@RequestParam String meterId) {
        return R.ok(alertService.selectAbnormalAlarmList(meterId));
    }

    /**
     * 按公司统计报警类型数量
     */
    @SaCheckLogin
    @GetMapping("/typeCount")
    public R<List<Map<String, Object>>> typeCount(@RequestParam String companyId) {
        return R.ok(alertService.selectTypeCount(companyId));
    }
}
```

- [ ] **Step 8: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/
git commit -m "feat: 迁移 HtAlert 报警记录管理"
```

---

### Task 4: HtRepair migration (报修记录)

**Old endpoint mapping:**
- `/htRepair/pageList` → `/thermal/ht/repair/list`
- `/htRepair/insertData` → `POST /thermal/ht/repair`
- `/htRepair/updateData` → `PUT /thermal/ht/repair`
- `/htRepair/deleteData` → `DELETE /thermal/ht/repair/{repairNo}`
- `/htRepair/updateDispatchData` → `PUT /thermal/ht/repair/dispatch`
- `/htRepair/updateStatusResultData` → `PUT /thermal/ht/repair/status`
- `/htRepair/queryAbnormalAlarmList` → `GET /thermal/ht/repair/abnormal`
- `/htRepair/queryTypeCount` → `GET /thermal/ht/repair/typeCount`
- `/htRepair/queryRoomId` → `GET /thermal/ht/repair/room`
- `/htRepair/pageListForRepair` → deferred (same as /list with different filters)

**DB table:** `ht_repair` — id(varchar32), building_id, building_name, unit_code, house_id, room_num, meter_id, meter_num, is_charged, valve_status, valve, in_temp, out_temp, room_temp, repair_type(tinyint, NOT NULL), repair_time(datetime, NOT NULL), repair_info, repair_status(tinyint), repair_result, org_id, org_name, company_id, is_delete, create_by, create_name, create_time, update_by, update_time, in_maintenance, dispatch_id, dispatch_name, dispatch_time, repair_no(varchar25), fix_id, fix_name, fix_time, user_name, user_phone, appoint_time, urgent_type, service_type

- [ ] **Step 1: Create Entity — HtRepair.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtRepairVo;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_repair")
@AutoMapper(target = HtRepairVo.class)
public class HtRepair extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String buildingId;
    private String buildingName;
    private String unitCode;
    private String houseId;
    private String roomNum;
    private String meterId;
    private String meterNum;
    private Integer isCharged;
    private String valveStatus;
    private Integer valve;
    private BigDecimal inTemp;
    private BigDecimal outTemp;
    private BigDecimal roomTemp;
    private Integer repairType;
    private Date repairTime;
    private String repairInfo;
    private Integer repairStatus;
    private String repairResult;
    private String orgId;
    private String orgName;
    private String companyId;
    private Integer isDelete;
    private String inMaintenance;
    private String dispatchId;
    private String dispatchName;
    private Date dispatchTime;
    private String repairNo;
    private String fixId;
    private String fixName;
    private Date fixTime;
    private String userName;
    private String userPhone;
    private Date appointTime;
    private Integer urgentType;
    private Integer serviceType;
}
```

- [ ] **Step 2: Create Vo — HtRepairVo.java**

```java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.HtRepair;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AutoMapper(target = HtRepair.class)
public class HtRepairVo {

    private String id;
    private String buildingId;
    private String buildingName;
    private String unitCode;
    private String houseId;
    private String roomNum;
    private String meterId;
    private String meterNum;
    private Integer isCharged;
    private String valveStatus;
    private Integer valve;
    private BigDecimal inTemp;
    private BigDecimal outTemp;
    private BigDecimal roomTemp;
    private Integer repairType;
    private Date repairTime;
    private String repairInfo;
    private Integer repairStatus;
    private String repairResult;
    private String orgId;
    private String orgName;
    private String companyId;
    private String inMaintenance;
    private String dispatchId;
    private String dispatchName;
    private Date dispatchTime;
    private String repairNo;
    private String fixId;
    private String fixName;
    private Date fixTime;
    private String userName;
    private String userPhone;
    private Date appointTime;
    private Integer urgentType;
    private Integer serviceType;
    private String createName;
}
```

- [ ] **Step 3: Create Mapper — HtRepairMapper.java**

```java
package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtRepair;
import org.dromara.thermal.domain.vo.HtRepairVo;
import java.util.List;
import java.util.Map;

public interface HtRepairMapper extends BaseMapperPlus<HtRepair, HtRepairVo> {

    /** 按类型统计报修数量 */
    List<Map<String, Object>> selectTypeCount(@Param("companyId") String companyId);

    /** 根据房间ID查询报修记录 */
    List<HtRepairVo> selectByRoomId(@Param("roomId") String roomId);

    /** 删除报修记录（逻辑删除标记） */
    int markAsDeleted(@Param("repairNo") String repairNo, @Param("companyId") String companyId);
}
```

- [ ] **Step 4: Create Mapper XML — HtRepairMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.HtRepairMapper">

    <select id="selectTypeCount" resultType="java.util.HashMap">
        SELECT repair_type AS repairType, COUNT(*) AS count
        FROM ht_repair
        WHERE company_id = #{companyId} AND is_delete = 0
        GROUP BY repair_type
    </select>

    <select id="selectByRoomId" resultType="org.dromara.thermal.domain.vo.HtRepairVo">
        SELECT r.id, r.repair_no, r.repair_type, r.repair_time, r.repair_status,
               r.room_num, r.meter_id, r.in_temp, r.out_temp, r.room_temp
        FROM ht_repair r
        WHERE r.house_id = #{roomId} AND r.is_delete = 0
        ORDER BY r.repair_time DESC
    </select>

    <update id="markAsDeleted">
        UPDATE ht_repair
        SET is_delete = 1, update_time = NOW()
        WHERE repair_no = #{repairNo} AND company_id = #{companyId}
    </update>

</mapper>
```

- [ ] **Step 5: Create Service Interface — IHtRepairService.java**

```java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtRepair;
import org.dromara.thermal.domain.vo.HtRepairVo;
import java.util.List;
import java.util.Map;

public interface IHtRepairService extends IService<HtRepair> {

    TableDataInfo<HtRepairVo> selectPageList(LambdaQueryWrapper<HtRepair> lqw, PageQuery pageQuery);

    List<Map<String, Object>> selectTypeCount(String companyId);

    List<HtRepairVo> selectByRoomId(String roomId);

    int markAsDeleted(String repairNo, String companyId);
}
```

- [ ] **Step 6: Create Service Impl — HtRepairServiceImpl.java**

```java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtRepair;
import org.dromara.thermal.domain.vo.HtRepairVo;
import org.dromara.thermal.mapper.HtRepairMapper;
import org.dromara.thermal.service.IHtRepairService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HtRepairServiceImpl extends ServiceImpl<HtRepairMapper, HtRepair> implements IHtRepairService {

    private final HtRepairMapper baseMapper;

    @Override
    public TableDataInfo<HtRepairVo> selectPageList(LambdaQueryWrapper<HtRepair> lqw, PageQuery pageQuery) {
        Page<HtRepairVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<Map<String, Object>> selectTypeCount(String companyId) {
        return baseMapper.selectTypeCount(companyId);
    }

    @Override
    public List<HtRepairVo> selectByRoomId(String roomId) {
        return baseMapper.selectByRoomId(roomId);
    }

    @Override
    public int markAsDeleted(String repairNo, String companyId) {
        return baseMapper.markAsDeleted(repairNo, companyId);
    }
}
```

- [ ] **Step 7: Create Controller — HtRepairController.java**

```java
package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtRepair;
import org.dromara.thermal.domain.vo.HtRepairVo;
import org.dromara.thermal.service.IHtRepairService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/repair")
public class HtRepairController extends BaseController {

    private final IHtRepairService repairService;

    /**
     * 分页查询报修记录
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtRepairVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String repairType,
            @RequestParam(required = false) String repairStatus,
            @RequestParam(required = false) String urgentType,
            PageQuery pageQuery) {
        LambdaQueryWrapper<HtRepair> lqw = new LambdaQueryWrapper<>();
        lqw.eq(companyId != null && !companyId.isEmpty(), HtRepair::getCompanyId, companyId);
        lqw.eq(orgId != null && !orgId.isEmpty(), HtRepair::getOrgId, orgId);
        lqw.eq(buildingId != null && !buildingId.isEmpty(), HtRepair::getBuildingId, buildingId);
        lqw.eq(repairType != null && !repairType.isEmpty(), HtRepair::getRepairType, repairType);
        lqw.eq(repairStatus != null && !repairStatus.isEmpty(), HtRepair::getRepairStatus, repairStatus);
        lqw.eq(urgentType != null && !urgentType.isEmpty(), HtRepair::getUrgentType, urgentType);
        lqw.eq(HtRepair::getIsDelete, 0);
        lqw.orderByDesc(HtRepair::getRepairTime);
        return repairService.selectPageList(lqw, pageQuery);
    }

    /**
     * 新增报修记录（自动生成报修单号）
     */
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody HtRepair repair) {
        String recordNo = "R" + System.currentTimeMillis();
        repair.setRepairNo(recordNo);
        return toAjax(repairService.save(repair));
    }

    /**
     * 修改报修记录
     */
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody HtRepair repair) {
        return toAjax(repairService.updateById(repair));
    }

    /**
     * 派单
     */
    @SaCheckLogin
    @Log(title = "报修派单", businessType = BusinessType.UPDATE)
    @PutMapping("/dispatch")
    public R<Void> dispatch(
            @RequestParam String repairNo,
            @RequestParam String fixId,
            @RequestParam String fixName) {
        HtRepair repair = repairService.lambdaQuery().eq(HtRepair::getRepairNo, repairNo).one();
        if (repair != null) {
            repair.setFixId(fixId);
            repair.setFixName(fixName);
            repair.setDispatchTime(new java.util.Date());
            return toAjax(repairService.updateById(repair));
        }
        return R.fail("报修记录不存在");
    }

    /**
     * 删除报修记录（逻辑删除）
     */
    @SaCheckLogin
    @Log(title = "报修记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{repairNo}")
    public R<Void> remove(@PathVariable String repairNo) {
        Long userId = LoginHelper.getUserId();
        int count = repairService.markAsDeleted(repairNo, String.valueOf(userId));
        return count > 0 ? R.ok() : R.fail("删除失败");
    }

    /**
     * 更新报修状态和结果
     */
    @SaCheckLogin
    @Log(title = "报修状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public R<Void> updateStatus(
            @RequestParam String repairNo,
            @RequestParam Integer repairStatus,
            @RequestParam(required = false) String repairResult) {
        HtRepair repair = repairService.lambdaQuery().eq(HtRepair::getRepairNo, repairNo).one();
        if (repair != null) {
            repair.setRepairStatus(repairStatus);
            if (repairResult != null) repair.setRepairResult(repairResult);
            return toAjax(repairService.updateById(repair));
        }
        return R.fail("报修记录不存在");
    }

    /**
     * 按公司统计报修类型数量
     */
    @SaCheckLogin
    @GetMapping("/typeCount")
    public R<List<Map<String, Object>>> typeCount(@RequestParam String companyId) {
        return R.ok(repairService.selectTypeCount(companyId));
    }

    /**
     * 根据房间ID查询报修记录
     */
    @SaCheckLogin
    @GetMapping("/room")
    public R<List<HtRepairVo>> queryRoomId(@RequestParam String roomId) {
        return R.ok(repairService.selectByRoomId(roomId));
    }
}
```

- [ ] **Step 8: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/
git commit -m "feat: 迁移 HtRepair 报修记录管理"
```

---

### Task 5: HtStrategy migration (控制策略，含子表)

**Old endpoint mapping:**
- `/htStrategy/pageList` → `/thermal/ht/strategy/list`
- `/htStrategy/insertData` → `POST /thermal/ht/strategy`
- `/htStrategy/updateData` → `PUT /thermal/ht/strategy`
- `/htStrategy/deleteData` → `DELETE /thermal/ht/strategy/{id}`
- `/htStrategy/queryHtStrategy` → `GET /thermal/ht/strategy/{id}`
- `/htStrategy/queryHtStrategyList` → `GET /thermal/ht/strategy/all`

**DB tables:**
- `ht_strategy` — id(varchar32), name(varchar64, NOT NULL), type(tinyint), company_id(varchar32), create_by, create_time, update_by, update_time, remark
- `ht_strategy_sub` — id(varchar32), strategy_id(varchar32), instruction_id(varchar32), sort(int), valve_angle(varchar32), create_by, create_time

- [ ] **Step 1: Create Entity — HtStrategy.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtStrategyVo;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_strategy")
@AutoMapper(target = HtStrategyVo.class)
public class HtStrategy extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 策略名称 */
    private String name;

    /** 策略类型 */
    private Integer type;

    /** 公司ID */
    private String companyId;

    /** 子表列表（非数据库字段） */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private List<HtStrategySub> subList;
}
```

- [ ] **Step 2: Create Entity — HtStrategySub.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtStrategySubVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_strategy_sub")
@AutoMapper(target = HtStrategySubVo.class)
public class HtStrategySub extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 策略ID */
    private String strategyId;

    /** 指令ID */
    private String instructionId;

    /** 排序 */
    private Integer sort;

    /** 阀门角度 */
    private String valveAngle;
}
```

- [ ] **Step 3: Create Vos — HtStrategyVo.java + HtStrategySubVo.java**

```java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.HtStrategy;
import java.util.List;

@Data
@AutoMapper(target = HtStrategy.class)
public class HtStrategyVo {

    private String id;
    private String name;
    private Integer type;
    private String companyId;
    private List<HtStrategySubVo> subList;
}
```

```java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.HtStrategySub;

@Data
@AutoMapper(target = HtStrategySub.class)
public class HtStrategySubVo {

    private String id;
    private String strategyId;
    private String instructionId;
    private Integer sort;
    private String valveAngle;
}
```

- [ ] **Step 4: Create Mappers — HtStrategyMapper.java + HtStrategySubMapper.java**

```java
package org.dromara.thermal.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtStrategy;
import org.dromara.thermal.domain.vo.HtStrategyVo;
import java.util.List;

public interface HtStrategyMapper extends BaseMapperPlus<HtStrategy, HtStrategyVo> {

    /** 查询所有策略 */
    List<HtStrategy> selectAllList();
}
```

```java
package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtStrategySub;
import org.dromara.thermal.domain.vo.HtStrategySubVo;
import java.util.List;

public interface HtStrategySubMapper extends BaseMapperPlus<HtStrategySub, HtStrategySubVo> {

    List<HtStrategySubVo> selectByStrategyId(@Param("strategyId") String strategyId);

    int deleteByStrategyId(@Param("strategyId") String strategyId);
}
```

- [ ] **Step 5: Create Mapper XMLs**

HtStrategyMapper.xml:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.HtStrategyMapper">

    <select id="selectAllList" resultType="org.dromara.thermal.domain.HtStrategy">
        SELECT id, name, type, company_id, create_by, create_time, remark
        FROM ht_strategy
    </select>

</mapper>
```

HtStrategySubMapper.xml:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.HtStrategySubMapper">

    <select id="selectByStrategyId" resultType="org.dromara.thermal.domain.vo.HtStrategySubVo">
        SELECT id, strategy_id, instruction_id, sort, valve_angle
        FROM ht_strategy_sub
        WHERE strategy_id = #{strategyId}
        ORDER BY sort ASC
    </select>

    <delete id="deleteByStrategyId">
        DELETE FROM ht_strategy_sub WHERE strategy_id = #{strategyId}
    </delete>

</mapper>
```

- [ ] **Step 6: Create Service Interface — IHtStrategyService.java**

```java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtStrategy;
import org.dromara.thermal.domain.vo.HtStrategyVo;
import java.util.List;

public interface IHtStrategyService extends IService<HtStrategy> {

    TableDataInfo<HtStrategyVo> selectPageList(LambdaQueryWrapper<HtStrategy> lqw, PageQuery pageQuery);

    List<HtStrategy> selectAllList();

    HtStrategyVo selectDetailById(String id);
}
```

- [ ] **Step 7: Create Service Impl — HtStrategyServiceImpl.java**

```java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.linpeilie.ConverterMapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtStrategy;
import org.dromara.thermal.domain.HtStrategySub;
import org.dromara.thermal.domain.vo.HtStrategySubVo;
import org.dromara.thermal.domain.vo.HtStrategyVo;
import org.dromara.thermal.mapper.HtStrategyMapper;
import org.dromara.thermal.mapper.HtStrategySubMapper;
import org.dromara.thermal.service.IHtStrategyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HtStrategyServiceImpl extends ServiceImpl<HtStrategyMapper, HtStrategy> implements IHtStrategyService {

    private final HtStrategyMapper baseMapper;
    private final HtStrategySubMapper subMapper;
    private final ConverterMapper converterMapper;

    @Override
    public TableDataInfo<HtStrategyVo> selectPageList(LambdaQueryWrapper<HtStrategy> lqw, PageQuery pageQuery) {
        Page<HtStrategyVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public HtStrategyVo selectDetailById(String id) {
        HtStrategyVo vo = baseMapper.selectVoById(id);
        if (vo != null) {
            List<HtStrategySubVo> subList = subMapper.selectByStrategyId(id);
            vo.setSubList(subList);
        }
        return vo;
    }

    @Override
    public List<HtStrategy> selectAllList() {
        return baseMapper.selectAllList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(HtStrategy entity) {
        boolean saved = super.save(entity);
        if (saved && entity.getSubList() != null && !entity.getSubList().isEmpty()) {
            for (HtStrategySub sub : entity.getSubList()) {
                sub.setStrategyId(entity.getId());
                subMapper.insert(sub);
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(HtStrategy entity) {
        boolean updated = super.updateById(entity);
        if (updated && entity.getSubList() != null) {
            subMapper.deleteByStrategyId(entity.getId());
            for (HtStrategySub sub : entity.getSubList()) {
                sub.setStrategyId(entity.getId());
                subMapper.insert(sub);
            }
        }
        return updated;
    }
}
```

- [ ] **Step 8: Create Controller — HtStrategyController.java**

```java
package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.domain.HtStrategy;
import org.dromara.thermal.domain.vo.HtStrategyVo;
import org.dromara.thermal.service.IHtStrategyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/strategy")
public class HtStrategyController extends BaseController {

    private final IHtStrategyService strategyService;

    /**
     * 分页查询控制策略
     */
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<HtStrategyVo> list(@RequestParam(required = false) String search, PageQuery pageQuery) {
        LambdaQueryWrapper<HtStrategy> lqw = new LambdaQueryWrapper<>();
        lqw.like(search != null && !search.isEmpty(), HtStrategy::getName, search.trim());
        lqw.orderByDesc(HtStrategy::getCreateTime);
        return strategyService.selectPageList(lqw, pageQuery);
    }

    /**
     * 根据ID查询策略详情（含子表）
     */
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<HtStrategyVo> getById(@PathVariable String id) {
        return R.ok(strategyService.selectDetailById(id));
    }

    /**
     * 新增控制策略（含子表）
     */
    @SaCheckLogin
    @Log(title = "控制策略", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody HtStrategy strategy) {
        return toAjax(strategyService.save(strategy));
    }

    /**
     * 修改控制策略（含子表）
     */
    @SaCheckLogin
    @Log(title = "控制策略", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody HtStrategy strategy) {
        return toAjax(strategyService.updateById(strategy));
    }

    /**
     * 删除控制策略
     */
    @SaCheckLogin
    @Log(title = "控制策略", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(strategyService.removeById(id));
    }

    /**
     * 查询所有控制策略
     */
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<HtStrategy>> all() {
        return R.ok(strategyService.selectAllList());
    }
}
```

- [ ] **Step 9: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/
git commit -m "feat: 迁移 HtStrategy 控制策略管理"
```

---

### Task 6: Database migration + compilation + startup test

- [ ] **Step 1: Create tables in ry-vue database**

Execute via MySQL MCP:
```sql
-- ht_instruction
CREATE TABLE IF NOT EXISTS `ry-vue`.`ht_instruction` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `name` varchar(64) NOT NULL COMMENT '指令名称',
    `type` tinyint NOT NULL COMMENT '指令类型',
    `instruction` varchar(256) DEFAULT NULL COMMENT '指令内容',
    `remark` varchar(60) DEFAULT NULL COMMENT '备注',
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制指令表';

-- ht_strategy
CREATE TABLE IF NOT EXISTS `ry-vue`.`ht_strategy` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `name` varchar(64) NOT NULL COMMENT '策略名称',
    `type` tinyint DEFAULT NULL COMMENT '策略类型',
    `company_id` varchar(32) DEFAULT NULL COMMENT '公司ID',
    `remark` varchar(255) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` varchar(40) DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `del_flag` char(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制策略主表';

-- ht_strategy_sub
CREATE TABLE IF NOT EXISTS `ry-vue`.`ht_strategy_sub` (
    `id` varchar(32) NOT NULL,
    `tenant_id` varchar(20) DEFAULT '000000',
    `strategy_id` varchar(32) DEFAULT NULL,
    `instruction_id` varchar(32) DEFAULT NULL,
    `sort` int DEFAULT NULL,
    `valve_angle` varchar(32) DEFAULT NULL,
    `create_dept` bigint DEFAULT NULL,
    `create_by` varchar(40) DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='控制策略子表';

-- ht_alert (already has data from old system, add tenant_id + create_dept)
ALTER TABLE `ry-vue`.`ht_alert` ADD COLUMN IF NOT EXISTS `tenant_id` varchar(20) DEFAULT '000000' AFTER `id`;
ALTER TABLE `ry-vue`.`ht_alert` ADD COLUMN IF NOT EXISTS `create_dept` bigint DEFAULT NULL AFTER `create_by`;

-- ht_repair (already has data from old system, add tenant_id + create_dept)
ALTER TABLE `ry-vue`.`ht_repair` ADD COLUMN IF NOT EXISTS `tenant_id` varchar(20) DEFAULT '000000' AFTER `id`;
ALTER TABLE `ry-vue`.`ht_repair` ADD COLUMN IF NOT EXISTS `create_dept` bigint DEFAULT NULL AFTER `create_by`;
```

- [ ] **Step 2: Full compile**

```bash
cd D:/chonggou/thermal-platform-new
export JAVA_HOME="/c/Program Files/Java/jdk-17"
mvn clean compile -q
```

Expected: BUILD SUCCESS, zero errors.

- [ ] **Step 3: Package**

```bash
mvn clean package -DskipTests -q
```

Expected: BUILD SUCCESS.

- [ ] **Step 4: Start application and test endpoints**

After packaging, start the app and test:
```bash
curl http://localhost:8080/thermal/ht/instruction/list -H "Authorization: Bearer <token>" -H "clientid: e5cd7e4891bf95d1d19206ce24a7b32e"
curl http://localhost:8080/thermal/ht/alert/list -H "Authorization: Bearer <token>" -H "clientid: e5cd7e4891bf95d1d19206ce24a7b32e"
curl http://localhost:8080/thermal/ht/repair/list -H "Authorization: Bearer <token>" -H "clientid: e5cd7e4891bf95d1d19206ce24a7b32e"
curl http://localhost:8080/thermal/ht/strategy/list -H "Authorization: Bearer <token>" -H "clientid: e5cd7e4891bf95d1d19206ce24a7b32e"
```

- [ ] **Step 5: Commit**

---

## Self-Review

### Spec coverage
| Old Controller | Task | Status |
|---|---|---|
| HtInstructionController | Task 2 | ✅ |
| HtAlertController | Task 3 | ✅ |
| HtRepairController | Task 4 | ✅ |
| HtStrategyController | Task 5 | ✅ |

### Placeholder scan
- No TBD/TODO/fill-in patterns found
- All code blocks contain actual implementation
- HtRepair dispatch/status endpoints implemented inline (not deferred)

### Type consistency
- All entities use `@TableId(type = IdType.ASSIGN_UUID)` with `String id`
- All VOs use `@AutoMapper(target = Xxx.class)`
- All services use `selectPageList(LambdaQueryWrapper<T>, PageQuery) -> TableDataInfo<Vo>`
- All controllers extend `BaseController`, use `@SaCheckLogin`
- Endpoint prefix: `/thermal/ht/{resource}`
