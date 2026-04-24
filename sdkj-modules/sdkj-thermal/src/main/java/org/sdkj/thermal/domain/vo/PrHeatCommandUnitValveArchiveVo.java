package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 单元控制阀门配表 View Object
 */
@Data
public class PrHeatCommandUnitValveArchiveVo implements Serializable {

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
    private String unitId;
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
    private String dtuNum;
    private Integer dtuType;
    private String dtuNumStatus;
    private String chanNum;
    private String installSite;
    private String dtuStatus;
    private String caliber;

    /** 单元信息（查询用） */
    private PrUnitVo prUnit;
}
