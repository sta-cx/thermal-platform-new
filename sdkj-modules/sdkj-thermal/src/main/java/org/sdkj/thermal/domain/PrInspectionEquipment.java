package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String equipmentName;

    private String equipmentCode;

    private String companyId;

    private String orgId;
}
