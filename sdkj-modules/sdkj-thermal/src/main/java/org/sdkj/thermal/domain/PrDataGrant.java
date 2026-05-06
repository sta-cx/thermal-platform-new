package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_data_grant")
public class PrDataGrant extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private Long userId;

    private String companyId;

    private String orgId;

    @TableLogic(value = "0", delval = "1")
    private String delFlag;
}
