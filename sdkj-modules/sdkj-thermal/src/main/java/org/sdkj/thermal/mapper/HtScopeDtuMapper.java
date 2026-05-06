package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.HtScopeDtu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DTU控制范围表Mapper接口
 */
public interface HtScopeDtuMapper extends BaseMapperPlus<HtScopeDtu, HtScopeDtu> {

    /**
     * 根据任务ID删除DTU控制范围
     */
    void deleteByTasksId(@Param("tasksId") Long tasksId);

    /**
     * 根据任务ID查询DTU控制范围列表
     */
    List<HtScopeDtu> queryHtScopeDtuListByTasksId(@Param("tasksId") Long tasksId);

    /**
     * 批量插入DTU控制范围
     */
    void insertList(@Param("list") List<HtScopeDtu> list);

}
