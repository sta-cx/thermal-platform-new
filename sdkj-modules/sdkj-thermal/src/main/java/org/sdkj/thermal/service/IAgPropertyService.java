package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.AgProperty;
import org.sdkj.thermal.domain.bo.AgPropertyBo;
import org.sdkj.thermal.domain.vo.AgPropertyVo;

/**
 * 代理商关联物业 Service
 */
public interface IAgPropertyService extends IService<AgProperty> {

    /**
     * 分页查询代理商关联物业列表
     */
    IPage<AgPropertyVo> selectPropertyPage(Page<?> page, String isEnabled, String isAudited,
                                           String searchContent, String agentCode);

    /**
     * 查询未绑定物业列表
     */
    IPage<AgPropertyVo> selectUnboundPage(Page<?> page, String agentCode, String searchContent);

    /**
     * 关联物业
     */
    boolean bindProperty(AgPropertyBo propertyBo);

    /**
     * 解除关联
     */
    boolean unbindProperty(String id);

    /**
     * 查询关联物业详情
     */
    AgPropertyVo getPropertyDetail(String id);

    /**
     * 更新审核状态
     */
    boolean updateAuditedStatus(String id, boolean audited);

    /**
     * 更新启用状态
     */
    boolean updateEnabledStatus(String id, boolean enabled);

    /**
     * 编辑关联物业信息
     */
    boolean editProperty(String id, AgPropertyBo propertyBo);
}
