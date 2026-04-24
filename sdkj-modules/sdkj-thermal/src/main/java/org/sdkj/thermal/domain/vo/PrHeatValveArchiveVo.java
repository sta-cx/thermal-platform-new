package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 户间阀门配表 View Object
 */
@Data
public class PrHeatValveArchiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    private String houseId;
    private String orgId;
    private String companyId;
    private String valveStatus;
    private Integer settingStatus;
    private Integer actualStatus;
    private BigDecimal inTemperature;
    private BigDecimal outTemperature;
    private String voltage;
    private Date valveTime;
    private Integer signalStrength;
    private Integer reportingInterval;
    private String intervalUnit;
    private Integer validTime;
    private Integer totalDegree;
    private Integer residueDegree;
    private Integer isChanged;
    private Integer isStop;
    private Date chanNumUpdateTime;
    private Date chanNumSyncTime;
    private String lastPerformId;
    private String dtuNum;
    private String dtuNumStatus;
    private String chanNum;
    private String installSite;
    private String dtuStatus;
    private Integer tradeTimes;
    private Integer isOpen;
    private String caliber;
    private String installType;
    private String groupNum25;
    private BigDecimal userSetTemp;
    private BigDecimal roomTemp;
    private BigDecimal avgTemp;
    private String valveModel;
    private Integer coldFlg;
    private Integer wkqLock;
    private Integer tempLow;
    private Integer tempHigh;
    private Integer workTime;
    private Integer totalOpenTime;
    private Integer dtuType;
    private BigDecimal insFlow;

    /** 关联房屋（非数据库字段） */
    private PrHouseVo prHouse;
    private String scopeStatus;
    private String statusName;
    private Integer isCharged;
}
