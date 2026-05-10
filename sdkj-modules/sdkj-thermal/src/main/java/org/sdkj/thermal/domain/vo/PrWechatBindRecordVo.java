package org.sdkj.thermal.domain.vo;

import lombok.Data;
import java.util.Date;

/**
 * 微信绑定记录 VO
 */
@Data
public class PrWechatBindRecordVo {
    private Long id;
    private Long houseId;
    private String heatPayCode;
    private String wxOpenId;
    private String createBy;
    private Date createTime;
}
