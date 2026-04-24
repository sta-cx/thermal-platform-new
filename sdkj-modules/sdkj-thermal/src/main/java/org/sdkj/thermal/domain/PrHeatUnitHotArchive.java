package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 单元热表配表
 * 迁移自旧系统 PrHeatUnitHotArchive
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_heat_unit_hot_archive")
@AutoMapper(target = PrHeatUnitHotArchiveVo.class)
public class PrHeatUnitHotArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 档案ID */
    private String archiveId;

    /** 仪表档案编码 */
    private String meterArcCode;

    /** 仪表档案名称 */
    private String meterArcName;

    /** 表号 */
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;

    /** 表序号 */
    private Integer meterSerial;

    /** 集中器编码 */
    private String concentratorCode;

    /** 囤积限额 */
    private BigDecimal hoardLimit;

    /** 报警值 */
    private BigDecimal alarmValue;

    /** 关阀值 */
    private Long closeValue;

    /** 计量方式 */
    private String measurement;

    /** 安装位置 */
    private String installSite;

    /** 收费标准ID */
    private String standardId;

    /** 收费标准单价 */
    private BigDecimal standardPrice;

    /** 是否阶梯 */
    private Integer isSteps;

    /** 起始读数 */
    private BigDecimal startReading;

    /** 当前读数 */
    private BigDecimal currentReading;

    /** 总用量 */
    private BigDecimal totalUsed;

    /** 交易次数 */
    private Integer tradeTimes;

    /** 历史欠费 */
    private BigDecimal hisMoney;

    /** 总金额 */
    private BigDecimal totalMoney;

    /** 总充值 */
    private BigDecimal totalRecharge;

    /** 当前余额 */
    private BigDecimal currentBalance;

    /** 已购量 */
    private BigDecimal payDegrees;

    /** 阀门状态 */
    private String valveStatus;

    /** 总流量 */
    private BigDecimal totalFlow;

    /** 当前流量 */
    private BigDecimal curFlow;

    /** 总工作时间 */
    private BigDecimal totalWorktime;

    /** 阀门时间 */
    private Date valveTime;

    /** 状态1 */
    private String status1;

    /** 状态2 */
    private String status2;

    /** 热功率 */
    private BigDecimal thermalPower;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 出水温度 */
    private BigDecimal outTemperature;

    /** 电压 */
    private BigDecimal voltage;

    /** 信号强度 */
    private Integer signalStrength;

    /** 电池状态 */
    private String cellStatus;

    /** 是否开户 */
    private Integer isOpened;

    /** 开户时间 */
    private Date openedTime;

    /** 是否计费 */
    private Integer isExpense;

    /** 是否提醒 */
    private Integer isNotify;

    /** 是否变更 */
    private Integer isChanged;

    /** 是否停用 */
    private Integer isStop;

    /** 单元ID */
    private String unitId;

    /** 公司ID */
    private String companyId;

    /** 小区ID */
    private String orgId;

    /** IMEI号 */
    private String imeiNum;

    /** DTU编号 */
    private String dtuNum;

    /** DTU类型 */
    private Integer dtuType;

    /** DTU编号状态 */
    private String dtuNumStatus;

    /** 通道号 */
    private String chanNum;

    /** DTU状态 */
    private String dtuStatus;

    // ========== 非数据库字段 ==========

    /** 单元信息（查询用） */
    @TableField(exist = false)
    private PrUnit prUnit;
}
