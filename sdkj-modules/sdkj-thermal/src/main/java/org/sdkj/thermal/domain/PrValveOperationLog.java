package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_use_card_log")
@AutoMapper(target = PrValveOperationLogVo.class)
public class PrValveOperationLog extends BaseEntity {
    @TableId(value = "id")
    private Long id;
    private Long meterId;
    private String meterNum;
    private String userId;
    private String cardNum;
    private Integer valveStatus;
    private Date operationTime;
    private String orgId;
    private String operatorId;

    /** 操作类型 1写卡 2开卡 3补卡（写卡日志视角，阶段5 阀门操作视角不使用） */
    private String type;
    /** 卡类型 */
    private String cardType;
    /** 写卡报文JSON */
    private String content;
}
