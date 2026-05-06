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

    private String parentId;
    private String level;
    private String name;
    private String code;
    private String orgId;
    private String leader;
    private String phone;
    private String address;
    private Integer sort;
    private Integer status;
    private String companyId;
    private String remark;

    @TableField(exist = false)
    private List<SysOrganization> children;
}
