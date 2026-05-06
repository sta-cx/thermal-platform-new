package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * DTU控制范围表视图对象
 */
@Data
public class HtScopeDtuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long tasksId;

    private String orgId;

    private String companyId;

    private String meterArcCode;

    private String dtuNum;

    private String chanNums;

    private String concentratorCode;

    private Integer status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    private Integer tenantId;

}
