package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrBuilding;

import java.util.Date;

/**
 * 楼宇信息视图对象
 */
@Data
@AutoMapper(target = PrBuilding.class)
public class PrBuildingVo {

    private Long id;

    /** 楼宇编码 */
    private String code;

    /** 楼宇名称 */
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
    private Long stationId;

    /** 小区ID */
    private String orgId;

    /** 小区名称（查询用） */
    private String orgName;

}
