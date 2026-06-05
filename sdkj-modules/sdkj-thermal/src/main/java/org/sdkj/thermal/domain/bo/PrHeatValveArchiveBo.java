package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHeatValveArchive;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热力阀门档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHeatValveArchive.class, reverseConvertGenerate = false)
public class PrHeatValveArchiveBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 档案ID */
    private Long archiveId;

    /** 表号 */
    @NotBlank(message = "表号不能为空")
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 表计档案编码 */
    @NotBlank(message = "表计档案编码不能为空")
    private String meterArcCode;

    /** 表计档案名称 */
    private String meterArcName;

    /** 集中器编码 */
    private String concentratorCode;

    /** IMEI号 */
    private String imeiNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;

    /** 表序列号 */
    private String meterSerial;

    /** 房屋ID */
    private Long houseId;

    /** 小区ID */
    private String orgId;


    /** 阀门状态 */
    private String valveStatus;

    /** 设定状态 */
    private String settingStatus;

    /** 实际状态 */
    private String actualStatus;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 出水温度 */
    private BigDecimal outTemperature;

    /** 电压 */
    private BigDecimal voltage;

    /** 阀门时间 */
    private Date valveTime;

    /** 信号强度 */
    private String signalStrength;

    /** 上报间隔 */
    private Integer reportingInterval;

    /** 间隔单位 */
    private String intervalUnit;

    /** 有效时间 */
    private Integer validTime;

    /** 总度数 */
    private BigDecimal totalDegree;

    /** 剩余度数 */
    private BigDecimal residueDegree;

    /** 是否变更 */
    private Integer isChanged;

    /** 是否停用 */
    private Integer isStop;

    /** 通道号更新时间 */
    private Date chanNumUpdateTime;

    /** 通道号同步时间 */
    private Date chanNumSyncTime;

    /** 最后执行ID */
    private Long lastPerformId;

    /** DTU编号 */
    private String dtuNum;

    /** DTU编号状态 */
    private String dtuNumStatus;

    /** 通道号 */
    private String chanNum;

    /** 安装位置 */
    private String installSite;

    /** DTU状态 */
    private String dtuStatus;

    /** 交易次数 */
    private Integer tradeTimes;

    /** 是否开启 */
    private Integer isOpen;

    /** 口径 */
    private String caliber;

    /** 安装类型 */
    private String installType;

    /** 分组号25 */
    private String groupNum25;

    /** 用户设定温度 */
    private BigDecimal userSetTemp;

    /** 室温 */
    private BigDecimal roomTemp;

    /** 平均温度 */
    private BigDecimal avgTemp;

    /** 阀门型号 */
    private String valveModel;

    /** 冷标志 */
    private String coldFlg;

    /** 温控器锁定 */
    private String wkqLock;

    /** 温度下限 */
    private BigDecimal tempLow;

    /** 温度上限 */
    private BigDecimal tempHigh;

    /** 工作时间 */
    private String workTime;

    /** 总开启时间 */
    private BigDecimal totalOpenTime;

    /** DTU类型 */
    private String dtuType;

    /** 瞬时流量 */
    private BigDecimal insFlow;

    /** 备注 */
    private String remark;
}
