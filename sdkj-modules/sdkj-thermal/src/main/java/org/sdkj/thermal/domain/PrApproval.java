package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 审批单
 * 迁移自旧系统 PrApproval
 *
 * @author sdkj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_approval")
public class PrApproval extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 流程编号 */
    private String no;

    /** 申请类型 */
    private String type;

    /** 申请人 */
    private String approvalUser;

    /** 申请时间 */
    private Date approvalTime;

    /** 任务名称 */
    private String title;

    /** 减免类型 */
    private Integer preferentialType;

    /** 减免金额 */
    private BigDecimal preferential;

    /** 减免原因 */
    private String preferentialReason;

    /** 审批环节 */
    private Integer approvalLink;

    /** 审批类型 */
    private Integer approvalType;

    /** 公司ID */
    private String companyId;

    /** 小区ID */
    private String orgId;

    /** 审批人 (当前环节) */
    private String approvalUsers;

    /** 所有审批人 */
    private String approvalUsersAll;
}
