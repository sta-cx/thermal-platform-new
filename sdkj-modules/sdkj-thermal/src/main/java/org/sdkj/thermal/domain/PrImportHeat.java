package org.sdkj.thermal.domain;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("pr_import_heat")
public class PrImportHeat {

    @TableId(value = "id")
    @ExcelIgnore
    private Long id;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "小区名称*"}, index = 0)
    @TableField(exist = false)
    private String orgName;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "楼宇名称*"}, index = 1)
    @TableField(exist = false)
    private String buildingName;

    @ExcelIgnore
    private Long houseId;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "房号*"}, index = 2)
    @TableField(exist = false)
    private String roomNum;

    @ExcelIgnore
    private Long archiveId;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "仪表名称*"}, index = 3)
    @TableField(exist = false)
    private String meterName;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "对应序号*"}, index = 4)
    private Integer meterSerial;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "表号*"}, index = 5)
    private String meterNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "卡号/IMSI"}, index = 6)
    private String cardNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "IMEI"}, index = 7)
    private String imei;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "设备ID"}, index = 8)
    private String deviceId;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "DTU编号"}, index = 9)
    private String dtuNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "DTU类型"}, index = 10)
    private Integer dtuType;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "集中器编号"}, index = 11)
    private String concentratorCode;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "通道号/组号"}, index = 12)
    private String chanNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "安装位置"}, index = 13)
    private String installSite;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "起始读数*"}, index = 14)
    private BigDecimal startReading;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "当前读数*"}, index = 15)
    private BigDecimal currentReading;

    @ExcelIgnore
    private BigDecimal totalUsed;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "交易次数"}, index = 16)
    private Integer tradeTimes;

    @ColumnWidth(17)
    @ExcelProperty(value = {title, "累积已用金额*"}, index = 17)
    private BigDecimal totalMoney;

    @ColumnWidth(17)
    @ExcelProperty(value = {title, "累积充值金额*"}, index = 18)
    private String totalRecharge;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "当前余额*"}, index = 19)
    private BigDecimal currentBalance;

    @ExcelIgnore
    private Long standardId;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "单价名称"}, index = 20)
    private String standardName;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "单价金额"}, index = 21)
    private BigDecimal standardPrice;

    @ColumnWidth(15)
    @ExcelProperty(value = {title, "安装类型"}, index = 22)
    private String installType;

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
    private static final String title = "1、带星号（*）为必填项。\n" +
        "2、房屋信息：导入前，请先导入基础数据，填写的房屋信息名称必须和基础数据模板填写的相同。\n" +
        "3、仪表名称必须与系统中设置的仪表一致。\n" +
        "4、集中器编号、通道号:(Mbus或Lora类仪表务必正确填写),如果没有，请默认填写0。\n" +
        "5、表号、卡号不得重复。请填写准确。卡号必须为10位，如果不够10位，前面补0即可。\n" +
        "6、安装位置：厨房、卫生间、水井、电井、楼道、地下室、其他。\n" +
        "7、安装类型：供水安装、回水安装。\n" +
        "8、单价需提前在系统中创建。\n" +
        "9、DTU类型：0:DTU-直连, 1:DTU-OneNet, 2:DTU-Comway, 输入时填写对应的数字。\n" +
        "10、请不要修改表头中的文字，也不要改变列的顺序。";
}
