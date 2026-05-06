package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热力实时数据 (IoT 阀门/热表)
 *
 * @author sdkj
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_heat_real_data")
public class PrHeatRealData extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 房屋ID */
    private Long houseId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 楼栋ID */
    private Long buildingId;

    /** 楼栋名称 */
    private String buildingName;

    /** 小区名称 */
    private String orgName;

    /** 房间号 */
    private String roomNum;

    /** 单元编号 */
    private String unitCode;

    /** 楼层 */
    private String floor;

    /** 换热站名称 */
    private String stationName;

    /** 进水温度(阀门) */
    private BigDecimal inTemperature;

    /** 回水温度(阀门) */
    private BigDecimal outTemperature;

    /** 设定开度 */
    private Integer settingStatus;

    /** 实际开度 */
    private Integer valveStatus;

    /** 阀门更新时间 */
    private Date rbCreateTime;

    /** 累计热量 */
    private BigDecimal totalHeat;

    /** 累计流量 */
    private BigDecimal totalFlow;

    /** 累计时长 */
    private BigDecimal totalWorktime;

    /** 阀门设备状态 */
    private String attackStatus;

    /** 阀门编号 */
    private String meterNum;

    /** 热表状态1 */
    private String rbStatus1;

    /** 热表状态2 */
    private String rbStatus2;

    /** 热表设备状态 */
    private String rbAttackStatus;

    /** 热表编号 */
    private String rbMeterNum;

    /** 热表电量 */
    private String rbVoltage;

    /** 阀门电量 */
    private String voltage;

    /** 进水温度(热表) */
    private String rbin;

    /** 回水温度(热表) */
    private BigDecimal rbout;
}
