package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 运行监控 - 变更历史 Vo
 */
@Data
public class ChangeHistoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long houseId;
    private String changeType;
    /** 变更值（对应 pr_house_log.change_val） */
    private Integer changeVal;
    private String remark;
    private String createByName;
    private Date createTime;
    /** 关联房屋信息（非数据库字段） */
    private String roomNum;
    private String orgName;
    private String buildingName;
    private String unitCode;
}
