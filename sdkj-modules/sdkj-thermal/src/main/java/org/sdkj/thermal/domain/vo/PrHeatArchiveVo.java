package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋热表配表 View Object
 */
@Data
public class PrHeatArchiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String orgId;
    private String orgName;
    private String buildingName;
    private Long houseId;
    private String roomNum;
    private Long archiveId;
    private String meterArcCode;
    private String meterArcName;
    private String meterNum;
    private String imei;
    private String cardNum;
    private String productId;
    private String deviceId;
    private Integer meterSerial;
    private String lineNumber;
    private String specification;
    private String model;
    private String concentratorCode;
    private String installSite;
    private Long standardId;
    private BigDecimal standardPrice;
    private BigDecimal inTemperature;
    private BigDecimal outTemperature;
    private BigDecimal diffTemperature;
    private BigDecimal settingTemperature;
    private String settingStatus;
    private String valveStatus;
    private Integer isOpened;
    private BigDecimal hisMoney;
    private Date openedTime;
    private Date startTime;
    private Date endTime;
    private Integer isExpense;
    private Integer isNotify;
    private Integer isChanged;
    private Integer isStop;
    private Integer startReading;
    private BigDecimal totalHeat;
    private BigDecimal totalFlow;
    private BigDecimal totalWorktime;
    private BigDecimal currentReading;
    private BigDecimal totalUsed;
    private Integer tradeTimes;
    private BigDecimal totalMoney;
    private BigDecimal totalRecharge;
    private BigDecimal currentBalance;
    private BigDecimal payDegrees;
    private BigDecimal hoardLimit;
    private BigDecimal alarmValue;
    private BigDecimal closeValue;
    private Integer isSteps;
    private String measurement;
    private String type;
    private String command;
    private String valveOpening;
    private Date commandTime;
    private String commandStatus;
    private Date returnTime;
    private String isPrint;
    private String printType;

    /** 缴费方式1 */
    private String paymentMethod1;
    /** 缴费方式2 */
    private String paymentMethod2;
    /** 缴费金额1 */
    private BigDecimal paymentMoney1;
    /** 缴费金额2 */
    private BigDecimal paymentMoney2;
    /** 已扣除 */
    private BigDecimal deducted;
    /** 实收 */
    private BigDecimal paidIn;

    /** 备注 */
    private String remark;
}
