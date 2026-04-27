package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_wechat_bill")
public class PrWechatBill extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String billDate;
    private String billType;
    private String billUrl;
    private String fileMd5;
    private Long fileSize;
    private Integer downloadStatus;
    private Date downloadTime;
    private Integer checkStatus;
    private Date checkTime;
    private Integer totalCount;
    private Integer successCount;
    private Integer diffCount;
    private String companyId;
    private String operator;
    private String remark;
    private Integer isDeleted;
}
