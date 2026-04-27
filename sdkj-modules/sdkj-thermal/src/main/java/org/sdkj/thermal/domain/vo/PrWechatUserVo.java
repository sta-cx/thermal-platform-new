package org.sdkj.thermal.domain.vo;

import lombok.Data;

/**
 * 微信用户 VO
 */
@Data
public class PrWechatUserVo {
    private String id;
    private String openId;
    private String otherCode;
    private String houseId;
    private String userName;
    private String phone;
    private Integer bindStatus;
    private String unionId;
}
