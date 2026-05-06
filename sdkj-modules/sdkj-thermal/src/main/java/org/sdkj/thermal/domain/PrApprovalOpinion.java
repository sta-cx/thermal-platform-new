package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 审批意见
 * 迁移自旧系统 PrApprovalOpinion
 *
 * @author sdkj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_approval_opinion")
public class PrApprovalOpinion extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 审批单ID */
    private Long approvalId;

    /** 审批人 */
    private String approvalUser;

    /** 审批时间 */
    private Date approvalTime;

    /** 意见 */
    private String opinions;

    /** 审批状态 (0待审批 1通过 2驳回) */
    private Integer approvalStatus;

    /** 审批环节 */
    private Integer approvalLink;

    /** 公司ID */
    private String companyId;

    /** 小区ID */
    private String orgId;
}
