package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 温采器配表 View Object
 */
@Data
public class PrHeatTempArchiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String archiveId;
    private String meterArcCode;
    private String meterArcName;
    private String meterNum;
    private String cardNum;
    private String valveStatus;
    private BigDecimal temper;
    private BigDecimal humidity;
    private BigDecimal voltage;
    private BigDecimal signalStrength;
    private Date collectTime;
    private Integer reportingInterval;
    private Integer intervalUnit;
    private Integer validTime;
    private Integer collectInterval;
    private Integer collectUnit;
    private Integer collectNum;
    private String movPlace;
    private Integer reportNumber;
    private Integer reportSuccNum;
    private Date reportTime;
    private int isChanged;
    private int isStop;
    private String houseId;
    private String orgId;
    private String companyId;
    private String concentratorCode;
    private String imeiNum;
    private String productId;
    private String deviceId;

    /** 关联房屋（非数据库字段） */
    private PrHouseVo prHouse;
}
