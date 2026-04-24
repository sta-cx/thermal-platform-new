package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHeatCommandValveArchive;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热力命令阀门档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHeatCommandValveArchive.class, reverseConvertGenerate = false)
public class PrHeatCommandValveArchiveBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 档案ID */
    private String archiveId;

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
    private String houseId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

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

    /** DTU编号 */
    private String dtuNum;

    /** DTU类型 */
    private String dtuType;

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
}
