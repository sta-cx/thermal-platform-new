package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 单元热表配表 DTO (用于导入)
 */
@Data
public class PrHeatUnitHotArchiveDto implements Serializable {

    private String id;
    private String archiveId;
    private String meterNum;
    private String cardNum;
    private String meterArcCode;
    private String meterArcName;
    private String concentratorCode;
    private String imeiNum;
    private String productId;
    private String deviceId;
    private Integer meterSerial;
    private String unitId;
    private String orgId;
    private String companyId;
    private String valveStatus;
    private Integer settingStatus;
    private Integer actualStatus;
    private BigDecimal inTemperature;
    private BigDecimal outTemperature;
    private BigDecimal voltage;
    private Integer signalStrength;
    private Integer reportingInterval;
    private String intervalUnit;
    private Integer validTime;
    private Integer totalDegree;
    private Integer residueDegree;
    private Integer isChanged;
    private Integer isStop;
    private String dtuNum;
    private Integer dtuType;
    private String dtuNumStatus;
    private String chanNum;
    private String installSite;
    private String dtuStatus;
    private String caliber;
    private BigDecimal hoardLimit;
    private BigDecimal alarmValue;
    private Long closeValue;
    private String measurement;
    private String standardId;
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
    private BigDecimal totalFlow;
    private BigDecimal curFlow;
    private BigDecimal totalWorktime;
    private String status1;
    private String status2;
    private BigDecimal thermalPower;
    private String cellStatus;
    private Integer isOpened;
    private Integer isExpense;
    private Integer isNotify;
}
