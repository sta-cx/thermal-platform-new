package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtAlertVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 报警记录
 * 迁移自旧系统 HtAlert
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_alert")
@AutoMapper(target = HtAlertVo.class)
public class HtAlert extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 楼栋ID */
    private String buildingId;

    /** 单元ID */
    private String unitId;

    /** 户ID */
    private String houseId;

    /** 仪表ID */
    private String meterId;

    /** 是否收费 */
    private Integer isCharged;

    /** 阀门状态 */
    private Integer valve;

    /** 进水温度 */
    private BigDecimal inTemp;

    /** 回水温度 */
    private BigDecimal outTemp;

    /** 室温 */
    private BigDecimal roomTemp;

    /** 报警类型 */
    private Integer alertType;

    /** 报警时间 */
    private Date alertTime;

    /** 报警状态 */
    private String alertStatus;

    /** 组织ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 是否在维修中 */
    private Integer inMaintenance;

}
