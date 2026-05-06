package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.vo.MtFormulaFileVo;

/**
 * 公式档案
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_formula_file")
@AutoMapper(target = MtFormulaFileVo.class)
public class MtFormulaFile extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 公式名称 */
    private String name;

    /** 公式类型 */
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
