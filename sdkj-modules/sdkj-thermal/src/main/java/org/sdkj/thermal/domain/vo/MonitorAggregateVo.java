package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运行监控聚合统计 View Object
 */
@Data
public class MonitorAggregateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 平均回水温度 */
    private BigDecimal avgOutTemp;

    /** 平均进水温度 */
    private BigDecimal avgInTemp;

    /** 回水温度偏差下限(正常区间下界) */
    private BigDecimal outTempPjMin;

    /** 回水温度偏差上限(正常区间上界) */
    private BigDecimal outTempPjMax;

    /** 总设备数 */
    private Integer totalCount;

    /** 在线数 */
    private Integer onlineCount;

    /** 离线数 */
    private Integer offlineCount;

    /** 位置分组统计: 中间户(siteType=4) */
    private SiteGroupStat midGroup;

    /** 位置分组统计: 边户(siteType=1) */
    private SiteGroupStat sideGroup;

    /** 位置分组统计: 顶户(siteType=2) */
    private SiteGroupStat topGroup;

    /** 位置分组统计: 底户(siteType=3) */
    private SiteGroupStat bottomGroup;

    @Data
    public static class SiteGroupStat implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** 该位置户数 */
        private Integer count;
        /** 该位置平均回水温度 */
        private BigDecimal avgOutTemp;
        /** 该位置平均室温 */
        private BigDecimal avgRoomTemp;
    }
}
