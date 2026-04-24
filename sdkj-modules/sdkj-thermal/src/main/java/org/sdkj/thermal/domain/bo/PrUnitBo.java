package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrUnit;

import java.math.BigDecimal;

/**
 * 单元信息业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrUnit.class, reverseConvertGenerate = false)
public class PrUnitBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 单元编码 */
    @NotBlank(message = "单元编码不能为空")
    private String code;

    /** 单元名称 */
    @NotBlank(message = "单元名称不能为空")
    private String name;

    /** 楼宇ID */
    @NotBlank(message = "楼宇ID不能为空")
    private String buildingId;

    /** 地上楼层 */
    private Integer onFloor;

    /** 供热面积 */
    private BigDecimal heatingArea;

    /** 地下楼层 */
    private Integer upFloor;

    /** 总楼层 */
    private Integer floor;

    /** 位置 */
    private String site;

    /** 排序 */
    private String seq;

    /** 热力站ID */
    private String stationId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

}
