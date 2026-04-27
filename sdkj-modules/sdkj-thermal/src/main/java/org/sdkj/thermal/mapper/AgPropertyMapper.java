package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.AgProperty;
import org.sdkj.thermal.domain.vo.AgPropertyVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 代理商关联物业 Mapper
 * 基础 CRUD 使用 BaseMapperPlus 提供，仅保留复杂查询
 */
public interface AgPropertyMapper extends BaseMapperPlus<AgProperty, AgPropertyVo> {

    /**
     * 分页查询代理商关联物业列表
     */
    IPage<AgPropertyVo> selectPropertyPage(Page<?> page,
                                           @Param("isEnabled") String isEnabled,
                                           @Param("isAudited") String isAudited,
                                           @Param("searchContent") String searchContent,
                                           @Param("agentCode") String agentCode);

    /**
     * 查询未绑定物业列表
     */
    IPage<AgPropertyVo> selectUnboundPage(Page<?> page,
                                         @Param("agentCode") String agentCode,
                                         @Param("searchContent") String searchContent);

    /**
     * 查询关联物业详情
     */
    AgPropertyVo selectPropertyDetail(@Param("id") String id);

    /**
     * 更新审核状态
     */
    int updateAuditedStatus(@Param("id") String id, @Param("isAudited") Integer isAudited);

    /**
     * 更新启用状态
     */
    int updateEnabledStatus(@Param("id") String id, @Param("isEnabled") Integer isEnabled);
}
