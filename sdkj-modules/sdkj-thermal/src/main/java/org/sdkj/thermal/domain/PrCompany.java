package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

/**
 * 物业公司实体
 * 对应 sys_company 表，用于物业管理端
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_company")
public class PrCompany extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String name;
    private String code;
    private String province;
    private String city;
    private String county;
    private String address;
    private String tele;
    private String principal;
    private String description;
    private Integer nature;
    private Integer isEnabled;
}
