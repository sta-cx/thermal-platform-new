package org.sdkj.thermal.domain;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("pr_import_history")
public class PrImportHistory {

    @TableId(value = "id")
    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = {title, "小区名称*"}, index = 0)
    private String orgName;

    @ExcelIgnore
    private String orgId;

    @ExcelProperty(value = {title, "楼宇名称*"}, index = 1)
    private String buildingName;

    @ExcelProperty(value = {title, "房号*"}, index = 2)
    private String roomNum;

    @ExcelIgnore
    private Long houseId;

    @ExcelProperty(value = {title, "项目名称*"}, index = 3)
    private String itemName;

    @ExcelIgnore
    private String itemGroup;

    @ExcelIgnore
    private String itemCode;

    @ExcelIgnore
    private Long standardId;

    @ExcelProperty(value = {title, "标准名称*"}, index = 4)
    private String standardName;

    @ExcelProperty(value = {title, "单价"}, index = 5)
    private BigDecimal standardPrice;

    @ExcelProperty(value = {title, "购量"}, index = 6)
    private BigDecimal qty;

    @ExcelProperty(value = {title, "购金额"}, index = 7)
    private BigDecimal receivable;

    @ExcelProperty(value = {title, "减免金额"}, index = 8)
    private BigDecimal deduction;

    @ExcelProperty(value = {title, "实收金额"}, index = 9)
    private BigDecimal paidIn;

    @ExcelProperty(value = {title, "余额支付"}, index = 10)
    private BigDecimal paymentBalance;

    @ExcelProperty(value = {title, "状态"}, index = 11)
    private String status;

    @ExcelProperty(value = {title, "收费员"}, index = 12)
    private String userName;

    @ExcelIgnore
    private String createBy;

    @ExcelIgnore
    private Date createTime;

    @ExcelIgnore
    private Integer type;

    @ExcelIgnore
    private String userId;

    @ExcelIgnore
    private Date payTime;

    @TableField(exist = false)
    private static final String title = "1、带星号（*）为必填项。\n"
        + "2、房屋信息：填写的房屋信息名称必须和基础数据模板填写的相同。\n"
        + "3、项目名称、标准名称需预先创建在系统中。\n"
        + "4、金额务必不要填写数字的单位信息。如：元、/元。\n"
        + "5、请不要修改表头中的文字，也不要改变列的顺序。";
}
