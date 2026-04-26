package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 代理商用户 VO
 */
@Data
public class AgUserVo implements Serializable {

    private String id;

    private String userName;

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

    private Date createTime;
}
