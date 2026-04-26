package org.sdkj.thermal.domain.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 代理商公司 BO
 */
@Data
public class AgCompanyBo implements Serializable {

    private String id;

    private String name;

    private String code;

    private String parentId;

    private String seq;

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

    private Date foundedDate;

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

    private Date expireTime;

    private Date settleTime;

    private Integer limitedHouses;

    private String description;

    private String longitude;

    private String latitude;
}
