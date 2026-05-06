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
@TableName("pr_import_basic_data")
public class PrImportBasicData {

    @TableId(value = "id")
    @ExcelIgnore
    private Long id;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "小区名称*"}, index = 0)
    private String orgName;

    @ExcelIgnore
    private String orgId;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "楼宇名称*"}, index = 1)
    private String buildingName;

    @ExcelIgnore
    private Long buildingId;

    @ExcelIgnore
    private String buildingCode;

    @ExcelIgnore
    private Long stationId;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "房号*"}, index = 2)
    private String roomNum;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "房屋编码"}, index = 3)
    private String roomCode;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "换热站名称*"}, index = 4)
    private String stationName;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "换热分站名称*"}, index = 5)
    private String substationName;

    @ExcelIgnore
    private String substationId;

    @ExcelIgnore
    private Long houseId;

    @ExcelIgnore
    private Long unitId;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "单元号*"}, index = 6)
    private String unitCode;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "单元位置*"}, index = 7)
    private String unitSite;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "楼层"}, index = 8)
    private Integer floor;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "使用面积*"}, index = 9)
    private BigDecimal nfloorArea;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "建筑面积*"}, index = 10)
    private BigDecimal gfloorArea;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "供暖面积*"}, index = 11)
    private BigDecimal heatingArea;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "建费时间*"}, index = 12)
    private Date establishTime;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "房屋性质*"}, index = 13)
    @TableField("nature")
    private String nature;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "位置属性*"}, index = 14)
    private String siteType;

    @ExcelIgnore
    private String stationType;

    @ExcelIgnore
    private String userId;

    @ExcelIgnore
    private String existUserId;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "业主姓名"}, index = 15)
    private String userName;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "手机号码"}, index = 16)
    private String phone;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "身份证号码"}, index = 17)
    private String idNo;

    @ColumnWidth(16)
    @ExcelProperty(value = {TITLE, "个人账户余额"}, index = 18)
    private BigDecimal account = new BigDecimal("0.00");

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "项目名称"}, index = 19)
    private String itemName;

    @ExcelIgnore
    private String itemGroup;

    @ExcelIgnore
    private String itemCode;

    @ExcelIgnore
    private Long standardId;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "标准名称"}, index = 20)
    private String standardName;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "单价(元)"}, index = 21)
    private BigDecimal standardPrice;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "缴费状态"}, index = 22)
    private String payStatus;

    @ColumnWidth(18)
    @ExcelProperty(value = {TITLE, "第三方编码"}, index = 23)
    private String otherCode;

    @ExcelIgnore
    private String companyId;

    @ExcelIgnore
    private String createBy;

    @ExcelIgnore
    private Date createTime;

    @ExcelIgnore
    private Integer type;

    @ExcelIgnore
    private String password;

    @TableField(exist = false)
    private static final String TITLE = "1、！！！！！请整理前阅读以下内容！！！！！\n"
        + "2、导入基础数据前，先在系统里手动建立小区及费项。\n"
        + "3、带星号（*）为必填、姓名、手机号、建费时间务必同时填写，如果只有姓名，请不要填写。如没有手机号则建立不了人员档案。\n"
        + "4、楼宇信息无需在系统里提前创建。房号格式严格按照：\"楼号-单元-房号\"如：A1-6-1002或1-3-1002，房号重复会造成无法导入！！！\n"
        + "5、换热站、换热分站请提前在系统内创建。\n"
        + "6、一个业主有多套房，业主信息重复填写即可。\n"
        + "7、建费日期:请按照xxxx-xx-xx格式填写，(如：2019-01-02)。\n"
        + "8、单元位置只能填写：中单元、西北边单元、东北边单元。\n"
        + "9、房屋性质需按照字典:楼宇用途(building_used)填写，新性质需提前维护。\n"
        + "10、房屋位置属性只能填写：边户、顶户、底户、中间户、不利于环路户。\n"
        + "11、项目名称、标准名称：切记！切记！导入前，请在系统里提前创建项目名称与标准名称，此表填写的名称必须与系统中设置的名称一致。\n"
        + "12、金额和单价：务必不要填写数字的单位信息。如：元、/元\n"
        + "13、缴费状态只能填写：已缴费、未缴费、停供。\n"
        + "14、请不要修改表头中的文字，也不要改变列的顺序。切记！切记！";
}
