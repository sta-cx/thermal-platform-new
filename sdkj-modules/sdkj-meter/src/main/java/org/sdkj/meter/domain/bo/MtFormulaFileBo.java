package org.sdkj.meter.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.MtFormulaFile;

/**
 * 公式档案业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MtFormulaFile.class, reverseConvertGenerate = false)
public class MtFormulaFileBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 公式名称 */
    @NotBlank(message = "公式名称不能为空")
    private String name;

    /** 公式类型 */
    @NotBlank(message = "公式类型不能为空")
    private String type;

    /** 中文公式 */
    private String cformula;

    /** 英文公式 */
    private String eformula;

    /** 排序 */
    private String seq;

    /** 是否启用 0=禁用 1=启用 */
    private String isEnabled;

}
