package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_pet")
public class PrPet extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private Long houseId;
    private String petName;
    private String petType;
    private String breed;
    private String color;
    private String vaccineStatus;
    private String orgId;
    private String companyId;
}
