package org.sdkj.thermal.domain.vo;

import lombok.Data;
import org.sdkj.common.sensitive.annotation.Sensitive;
import org.sdkj.common.sensitive.core.SensitiveStrategy;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 热表抄表数据副本 View Object
 * 手工远传抄表数据（携带物业公司及小区信息）
 */
@Data
public class PrHeatReadingCopy1Vo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========== pr_heat_reading_copy1 表字段 ==========

    /** 主键 */
    private Long id;

    /** 房号 */
    private String roomNum;

    /** 阀门号 */
    private String meterNum;

    /** 调控类型 */
    private String type;

    /** 阀门设定开度 */
    private String settingStatus;

    /** 阀门当前实际开度 */
    private String valveStatus;

    /** 抄表时间 */
    private Date readTime;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 回水温度 */
    private BigDecimal outTemperature;

    /** 当前室温 */
    private String nowTemperature;

    /** 创建人 */
    private String createBy;

    // ========== 聚合计算字段（pageList 查询） ==========

    /** 回水温度变化序列 */
    private String nowTemperatureString;

    /** 阀门开度变化序列 */
    private String nowValveStatusString;

    /** 平均进水温度 */
    private String avgInTemperature;

    /** 平均回水温度 */
    private String avgOutTemperature;

    /** 平均室温 */
    private String avgNowTemperature;

    /** 平均阀门开度 */
    private String avgValveStatus;

    // ========== 配表读数字段（pageHeatReadingList 查询） ==========

    /** 小区名称 */
    private String orgName;

    /** 楼栋名称 */
    private String buildingName;

    /** 单元编码 */
    private String unitCode;

    /** 楼层 */
    private String floor;

    /** 房屋ID */
    private Long houseId;

    /** 子表序号 */
    private Integer meterSerial;

    /** 热表档案编号 */
    private String meterArcCode;

    /** 异常状态 */
    private String attackStatus;

    /** 创建时间 */
    private Date createTime;

    /** 手机号 */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;

    /** 用户名 */
    private String userName;

    /** 累积工作时间 */
    private BigDecimal totalWorktime;

    /** 信号强度 */
    private String csq;

    /** 电压 */
    private String voltage;

    /** 湿度 */
    private String humi;

    /** 温度 */
    private BigDecimal temperature;

    /** 累积热量 */
    private BigDecimal totalHeat;

    /** 累积流量 */
    private BigDecimal totalFlow;

    /** 热表状态1 */
    private String rbStatus1;

    /** 热表状态2 */
    private String rbStatus2;
}
