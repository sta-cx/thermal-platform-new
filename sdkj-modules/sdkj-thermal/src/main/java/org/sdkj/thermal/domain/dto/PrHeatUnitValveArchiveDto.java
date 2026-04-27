package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 单元阀门配表 DTO (用于导入)
 */
@Data
public class PrHeatUnitValveArchiveDto implements Serializable {

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
    private String voltage;
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
    private String installType;
    private String groupNum25;
    private String valveModel;
}
