package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrOptionsHeatVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 供热系统选项配置
 * 迁移自旧系统 PrOptionsHeat
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_options_heat")
@AutoMapper(target = PrOptionsHeatVo.class)
public class PrOptionsHeat extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 公司ID */
    private String companyId;

    /** 小区ID */
    private String orgId;

    /** 配置级别 (company/org) */
    private String level;

    /** 热力表是否按金额收费    0 否  1 是 */
    private Integer moneyCharge;

    /** 热力表远传表是否自动关阀   0 否  1 是 */
    private Integer autoClose;

    /** 热力表是否自动发送催费短信  0 否 1 是 */
    private Integer autoSms;

    /** 热力统一开阀时间 */
    private Date openTime;

    /** 热力统一关阀时间 */
    private Date closeTime;

    /** 热力开阀提前天数 */
    private Integer openEarlyDays;

    /** 热力关阀推后天数 */
    private Integer closeLaterDays;

    /** 报停收费比例 */
    private Integer scale;

    /** 是否启用热力表收据单独设置 */
    private Integer isEnable;

    /** 热力收据抬头 */
    private String quittanceTitle;

    /** 热力收据起始流水号 */
    private Long startSerial;

    /** 热力收据流水号长度 */
    private Integer serialLength;

    /** 热力收据字母前缀 */
    private String letterPrefix;

    /** 热力收据流水前缀 */
    private String serialPrefix;

    /** 热力收据循环方式    0 按月  1  按天 */
    private Integer roundMode;

    /** 热力收据自定义1 */
    private String define1;

    /** 热力收据自定义2 */
    private String define2;

    /** 调控最小值 */
    private Integer controlMin;

    /** 调控最大值 */
    private Integer controlMax;

    /** 是否允许用户调控 */
    private Integer regulation;

    /** 用户调控次数 */
    private Integer regulationNum;

    /** 指令发送条数 */
    private Integer commandNum;

    /** 指令发送间隔 */
    private Integer intervalTime;

    /** 指令最后执行时间 */
    private Date commandTime;

    /** 电信平台master-apikey */
    private String teleApiKey;

    /** 电信平台appkey */
    private String teleAppKey;

    /** 电信平台appsecret */
    private String teleAppSecret;

    /** 电信产品id */
    private String teleProductId;

    /** 运营商 0移动 1电信 2联通 */
    private Integer service;

    /** 大连世达单位编码 */
    private Integer dlsdUnitCode;

    /** 调节步长 */
    private Integer stride;

    /** 温度报警(min) */
    private BigDecimal wdbjx;

    /** 温度报警(max) */
    private BigDecimal wdbjd;

    /** 室温报警(min) */
    private BigDecimal swbjx;

    /** 室温报警(max) */
    private BigDecimal swbjd;

    /** 控制任务未处理次数 */
    private Integer kzwclcs;

    /** 控制失败次数 */
    private Integer kzsbcs;

    /** 室温(min) */
    private BigDecimal houseMin;

    /** 室温(max) */
    private BigDecimal houseMax;

    /** 室温小颜色 */
    private String houseSmallColor;

    /** 室温正常颜色 */
    private String houseMediumColor;

    /** 室温大颜色 */
    private String houseBigColor;

    /** 回水(min) */
    private BigDecimal backWaterMin;

    /** 回水(max) */
    private BigDecimal backWaterMax;

    /** 回水小颜色 */
    private String backWaterSmallColor;

    /** 回水小颜色 */
    private String backWaterMediumColor;

    /** 回水小颜色 */
    private String backWaterBigColor;

    /** 楼市已完成颜色 */
    private String floorViewCompleteColor;

    /** 楼市未完成颜色 */
    private String floorViewNoCompleteColor;

    /** 楼市异常颜色 */
    private String floorViewAbnormalColor;

    /** 保温参数 - 保温保护回水下限 */
    private BigDecimal bwbh;

    /** 保温参数 - 保温保护回水上限 */
    private BigDecimal bwbsh;

    /** 保温参数 - 保温回水下限 */
    private BigDecimal bwsh;

    /** 保温参数 - 保温回水上限 */
    private BigDecimal bwxh;

    /** 保温参数 - 保温总计下限 */
    private BigDecimal bwzjh;

    /** 保温参数 - 保温不限回水下限 */
    private BigDecimal bwbxh;

    /** 保温参数 - 保温不限回水循环下限 */
    private BigDecimal bwblyhxh;

    /** 保温参数 - 室温偏差值 */
    private BigDecimal hswdpcz;

    /** 启用稽查报警   0 否  1 是 */
    private Integer isAbnormalEnable;

    /** 违反非合法供热室温 */
    private BigDecimal wjfhswd;

    /** 供热周期开始日期 */
    private String heatStartDate;

    /** 供热周期结束日期 */
    private String heatEndDate;

    /** 收费标准类型 */
    private String chargeStandardType;

    /** 违约金比例 */
    private String penaltyRate;

    /** 发票备注 */
    private String invoiceNotes;

    /** 缴费提示 */
    private String paymentReminder;

    /** 默认室温平衡度 */
    private BigDecimal mrphwd;
}
