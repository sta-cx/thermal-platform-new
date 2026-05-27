package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.common.sensitive.annotation.Sensitive;
import org.sdkj.common.sensitive.core.SensitiveStrategy;
import org.sdkj.thermal.domain.PrUserHouse;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AutoMapper(target = PrUserHouse.class)
public class PrUserHouseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String userName;
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;
    private Long houseId;
    private String relationType;
    private String remark;
    private String orgId;
    private String recordSource;
    private String delFlag;
    private Date createTime;
    private Date updateTime;
}
