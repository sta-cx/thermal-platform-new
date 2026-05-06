package org.sdkj.meter.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.MtWaterArchive;

/**
 * 水表档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MtWaterArchive.class, reverseConvertGenerate = false)
public class MtWaterArchiveBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 分类ID */
    @NotNull(message = "分类ID不能为空")
    private Long sortId;

    /** 编号 */
    @NotBlank(message = "编号不能为空")
    private String code;

    /** 名称 */
    @NotBlank(message = "名称不能为空")
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
