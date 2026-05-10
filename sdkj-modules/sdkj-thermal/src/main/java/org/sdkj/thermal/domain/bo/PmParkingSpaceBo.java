package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PmParkingSpace;

import java.math.BigDecimal;

/**
 * 车位信息业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PmParkingSpace.class, reverseConvertGenerate = false)
public class PmParkingSpaceBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 车位编号 */
    private String parkingCode;

    /** 停车场名称 */
    private String parkinglotName;

    /** 小区ID */
    private String orgId;


    /** 收费标准ID */
    private Long standardId;

    /** 单价 */
    private BigDecimal standardPrice;
}
