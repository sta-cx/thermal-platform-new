package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHeatArchive;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热力档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHeatArchive.class, reverseConvertGenerate = false)
public class PrHeatArchiveBo extends BaseEntity {

    /** 主键 */
    private Long id;


    /** 小区ID */
    private String orgId;

    /** 小区名称 */
    private String orgName;

    /** 楼宇名称 */
    private String buildingName;

    /** 房屋ID */
    @NotNull(message = "房屋ID不能为空")
    private Long houseId;

    /** 房间号 */
    private String roomNum;

    /** 档案ID */
    private Long archiveId;

    /** 表计档案编码 */
    @NotBlank(message = "表计档案编码不能为空")
    private String meterArcCode;

    /** 表计档案名称 */
    private String meterArcName;

    /** 表号 */
    @NotBlank(message = "表号不能为空")
    private String meterNum;

    /** IMEI */
    private String imei;

    /** 卡号 */
    private String cardNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;

    /** 表序列号 */
    private String meterSerial;

    /** 线号 */
    private String lineNumber;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 集中器编码 */
    private String concentratorCode;

    /** 安装位置 */
    private String installSite;

    /** 收费标准ID */
    private Long standardId;

    /** 收费标准价格 */
    private BigDecimal standardPrice;

    /** 进水温度 */
    private BigDecimal inTemperature;

    /** 出水温度 */
    private BigDecimal outTemperature;

    /** 温差 */
    private BigDecimal diffTemperature;

    /** 设定温度 */
    private BigDecimal settingTemperature;

    /** 设定状态 */
    private String settingStatus;

    /** 阀门状态 */
    private String valveStatus;

    /** 是否已开阀 */
    private Integer isOpened;

    /** 历史金额 */
    private BigDecimal hisMoney;

    /** 开阀时间 */
    private Date openedTime;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 是否计费 */
    private Integer isExpense;

    /** 是否通知 */
    private Integer isNotify;

    /** 是否变更 */
    private Integer isChanged;

    /** 是否停用 */
    private Integer isStop;

    /** 起始读数 */
    private Integer startReading;

    /** 总热量 */
    private BigDecimal totalHeat;

    /** 总流量 */
    private BigDecimal totalFlow;

    /** 总工作时间 */
    private BigDecimal totalWorktime;

    /** 当前读数 */
    private BigDecimal currentReading;

    /** 总用量 */
    private BigDecimal totalUsed;

    /** 交易次数 */
    private Integer tradeTimes;

    /** 总金额 */
    private BigDecimal totalMoney;

    /** 总充值 */
    private BigDecimal totalRecharge;

    /** 当前余额 */
    private BigDecimal currentBalance;

    /** 已购度数 */
    private BigDecimal payDegrees;

    /** 囤积上限 */
    private BigDecimal hoardLimit;

    /** 报警值 */
    private BigDecimal alarmValue;

    /** 关闭值 */
    private BigDecimal closeValue;

    /** 是否阶梯 */
    private Integer isSteps;

    /** 计量方式 */
    private String measurement;

    /** 类型 */
    private String type;

    /** 命令 */
    private String command;

    /** 阀门开度 */
    private BigDecimal valveOpening;

    /** 命令时间 */
    private Date commandTime;

    /** 命令状态 */
    private String commandStatus;

    /** 返回时间 */
    private Date returnTime;

    /** 是否打印 */
    private String isPrint;

    /** 打印类型 */
    private String printType;

    /** 备注 */
    private String remark;
}
