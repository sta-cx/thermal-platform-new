package org.sdkj.thermal.domain.vo;

import org.sdkj.common.sensitive.annotation.Sensitive;
import org.sdkj.common.sensitive.core.SensitiveStrategy;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrHouse;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 房屋信息 View Object
 */
@Data
@AutoMapper(target = PrHouse.class)
public class PrHouseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 房屋编码 */
    private String code;

    /** 房间号 */
    private String roomNum;

    /** 楼宇ID */
    private Long buildingId;

    /** 楼宇名称 */
    private String buildingName;

    /** 单元编码 */
    private String unitCode;

    /** 楼层 */
    private Integer floor;

    /** 使用面积 */
    private BigDecimal nfloorArea;

    /** 建筑面积 */
    private BigDecimal gfloorArea;

    /** 供热面积 */
    private BigDecimal heatingArea;

    /** 一楼内面积 */
    private BigDecimal fristInsidearea;

    /** 二楼内面积 */
    private BigDecimal secondInsidearea;

    /** 三楼内面积 */
    private BigDecimal thirdInsidearea;

    /** 房屋性质 */
    private String nature;

    /** 房屋结构 */
    private String structure;

    /** 房屋类型 */
    private String type;

    /** 朝向 */
    private String towards;

    /** 单元类型 */
    private String unitType;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 产权年限 */
    private String propertyTerm;

    /** 工程交付时间 */
    private Date deliveryTime;

    /** 物业验收时间 */
    private Date acceptTime;

    /** 入住时间 */
    private Date occupancyTime;

    /** 立户时间 */
    private Date establishTime;

    /** 邮寄地址 */
    private String address;

    /** 装修状态 */
    private String decorationStatus;

    /** 房屋状态 */
    private String status;

    /** 出租状态 */
    private String rentalStatus;

    /** 排序 */
    private String seq;

    /** 位置属性 */
    private String siteType;

    /** 历史位置 */
    private String siteTypeOld;

    /** 供热区域属性 */
    private String stationType;

    /** 预设角度 */
    private BigDecimal presetAngle;

    /** 预设流量 */
    private BigDecimal presetFlowRate;

    /** 进水温度 */
    private BigDecimal inTemp;

    /** 出水温度 */
    private BigDecimal outTemp;

    /** 室温 */
    private BigDecimal roomTemp;

    /** 阀门开度百分比 */
    private Integer valveOpen;

    /** 当前流量 */
    private BigDecimal curFlow;

    /** 外部缴费编码 */
    private String otherCode;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    // ========== 非数据库字段 ==========

    /** 费项分组 */
    private String itemGroup;

    /** 费项编码 */
    private String itemCode;

    /** 费项名称 */
    private String itemName;

    /** 用户名 */
    private String userName;

    /** 手机号 */
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;

    /** 用户ID */
    private String userId;

    /** 小区名称 */
    private String orgName;

    /** 供热分区名称 */
    private String stationPartitionName;

    /** 热力站名称 */
    private String stationName;

    /** 是否已收费 */
    private Integer isCharged;

    /** 是否特殊户 */
    private Integer isSpecial;

    /** 缴费状态 */
    private String payStatus;

    /** 金额 */
    private BigDecimal money;

    /** 预充值金额 */
    private BigDecimal preloaded;
}
