package org.sdkj.thermal.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 代理商角色 BO
 */
@Data
public class AgRoleBo implements Serializable {

    private String id;

    private String name;

    private String identifying;

    private String nature;

    private String roleDesc;

    private Integer isSuper;

    private String companyId;
}
