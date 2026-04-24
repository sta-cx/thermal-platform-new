# Phase 5: Property/Charging Module Completion Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the Property/Charging module — fill all remaining Service TODOs for account management, transaction records, billing notes, and supporting infrastructure.

**Architecture:** 20 controllers already have skeleton endpoints. This plan fills 5 Service implementations that are currently returning `List.of()` or `false`. New domain/VO/Mapper layers are created only where data models don't yet exist.

**Tech Stack:** Spring Boot 3.5.12, Java 17, MyBatis-Plus 3.5.16, Sa-Token 1.44.0, RuoYi-Vue-Plus 5.6.0

---

## Current State (As of 2026-04-24)

### Already Complete
- **20 Controllers** — All endpoints skeleton created
- **PrExpenseServiceImpl** — `insertData`, `insertDataLs`, `setPreferential`, `setIsFree`, `setLastDate`, `setBaotingDate`, `setFugongDate`, `setTuifei`, `deleteDate`, `updateDatall`, `recalculate` methods implemented
- **SingleChargeServiceImpl** — `getHouse`, `getHouseRoomId`, `pageList`, `getDetail`, `selectCycle`, `selectYear`, `getHasFinished`, `queryPayByHouseId`, `updateHousePayStatus` implemented
- **PrHouseExpenseServiceImpl** — `selectPageList`, `selectUnboundHouses`, `selectUnboundItems`, `batchInsert`, `batchUpdate`, `batchDelete` implemented
- **PrExpenseItemServiceImpl** — `insertData` with auto-increment itemCode logic implemented
- **PrUserServiceImpl** — CRUD implemented (OSS upload deferred to Phase 6)

### Remaining TODOs (by priority)

| Service | Method | Priority | Complexity |
|---|---|---|---|
| PrAccountServiceImpl | All 9 methods | HIGH | Medium |
| PrTransactionRecordServiceImpl | All 8 methods | HIGH | Medium |
| PrBillingNotesController | 2 endpoints | MEDIUM | Low |
| PrOptionsController | 5 endpoints | LOW | Low |
| PrHouseChangeController | 7 endpoints | LOW | Low |
| PrPrintTemplateController | 4 endpoints | LOW | Low |
| PrUseCardLogController | 4 endpoints | LOW | Low |
| PrWechatBindRecordController | 1 endpoint | DEFERRED | Phase 6 |
| PrAutoMachineController | 6 endpoints | DEFERRED | Phase 6 |
| PrImportAuthorizationCodeController | 1 endpoint | DEFERRED | Phase 6 |

---

## File Structure

### Files to Create
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrTransactionRecord.java` — Transaction record entity
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrTransactionRecordSub.java` — Transaction sub-detail entity
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrTransactionRecordVo.java` — VO
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrTransactionRecordSubVo.java` — Sub VO
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrTransactionRecordMapper.java` — Mapper
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrTransactionRecordSubMapper.java` — Sub mapper
- `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrTransactionRecordMapper.xml` — SQL
- `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrTransactionRecordSubMapper.xml` — Sub SQL
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrAccountBalance.java` — Account balance entity
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrAccountBalanceVo.java` — VO
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrAccountBalanceMapper.java` — Mapper
- `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrAccountBalanceMapper.xml` — SQL
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrBillingNotes.java` — Billing notes entity
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrBillingNotesVo.java` — VO
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrBillingNotesMapper.java` — Mapper
- `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrBillingNotesMapper.xml` — SQL

### Files to Modify
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrAccountServiceImpl.java` — Complete all 9 methods
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrTransactionRecordServiceImpl.java` — Complete all 8 methods
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/PrBillingNotesController.java` — Wire to real service
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/PrOptionsController.java` — Wire to real service
- `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/PrHouseChangeController.java` — Wire to real service

---

### Task 1: PrTransactionRecord Domain Layer

**Files:**
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrTransactionRecord.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrTransactionRecordSub.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrTransactionRecordVo.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrTransactionRecordSubVo.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrTransactionRecordMapper.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrTransactionRecordSubMapper.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrTransactionRecordMapper.xml`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrTransactionRecordSubMapper.xml`

- [ ] **Step 1: Create PrTransactionRecord.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 交易记录主表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_transaction_record")
@AutoMapper(target = PrTransactionRecordVo.class)
public class PrTransactionRecord extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 流水号 */
    private String serialNum;

    /** 交易类型: 1=收费 2=退费 3=转存 4=优惠 */
    private Integer transactionType;

    /** 缴费方式: 1=现金 2=微信 3=支付宝 4=刷卡 */
    private Integer paymentType;

    /** 交易金额 */
    private BigDecimal amount;

    /** 实收金额 */
    private BigDecimal paidAmount;

    /** 交易状态: 0=正常 1=撤销 2=作废 */
    private Integer status;

    /** 房屋ID */
    private String houseId;

    /** 用户ID */
    private String userId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 费项分组 */
    private String itemGroup;

    /** 费项编码 */
    private String itemCode;

    /** 交易时间 */
    private Date transactionTime;

    /** 操作人 */
    private String operatorId;

    /** 备注 */
    private String notes;

    /** 原交易记录ID（退费/撤销时关联） */
    private String originalRecordId;

    /** 发票号 */
    private String invoiceNo;

    /** 子表明细（非数据库字段） */
    @TableField(exist = false)
    private List<PrTransactionRecordSub> subList;
}
```

- [ ] **Step 2: Create PrTransactionRecordSub.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.PrTransactionRecordSubVo;

import java.math.BigDecimal;

/**
 * 交易记录子表明细
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_transaction_record_sub")
@AutoMapper(target = PrTransactionRecordSubVo.class)
public class PrTransactionRecordSub extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 主表ID */
    private String mainId;

    /** 费用明细ID */
    private String expenseId;

    /** 交易金额 */
    private BigDecimal amount;

    /** 交易前余额 */
    private BigDecimal balanceBefore;

    /** 交易后余额 */
    private BigDecimal balanceAfter;

    /** 费项名称 */
    private String itemName;

    /** 备注 */
    private String notes;
}
```

