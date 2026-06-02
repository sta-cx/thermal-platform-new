package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/** 写卡日志 Bo —— insert 用前 8 字段；查询用 orgId/buildingId/unitCode/type/operatorId/startTime/endTime */
@Data
public class PrWriteCardLogBo {
    // —— insert ——
    @NotNull(message = "表ID不能为空")
    private Long meterId;
    private String meterNum;
    private String cardNum;
    @NotBlank(message = "操作类型不能为空")
    private String type;       // 1写卡 2开卡 3补卡
    private String cardType;   // 默认 "1"
    private String content;    // heat_dlsd_write JSON
    private String orgId;
    private Integer valveStatus;

    // —— 查询筛选 ——
    private String buildingId;
    private String unitCode;
    private String operatorId;
    private Date startTime;
    private Date endTime;
}
