package org.sdkj.thermal.domain.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 代理商用户 BO
 */
@Data
public class AgUserBo implements Serializable {

    private String id;

    private String userName;

    private String userPwd;

    private String phone;

    private String realName;

    private String nickName;

    private String idcard;

    private Integer sex;

    private String email;

    private String wxNumber;

    private String companyId;

    private Integer isEnabled;

    private Integer isSuper;
}
