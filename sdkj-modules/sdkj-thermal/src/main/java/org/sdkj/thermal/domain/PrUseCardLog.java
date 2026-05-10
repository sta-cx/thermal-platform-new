package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrUseCardLogVo;

import java.util.Date;

/**
 * 写卡日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_use_card_log")
@AutoMapper(target = PrUseCardLogVo.class)
public class PrUseCardLog extends BaseEntity {

    @TableId(value = "id")
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
