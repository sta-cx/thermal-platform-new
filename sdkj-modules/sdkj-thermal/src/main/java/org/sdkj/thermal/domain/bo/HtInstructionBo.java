package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.HtInstruction;

/**
 * 控制指令业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = HtInstruction.class, reverseConvertGenerate = false)
public class HtInstructionBo extends BaseEntity {

    /** 主键 */
    private String id;

    /** 指令名称 */
    private String name;

    /** 指令类型 */
    private Integer type;

    /** 指令内容 */
    private String instruction;

    /** 备注 */
    private String remark;
}
