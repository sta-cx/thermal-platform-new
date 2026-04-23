package org.dromara.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.meter.domain.MtWaterArchive;

/**
 * 水表档案视图对象
 * 迁移自旧系统 MtWaterArchive
 */
@Data
@AutoMapper(target = MtWaterArchive.class)
public class MtWaterArchiveVo {

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
