package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrHeatCommandValveArchiveVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 户间控制阀门配表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_heat_command_valve_archive")
@AutoMapper(target = PrHeatCommandValveArchiveVo.class)
public class PrHeatCommandValveArchive extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 档案ID */
    private Long archiveId;

    /** 仪表编号 */
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 仪表档案编码 */
    private String meterArcCode;

    /** 仪表档案名称 */
    private String meterArcName;

    /** 集中器编码 */
    private String concentratorCode;

    /** IMEI号 */
    private String imeiNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;

    /** 仪表序列号 */
    private Integer meterSerial;

    /** 房屋ID */
    private Long houseId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 阀门状态 */
    private String valveStatus;

    /** 设置状态 */
    private Integer settingStatus;

    /** 实际状态 */
    private Integer actualStatus;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 出水温度 */
    private BigDecimal outTemperature;

    /** 电压 */
    private String voltage;

    /** 阀门时间 */
    private Date valveTime;

    /** 信号强度 */
    private Integer signalStrength;

    /** 上报间隔 */
    private Integer reportingInterval;

    /** 间隔单位 */
    private String intervalUnit;

    /** 有效时间 */
    private Integer validTime;

    /** 总开度 */
    private Integer totalDegree;

    /** 剩余开度 */
    private Integer residueDegree;

    /** 是否变更 */
    private Integer isChanged;

    /** 是否停用 */
    private Integer isStop;

    /** DTU编号 */
    private String dtuNum;

    /** DTU类型 */
    private Integer dtuType;

    /** DTU编号状态 */
    private String dtuNumStatus;

    /** 通道号 */
    private String chanNum;

    /** 安装位置 */
    private String installSite;

    /** DTU状态 */
    private String dtuStatus;

    /** 交易次数 */
    private Integer tradeTimes;

    /** 是否开启 */
    private Integer isOpen;

    /** 口径 */
    private String caliber;

    // ========== 非数据库字段 ==========

    /** 关联房屋 */
    @TableField(exist = false)
    private PrHouse prHouse;

    /** 范围状态 */
    @TableField(exist = false)
    private String scopeStatus;

    /** 状态名称 */
    @TableField(exist = false)
    private String statusName;
}
