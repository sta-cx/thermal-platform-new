package org.sdkj.meter.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.MtTcValve;

/**
 * 阀门档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MtTcValve.class, reverseConvertGenerate = false)
public class MtTcValveBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 分类ID */
    @NotBlank(message = "分类ID不能为空")
    private String sortId;

    /** 编号 */
    @NotBlank(message = "编号不能为空")
    private String code;

    /** 名称 */
    @NotBlank(message = "名称不能为空")
    private String name;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 类型 */
    private String type;

    /** 是否动作 (0=否 1=是) */
    private Integer isAction;

    /** 安装位置 */
    private String installSite;

    /** 排序 */
    private String seq;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

}
