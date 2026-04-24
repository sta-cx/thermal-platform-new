package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋热量表配表 View Object
 */
@Data
public class PrHeatHotArchiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String archiveId;
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
    private String standardId;
    private BigDecimal standardPrice;
    private int isSteps;
    private BigDecimal startReading;
    private BigDecimal currentReading;
    private BigDecimal totalUsed;
    private int tradeTimes;
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
    private int signalStrength;
    private String cellStatus;
    private int isOpened;
    private Date openedTime;
    private int isExpense;
    private int isNotify;
    private int isChanged;
    private int isStop;
    private String houseId;
    private String companyId;
    private String orgId;
    private String imeiNum;
    private String dtuNum;
    private Integer dtuType;
    private String dtuNumStatus;
    private String chanNum;
    private String dtuStatus;
    private String installType;

    /** 关联房屋（非数据库字段） */
    private PrHouseVo prHouse;
    /** 关联收费标准（非数据库字段） */
    private PrStandardVo prStandard;
    /** 关联用户房屋（非数据库字段） */
    private Object prUserHouse;
}
