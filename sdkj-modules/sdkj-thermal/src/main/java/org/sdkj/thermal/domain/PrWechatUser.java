package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_wechat_user")
public class PrWechatUser extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String openId;
    private String otherCode;
    private Long houseId;
    private String userName;
    private String phone;
    private Integer bindStatus;
    private String sessionKey;
    private String unionId;
    private String delFlag;
}
