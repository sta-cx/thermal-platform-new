package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热力单元热量表档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHeatUnitHotArchive.class, reverseConvertGenerate = false)
public class PrHeatUnitHotArchiveBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 档案ID */
    private String archiveId;

    /** 表计档案编码 */
    @NotBlank(message = "表计档案编码不能为空")
    private String meterArcCode;

    /** 表计档案名称 */
    private String meterArcName;

    /** 表号 */
    @NotBlank(message = "表号不能为空")
    private String meterNum;

    /** 卡号 */
    private String cardNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;

    /** 表序列号 */
    private String meterSerial;

    /** 集中器编码 */
    private String concentratorCode;

    /** 囤积上限 */
    private BigDecimal hoardLimit;

    /** 报警值 */
    private BigDecimal alarmValue;

    /** 关闭值 */
    private BigDecimal closeValue;

    /** 计量方式 */
    private String measurement;

    /** 安装位置 */
    private String installSite;

    /** 收费标准ID */
    private String standardId;

    /** 收费标准价格 */
    private BigDecimal standardPrice;

    /** 是否阶梯 */
    private String isSteps;

    /** 起始读数 */
    private BigDecimal startReading;

    /** 当前读数 */
    private BigDecimal currentReading;

    /** 总用量 */
    private BigDecimal totalUsed;

    /** 交易次数 */
    private Integer tradeTimes;

    /** 历史金额 */
    private BigDecimal hisMoney;

    /** 总金额 */
    private BigDecimal totalMoney;

    /** 总充值 */
    private BigDecimal totalRecharge;

    /** 当前余额 */
    private BigDecimal currentBalance;

    /** 已购度数 */
    private BigDecimal payDegrees;

    /** 阀门状态 */
    private String valveStatus;

    /** 总流量 */
    private BigDecimal totalFlow;

    /** 当前流量 */
    private BigDecimal curFlow;

    /** 总工作时间 */
    private BigDecimal totalWorktime;

    /** 阀门时间 */
    private Date valveTime;

    /** 状态1 */
    private String status1;

    /** 状态2 */
    private String status2;

    /** 热功率 */
    private BigDecimal thermalPower;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 出水温度 */
    private BigDecimal outTemperature;

    /** 电压 */
    private BigDecimal voltage;

    /** 信号强度 */
    private String signalStrength;

    /** 电池状态 */
    private String cellStatus;

    /** 是否已开阀 */
    private String isOpened;

    /** 开阀时间 */
    private Date openedTime;

    /** 是否计费 */
    private String isExpense;

    /** 是否通知 */
    private String isNotify;

    /** 是否变更 */
    private String isChanged;

    /** 是否停用 */
    private String isStop;

    /** 单元ID */
    private String unitId;

    /** 公司ID */
    private String companyId;

    /** 小区ID */
    private String orgId;

    /** IMEI号 */
    private String imeiNum;

    /** DTU编号 */
    private String dtuNum;

    /** DTU类型 */
    private String dtuType;

    /** DTU编号状态 */
    private String dtuNumStatus;

    /** 通道号 */
    private String chanNum;

    /** DTU状态 */
    private String dtuStatus;
}
