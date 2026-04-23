package org.dromara.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.thermal.domain.vo.HtTaskSettingLogVo;
import java.util.Date;
import java.util.List;

/**
 * 调控设定日志记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ht_task_setting_log")
@AutoMapper(target = HtTaskSettingLogVo.class)
public class HtTaskSettingLog extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 任务ID */
    private String taskId;

    /** 控制范围类型 1=户阀 2=单元阀 */
    private String scopeType;

    /** 子表列表（非数据库字段） */
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private List<HtTaskSettingLogItem> items;
}
