package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

/**
 * 仪表匹配（仪表档案与公司关联表）
 * 迁移自旧系统 MtMeterMatch
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_meter_match")
public class MtMeterMatch extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 档案ID */
    private Long archiveId;

    /** 公司ID */
    private String companyId;

    /** 仪表类型 (01=电表 02=水表 03=热力表 04=燃气表 11=集中器) */
    private String meterType;

}
