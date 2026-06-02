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

    /** 小区名称（enrich，非数据库字段） */
    private String orgName;

    /** 操作人名称（enrich，sys_user.nick_name） */
    private String operatorName;

    /** 操作类型 1写卡 2开卡 3补卡 */
    private String type;
    /** 卡类型 */
    private String cardType;
    /** 写卡报文JSON */
    private String content;
}
