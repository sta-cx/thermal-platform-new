package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrHeatDtuArchive;

import java.util.Date;

/**
 * 热力DTU档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrHeatDtuArchive.class, reverseConvertGenerate = false)
public class PrHeatDtuArchiveBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** DTU编号 */
    @NotBlank(message = "DTU编号不能为空")
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
    private String channelNum;

    /** 通道数量时间 */
    private Date channelNumTime;

    /** 最新时间 */
    private Date latestTime;

    /** 最后时间 */
    private Date lastTime;

    /** 小区ID */
    private String orgId;

    /** 控制范围 */
    private String controlRange;

    /** 备注 */
    private String remark;

}