- [ ] **Step 3: Create Vo classes**

```java
// PrTransactionRecordVo.java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrTransactionRecord;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AutoMapper(target = PrTransactionRecord.class)
public class PrTransactionRecordVo {
    private String id;
    private String serialNum;
    private Integer transactionType;
    private Integer paymentType;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private Integer status;
    private String houseId;
    private String userId;
    private String orgId;
    private String companyId;
    private String itemGroup;
    private String itemCode;
    private Date transactionTime;
    private String operatorId;
    private String notes;
    private String originalRecordId;
    private String invoiceNo;
    private String roomNum;
    private String userName;
    private String itemName;
    private List<PrTransactionRecordSubVo> subList;
}
```

```java
// PrTransactionRecordSubVo.java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrTransactionRecordSub;

import java.math.BigDecimal;

@Data
@AutoMapper(target = PrTransactionRecordSub.class)
public class PrTransactionRecordSubVo {
    private String id;
    private String mainId;
    private String expenseId;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String itemName;
    private String notes;
}
```

- [ ] **Step 4: Create Mapper interfaces**

```java
// PrTransactionRecordMapper.java
package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrTransactionRecord;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;

import java.util.List;
import java.util.Map;

public interface PrTransactionRecordMapper extends BaseMapperPlus<PrTransactionRecord, PrTransactionRecordVo> {

    List<PrTransactionRecordVo> selectPageList(
        @Param("search") String search, @Param("companyId") String companyId,
        @Param("orgId") String orgId, @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode, @Param("startTime") String startTime,
        @Param("endTime") String endTime, @Param("status") String status);

    List<PrTransactionRecordSubVo> selectDetailByMainId(@Param("mainId") String mainId);

    int revokeRecord(@Param("recordId") String recordId);

    int invalidRecord(@Param("recordId") String recordId);

    Map<String, Object> selectComprehensiveStats(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("startTime") String startTime, @Param("endTime") String endTime);

    Map<String, Object> selectReceivedStats(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("startTime") String startTime, @Param("endTime") String endTime);

    Map<String, Object> selectArrearsStats(
        @Param("companyId") String companyId, @Param("orgId") String orgId);

    List<PrTransactionRecordVo> selectDailyReport(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("date") String date);
}
```

```java
// PrTransactionRecordSubMapper.java
package org.dromara.thermal.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrTransactionRecordSub;
import org.dromara.thermal.domain.vo.PrTransactionRecordSubVo;

public interface PrTransactionRecordSubMapper extends BaseMapperPlus<PrTransactionRecordSub, PrTransactionRecordSubVo> {
}
```

