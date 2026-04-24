package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHouse;

/**
 * 房屋信息业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHouse.class, reverseConvertGenerate = false)
public class PrHouseBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 房间号 */
    private String roomNum;

    /** 楼宇ID */
    private String buildingId;

    /** 单元编码 */
    private String unitCode;

    /** 楼层 */
    private Integer floor;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 是否删除 */
    private Integer isDeleted;
}
