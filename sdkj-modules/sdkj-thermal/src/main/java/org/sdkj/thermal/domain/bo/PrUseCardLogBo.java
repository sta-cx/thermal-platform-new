package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrUseCardLog;

import java.util.Date;

/**
 * 写卡日志业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrUseCardLog.class, reverseConvertGenerate = false)
public class PrUseCardLogBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 仪表ID */
    private Long meterId;

    /** 仪表编号 */
    private String meterNum;

    /** 用户ID */
    private String userId;

    /** 卡号 */
    private String cardNum;

    /** 阀门状态 */
    private Integer valveStatus;

    /** 操作时间 */
    private Date operationTime;

    /** 小区ID */
    private String orgId;


    /** 操作员ID */
    private String operatorId;
}
