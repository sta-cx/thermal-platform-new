package org.dromara.system.service;

import org.dromara.system.domain.SysColumn;
import org.dromara.system.domain.vo.SysColumnVo;

/**
 * 用户自定义表格列服务
 */
public interface ISysColumnService {

    /**
     * 根据用户ID和页面名称查找自定义列
     */
    SysColumnVo getByUserIdAndPageName(Long userId, String pageName);

    /**
     * 新增或更新自定义列
     */
    int saveOrUpdate(SysColumn sysColumn);

    /**
     * 删除自定义列
     */
    int deleteById(Long id);
}
