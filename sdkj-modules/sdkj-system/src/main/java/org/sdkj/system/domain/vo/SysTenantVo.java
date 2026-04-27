package org.sdkj.system.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.sdkj.common.excel.annotation.ExcelDictFormat;
import org.sdkj.common.excel.convert.ExcelDictConvert;
import org.sdkj.system.domain.SysTenant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 租户视图对象 sys_tenant
 *
 * @author Michelle.Chung
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysTenant.class)
public class SysTenantVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 租户编号
     */
    @ExcelProperty(value = "租户编号")
    private String tenantId;

    /**
     * 联系人
     */
    @ExcelProperty(value = "联系人")
    private String contactUserName;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String contactPhone;

    /**
     * 企业名称
     */
    @ExcelProperty(value = "企业名称")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @ExcelProperty(value = "统一社会信用代码")
    private String licenseNumber;

    /**
     * 地址
     */
    @ExcelProperty(value = "地址")
    private String address;

    /**
     * 企业性质（0运营商 1代理商 2物业 3商户 4服务商 5其他）
     */
    @ExcelProperty(value = "企业性质", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=运营商,1=代理商,2=物业,3=商户,4=服务商,5=其他")
    private String nature;

    /**
     * 营业执照号
     */
    @ExcelProperty(value = "营业执照号")
    private String businessLicense;

    /**
     * 法人代表
     */
    @ExcelProperty(value = "法人代表")
    private String legalPerson;

    /**
     * 开户银行
     */
    @ExcelProperty(value = "开户银行")
    private String bankName;

    /**
     * 银行地址
     */
    @ExcelProperty(value = "银行地址")
    private String bankAddress;

    /**
     * 账户名称
     */
    @ExcelProperty(value = "账户名称")
    private String accountName;

    /**
     * 对公账号
     */
    @ExcelProperty(value = "对公账号")
    private String corporateAccount;

    /**
     * 省份
     */
    @ExcelProperty(value = "省份")
    private String province;

    /**
     * 城市
     */
    @ExcelProperty(value = "城市")
    private String city;

    /**
     * 区县
     */
    @ExcelProperty(value = "区县")
    private String county;

    /**
     * 域名
     */
    @ExcelProperty(value = "域名")
    private String domain;

    /**
     * 企业简介
     */
    @ExcelProperty(value = "企业简介")
    private String intro;

    /**
     * 经度
     */
    @ExcelProperty(value = "经度")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @ExcelProperty(value = "纬度")
    private BigDecimal latitude;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 租户套餐编号
     */
    @ExcelProperty(value = "租户套餐编号")
    private Long packageId;

    /**
     * 过期时间
     */
    @ExcelProperty(value = "过期时间")
    private Date expireTime;

    /**
     * 用户数量（-1不限制）
     */
    @ExcelProperty(value = "用户数量")
    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    @ExcelProperty(value = "租户状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
    private String status;


}
