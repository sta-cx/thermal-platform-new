package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrHeatDtuArchiveVo;

import java.util.Date;

/**
 * DTU采集器配表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_heat_dtu_archive")
@AutoMapper(target = PrHeatDtuArchiveVo.class)
public class PrHeatDtuArchive extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** DTU编号 */
    private String dtuNum;

    /** 安装位置 */
    private String installSite;

    /** 状态 */
    private String status;

    /** IP地址 */
    private String ip;

    /** 通道号 */
    private String chanNum;

    /** 通道数量 */
    private Integer channelNum;

    /** 通道数量时间 */
    private Date channelNumTime;

    /** 最新时间 */
    private Date latestTime;

    /** 最后时间 */
    private Date lastTime;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    // ========== 非数据库字段 ==========

    /** 控制范围 */
    @TableField(exist = false)
    private String controlRange;
}
