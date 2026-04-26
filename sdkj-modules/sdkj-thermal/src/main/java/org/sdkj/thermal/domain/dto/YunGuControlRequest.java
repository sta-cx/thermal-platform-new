package org.sdkj.thermal.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 云谷阀门控制请求体
 * 对应旧系统 YGData
 */
@Data
public class YunGuControlRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 设备编码（meter_num） */
    @JsonProperty("ManuId")
    private String manuId;

    /** 控制模式（必须为 11，表示手动模式） */
    @JsonProperty("ControlMode")
    private Integer controlMode;

    /** 阀门开度值（0-100） */
    @JsonProperty("Value")
    private String value;
}
