package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * DTU采集器配表 View Object
 */
@Data
public class PrHeatDtuArchiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    private String dtuNum;

    private String installSite;

    private String status;

    private String ip;

    private String chanNum;

    private Integer channelNum;

    private Date channelNumTime;

    private Date latestTime;

    private Date lastTime;

    private String orgId;

    private String companyId;

    /** 控制范围（非数据库字段） */
    private String controlRange;
}
