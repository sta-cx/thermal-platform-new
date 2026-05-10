package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_wechat_order")
public class PrWechatOrder extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String outTradeNo;
    private String transactionId;
    private String openId;
    private String otherCode;
    private Long houseId;
    private String houseAddress;
    private BigDecimal totalFee;
    private String body;
    private Integer orderStatus;
    private String spBillCreateIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    private String notifyUrl;
    private String returnUrl;
    private String tradeType;
    private String bankType;
    private String attach;
    private String operator;
    private String remark;
    private String delFlag;

    @TableField(exist = false)
    private String orgId;

    @TableField(exist = false)
    private String orgName;

    @TableField(exist = false)
    private Long buildingId;

    @TableField(exist = false)
    private String buildingName;

    @TableField(exist = false)
    private String unitCode;

    @TableField(exist = false)
    private String roomNum;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String phone;
}
