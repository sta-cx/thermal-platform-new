package org.sdkj.thermal.domain.vo;

import lombok.Data;
import java.util.Date;

/**
 * 微信绑定记录 VO
 */
@Data
public class PrWechatBindRecordVo {
    private String id;
    private String houseId;
    private String heatPayCode;
    private String wxOpenId;
    private String companyId;
    private String createBy;
    private Date createTime;
}