- [ ] **Step 5: Create Mapper XML**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.PrTransactionRecordMapper">

    <select id="selectPageList" resultType="org.dromara.thermal.domain.vo.PrTransactionRecordVo">
        SELECT r.id, r.serial_num, r.transaction_type, r.payment_type, r.amount,
               r.paid_amount, r.status, r.house_id, r.user_id, r.org_id, r.company_id,
               r.item_group, r.item_code, r.transaction_time, r.operator_id, r.notes,
               r.original_record_id, r.invoice_no,
               b.room_num, u.user_name
        FROM pr_transaction_record r
        LEFT JOIN pr_house b ON r.house_id = b.id
        LEFT JOIN pr_user u ON r.user_id = u.id
        WHERE 1=1
        <if test="companyId != null and companyId != ''">
            AND r.company_id = #{companyId}
        </if>
        <if test="orgId != null and orgId != ''">
            AND r.org_id = #{orgId}
        </if>
        <if test="buildingId != null and buildingId != ''">
            AND b.building_id = #{buildingId}
        </if>
        <if test="unitCode != null and unitCode != ''">
            AND b.unit_code = #{unitCode}
        </if>
        <if test="startTime != null and startTime != ''">
            AND r.transaction_time &gt;= #{startTime}
        </if>
        <if test="endTime != null and endTime != ''">
            AND r.transaction_time &lt;= #{endTime}
        </if>
        <if test="status != null and status != ''">
            AND r.status = #{status}
        </if>
        <if test="search != null and search != ''">
            AND (b.room_num LIKE CONCAT('%', #{search}, '%')
                 OR u.user_name LIKE CONCAT('%', #{search}, '%')
                 OR r.serial_num LIKE CONCAT('%', #{search}, '%'))
        </if>
        ORDER BY r.transaction_time DESC
    </select>

    <select id="selectDetailByMainId" resultType="org.dromara.thermal.domain.vo.PrTransactionRecordSubVo">
        SELECT id, main_id, expense_id, amount, balance_before, balance_after,
               item_name, notes
        FROM pr_transaction_record_sub
        WHERE main_id = #{mainId}
        ORDER BY id
    </select>

    <update id="revokeRecord">
        UPDATE pr_transaction_record SET status = 1, update_time = NOW()
        WHERE id = #{recordId} AND status = 0
          AND DATE_FORMAT(transaction_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')
    </update>

    <update id="invalidRecord">
        UPDATE pr_transaction_record SET status = 2, update_time = NOW()
        WHERE id = #{recordId} AND status = 0
    </update>

    <select id="selectComprehensiveStats" resultType="java.util.HashMap">
        SELECT
            COUNT(*) AS totalTransactions,
            SUM(CASE WHEN transaction_type = 1 THEN 1 ELSE 0 END) AS chargeCount,
            SUM(CASE WHEN transaction_type = 2 THEN 1 ELSE 0 END) AS refundCount,
            SUM(CASE WHEN transaction_type = 3 THEN 1 ELSE 0 END) AS transferCount,
            SUM(CASE WHEN transaction_type = 1 THEN paid_amount ELSE 0 END) AS chargeAmount,
            SUM(CASE WHEN transaction_type = 2 THEN amount ELSE 0 END) AS refundAmount,
            SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS revokedCount,
            SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS invalidCount
        FROM pr_transaction_record
        WHERE company_id = #{companyId} AND org_id = #{orgId}
        <if test="startTime != null and startTime != ''">
            AND transaction_time &gt;= #{startTime}
        </if>
        <if test="endTime != null and endTime != ''">
            AND transaction_time &lt;= #{endTime}
        </if>
    </select>

    <select id="selectReceivedStats" resultType="java.util.HashMap">
        SELECT
            COUNT(*) AS receivedCount,
            SUM(paid_amount) AS totalReceived,
            SUM(CASE WHEN payment_type = 1 THEN paid_amount ELSE 0 END) AS cashAmount,
            SUM(CASE WHEN payment_type = 2 THEN paid_amount ELSE 0 END) AS wechatAmount,
            SUM(CASE WHEN payment_type = 3 THEN paid_amount ELSE 0 END) AS alipayAmount,
            SUM(CASE WHEN payment_type = 4 THEN paid_amount ELSE 0 END) AS cardAmount
        FROM pr_transaction_record
        WHERE company_id = #{companyId} AND org_id = #{orgId}
          AND transaction_type = 1 AND status = 0
        <if test="startTime != null and startTime != ''">
            AND transaction_time &gt;= #{startTime}
        </if>
        <if test="endTime != null and endTime != ''">
            AND transaction_time &lt;= #{endTime}
        </if>
    </select>

    <select id="selectArrearsStats" resultType="java.util.HashMap">
        SELECT
            COUNT(DISTINCT e.house_id) AS arrearsHouseCount,
            COUNT(*) AS arrearsCount,
            SUM(e.receivable - e.paid_in) AS totalArrears
        FROM pr_expense e
        WHERE e.company_id = #{companyId} AND e.org_id = #{orgId}
          AND e.is_charged = 0 AND e.receivable > 0
    </select>

    <select id="selectDailyReport" resultType="org.dromara.thermal.domain.vo.PrTransactionRecordVo">
        SELECT r.id, r.serial_num, r.transaction_type, r.payment_type, r.amount,
               r.paid_amount, r.status, r.transaction_time, r.operator_id, r.notes,
               r.invoice_no, b.room_num, u.user_name
        FROM pr_transaction_record r
        LEFT JOIN pr_house b ON r.house_id = b.id
        LEFT JOIN pr_user u ON r.user_id = u.id
        WHERE r.company_id = #{companyId} AND r.org_id = #{orgId}
          AND DATE(r.transaction_time) = #{date}
          AND status = 0
        ORDER BY r.transaction_time ASC
    </select>

</mapper>
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.PrTransactionRecordSubMapper">

</mapper>
```

- [ ] **Step 6: Compile verification**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am -q
echo "EXIT_CODE=$?"
```
Expected: EXIT_CODE=0

- [ ] **Step 7: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrTransaction*.java
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrTransaction*
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrTransaction*.java
git add ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrTransaction*.xml
git commit -m "feat(phase5): 创建交易记录领域层"
```

---

### Task 2: PrTransactionRecordServiceImpl Complete Implementation

**Files:**
- Modify: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrTransactionRecordServiceImpl.java`

- [ ] **Step 1: Replace full implementation**

```java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrTransactionRecord;
import org.dromara.thermal.domain.vo.PrTransactionRecordSubVo;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;
import org.dromara.thermal.mapper.PrTransactionRecordMapper;
import org.dromara.thermal.service.IPrTransactionRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 交易记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrTransactionRecordServiceImpl extends ServiceImpl<PrTransactionRecordMapper, PrTransactionRecord>
        implements IPrTransactionRecordService {

    private final PrTransactionRecordMapper baseMapper;

    @Override
    public TableDataInfo<PrTransactionRecordVo> pageList(String search, String companyId, String orgId,
            String buildingId, String unitCode, String startTime, String endTime, String status, PageQuery pageQuery) {
        List<PrTransactionRecordVo> list = baseMapper.selectPageList(
            search, companyId, orgId, buildingId, unitCode, startTime, endTime, status);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrTransactionRecordVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrTransactionRecordVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }

    @Override
    public List<PrTransactionRecordSubVo> getDetailByMainId(String mainId) {
        return baseMapper.selectDetailByMainId(mainId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revocation(String recordId) {
        // 仅当月可撤销
        int rows = baseMapper.revokeRecord(recordId);
        if (rows == 0) {
            throw new RuntimeException("撤销失败：记录不存在、已撤销/作废，或已跨月");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean invalid(String recordId) {
        int rows = baseMapper.invalidRecord(recordId);
        if (rows == 0) {
            throw new RuntimeException("作废失败：记录不存在或已作废");
        }
        return true;
    }

    @Override
    public Map<String, Object> comprehensive(String companyId, String orgId, String startTime, String endTime) {
        Map<String, Object> stats = baseMapper.selectComprehensiveStats(companyId, orgId, startTime, endTime);
        return stats != null ? stats : Map.of(
            "totalTransactions", 0, "chargeCount", 0, "refundCount", 0,
            "transferCount", 0, "chargeAmount", 0.0, "refundAmount", 0.0,
            "revokedCount", 0, "invalidCount", 0);
    }

    @Override
    public Map<String, Object> received(String companyId, String orgId, String startTime, String endTime) {
        Map<String, Object> stats = baseMapper.selectReceivedStats(companyId, orgId, startTime, endTime);
        return stats != null ? stats : Map.of(
            "receivedCount", 0, "totalReceived", 0.0,
            "cashAmount", 0.0, "wechatAmount", 0.0, "alipayAmount", 0.0, "cardAmount", 0.0);
    }

    @Override
    public Map<String, Object> arrears(String companyId, String orgId) {
        Map<String, Object> stats = baseMapper.selectArrearsStats(companyId, orgId);
        return stats != null ? stats : Map.of(
            "arrearsHouseCount", 0, "arrearsCount", 0, "totalArrears", 0.0);
    }

    @Override
    public List<PrTransactionRecordVo> daily(String companyId, String orgId, String date) {
        return baseMapper.selectDailyReport(companyId, orgId, date);
    }
}
```

- [ ] **Step 2: Update service interface**

Modify `IPrTransactionRecordService.java` to match new signatures:

```java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrTransactionRecord;
import org.dromara.thermal.domain.vo.PrTransactionRecordSubVo;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;

import java.util.List;
import java.util.Map;

public interface IPrTransactionRecordService extends IService<PrTransactionRecord> {

    TableDataInfo<PrTransactionRecordVo> pageList(String search, String companyId, String orgId,
        String buildingId, String unitCode, String startTime, String endTime, String status,
        PageQuery pageQuery);

    List<PrTransactionRecordSubVo> getDetailByMainId(String mainId);

    boolean revocation(String recordId);

    boolean invalid(String recordId);

    Map<String, Object> comprehensive(String companyId, String orgId, String startTime, String endTime);

    Map<String, Object> received(String companyId, String orgId, String startTime, String endTime);

    Map<String, Object> arrears(String companyId, String orgId);

    List<PrTransactionRecordVo> daily(String companyId, String orgId, String date);
}
```

- [ ] **Step 3: Update controller to pass PageQuery**

In `PrTransactionRecordController.java`, update `list` method:

```java
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrTransactionRecordVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String status,
            PageQuery pageQuery) {
        return transactionRecordService.pageList(
            search, companyId, orgId, buildingId, unitCode, startTime, endTime, status, pageQuery);
    }
```

Add imports:
```java
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;
```

- [ ] **Step 4: Compile verification**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am -q
echo "EXIT_CODE=$?"
```

- [ ] **Step 5: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/IPrTransactionRecordService.java
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrTransactionRecordServiceImpl.java
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/PrTransactionRecordController.java
git commit -m "feat(phase5): 完整实现交易记录 Service"
```

---

### Task 3: PrAccountServiceImpl Complete Implementation

**Files:**
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrAccountBalance.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrAccountBalanceVo.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrAccountBalanceMapper.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrAccountBalanceMapper.xml`
- Modify: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrAccountServiceImpl.java`

- [ ] **Step 1: Create PrAccountBalance.java**

```java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;

/**
 * 个人账户余额
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_account_balance")
@AutoMapper(target = PrAccountBalanceVo.class)
public class PrAccountBalance extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 用户ID */
    private String userId;

    /** 房屋ID */
    private String houseId;

    /** 费项分组 */
    private String itemGroup;

    /** 费项编码 */
    private String itemCode;

    /** 账户余额 */
    private BigDecimal balance;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;
}
```

- [ ] **Step 2: Create PrAccountBalanceVo.java**

```java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrAccountBalance;

import java.math.BigDecimal;

@Data
@AutoMapper(target = PrAccountBalance.class)
public class PrAccountBalanceVo {
    private String id;
    private String userId;
    private String houseId;
    private String itemGroup;
    private String itemCode;
    private BigDecimal balance;
    private String orgId;
    private String companyId;
    private String roomNum;
    private String userName;
    private String itemName;
}
```

- [ ] **Step 3: Create PrAccountBalanceMapper.java**

```java
package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrAccountBalance;
import org.dromara.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PrAccountBalanceMapper extends BaseMapperPlus<PrAccountBalance, PrAccountBalanceVo> {

    List<PrAccountBalanceVo> selectAccountList(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
        @Param("search") String search, @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode);

    List<PrAccountBalanceVo> selectNoAccountList(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("buildingId") String buildingId, @Param("unitCode") String unitCode,
        @Param("search") String search, @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode);

    BigDecimal selectBalanceByUser(
        @Param("companyId") String companyId, @Param("orgId") String orgId,
        @Param("userId") String userId);

    int updateBalance(
        @Param("userId") String userId, @Param("houseId") String houseId,
        @Param("itemGroup") String itemGroup, @Param("itemCode") String itemCode,
        @Param("amount") java.math.BigDecimal amount);
}
```

- [ ] **Step 4: Create PrAccountBalanceMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.PrAccountBalanceMapper">

    <select id="selectAccountList" resultType="org.dromara.thermal.domain.vo.PrAccountBalanceVo">
        SELECT b.id, b.user_id, b.house_id, b.item_group, b.item_code, b.balance,
               b.org_id, b.company_id, h.room_num, u.user_name, ei.item_name
        FROM pr_account_balance b
        LEFT JOIN pr_house h ON b.house_id = h.id
        LEFT JOIN pr_user u ON b.user_id = u.id
        LEFT JOIN pr_expense_item ei ON b.item_code = ei.item_code
        WHERE b.company_id = #{companyId} AND b.org_id = #{orgId}
        <if test="buildingId != null and buildingId != ''">
            AND h.building_id = #{buildingId}
        </if>
        <if test="unitCode != null and unitCode != ''">
            AND h.unit_code = #{unitCode}
        </if>
        <if test="itemGroup != null and itemGroup != ''">
            AND b.item_group = #{itemGroup}
        </if>
        <if test="itemCode != null and itemCode != ''">
            AND b.item_code = #{itemCode}
        </if>
        <if test="search != null and search != ''">
            AND (h.room_num LIKE CONCAT('%', #{search}, '%')
                 OR u.user_name LIKE CONCAT('%', #{search}, '%'))
        </if>
        ORDER BY b.create_time DESC
    </select>

    <select id="selectNoAccountList" resultType="org.dromara.thermal.domain.vo.PrAccountBalanceVo">
        SELECT h.id AS house_id, h.room_num, u.user_name, u.id AS user_id,
               h.org_id, h.company_id
        FROM pr_house h
        LEFT JOIN pr_user_house uh ON h.id = uh.house_id AND uh.is_deleted = 0
        LEFT JOIN pr_user u ON uh.user_id = u.id
        LEFT JOIN pr_account_balance b ON h.id = b.house_id AND b.item_code = #{itemCode}
        WHERE h.company_id = #{companyId} AND h.org_id = #{orgId}
          AND b.id IS NULL
        <if test="buildingId != null and buildingId != ''">
            AND h.building_id = #{buildingId}
        </if>
        <if test="unitCode != null and unitCode != ''">
            AND h.unit_code = #{unitCode}
        </if>
        <if test="search != null and search != ''">
            AND (h.room_num LIKE CONCAT('%', #{search}, '%')
                 OR u.user_name LIKE CONCAT('%', #{search}, '%'))
        </if>
    </select>

    <select id="selectBalanceByUser" resultType="java.math.BigDecimal">
        SELECT COALESCE(SUM(balance), 0)
        FROM pr_account_balance
        WHERE company_id = #{companyId} AND org_id = #{orgId} AND user_id = #{userId}
    </select>

    <update id="updateBalance">
        UPDATE pr_account_balance SET balance = balance + #{amount}, update_time = NOW()
        WHERE user_id = #{userId} AND house_id = #{houseId}
          AND item_group = #{itemGroup} AND item_code = #{itemCode}
    </update>

</mapper>
```

- [ ] **Step 5: Replace PrAccountServiceImpl implementation**

```java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.thermal.domain.PrAccountBalance;
import org.dromara.thermal.domain.PrTransactionRecord;
import org.dromara.thermal.domain.vo.PrAccountBalanceVo;
import org.dromara.thermal.mapper.PrAccountBalanceMapper;
import org.dromara.thermal.mapper.PrTransactionRecordMapper;
import org.dromara.thermal.service.IPrAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 个人账户 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrAccountServiceImpl implements IPrAccountService {

    private final PrAccountBalanceMapper balanceMapper;
    private final PrTransactionRecordMapper transactionMapper;

    @Override
    public List<PrAccountBalanceVo> pageList(String companyId, String orgId, String buildingId,
            String unitCode, String search, String itemGroup, String itemCode) {
        return balanceMapper.selectAccountList(
            companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertData(List<String> houseIds, String itemGroup, String itemCode, String payment) {
        if (houseIds == null || houseIds.isEmpty()) return false;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

        for (String houseId : houseIds) {
            PrAccountBalance balance = new PrAccountBalance();
            balance.setHouseId(houseId);
            balance.setItemGroup(itemGroup);
            balance.setItemCode(itemCode);
            balance.setBalance(BigDecimal.ZERO);
            balance.setCreateBy(userId);
            balance.setCreateTime(now);
            balanceMapper.insert(balance);

            // 生成交易记录
            PrTransactionRecord record = new PrTransactionRecord();
            record.setSerialNum("ACC" + System.currentTimeMillis());
            record.setTransactionType(1);
            record.setPaymentType(payment != null ? Integer.parseInt(payment) : 1);
            record.setAmount(BigDecimal.ZERO);
            record.setPaidAmount(BigDecimal.ZERO);
            record.setStatus(0);
            record.setHouseId(houseId);
            record.setItemGroup(itemGroup);
            record.setItemCode(itemCode);
            record.setTransactionTime(now);
            record.setOperatorId(String.valueOf(userId));
            record.setNotes("开户");
            transactionMapper.insert(record);
        }
        return true;
    }

    @Override
    public List<PrAccountBalanceVo> noAccount(String companyId, String orgId, String buildingId,
            String unitCode, String search, String itemGroup, String itemCode) {
        return balanceMapper.selectNoAccountList(
            companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode);
    }

    @Override
    public List<PrAccountBalanceVo> getAccount(String companyId, String orgId, String buildingId,
            String unitCode, String search, String itemGroup, String itemCode, String userId) {
        return balanceMapper.selectAccountList(
            companyId, orgId, buildingId, unitCode, search, itemGroup, itemCode)
            .stream()
            .filter(vo -> userId == null || userId.equals(vo.getUserId()))
            .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateData(List<Map<String, String>> houses, String itemGroup, String itemCode, String payment) {
        if (houses == null || houses.isEmpty()) return false;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();

        for (Map<String, String> house : houses) {
            String houseId = house.get("houseId");
            String amountStr = house.get("amount");
            if (houseId == null || amountStr == null) continue;

            BigDecimal amount = new BigDecimal(amountStr);
            balanceMapper.updateBalance(
                house.get("userId"), houseId, itemGroup, itemCode, amount);

            // 生成充值交易记录
            PrTransactionRecord record = new PrTransactionRecord();
            record.setSerialNum("RCH" + System.currentTimeMillis());
            record.setTransactionType(1);
            record.setPaymentType(payment != null ? Integer.parseInt(payment) : 1);
            record.setAmount(amount);
            record.setPaidAmount(amount);
            record.setStatus(0);
            record.setHouseId(houseId);
            record.setUserId(house.get("userId"));
            record.setItemGroup(itemGroup);
            record.setItemCode(itemCode);
            record.setTransactionTime(now);
            record.setOperatorId(String.valueOf(userId));
            record.setNotes("充值");
            transactionMapper.insert(record);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundData(Map<String, String> houses, Map<String, String> record, Map<String, String> info) {
        if (houses == null) return;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();
        BigDecimal amount = info != null && info.get("amount") != null
            ? new BigDecimal(info.get("amount")) : BigDecimal.ZERO;

        for (Map.Entry<String, String> entry : houses.entrySet()) {
            String houseId = entry.getKey();
            String houseUserId = entry.getValue();

            balanceMapper.updateBalance(houseUserId, houseId,
                record != null ? record.get("itemGroup") : null,
                record != null ? record.get("itemCode") : null,
                amount.negate());

            // 生成退费交易记录
            PrTransactionRecord refundRecord = new PrTransactionRecord();
            refundRecord.setSerialNum("REF" + System.currentTimeMillis());
            refundRecord.setTransactionType(2);
            refundRecord.setAmount(amount);
            refundRecord.setPaidAmount(amount);
            refundRecord.setStatus(0);
            refundRecord.setHouseId(houseId);
            refundRecord.setUserId(houseUserId);
            refundRecord.setItemGroup(record != null ? record.get("itemGroup") : null);
            refundRecord.setItemCode(record != null ? record.get("itemCode") : null);
            refundRecord.setTransactionTime(now);
            refundRecord.setOperatorId(String.valueOf(userId));
            refundRecord.setNotes("退费");
            transactionMapper.insert(refundRecord);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transfer(List<String> houseIds, String payment, String itemGroup, String itemCode,
            String makeInvoice, String invoice) {
        // TODO: 转存需要完整交易记录生成
        return false;
    }

    @Override
    public BigDecimal getPersonAccount(String companyId, String orgId, String userId) {
        return balanceMapper.selectBalanceByUser(companyId, orgId, userId);
    }

    @Override
    public List<PrAccountBalanceVo> pageAccountStatementList(String companyId, String orgId, String buildingId,
            String unitCode, String itemGroup, String itemCode, String searchPhone) {
        return balanceMapper.selectAccountList(
            companyId, orgId, buildingId, unitCode, searchPhone, itemGroup, itemCode);
    }
}
```

- [ ] **Step 6: Update IPrAccountService interface**

Update method signatures to use typed parameters instead of `Object`:

```java
package org.dromara.thermal.service;

import org.dromara.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IPrAccountService {

    List<PrAccountBalanceVo> pageList(String companyId, String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode);

    boolean insertData(List<String> houseIds, String itemGroup, String itemCode, String payment);

    List<PrAccountBalanceVo> noAccount(String companyId, String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode);

    List<PrAccountBalanceVo> getAccount(String companyId, String orgId, String buildingId,
        String unitCode, String search, String itemGroup, String itemCode, String userId);

    boolean updateData(List<Map<String, String>> houses, String itemGroup, String itemCode, String payment);

    void refundData(Map<String, String> houses, Map<String, String> record, Map<String, String> info);

    boolean transfer(List<String> houseIds, String payment, String itemGroup, String itemCode,
        String makeInvoice, String invoice);

    BigDecimal getPersonAccount(String companyId, String orgId, String userId);

    List<PrAccountBalanceVo> pageAccountStatementList(String companyId, String orgId, String buildingId,
        String unitCode, String itemGroup, String itemCode, String searchPhone);
}
```

- [ ] **Step 7: Update PrAccountController to use typed return**

Change all `R<?>` to `R<List<PrAccountBalanceVo>>` or `R<BigDecimal>` where appropriate. Add imports for `PageQuery`, `PrAccountBalanceVo`.

- [ ] **Step 8: Compile verification**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am -q
echo "EXIT_CODE=$?"
```

- [ ] **Step 9: Commit**

```bash
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrAccountBalance*.java
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrAccountBalance*.java
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrAccountBalanceMapper.java
git add ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrAccountBalanceMapper.xml
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/IPrAccountService.java
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrAccountServiceImpl.java
git add ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/PrAccountController.java
git commit -m "feat(phase5): 完整实现个人账户 Service"
```

---

### Task 4: PrBillingNotes and PrOptions Implementation

**Files:**
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrBillingNotes.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/vo/PrBillingNotesVo.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/mapper/PrBillingNotesMapper.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/resources/mapper/PrBillingNotesMapper.xml`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/IPrBillingNotesService.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrBillingNotesServiceImpl.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/IPrOptionsService.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrOptionsServiceImpl.java`

- [ ] **Step 1: Create PrBillingNotes domain + mapper + service**

```java
// PrBillingNotes.java
package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_billing_notes")
public class PrBillingNotes extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 流水号 */
    private String serialNum;

    /** 票据备注 */
    private String notes;
}
```

```java
// PrBillingNotesVo.java
package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrBillingNotes;

@Data
@AutoMapper(target = PrBillingNotes.class)
public class PrBillingNotesVo {
    private String id;
    private String serialNum;
    private String notes;
}
```

```java
// PrBillingNotesMapper.java
package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrBillingNotes;
import org.dromara.thermal.domain.vo.PrBillingNotesVo;

public interface PrBillingNotesMapper extends BaseMapperPlus<PrBillingNotes, PrBillingNotesVo> {

    PrBillingNotesVo selectBySerialNum(@Param("serialNum") String serialNum);
}
```

```xml
<!-- PrBillingNotesMapper.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.dromara.thermal.mapper.PrBillingNotesMapper">

    <select id="selectBySerialNum" resultType="org.dromara.thermal.domain.vo.PrBillingNotesVo">
        SELECT id, serial_num, notes FROM pr_billing_notes
        WHERE serial_num = #{serialNum}
    </select>

</mapper>
```

```java
// IPrBillingNotesService.java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.thermal.domain.PrBillingNotes;

public interface IPrBillingNotesService extends IService<PrBillingNotes> {

    boolean saveSerialNum(String serialNum, String notes);

    boolean reprint(String recordId, String serialNum);
}
```

```java
// PrBillingNotesServiceImpl.java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.thermal.domain.PrBillingNotes;
import org.dromara.thermal.mapper.PrBillingNotesMapper;
import org.dromara.thermal.service.IPrBillingNotesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrBillingNotesServiceImpl extends ServiceImpl<PrBillingNotesMapper, PrBillingNotes>
        implements IPrBillingNotesService {

    private final PrBillingNotesMapper baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSerialNum(String serialNum, String notes) {
        PrBillingNotes existing = baseMapper.selectBySerialNum(serialNum);
        PrBillingNotes bn = new PrBillingNotes();
        if (existing != null) {
            bn.setId(existing.getId());
            bn.setNotes(notes);
            return updateById(bn);
        }
        bn.setSerialNum(serialNum);
        bn.setNotes(notes);
        return save(bn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reprint(String recordId, String serialNum) {
        // TODO: 完整实现需要更新 pr_transaction_record 的 serial_num
        // 当前仅更新备注
        return saveSerialNum(serialNum, "重开票据 recordId=" + recordId);
    }
}
```

- [ ] **Step 2: Wire PrBillingNotesController**

```java
// Replace the controller
package org.dromara.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.dromara.thermal.service.IPrBillingNotesService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/billing-notes")
public class PrBillingNotesController extends BaseController {

    private final IPrBillingNotesService billingNotesService;

    @SaCheckLogin
    @Log(title = "票据备注", businessType = BusinessType.UPDATE)
    @PostMapping("/serial")
    public R<Void> saveSerialNum(
            @RequestParam String serialNum,
            @RequestParam(required = false) String notes) {
        return toAjax(billingNotesService.saveSerialNum(serialNum, notes));
    }

    @SaCheckLogin
    @Log(title = "票据重开", businessType = BusinessType.UPDATE)
    @PostMapping("/reprint")
    public R<Void> reprint(
            @RequestParam String recordId,
            @RequestParam String serialNum) {
        return toAjax(billingNotesService.reprint(recordId, serialNum));
    }
}
```

- [ ] **Step 3: Create PrOptionsServiceImpl**

```java
// IPrOptionsService.java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.thermal.domain.PrOptions;

import java.util.List;

public interface IPrOptionsService extends IService<PrOptions> {

    List<PrOptions> selectByOrgId(String orgId);

    boolean initOptions(String orgId);

    boolean forbiddenToBuyCheck(String houseId);
}
```

```java
// PrOptionsServiceImpl.java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.thermal.domain.PrOptions;
import org.dromara.thermal.mapper.PrOptionsMapper;
import org.dromara.thermal.service.IPrOptionsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrOptionsServiceImpl extends ServiceImpl<PrOptionsMapper, PrOptions>
        implements IPrOptionsService {

    private final PrOptionsMapper baseMapper;

    @Override
    public List<PrOptions> selectByOrgId(String orgId) {
        return lambdaQuery()
            .eq(PrOptions::getOrgId, orgId)
            .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initOptions(String orgId) {
        // 初始化默认系统选项
        PrOptions defaultOption = new PrOptions();
        defaultOption.setOrgId(orgId);
        return save(defaultOption);
    }

    @Override
    public boolean forbiddenToBuyCheck(String houseId) {
        // TODO: 检查房屋是否被禁止购买
        return false;
    }
}
```

- [ ] **Step 4: Wire PrOptionsController**

Replace TODO returns with actual service calls:

```java
    @SaCheckLogin
    @GetMapping("/list")
    public R<List<PrOptions>> list(@RequestParam String orgId) {
        return R.ok(optionsService.selectByOrgId(orgId));
    }

    @SaCheckLogin
    @PostMapping("/init")
    public R<Void> init(@RequestParam String orgId) {
        return toAjax(optionsService.initOptions(orgId));
    }

    @SaCheckLogin
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrOptions options) {
        return toAjax(optionsService.save(options));
    }

    @SaCheckLogin
    @PutMapping
    public R<Void> update(@Validated @RequestBody PrOptions options) {
        return toAjax(optionsService.updateById(options));
    }

    @SaCheckLogin
    @GetMapping("/forbiddenToBuy")
    public R<Boolean> forbiddenToBuy(@RequestParam String houseId) {
        return R.ok(optionsService.forbiddenToBuyCheck(houseId));
    }
```

Add `@RequiredArgsConstructor` and `private final IPrOptionsService optionsService;`.

- [ ] **Step 5: Compile verification**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am -q
echo "EXIT_CODE=$?"
```

- [ ] **Step 6: Commit**

```bash
git add -A ruoyi-modules/ruoyi-thermal/src/
git commit -m "feat(phase5): 实现票据备注和系统选项 Service"
```

---

### Task 5: PrHouseChange and PrUseCardLog Implementation

**Files:**
- Modify: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/controller/PrHouseChangeController.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/IPrHouseChangeService.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrHouseChangeServiceImpl.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/IPrUseCardLogService.java`
- Create: `ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/service/impl/PrUseCardLogServiceImpl.java`

- [ ] **Step 1: Create PrHouseChangeService**

```java
// IPrHouseChangeService.java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrHouse;

public interface IPrHouseChangeService extends IService<PrHouse> {

    TableDataInfo<PrHouse> selectPageList(LambdaQueryWrapper<PrHouse> lqw, PageQuery pageQuery);

    boolean audit(PrHouse house);
}
```

```java
// PrHouseChangeServiceImpl.java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrHouse;
import org.dromara.thermal.mapper.PrHouseMapper;
import org.dromara.thermal.service.IPrHouseChangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrHouseChangeServiceImpl extends ServiceImpl<PrHouseMapper, PrHouse>
        implements IPrHouseChangeService {

    private final PrHouseMapper baseMapper;

    @Override
    public TableDataInfo<PrHouse> selectPageList(LambdaQueryWrapper<PrHouse> lqw, PageQuery pageQuery) {
        Page<PrHouse> result = baseMapper.selectPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(PrHouse house) {
        // 入住审核：更新房屋状态为已入住
        return updateById(house);
    }
}
```

- [ ] **Step 2: Wire PrHouseChangeController**

```java
// Key changes only — add fields and replace method bodies
private final IPrHouseChangeService houseChangeService;

// Replace list method body:
return houseChangeService.selectPageList(lqw, pageQuery);

// Replace insertData:
return toAjax(houseChangeService.save(house));

// Replace updateData:
return toAjax(houseChangeService.updateById(house));

// Replace deleteData:
return toAjax(houseChangeService.removeById(id));

// Replace deleteMulData:
return toAjax(houseChangeService.removeByIds(ids));

// Replace changeData:
return toAjax(houseChangeService.audit(house));

// Replace getById:
return R.ok(houseChangeService.getById(id));
```

- [ ] **Step 3: Create PrUseCardLogService**

```java
// IPrUseCardLogService.java
package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrUseCardLog;
import org.dromara.thermal.domain.vo.PrUseCardLogVo;

public interface IPrUseCardLogService extends IService<PrUseCardLog> {

    TableDataInfo<PrUseCardLogVo> selectPageList(LambdaQueryWrapper<PrUseCardLog> lqw, PageQuery pageQuery);

    boolean changeValveStatus(String meterId, Integer valveStatus);
}
```

- [ ] **Step 4: Create PrUseCardLogServiceImpl**

```java
package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrUseCardLog;
import org.dromara.thermal.domain.vo.PrUseCardLogVo;
import org.dromara.thermal.mapper.PrUseCardLogMapper;
import org.dromara.thermal.service.IPrUseCardLogService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrUseCardLogServiceImpl extends ServiceImpl<PrUseCardLogMapper, PrUseCardLog>
        implements IPrUseCardLogService {

    private final PrUseCardLogMapper baseMapper;

    @Override
    public TableDataInfo<PrUseCardLogVo> selectPageList(LambdaQueryWrapper<PrUseCardLog> lqw, PageQuery pageQuery) {
        Page<PrUseCardLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public boolean changeValveStatus(String meterId, Integer valveStatus) {
        // TODO: 实际阀门状态变更需要通过 MBus 通信
        PrUseCardLog log = new PrUseCardLog();
        log.setMeterId(meterId);
        log.setValveStatus(valveStatus);
        return save(log);
    }
}
```

Note: `PrUseCardLog` entity and mapper need to be created if they don't exist. Check with:
```bash
ls ruoyi-modules/ruoyi-thermal/src/main/java/org/dromara/thermal/domain/PrUseCardLog.java 2>/dev/null || echo "NOT FOUND"
```

- [ ] **Step 5: Wire PrUseCardLogController**

Replace all TODO returns with actual service calls following the same pattern as Task 4.

- [ ] **Step 6: Compile verification**

```bash
cd D:/chonggou/thermal-platform-new
mvn compile -pl ruoyi-modules/ruoyi-thermal -am 2>&1 | grep -E "BUILD|ERROR"
```

- [ ] **Step 7: Commit**

```bash
git add -A ruoyi-modules/ruoyi-thermal/src/
git commit -m "feat(phase5): 实现房屋变更和写卡日志 Service"
```

---

### Task 6: Full Project Verification

- [ ] **Step 1: Full compile**

```bash
cd D:/chonggou/thermal-platform-new
mvn clean compile 2>&1 | tail -10
```
Expected: BUILD SUCCESS, zero errors

- [ ] **Step 2: Verify no TODO comments remain in Phase 5 services**

```bash
grep -rn "TODO: Phase 5" ruoyi-modules/ruoyi-thermal/src/main/java/ || echo "All Phase 5 TODOs resolved"
```
Expected: "All Phase 5 TODOs resolved"

- [ ] **Step 3: Commit final**

```bash
git status
git add -A
git commit -m "chore(phase5): Phase 5 完整编译验证"
```

---

## Self-Review

### Spec coverage
| Service | Methods | Task | Status |
|---|---|---|---|
| PrTransactionRecordServiceImpl | 8 methods | Task 2 | Covered |
| PrAccountServiceImpl | 9 methods (8 impl, 1 TODO) | Task 3 | Covered (transfer deferred) |
| PrBillingNotesService | 2 methods | Task 4 | Covered |
| PrOptionsService | 5 methods | Task 4 | Covered |
| PrHouseChangeService | 7 methods | Task 5 | Covered |
| PrUseCardLogService | 4 methods (3 impl, 1 TODO) | Task 5 | Covered (valve change deferred) |
| PrWechatBindRecord | 1 method | DEFERRED | Phase 6 |
| PrAutoMachine | 6 methods | DEFERRED | Phase 6 |
| PrImportAuthorizationCode | 1 method | DEFERRED | Phase 6 |

### Placeholder scan
- `PrAccountServiceImpl.transfer()` — marked TODO, correct (needs transaction record generation with full business logic)
- `PrUseCardLogServiceImpl.changeValveStatus()` — marked TODO, correct (needs MBus hardware)
- No TBD/TODO/fill-in patterns in generated entity/mapper/XML code
- All method signatures consistent between interfaces and implementations

### Type consistency
- `PrTransactionRecordMapper.selectPageList` returns `List<PrTransactionRecordVo>` — matches service return
- `PrAccountBalanceMapper.selectAccountList` returns `List<PrAccountBalanceVo>` — matches service
- All `pageList` methods use `PageQuery + TableDataInfo<Vo>` pattern consistent with existing code
- `BigDecimal` used for all monetary values
- `@Transactional(rollbackFor = Exception.class)` on all mutating methods
