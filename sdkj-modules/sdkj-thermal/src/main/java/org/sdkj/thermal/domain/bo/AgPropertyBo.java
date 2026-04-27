package org.sdkj.thermal.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 代理商关联物业 BO
 */
@Data
public class AgPropertyBo implements Serializable {

    private String id;

    /** 代理商公司ID */
    private String agentCompanyId;

    /** 物业公司ID */
    private String propertyCompanyId;

    /** 是否审核 0未审核 1已审核 */
    private Integer isAudited;

    /** 是否启用 0未启用 1启用 */
    private Integer isEnabled;
}
