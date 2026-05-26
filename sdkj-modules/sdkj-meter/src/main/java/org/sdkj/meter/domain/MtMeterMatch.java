package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

/**
 * 仪表匹配（档案ID与类型关联表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_meter_match")
public class MtMeterMatch extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 档案ID */
    private Long archiveId;

    /** 仪表类型 (03=热力表 11=集中器 21=温控器 31=阀门) */
    private String meterType;

}
