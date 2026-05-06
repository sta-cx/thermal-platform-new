package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_inspection_plan")
public class PrInspectionPlan extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String name;
    private Date startTime;
    private Date endTime;
    private Long equipmentId;
    private String orgId;
    private String companyId;
}
