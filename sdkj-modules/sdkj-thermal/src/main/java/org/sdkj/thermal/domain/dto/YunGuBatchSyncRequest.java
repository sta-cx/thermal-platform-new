package org.sdkj.thermal.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 云谷批量数据同步请求体
 * 对应旧系统 ManuIdsVo
 */
@Data
public class YunGuBatchSyncRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 逗号分隔的设备编码列表（最多100个） */
    @JsonProperty("ManuIds")
    private String manuIds;
}
