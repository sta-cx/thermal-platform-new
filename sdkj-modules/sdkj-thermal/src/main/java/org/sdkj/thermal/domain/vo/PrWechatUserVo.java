package org.sdkj.thermal.domain.vo;

import org.sdkj.common.sensitive.annotation.Sensitive;
import org.sdkj.common.sensitive.core.SensitiveStrategy;
import lombok.Data;

/**
 * 微信用户 VO
 */
@Data
public class PrWechatUserVo {
    private Long id;
    private String openId;
    private String otherCode;
    private Long houseId;
    private String userName;
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;
    private Integer bindStatus;
    private String unionId;
}
