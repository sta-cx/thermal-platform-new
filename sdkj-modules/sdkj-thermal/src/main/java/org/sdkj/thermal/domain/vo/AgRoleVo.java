package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 代理商角色 VO
 */
@Data
public class AgRoleVo implements Serializable {

    private String id;

    private String name;

    private String identifying;

    private String nature;

    private String roleDesc;

    private Integer isSuper;

    private String companyId;
}
