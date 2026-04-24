package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrHouseVo;

/**
 * 房屋信息（骨架 - Phase 5b 需要）
 * 完整迁移将在后续阶段进行
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_house")
@AutoMapper(target = PrHouseVo.class)
public class PrHouse extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
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

    /** 费项分组（查询用） */
    @TableField(exist = false)
    private String itemGroup;

    /** 费项编码（查询用） */
    @TableField(exist = false)
    private String itemCode;

    /** 费项名称（查询用） */
    @TableField(exist = false)
    private String itemName;

    /** 用户名（查询用） */
    @TableField(exist = false)
    private String userName;

    /** 手机号（查询用） */
    @TableField(exist = false)
    private String phone;

    /** 用户ID（查询用） */
    @TableField(exist = false)
    private String userId;
}
