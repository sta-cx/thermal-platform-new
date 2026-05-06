package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 单元热表配表 View Object
 */
@Data
public class PrHeatUnitHotArchiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long archiveId;
    private String meterArcCode;
    private String meterArcName;
    private String meterNum;
    private String cardNum;
    private String productId;
    private String deviceId;
    private Integer meterSerial;
    private String concentratorCode;
    private BigDecimal hoardLimit;
    private BigDecimal alarmValue;
    private Long closeValue;
    private String measurement;
    private String installSite;
    private Long standardId;
    private BigDecimal standardPrice;
    private Integer isSteps;
    private BigDecimal startReading;
    private BigDecimal currentReading;
    private BigDecimal totalUsed;
    private Integer tradeTimes;
    private BigDecimal hisMoney;
    private BigDecimal totalMoney;
    private BigDecimal totalRecharge;
    private BigDecimal currentBalance;
    private BigDecimal payDegrees;
    private String valveStatus;
    private BigDecimal totalFlow;
    private BigDecimal curFlow;
    private BigDecimal totalWorktime;
    private Date valveTime;
    private String status1;
    private String status2;
    private BigDecimal thermalPower;
    private BigDecimal inTemperature;
    private BigDecimal outTemperature;
    private BigDecimal voltage;
    private Integer signalStrength;
    private String cellStatus;
    private Integer isOpened;
    private Date openedTime;
    private Integer isExpense;
    private Integer isNotify;
    private Integer isChanged;
    private Integer isStop;
    private Long unitId;
    private String companyId;
    private String orgId;
    private String imeiNum;
    private String dtuNum;
    private Integer dtuType;
    private String dtuNumStatus;
    private String chanNum;
    private String dtuStatus;

    /** 单元信息（查询用） */
    private PrUnitVo prUnit;
}
