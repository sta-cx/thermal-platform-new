package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtRepairVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 报修记录
 * 迁移自旧系统 HtRepair
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_repair")
@AutoMapper(target = HtRepairVo.class)
public class HtRepair extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 楼宇ID */
    private Long buildingId;

    /** 楼宇名称 */
    private String buildingName;

    /** 单元编码 */
    private String unitCode;

    /** 房屋ID */
    private Long houseId;

    /** 房号 */
    private String roomNum;

    /** 仪表ID */
    private Long meterId;

    /** 仪表号 */
    private String meterNum;

    /** 是否缴费 */
    private Integer isCharged;

    /** 阀门状态 */
    private String valveStatus;

    /** 阀门角度 */
    private Integer valve;

    /** 进水温度 */
    private BigDecimal inTemp;

    /** 回水温度 */
    private BigDecimal outTemp;

    /** 室温 */
    private BigDecimal roomTemp;

    /** 报修类型 */
    private Integer repairType;

    /** 报修时间 */
    private Date repairTime;

    /** 报修信息 */
    private String repairInfo;

    /** 处理状态 */
    private Integer repairStatus;

    /** 处理结果 */
    private String repairResult;

    /** 小区ID */
    private String orgId;

    /** 小区名称 */
    private String orgName;


    /** 是否删除 (0=正常 1=删除) */
    @TableLogic(value = "0", delval = "1")
    private Integer isDelete;

    /** 创建人姓名 */
    private String createName;

    /** 是否维修中 */
    private String inMaintenance;

    /** 派单人员ID */
    private String dispatchId;

    /** 派单人员姓名 */
    private String dispatchName;

    /** 派单时间 */
    private Date dispatchTime;

    /** 报修编号 */
    private String repairNo;

    /** 维修人员ID */
    private String fixId;

    /** 维修人员姓名 */
    private String fixName;

    /** 维修完成时间 */
    private Date fixTime;

    /** 用户姓名 */
    private String userName;

    /** 用户电话 */
    private String userPhone;

    /** 预约时间 */
    private Date appointTime;

    /** 紧急程度 */
    private Integer urgentType;

    /** 服务类型 */
    private Integer serviceType;

}
