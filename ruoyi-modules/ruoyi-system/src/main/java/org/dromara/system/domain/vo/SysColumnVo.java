package org.dromara.system.domain.vo;

import lombok.Data;

/**
 * 用户自定义表格列视图对象
 */
@Data
public class SysColumnVo {

    private Long id;

    private Long userId;

    private String pageName;

    private String columnName;
}
