package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;

/**
 * DTU控制范围表
 * 迁移自旧系统 HtScopeDtu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_scope_dtu")
public class HtScopeDtu extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 任务主表ID */
    private String tasksId;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 档案编号 */
    private String meterArcCode;

    /** DTU编号 */
    private String dtuNum;

    /** 通道号/组号 集合 */
    private String chanNums;

    /** 集中器编号 */
    private String concentratorCode;

    /** 执行状态 0 正常 1 继续执行 2控制未处理 3控制下发失败 4阀门角度报警 5回水温度报警 6室温报警 9完成调控 */
    private Integer status;

}
