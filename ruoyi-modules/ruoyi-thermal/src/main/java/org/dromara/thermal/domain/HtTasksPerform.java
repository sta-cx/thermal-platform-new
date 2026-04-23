package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtTasksPerformVo;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 调控执行记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_tasks_perform")
@AutoMapper(target = HtTasksPerformVo.class)
public class HtTasksPerform extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String instructionId;
    private String groupId;
    private String strategyId;
    private Integer commandIndex;
    private Integer orderr;
    private Integer instructionType;
    private Integer instruction;
    private Integer number;
    private Integer intervall;
    private Integer foreStart;
    private Integer unit;
    private Integer duration;
    private String orgId;
    private String companyId;
    private String concentratorCode;
    private String deviceId;
    private String meterNum;
    private String meterId;
    private String meterArcCode;
    private Integer status;
    private Integer instructionStatus;
    private Date sendTime;
    private String tasksId;
    private BigDecimal inTemp;
    private BigDecimal outTemp;
    private BigDecimal roomTemp;
    private Integer valveOpen;
    private String imei;
    private String dtuNum;
    private String chanNum;
    private BigDecimal outTempPj;
    private BigDecimal refHeat;
}
