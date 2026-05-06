package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrUnit;

import java.math.BigDecimal;

/**
 * 单元信息视图对象
 */
@Data
@AutoMapper(target = PrUnit.class)
public class PrUnitVo {

    private Long id;

    /** 单元编码 */
    private String code;

    /** 单元名称 */
    private String name;

    /** 楼宇ID */
    private Long buildingId;

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
    private Long stationId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 楼宇名称（查询用） */
    private String buildingName;

    /** 热力站名称（查询用） */
    private String stationName;

    /** 小区名称（查询用） */
    private String orgName;

}
