package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_heat_station")
public class PrHeatStation extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String code;
    private String name;
    private String type;
    private String tel;
    private String principal;
    private String address;
    private String seq;
    private String orgId;
    private String companyName;
}
