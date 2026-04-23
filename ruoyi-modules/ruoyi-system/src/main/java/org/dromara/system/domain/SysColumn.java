package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 用户自定义表格列
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_column")
public class SysColumn extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 页面/表格名称 */
    private String pageName;

    /** 自定义列名（逗号分隔） */
    private String columnName;
}
