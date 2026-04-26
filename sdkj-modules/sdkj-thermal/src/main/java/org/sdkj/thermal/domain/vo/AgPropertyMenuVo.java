package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 代理商物业菜单 VO
 */
@Data
public class AgPropertyMenuVo implements Serializable {

    private String id;

    private String companyId;

    private String menuId;

    private String menuName;

    private String menuCode;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}
