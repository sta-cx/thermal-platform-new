package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.AgUserVo;

import java.util.Date;

/**
 * 代理商用户表
 * 对应 sys_user 表，用于代理商员工管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ag_user")
@AutoMapper(target = AgUserVo.class)
public class AgUser extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userName;

    private String userPwd;

    private String idcard;

    private String nickName;

    private String realName;

    private Integer sex;

    private String phone;

    private Integer isEnabled;

    private String address;

    private String avatar;

    private String wxOpenid;

    private String wxNumber;

    private String qqNumber;

    private String email;

    private String nation;

    private String companyId;

    private String nativePlace;

    private String nationality;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    private Integer isRealname;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date idStartdate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date idEnddate;

    private String idDepartment;

    private Integer isSuper;

    private String deptId;

    private String isDeleted;
}
