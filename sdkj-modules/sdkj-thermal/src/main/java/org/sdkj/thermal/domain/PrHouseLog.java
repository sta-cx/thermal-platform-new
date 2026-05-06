package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_house_log")
public class PrHouseLog extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private Long houseId;
    private String changeType;
    private Integer changeVal;
    private String orgId;
    private String companyId;
    private String remark;

    @TableField(exist = false)
    private String roomNum;
    @TableField(exist = false)
    private String orgName;
    @TableField(exist = false)
    private String unitCode;
    @TableField(exist = false)
    private String buildingName;
    @TableField(exist = false)
    private String createByName;
}
