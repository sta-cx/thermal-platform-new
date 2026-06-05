package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋热表配表
 * 迁移自旧系统 PrHeatArchive
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_heat_archive")
@AutoMapper(target = PrHeatArchiveVo.class)
public class PrHeatArchive extends BaseEntity {

    @TableId(value = "id")
    private Long id;


    /** 小区ID */
    private String orgId;

    /** 小区名称 */
    private String orgName;

    /** 楼宇名称 */
    private String buildingName;

    /** 房屋ID */
    private Long houseId;

    /** 房间号 */
    private String roomNum;

    /** 档案ID */
    private Long archiveId;

    /** 仪表档案编码 */
    private String meterArcCode;

    /** 仪表档案名称 */
    private String meterArcName;

    /** 表号 */
    private String meterNum;

    /** IMEI号 */
    private String imei;

    /** 卡号 */
    private String cardNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;

    /** 表序号 */
    private Integer meterSerial;

    /** 线路号 */
    private String lineNumber;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 集中器编码 */
    private String concentratorCode;

    /** 安装位置 */
    private String installSite;

    /** 收费标准ID */
    private Long standardId;

    /** 收费标准单价 */
    private BigDecimal standardPrice;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 出水温度 */
    private BigDecimal outTemperature;

    /** 温差 */
    private BigDecimal diffTemperature;

    /** 设定温度 */
    private BigDecimal settingTemperature;

    /** 设定状态 */
    private Integer settingStatus;

    /** 阀门状态 */
    private String valveStatus;

    /** 是否开户 */
    private Integer isOpened;

    /** 历史欠费 */
    private BigDecimal hisMoney;

    /** 开户时间 */
    private Date openedTime;

    /** 开始时间（查询参数，非持久化） */
    @TableField(exist = false)
    private Date startTime;

    /** 结束时间（查询参数，非持久化） */
    @TableField(exist = false)
    private Date endTime;

    /** 是否计费 */
    private Integer isExpense;

    /** 是否提醒 */
    private Integer isNotify;

    /** 是否变更 */
    private Integer isChanged;

    /** 是否停用 */
    private Integer isStop;

    /** 起始读数 */
    private Integer startReading;

    /** 总热量 */
    private BigDecimal totalHeat;

    /** 总流量 */
    private BigDecimal totalFlow;

    /** 总工作时间 */
    private BigDecimal totalWorktime;

    /** 当前读数 */
    private BigDecimal currentReading;

    /** 总用量 */
    private BigDecimal totalUsed;

    /** 交易次数 */
    private Integer tradeTimes;

    /** 总金额 */
    private BigDecimal totalMoney;

    /** 总充值 */
    private BigDecimal totalRecharge;

    /** 当前余额 */
    private BigDecimal currentBalance;

    /** 已购量 */
    private BigDecimal payDegrees;

    /** 囤积限额 */
    private BigDecimal hoardLimit;

    /** 报警值 */
    private BigDecimal alarmValue;

    /** 关阀值 */
    private BigDecimal closeValue;

    /** 是否阶梯 */
    private Integer isSteps;

    /** 计量方式 */
    private String measurement;

    /** 类型 */
    private String type;

    /** 命令 */
    private String command;

    /** 阀门开度 */
    private String valveOpening;

    /** 命令时间 */
    private Date commandTime;

    /** 命令状态 */
    private String commandStatus;

    /** 返回时间 */
    private Date returnTime;

    /** 是否打印 */
    private String isPrint;

    /** 打印类型 */
    private String printType;

    // ========== 非数据库字段 ==========

    /** 缴费方式1（查询用） */
    @TableField(exist = false)
    private String paymentMethod1;

    /** 缴费方式2（查询用） */
    @TableField(exist = false)
    private String paymentMethod2;

    /** 缴费金额1（查询用） */
    @TableField(exist = false)
    private BigDecimal paymentMoney1;

    /** 缴费金额2（查询用） */
    @TableField(exist = false)
    private BigDecimal paymentMoney2;

    /** 已扣除（查询用） */
    @TableField(exist = false)
    private BigDecimal deducted;

    /** 实收（查询用） */
    @TableField(exist = false)
    private BigDecimal paidIn;

    /** 备注 */
    private String remark;
}
