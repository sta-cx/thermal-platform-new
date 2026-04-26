package org.sdkj.thermal.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 云谷数据同步响应数据项
 * 对应旧系统 YGDataDO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YunGuDataResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 设备编码 */
    @JsonProperty("MeterManuId")
    private String meterManuId;

    /** 显示名称 */
    @JsonProperty("ShowName")
    private String showName;

    /** 瞬时流量 */
    @JsonProperty("LastFlow")
    private BigDecimal lastFlow;

    /** 进水温度 */
    @JsonProperty("LastForwardT")
    private BigDecimal lastForwardT;

    /** 回水温度 */
    @JsonProperty("LastReturnT")
    private BigDecimal lastReturnT;

    /** 累计热量 */
    @JsonProperty("LastHeatSum")
    private BigDecimal lastHeatSum;

    /** 累计流量 */
    @JsonProperty("LastFlowSum")
    private BigDecimal lastFlowSum;

    /** 热功率 */
    @JsonProperty("LastHeatPower")
    private BigDecimal lastHeatPower;

    /** 本月流量 */
    @JsonProperty("LastFlowMonth")
    private BigDecimal lastFlowMonth;

    /** 本月热量 */
    @JsonProperty("LastHeatMonth")
    private BigDecimal lastHeatMonth;

    /** 阀门开度百分比 */
    @JsonProperty("LastValveOpenPercent")
    private Integer lastValveOpenPercent;

    /** 最后记录时间戳（毫秒） */
    @JsonProperty("LastRecordTs")
    private Long lastRecordTs;

    /** 室温 */
    @JsonProperty("LastRoomTemp")
    private BigDecimal lastRoomTemp;

    /** 室温记录时间戳 */
    @JsonProperty("LastRoomTempRecordTs")
    private Long lastRoomTempRecordTs;

    /** 阀门螺栓状态：1=开, 0=关 */
    @JsonProperty("BoltStatus")
    private Integer boltStatus;
}
