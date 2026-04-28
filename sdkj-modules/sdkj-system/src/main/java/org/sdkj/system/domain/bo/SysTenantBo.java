package org.sdkj.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.core.validate.AddGroup;
import org.sdkj.common.core.validate.EditGroup;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.system.domain.SysTenant;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租户业务对象 sys_tenant
 *
 * @author Michelle.Chung
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenant.class, reverseConvertGenerate = false)
public class SysTenantBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空", groups = { AddGroup.class, EditGroup.class })
    private String contactUserName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空", groups = { AddGroup.class, EditGroup.class })
    private String contactPhone;

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String companyName;

    /**
     * 用户名（创建系统用户）
     */
    @NotBlank(message = "用户名不能为空", groups = { AddGroup.class })
    private String username;

    /**
     * 密码（创建系统用户）
     */
    @NotBlank(message = "密码不能为空", groups = { AddGroup.class })
//    @Pattern(regexp = RegexConstants.PASSWORD, message = "{user.password.format.valid}", groups = { AddGroup.class })
    private String password;

    /**
     * 统一社会信用代码
     */
    private String licenseNumber;

    /**
     * 地址
     */
    private String address;

    /**
     * 企业性质（0运营商 1代理商 2物业 3商户 4服务商 5其他）
     */
    private String nature;

    /**
     * 营业执照号
     */
    private String businessLicense;

    /**
     * 法人代表
     */
    private String legalPerson;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行地址
     */
    private String bankAddress;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 对公账号
     */
    private String corporateAccount;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String county;

    /**
     * 域名
     */
    private String domain;

    /**
     * 租户数据库连接URL
     */
    private String dbUrl;

    /**
     * 租户数据库用户名
     */
    private String dbUsername;

    /**
     * 租户数据库密码
     */
    private String dbPassword;

    /**
     * 租户数据库驱动
     */
    private String dbDriver;

    /**
     * 企业简介
     */
    private String intro;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户套餐编号
     */
    @NotNull(message = "租户套餐不能为空", groups = { AddGroup.class })
    private Long packageId;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户数量（-1不限制）
     */
    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    private String status;


}
