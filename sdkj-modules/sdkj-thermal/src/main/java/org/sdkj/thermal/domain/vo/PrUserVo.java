package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import org.sdkj.common.sensitive.core.SensitiveStrategy;
import org.sdkj.common.sensitive.annotation.Sensitive;
import lombok.Data;
import org.sdkj.thermal.domain.PrUser;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户档案 View Object
 */
@Data
@AutoMapper(target = PrUser.class)
public class PrUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;
    private String name;
    private Integer idType;
    private String idNo;
    private Integer sex;
    private Integer isIdAuth;
    private String nation;
    private Date birthday;
    private String idStartdate;
    private String idEnddate;
    private String idDepartment;
    private String address;
    private String employer;
    private String openId;
    private String wxNumber;
    private String qqNumber;
    private Integer occupation;
    private Integer education;
    private Integer hobby;
    private String email;
    private String emerContact;
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String emerPhone;
    private String orgId;
    private String companyId;
    private String seq;
    private String headPhoto;
    private String frontPhoto;
    private String backPhoto;
    private Date createTime;
    private Date updateTime;
}
