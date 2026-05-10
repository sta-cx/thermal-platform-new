package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_inspection_person")
public class PrInspectionPerson extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String name;
    private String phone;
    private String type;
}
