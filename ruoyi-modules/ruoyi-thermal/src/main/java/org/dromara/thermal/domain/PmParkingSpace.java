package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 车位信息（骨架 - Phase 5b 需要）
 * 完整迁移在后续物业模块阶段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pm_parking_space")
public class PmParkingSpace extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 车位编号 */
    private String parkingCode;

    /** 停车场名称 */
    private String parkinglotName;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 收费标准ID */
    private String standardId;

    /** 单价 */
    private BigDecimal standardPrice;
}
