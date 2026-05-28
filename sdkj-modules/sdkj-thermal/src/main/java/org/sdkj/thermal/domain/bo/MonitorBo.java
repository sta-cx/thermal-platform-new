package org.sdkj.thermal.domain.bo;

import lombok.Data;

/**
 * 运行监控查询参数
 */
@Data
public class MonitorBo {

    /** 小区ID */
    private String orgId;

    /** 楼宇ID */
    private String buildingId;

    /** 单元 */
    private String unit;

    /** 搜索关键字（表号/档案名称） */
    private String search;

    /** 父级ID（单元级设备查询） */
    private String parentId;

    /** 换热站ID */
    private String stationId;

    /** 分区ID */
    private String partitionId;
}
