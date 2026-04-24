package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrUnitVo;

import java.math.BigDecimal;

/**
 * 单元信息
 * 迁移自旧系统 PrUnit
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_unit")
@AutoMapper(target = PrUnitVo.class)
public class PrUnit extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 单元编码 */
    private String code;

    /** 单元名称 */
    private String name;

    /** 楼宇ID */
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

    // ========== 非数据库字段 ==========

    /** 楼宇名称（查询用） */
    @TableField(exist = false)
    private String buildingName;

    /** 热力站名称（查询用） */
    @TableField(exist = false)
    private String stationName;

    /** 小区名称（查询用） */
    @TableField(exist = false)
    private String orgName;

}
