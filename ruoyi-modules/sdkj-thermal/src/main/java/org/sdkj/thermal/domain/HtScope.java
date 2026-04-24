package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtScopeVo;

/**
 * 控制范围表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_scope")
@AutoMapper(target = HtScopeVo.class)
public class HtScope extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 任务主表ID */
    private String tasksId;

    /** 小区ID */
    private String orgId;

    /** 楼宇ID */
    private String buildingId;

    /** 单元ID */
    private String unitId;

    /** 公司ID */
    private String companyId;

    /** 房屋ID */
    private String houseId;

    /** 仪表号 */
    private String meterNum;

    /** 仪表ID */
    private String meterId;

    /** 档案编号 */
    private String meterArcCode;

    /** 集中器编号 */
    private String concentratorCode;

    /** 设备IMEI */
    private String imei;

    /** 设备ID */
    private String deviceId;

    /** 执行状态 */
    private Integer status;

    /** 是否特殊户 */
    private Integer isSpecial;

    /** DTU编号 */
    private String dtuNum;

    /** 通道号 */
    private String chanNum;
}
