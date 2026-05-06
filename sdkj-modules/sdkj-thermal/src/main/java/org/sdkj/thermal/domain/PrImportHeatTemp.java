package org.sdkj.thermal.domain;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("pr_import_heat_temp")
public class PrImportHeatTemp {

    @TableId(value = "id")
    @ExcelIgnore
    private Long id;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "小区名称*"}, index = 0)
    private String orgName;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "楼宇名称*"}, index = 1)
    private String buildingName;

    @ExcelIgnore
    private Long houseId;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "房号*"}, index = 2)
    private String roomNum;

    @ExcelIgnore
    private Long archiveId;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "仪表名称*"}, index = 3)
    private String meterName;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "仪表规格*"}, index = 4)
    private String specification;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "仪表型号*"}, index = 5)
    private String model;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "对应序号*"}, index = 6)
    private Integer meterSerial;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "表号*"}, index = 7)
    private String meterNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "卡号/IMSI"}, index = 8)
    private String cardNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "IMEI"}, index = 9)
    private String imei;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "DTU编号"}, index = 10)
    private String dtuNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "DTU类型"}, index = 11)
    private Integer dtuType;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "集中器编号"}, index = 12)
    private String concentratorCode;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "通道号/组号"}, index = 13)
    private String chanNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "设备ID"}, index = 14)
    private String deviceId;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "口径"}, index = 15)
    private String caliber;

    @ExcelIgnore
    private String orgId;

    @ExcelIgnore
    private String companyId;

    @ExcelIgnore
    private String createBy;

    @ExcelIgnore
    private Date createTime;

    @ExcelIgnore
    private Integer type;

    @ExcelIgnore
    private String meterArcCode;

    @TableField(exist = false)
    private static final String title = "1、带星号（*）为必填项。\n"
        + "2、房屋信息：导入前，请先导入基础数据，填写的房屋信息名称必须和基础数据模板填写的相同。\n"
        + "3、仪表名称必须与系统中设置的仪表一致。\n"
        + "4、集中器编号、通道号:(Mbus或Lora仪表务必正确填写),如果没有，请默认填写0。\n"
        + "5、表号、卡号不得重复。请填写准确。卡号必须为10位，如果不够10位，前面补0即可。\n"
        + "6、NB系列设备请务必填写IMEI与设备ID。\n"
        + "7、DTU类型：0:DTU-直连, 1:DTU-OneNet, 2:DTU-Comway。\n"
        + "8、请不要修改表头中的文字，也不要改变列的顺序。";
}
