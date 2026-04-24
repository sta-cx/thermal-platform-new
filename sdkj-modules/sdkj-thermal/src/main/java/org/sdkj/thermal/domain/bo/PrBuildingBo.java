package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrBuilding;

import java.util.Date;

/**
 * 楼宇信息业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrBuilding.class, reverseConvertGenerate = false)
public class PrBuildingBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 楼宇编码 */
    @NotBlank(message = "楼宇编码不能为空")
    private String code;

    /** 楼宇名称 */
    @NotBlank(message = "楼宇名称不能为空")
    private String name;

    /** 地上楼层 */
    private Integer onFloor;

    /** 地下楼层 */
    private Integer upFloor;

    /** 总楼层 */
    private Integer floor;

    /** 总单元数 */
    private Integer unitNums;

    /** 排序 */
    private String seq;

    /** 用途 */
    private String used;

    /** 交付时间 */
    private Date deliveryTime;

    /** 热力站ID */
    private String stationId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

}
