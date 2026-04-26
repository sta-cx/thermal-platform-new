package org.sdkj.thermal.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 代理商物业菜单 BO
 */
@Data
public class AgPropertyMenuBo implements Serializable {

    private String id;

    private String companyId;

    private String menuIds;
}
