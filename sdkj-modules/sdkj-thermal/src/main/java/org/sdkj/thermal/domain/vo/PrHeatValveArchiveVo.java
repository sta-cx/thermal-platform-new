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

    private Long id;
    private Long archiveId;
    private String meterNum;
    private String cardNum;
    private String meterArcCode;
    private String meterArcName;
    private String concentratorCode;
    private String imeiNum;
    private String productId;
    private String deviceId;
    private Integer meterSerial;
    private Long houseId;
    private String orgId;
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
    private Long lastPerformId;
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

    // ========== 房屋关联字段（非数据库字段，用于列表展示） ==========

    private String orgName;
    private String buildingName;
    private String roomNum;
    private String houseType;
    private Integer isSpecial;
    private BigDecimal heatingArea;
    private String siteType;
    private Integer isVirtual;
    private BigDecimal curFlow;
}
