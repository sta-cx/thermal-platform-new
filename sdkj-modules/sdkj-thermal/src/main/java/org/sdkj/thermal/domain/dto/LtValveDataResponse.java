package org.sdkj.thermal.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 新奥阀门数据查询响应项
 * 对应旧系统 LTDataDO
 */
@Data
public class LtValveDataResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 仪表编号 */
    private String meterNum;

    /** 阀门状态 */
    private String valveStatus;

    /** 设置状态 */
    private Integer settingStatus;

    /** 实际状态 */
    private Integer actualStatus;

    /** 进水温度 */
    private BigDecimal inTemp;

    /** 出水温度 */
    private BigDecimal outTemp;

    /** 电压 */
    private BigDecimal voltage;

    /** 阀门时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date valveTime;

    /** 上报间隔 */
    private Integer reportInterval;

    /** 上报间隔单位 */
    private Integer reportingUnit;

    /** 有效时间 */
    private Integer validTime;

    /** 信号强度 */
    private Integer csq;

    /** 小区名称 */
    private String orgName;

    /** 楼宇名称 */
    private String buildingName;

    /** 单元编码 */
    private String unitCode;

    /** 房间号 */
    private String roomNum;
}
