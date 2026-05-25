package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrUserHouse;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrUserHouse.class, reverseConvertGenerate = false)
public class PrUserHouseBo extends BaseEntity {

    private Long id;
    private String userId;
    private String userName;
    private String phone;
    private Long houseId;
    private String relationType;
    private String remark;
    private String orgId;
    private String recordSource;
}
