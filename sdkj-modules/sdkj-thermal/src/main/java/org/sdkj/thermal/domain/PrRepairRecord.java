package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_repair_record")
public class PrRepairRecord extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String userId;
    private String userName;
    private String phone;
    private Long houseId;
    private Date repairTime;
    private String createByName;
    private String repairName;
    private String repairPhone;
    private String repairRoomNum;
    private String inUserName;
    private String inPhone;
    private String repairAddress;
    private String serviceType;
    private String repairType;
    private String repairContent;
    private String urgentType;
    private Date appointTime;
    private String repairNo;
    private Integer repairStatus;
    private Integer isReject;
    private String rejectReason;
    private Date confirmTime;
    private Date evaluationTime;
    private Date completionTime;
    private String dispatchId;
    private BigDecimal dispatchMoney;
    private Date dispatchTime;
    private String serviceAttitude;
    private String serviceQuality;
    private String serviceEfficiency;
    private String getMaterial;
    private String serviceObject;
    private String serviceResult;
    private String whyFailure;
    private String alertStatus;
    private String orgId;

    /** 前端勾选的告警 ID 列表，从告警发起报修时使用；不持久化 */
    @TableField(exist = false)
    private List<Long> alertIds;
}
