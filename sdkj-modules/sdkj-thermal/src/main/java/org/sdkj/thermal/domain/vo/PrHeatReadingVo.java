package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrHeatReading;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 热表抄表记录 View Object
 */
@Data
@AutoMapper(target = PrHeatReading.class)
public class PrHeatReadingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 厂家反馈的产品代码 */
    private String manuId;

    /** 热表档案编号 */
    private String meterArcCode;

    /** 表号 */
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 平台给终端分配的设备ID */
    private String deviceId;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 回水温度 */
    private BigDecimal outTemperature;

    /** 供回水温差 */
    private BigDecimal diffTemperature;

    /** 累积热量 */
    private BigDecimal totalHeat;

    /** 累积流量 */
    private BigDecimal totalFlow;

    /** 瞬时流量 */
    private BigDecimal flowRate;

    /** 累积工作时间 */
    private BigDecimal totalWorktime;

    /** 水压 */
    private BigDecimal waterPress;

    /** 反向流量 */
    private BigDecimal reverseFlow;

    /** 阀门设定状态 */
    private String settingStatus;

    /** 阀门当前状态 */
    private String valveStatus;

    /** 电源状态 */
    private String powerState;

    /** 异常状态 */
    private String attackStatus;

    /** 抄表时间 */
    private Date readTime;

    /** 状态字 */
    private String st;

    /** 是否使用 */
    private Integer isUsed;

    /** 是否有效(是否系统内表号) */
    private Integer isValid;

    /** 小区ID */
    private String orgId;


    /** 信号 */
    private String csq;

    /** 电压 */
    private String voltage;

    /** 湿度 */
    private String humi;

    /** 温度 */
    private BigDecimal temperature;

    /** 热功率 */
    private BigDecimal heatPower;

    /** 热表状态1 */
    private String status1;

    /** 热表状态2 */
    private String status2;

    /** 子表序号 */
    private Integer meterSerial;

    /** 用户设定温度 */
    private BigDecimal userSetTemp;

    /** 室内温度 */
    private BigDecimal roomTemp;

    /** 平均温度 */
    private BigDecimal avgTemp;

    /** 阀门型号 */
    private String valveModel;

    /** 冷水标志 */
    private Integer coldFlg;

    /** 温控器锁定 */
    private Integer wkqLock;

    /** 温度下限 */
    private Integer tempLow;

    /** 温度上限 */
    private Integer tempHigh;

    /** 工作时间 */
    private Integer workTime;

    /** 总开启时间 */
    private Integer totalOpenTime;
}
