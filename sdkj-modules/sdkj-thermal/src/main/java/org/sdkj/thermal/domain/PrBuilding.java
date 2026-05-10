package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrBuildingVo;

import java.util.Date;

/**
 * 楼宇信息
 * 迁移自旧系统 PrBuilding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_building")
@AutoMapper(target = PrBuildingVo.class)
public class PrBuilding extends BaseEntity {

    @TableId(value = "id")
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

    // ========== 非数据库字段 ==========

    /** 小区名称（查询用） */
    @TableField(exist = false)
    private String orgName;

}
