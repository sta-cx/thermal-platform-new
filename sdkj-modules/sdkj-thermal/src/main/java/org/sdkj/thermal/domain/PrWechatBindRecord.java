package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_wechat_bind_record")
public class PrWechatBindRecord extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private Long houseId;
    private String heatPayCode;
    private String wxOpenId;
}
