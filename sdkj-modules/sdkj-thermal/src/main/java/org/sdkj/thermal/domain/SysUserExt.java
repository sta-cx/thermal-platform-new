package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户扩展表 — 供热业务用户字段扩展
 * 不修改 sdkj-system 的 SysUser，通过 userId 关联
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_ext")
public class SysUserExt extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private Long userId;
    private String wxOpenid;
    private String wxNumber;
    private String qqNumber;
    private String nation;
    private String nativePlace;
    private String nationality;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    private Integer isRealname;
    private String idcard;
    private String realName;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date idStartdate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date idEnddate;

    private String idDepartment;
    private Integer isSuper;
}
