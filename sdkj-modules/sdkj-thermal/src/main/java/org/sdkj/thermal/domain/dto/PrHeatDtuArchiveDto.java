package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * DTU 档案 DTO
 * 迁移自旧系统 PrHeatDtuArchive
 */
@Data
public class PrHeatDtuArchiveDto implements Serializable {

    private Long id;
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
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;
}
