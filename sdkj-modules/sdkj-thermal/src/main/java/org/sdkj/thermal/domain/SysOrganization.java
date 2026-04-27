package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_organization")
public class SysOrganization extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;
    private String companyId;
    private String parentId;
    private String code;
    private String level;
    private String address;
    private String tele;
    private String bankName;
    private String corpotateAccount;
    private String businessLicense;
    private String parentName;
    private String province;
    private String city;
    private String county;
    private String street;
    private Boolean disabled;
    private String type;
    private String longitude;
    private String latitude;

    @TableField(exist = false)
    private List<SysOrganization> children;
}
