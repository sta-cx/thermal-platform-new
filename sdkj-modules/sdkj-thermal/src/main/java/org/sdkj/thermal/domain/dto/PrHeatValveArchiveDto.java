package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 阀门档案 DTO
 * 迁移自旧系统 PrHeatValveArchive
 */
@Data
public class PrHeatValveArchiveDto implements Serializable {

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
    private int isChanged;
    private int isStop;
    private Date createTime;
    private Date updateTime;
    private Date chanNumUpdateTime;
    private Date chanNumSyncTime;
    private Long lastPerformId;
    private String dtuNum;
    private String dtuNumStatus;
    private String chanNum;
    private String installSite;
    private String dtuStatus;
    private String remark;
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
}
