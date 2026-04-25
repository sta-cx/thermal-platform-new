package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_scheduling")
public class PrScheduling extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String personId;
    private String personName;
    private Date workDate;
    private String shift;
    private String orgId;
    private String companyId;
}
