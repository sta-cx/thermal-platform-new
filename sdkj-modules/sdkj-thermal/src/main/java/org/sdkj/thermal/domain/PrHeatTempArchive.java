package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 温采器配表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_heat_temp_archive")
@AutoMapper(target = PrHeatTempArchiveVo.class)
public class PrHeatTempArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 档案ID */
    private String archiveId;

    /** 仪表档案编码 */
    private String meterArcCode;

    /** 仪表档案名称 */
    private String meterArcName;

    /** 仪表编号 */
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 阀门状态 */
    private String valveStatus;

    /** 温度 */
    private BigDecimal temper;

    /** 湿度 */
    private BigDecimal humidity;

    /** 电压 */
    private BigDecimal voltage;

    /** 信号强度 */
    private BigDecimal signalStrength;

    /** 采集时间 */
    private Date collectTime;

    /** 上报间隔 */
    private Integer reportingInterval;

    /** 间隔单位 */
    private Integer intervalUnit;

    /** 有效时间 */
    private Integer validTime;

    /** 采集间隔 */
    private Integer collectInterval;

    /** 采集单位 */
    private Integer collectUnit;

    /** 采集数量 */
    private Integer collectNum;

    /** 移动位置 */
    private String movPlace;

    /** 上报次数 */
    private Integer reportNumber;

    /** 上报成功次数 */
    private Integer reportSuccNum;

    /** 上报时间 */
    private Date reportTime;

    /** 是否变更 */
    private int isChanged;

    /** 是否停用 */
    private int isStop;

    /** 房屋ID */
    private String houseId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 集中器编码 */
    private String concentratorCode;

    /** IMEI号 */
    private String imeiNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;

    // ========== 非数据库字段 ==========

    /** 关联房屋 */
    @TableField(exist = false)
    private PrHouse prHouse;
}
