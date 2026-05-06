package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_inspection_record")
public class PrInspectionRecord extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private Long planId;
    private Long personId;
    private String personName;
    private Long equipmentId;
    private String equipmentName;
    private String result;
    private String content;
    private String images;
    private String orgId;
    private String companyId;
}
