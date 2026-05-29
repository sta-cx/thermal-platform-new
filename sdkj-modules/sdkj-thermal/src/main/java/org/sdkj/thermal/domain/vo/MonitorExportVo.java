package org.sdkj.thermal.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运行监控户间数据导出 View Object
 */
@Data
@ExcelIgnoreUnannotated
public class MonitorExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "小区", index = 0)
    private String orgName;

    @ExcelProperty(value = "楼宇", index = 1)
    private String buildingName;

    @ExcelProperty(value = "单元", index = 2)
    private String unitCode;

    @ExcelProperty(value = "房号", index = 3)
    private String roomNum;

    @ExcelProperty(value = "缴费状态", index = 4)
    private String paymentLabel;

    @ExcelProperty(value = "位置属性", index = 5)
    private String siteType;

    @ExcelProperty(value = "供暖面积㎡", index = 6)
    private BigDecimal heatingArea;

    @ExcelProperty(value = "室温℃", index = 7)
    private BigDecimal roomTemp;

    @ExcelProperty(value = "回水温度℃", index = 8)
    private BigDecimal outTemperature;

    @ExcelProperty(value = "瞬时流量L/h", index = 9)
    private BigDecimal curFlow;

    @ExcelProperty(value = "通讯状态", index = 10)
    private String scopeStatus;
}
