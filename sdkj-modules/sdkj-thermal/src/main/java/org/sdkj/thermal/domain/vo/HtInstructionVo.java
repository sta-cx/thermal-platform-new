package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtInstruction;

/**
 * 控制指令视图对象
 * 迁移自旧系统 HtInstruction
 */
@Data
@AutoMapper(target = HtInstruction.class)
public class HtInstructionVo {

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
