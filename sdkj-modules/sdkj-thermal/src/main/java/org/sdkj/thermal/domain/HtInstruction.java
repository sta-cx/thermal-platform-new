package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.HtInstructionVo;

/**
 * 控制指令
 * 迁移自旧系统 HtInstruction
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_instruction")
@AutoMapper(target = HtInstructionVo.class)
public class HtInstruction extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 指令名称 */
    private String name;

    /** 指令类型 */
    private Integer type;

    /** 指令内容 */
    private String instruction;

    /** 备注 */
    private String remark;

}
