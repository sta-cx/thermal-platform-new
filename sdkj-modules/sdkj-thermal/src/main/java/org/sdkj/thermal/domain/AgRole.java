package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.AgRoleVo;

/**
 * 代理商角色表
 * 对应 sys_role 表，用于代理商角色管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@AutoMapper(target = AgRoleVo.class)
public class AgRole extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String identifying;

    private String nature;

    private String roleDesc;

    private Integer isSuper;

    private String companyId;
}
