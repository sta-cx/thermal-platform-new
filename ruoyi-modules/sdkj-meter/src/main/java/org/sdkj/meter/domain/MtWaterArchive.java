package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.vo.MtWaterArchiveVo;

/**
 * 水表档案
 * 迁移自旧系统 MtWaterArchive
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_water_archive")
@AutoMapper(target = MtWaterArchiveVo.class)
public class MtWaterArchive extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 分类ID */
    private String sortId;

    /** 编号 */
    private String code;

    /** 名称 */
    private String name;

    /** 消息类型 */
    private Integer msgType;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 常量 */
    private String constant;

    /** 关阀值 */
    private String closeVal;

    /** 报警值 */
    private String alarmVal;

    /** 负载限制 */
    private String loadLimit;

    /** 排序 */
    private String seq;

    /** 表号是否必填 (0=否 1=是) */
    private Integer meterNumRequired;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

}
