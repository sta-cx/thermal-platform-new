package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

/**
 * 巡检设备
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_inspection_equipment")
public class PrInspectionEquipment extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String equipmentName;

    private String equipmentCode;


    private String orgId;
}
