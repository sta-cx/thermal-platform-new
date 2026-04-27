package org.sdkj.thermal.domain;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Excel 导入模板 — 按房屋编码导入基础数据
 * 用于已存在房屋编码的房屋，通过编码匹配后更新信息
 */
@Data
public class PrImportBasicDataByCode {

    @ColumnWidth(20)
    @ExcelProperty(value = {TITLE, "房屋编码*"}, index = 0)
    private String code;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "业主姓名"}, index = 1)
    private String userName;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "手机号码"}, index = 2)
    private String phone;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "身份证号码"}, index = 3)
    private String idNo;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "使用面积"}, index = 4)
    private BigDecimal nfloorArea;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "建筑面积"}, index = 5)
    private BigDecimal gfloorArea;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "供暖面积"}, index = 6)
    private BigDecimal heatingArea;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "房屋性质"}, index = 7)
    private String nature;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "位置属性"}, index = 8)
    private String siteType;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "建费时间"}, index = 9)
    private Date establishTime;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "项目名称"}, index = 10)
    private String itemName;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "标准名称"}, index = 11)
    private String standardName;

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "单价(元)"}, index = 12)
    private BigDecimal standardPrice;

    @ColumnWidth(16)
    @ExcelProperty(value = {TITLE, "个人账户余额"}, index = 13)
    private BigDecimal account = new BigDecimal("0.00");

    @ColumnWidth(15)
    @ExcelProperty(value = {TITLE, "缴费状态"}, index = 14)
    private String payStatus;

    @ColumnWidth(18)
    @ExcelProperty(value = {TITLE, "第三方编码"}, index = 15)
    private String otherCode;

    @ExcelIgnore
    private String errorMessage;

    private static final String TITLE = "1、！！！！！请整理前阅读以下内容！！！！！\n"
        + "2、此模板用于按房屋编码导入/更新基础数据。\n"
        + "3、房屋编码为必填，系统根据编码匹配已有房屋。\n"
        + "4、带星号（*）为必填项。\n"
        + "5、业主姓名、手机号、身份证号码：如同时填写姓名和手机号，则同步更新用户档案。\n"
        + "6、建费日期:请按照xxxx-xx-xx格式填写，(如：2019-01-02)。\n"
        + "7、房屋性质需按照字典:楼宇用途(building_used)填写。\n"
        + "8、房屋位置属性只能填写：边户、顶户、底户、中间户、不利于环路户。\n"
        + "9、项目名称、标准名称：导入前，请在系统里提前创建，名称必须一致。\n"
        + "10、缴费状态只能填写：已缴费、未缴费、停供。\n"
        + "11、请不要修改表头中的文字，也不要改变列的顺序。";
}
