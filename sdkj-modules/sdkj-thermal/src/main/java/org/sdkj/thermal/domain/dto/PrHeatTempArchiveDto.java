package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 温采器配表 DTO (用于导入)
 */
@Data
public class PrHeatTempArchiveDto implements Serializable {

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
    private String houseId;
    private String orgId;
    private String companyId;
    private String valveStatus;
    private BigDecimal temper;
    private BigDecimal humidity;
    private BigDecimal voltage;
    private BigDecimal signalStrength;
    private Integer reportingInterval;
    private Integer intervalUnit;
    private Integer validTime;
    private Integer collectInterval;
    private Integer collectUnit;
    private Integer collectNum;
    private String movPlace;
    private Integer reportNumber;
    private Integer reportSuccNum;
    private Integer isChanged;
    private Integer isStop;
}
