package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtTaskSettingLogItemVo;

/**
 * 调控设定日志明细
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_task_setting_log_item")
@AutoMapper(target = HtTaskSettingLogItemVo.class)
public class HtTaskSettingLogItem extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 主表ID */
    private String mainId;

    /** 房屋/单元ID */
    private String scopeId;

    /** 仪表号 */
    private String meterNum;

    /** 原角度 */
    private Integer oldAngle;

    /** 新角度 */
    private Integer newAngle;
}
