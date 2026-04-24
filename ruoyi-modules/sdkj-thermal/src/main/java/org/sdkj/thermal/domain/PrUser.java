package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrUserVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户档案
 * 迁移自旧系统 PrUser
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_user")
@AutoMapper(target = PrUserVo.class)
public class PrUser extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 手机号 */
    private String phone;

    /** 姓名 */
    private String name;

    /** 证件类型 */
    private Integer idType;

    /** 证件号码 */
    private String idNo;

    /** 性别 */
    private Integer sex;

    /** 是否实名认证 */
    private Integer isIdAuth;

    /** 民族 */
    private String nation;

    /** 生日 */
    private Date birthday;

    /** 身份证起始日期 */
    private String idStartdate;

    /** 身份证到期日期 */
    private String idEnddate;

    /** 发证机关 */
    private String idDepartment;

    /** 现住址 */
    private String address;

    /** 工作单位 */
    private String employer;

    /** 微信公众号openid */
    private String openId;

    /** 微信号 */
    private String wxNumber;

    /** QQ号 */
    private String qqNumber;

    /** 职业 */
    private Integer occupation;

    /** 学历 */
    private Integer education;

    /** 兴趣爱好 */
    private Integer hobby;

    /** EMAIL */
    private String email;

    /** 紧急联系人（姓名） */
    private String emerContact;

    /** 紧急联系人手机号 */
    private String emerPhone;

    /** 小区id */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 排序 */
    private String seq;

    /** 密码 */
    private String password;

    /** 人物头像 */
    private String headPhoto;

    /** 身份证正面 */
    private String frontPhoto;

    /** 身份证反面 */
    private String backPhoto;

    // ========== 非数据库字段 ==========

    /** 房屋ID（查询用） */
    @TableField(exist = false)
    private String houseId;

    /** 房间号（查询用） */
    @TableField(exist = false)
    private String roomNum;

    /** 小区名称（查询用） */
    @TableField(exist = false)
    private String orgName;

    /** 当前余额（查询用） */
    @TableField(exist = false)
    private BigDecimal currentBalance;
}
