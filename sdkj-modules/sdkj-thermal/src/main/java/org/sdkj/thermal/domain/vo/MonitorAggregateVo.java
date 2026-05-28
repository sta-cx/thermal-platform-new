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

    /** 总设备数 */
    private Integer totalCount;

    /** 在线数 */
    private Integer onlineCount;

    /** 离线数 */
    private Integer offlineCount;
}
