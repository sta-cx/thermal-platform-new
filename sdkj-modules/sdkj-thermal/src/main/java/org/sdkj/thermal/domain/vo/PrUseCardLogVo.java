package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrUseCardLog;

import java.util.Date;

/**
 * 写卡日志 VO
 */
@Data
@AutoMapper(target = PrUseCardLog.class)
public class PrUseCardLogVo {
    private Long id;
    private Long meterId;
    private String meterNum;
    private String userId;
    private String cardNum;
    private Integer valveStatus;
    private Date operationTime;
    private String orgId;
    private String operatorId;
}
