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
@TableName("pr_import_record")
public class PrImportRecord {

    @TableId(value = "id")
    @ExcelIgnore
    private Long id;

    @ExcelIgnore
    private String orgId;

    @ExcelIgnore
    private String companyId;

    @ExcelIgnore
    private Long houseId;

    @ExcelProperty(value = {TITLE, "小区*"}, index = 0)
    private String orgName;

    @ExcelProperty(value = {TITLE, "房号*"}, index = 1)
    private String roomNum;

    @ExcelProperty(value = {TITLE, "费项*"}, index = 2)
    private String itemName;

    @ExcelProperty(value = {TITLE, "基本单价*"}, index = 3)
    private BigDecimal standardPrice;

    @ExcelProperty(value = {TITLE, "卡号*"}, index = 4)
    private String cardNum;

    @ExcelProperty(value = {TITLE, "交易次数*"}, index = 5)
    private Integer tradeTimes;

    @ExcelProperty(value = {TITLE, "购量*"}, index = 6)
    private Integer qty;

    @ExcelProperty(value = {TITLE, "应收金额*"}, index = 7)
    private BigDecimal receivable;

    @ExcelProperty(value = {TITLE, "余额支付*"}, index = 8)
    private BigDecimal paymentBalance;

    @ExcelProperty(value = {TITLE, "实收金额*"}, index = 9)
    private BigDecimal paidIn;

    @ExcelProperty(value = {TITLE, "交易时间*"}, index = 10)
    private Date tradeTime;

    @ExcelProperty(value = {TITLE, "补贴金额"}, index = 11)
    private BigDecimal allowAmount;

    @ExcelProperty(value = {TITLE, "本年度热费减免金额"}, index = 12)
    private BigDecimal deduction;

    @ExcelIgnore
    private String itemId;

    @ExcelIgnore
    private Long archiveId;

    @ExcelIgnore
    private String userId;

    @ExcelIgnore
    private String meterNum;

    @ExcelIgnore
    private String meterArcCode;

    @ExcelIgnore
    private Integer meterSerial;

    @ExcelIgnore
    private BigDecimal totalUsed;

    @ExcelIgnore
    private BigDecimal totalMoney;

    @ExcelIgnore
    private BigDecimal totalRecharge;

    @ExcelIgnore
    private BigDecimal currentBalance;

    @ExcelIgnore
    private String createBy;

    @ExcelIgnore
    private Date createTime;

    @ExcelIgnore
    private Integer type;

    @TableField(exist = false)
    private static final String TITLE = "1、交易记录导入模板\n"
        + "2、小区名称、房号、费项、卡号、购量、应收金额、实收金额为必填项。\n"
        + "3、交易时间格式：yyyy-MM-dd HH:mm:ss。\n"
        + "4、费项名称必须与系统中设置的费项名称一致。\n"
        + "5、请不要修改表头中的文字，也不要改变列的顺序。";
}
