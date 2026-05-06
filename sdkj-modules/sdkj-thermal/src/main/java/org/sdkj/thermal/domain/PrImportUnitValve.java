package org.sdkj.thermal.domain;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 单元阀门导入实体类
 * 迁移自旧系统 PrImportUnitValve
 */
@Data
@TableName("pr_import_unit_valve")
public class PrImportUnitValve {

    /**
     * 主键
     */
    @TableId(value = "id")
    @ExcelIgnore
    private Long id;

    /**
     * 小区名称
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "小区名称*"}, index = 0)
    @TableField(exist = false)
    private String orgName;

    /**
     * 楼宇名称
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "楼宇名称*"}, index = 1)
    @TableField(exist = false)
    private String buildingName;

    /**
     * 楼宇ID
     */
    @ExcelIgnore
    private Long buildingId;

    /**
     * 单元id
     */
    @ExcelIgnore
    private Long unitId;

    /**
     * 单元名称
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "单元名称*"}, index = 2)
    @TableField(exist = false)
    private String unitName;

    /**
     * 仪表id
     */
    @ExcelIgnore
    private Long archiveId;

    /**
     * 仪表名称
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "仪表名称*"}, index = 3)
    @TableField(exist = false)
    private String meterName;

    /**
     * 子表号
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "对应序号*"}, index = 4)
    private Integer meterSerial;

    /**
     * 表号
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "表号*"}, index = 5)
    private String meterNum;

    /**
     * 卡号
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "卡号/IMSI"}, index = 6)
    private String cardNum;

    /**
     * imei
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "IMEI"}, index = 7)
    private String imei;

    /**
     * 平台给终端分配的设备ID
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "设备ID"}, index = 8)
    private String deviceId;

    /**
     * DTU编号
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "DTU编号"}, index = 9)
    private String dtuNum;

    /**
     * DTU类型
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "DTU类型"}, index = 10)
    private Integer dtuType;

    /**
     * 集中器编号
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "集中器编号"}, index = 11)
    private String concentratorCode;

    /**
     * 通道号
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "通道号/组号"}, index = 12)
    private String chanNum;

    /**
     * 小区id
     */
    @ExcelIgnore
    private String orgId;

    /**
     * 所属物业公司id
     */
    @ExcelIgnore
    private String companyId;

    /**
     * 创建者
     */
    @ExcelIgnore
    private String createBy;

    /**
     * 创建时间
     */
    @ExcelIgnore
    private Date createTime;

    /**
     * 状态
     */
    @ExcelIgnore
    private Integer type;

    /**
     * 是否有控制阀门
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "是否有控制阀门*"}, index = 13)
    @TableField(exist = false)
    private String isCommand;

    /**
     * 控制阀门表名称
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "控制阀门表名称"}, index = 14)
    @TableField(exist = false)
    private String commandMeterName;

    /**
     * 档案编号
     */
    @ExcelIgnore
    private String meterArcCode;

    /**
     * 控制阀门档案ID
     */
    @ExcelIgnore
    private String commandArchiveId;

    /**
     * 控制阀门档案编号
     */
    @ExcelIgnore
    private String commandMeterArcCode;

    /**
     * 口径
     */
    @ColumnWidth(15)
    @ExcelProperty(value = {"title", "口径"}, index = 15)
    @TableField(exist = false)
    private String caliber;

    /**
     * 标题说明（用于Excel导出）
     */
    @TableField(exist = false)
    private static final String title = "1、带星号（*）为必填项。\n" +
        "2、单元信息：导入前，请先导入基础数据，填写的单元信息名称必须和基础数据模板填写的相同。\n" +
        "3、仪表名称必须与系统中设置的仪表一致。\n" +
        "4、集中器编号、通道号:(Mbus或Lora仪表务必正确填写),如果没有，请默认填写0。\n" +
        "5、表号、卡号不得重复。请填写准确。卡号必须为最少10位，如果不够10位，前面补0即可。\n" +
        "6、NB系列设备请务必填写IMEI与设备ID（即：电信平台给设备分配的ID）。\n" +
        "7、如果存在控制阀门，控制阀门表名称需自行填写，其他信息与阀门信息相同。\n" +
        "8、DTU类型：0:DTU-直连,  1:DTU-OneNet, 2:DTU-Comway, 输入时填写对应的数字。\n" +
        "9、请不要修改表头中的文字，也不要改变列的顺序。切记！切记！";
}
