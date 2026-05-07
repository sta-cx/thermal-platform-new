package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.AgCompanyVo;

import java.util.Date;

/**
 * 代理商公司表
 * 对应 sys_company 表，用于代理商管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_company")
@AutoMapper(target = AgCompanyVo.class)
public class AgCompany extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String code;

    private String province;

    private String city;

    private String county;

    private String street;

    private String address;

    private String postCode;

    private String principal;

    private String tele;

    private String fax;

    private String email;

    private Integer isEnabled;

    private Integer isAudited;

    private Integer nature;

    private String businessLicense;

    private String businessScope;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date foundedDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date businessTerm;

    private String economyCharacter;

    private String legalPerson;

    private String registeredCapital;

    private String businessLicenseImg;

    private String legalRepresentativeIdcardImg1;

    private String legalRepresentativeIdcardImg2;

    private String bankName;

    private String bankAddress;

    private String accountName;

    private String corpotateAccount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date settleTime;

    private Integer limitedHouses;

    private String description;

    private String longitude;

    private String latitude;
}
