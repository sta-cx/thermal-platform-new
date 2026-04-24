package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHeatTempArchive;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 热力温度计档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHeatTempArchive.class, reverseConvertGenerate = false)
public class PrHeatTempArchiveBo extends BaseEntity {

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

    /** 阀门状态 */
    private String valveStatus;

    /** 温度 */
    private BigDecimal temper;

    /** 湿度 */
    private BigDecimal humidity;

    /** 电压 */
    private BigDecimal voltage;

    /** 信号强度 */
    private String signalStrength;

    /** 采集时间 */
    private Date collectTime;

    /** 上报间隔 */
    private Integer reportingInterval;

    /** 间隔单位 */
    private String intervalUnit;

    /** 有效时间 */
    private Integer validTime;

    /** 采集间隔 */
    private Integer collectInterval;

    /** 采集单位 */
    private String collectUnit;

    /** 采集次数 */
    private Integer collectNum;

    /** 移动位置 */
    private String movPlace;

    /** 上报次数 */
    private Integer reportNumber;

    /** 上报成功次数 */
    private Integer reportSuccNum;

    /** 上报时间 */
    private Date reportTime;

    /** 是否变更 */
    private String isChanged;

    /** 是否停用 */
    private String isStop;

    /** 房屋ID */
    private String houseId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 集中器编码 */
    private String concentratorCode;

    /** IMEI号 */
    private String imeiNum;

    /** 产品ID */
    private String productId;

    /** 设备ID */
    private String deviceId;
}
