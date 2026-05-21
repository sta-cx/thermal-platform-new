package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrValveOperationLog;

import java.util.Date;

@Data
@AutoMapper(target = PrValveOperationLog.class)
public class PrValveOperationLogVo {
    private Long id;
    private Long meterId;
    private String meterNum;
    private String userId;
    private String cardNum;
    private Integer valveStatus;
    private Date operationTime;
    private String orgId;
    private String operatorId;
    private Date createTime;
}
