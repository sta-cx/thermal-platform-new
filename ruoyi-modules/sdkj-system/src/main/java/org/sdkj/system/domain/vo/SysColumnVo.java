package org.sdkj.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.system.domain.SysColumn;

/**
 * 用户自定义表格列视图对象
 */
@Data
@AutoMapper(target = SysColumn.class)
public class SysColumnVo {

    private Long id;

    private Long userId;

    private String pageName;

    private String columnName;
}
