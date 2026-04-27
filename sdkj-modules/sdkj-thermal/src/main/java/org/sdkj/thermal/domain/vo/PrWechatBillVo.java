package org.sdkj.thermal.domain.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 微信账单 VO
 */
@Data
public class PrWechatBillVo {
    private String id;
    private String billDate;
    private String billType;
    private String billUrl;
    private Integer downloadStatus;
    private LocalDateTime downloadTime;
    private Integer checkStatus;
    private Integer totalCount;
    private Integer successCount;
    private Integer diffCount;
    private String companyId;
}
