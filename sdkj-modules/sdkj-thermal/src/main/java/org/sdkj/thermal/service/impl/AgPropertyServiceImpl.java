package org.sdkj.thermal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.AgProperty;
import org.sdkj.thermal.domain.bo.AgPropertyBo;
import org.sdkj.thermal.domain.vo.AgPropertyVo;
import org.sdkj.thermal.mapper.AgPropertyMapper;
import org.sdkj.thermal.service.IAgPropertyService;
import org.springframework.stereotype.Service;

/**
 * 代理商关联物业 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AgPropertyServiceImpl extends ServiceImpl<AgPropertyMapper, AgProperty> implements IAgPropertyService {

    private final AgPropertyMapper propertyMapper;

    @Override
    public IPage<AgPropertyVo> selectPropertyPage(Page<?> page, String isEnabled, String isAudited,
                                                   String searchContent, String agentCode) {
        return propertyMapper.selectPropertyPage(page, isEnabled, isAudited, searchContent, agentCode);
    }

    @Override
    public IPage<AgPropertyVo> selectUnboundPage(Page<?> page, String agentCode, String searchContent) {
        return propertyMapper.selectUnboundPage(page, agentCode, searchContent);
    }

    @Override
    public boolean bindProperty(AgPropertyBo propertyBo) {
        AgProperty entity = new AgProperty();
        entity.setAgentCompanyId(propertyBo.getAgentCompanyId());
        entity.setPropertyCompanyId(propertyBo.getPropertyCompanyId());
        return save(entity);
    }

    @Override
    public boolean unbindProperty(String id) {
        return removeById(id);
    }

    @Override
    public AgPropertyVo getPropertyDetail(String id) {
        return propertyMapper.selectPropertyDetail(id);
    }

    @Override
    public boolean updateAuditedStatus(String id, boolean audited) {
        int rows = propertyMapper.updateAuditedStatus(id, audited ? 1 : 0);
        return rows > 0;
    }

    @Override
    public boolean updateEnabledStatus(String id, boolean enabled) {
        int rows = propertyMapper.updateEnabledStatus(id, enabled ? 1 : 0);
        return rows > 0;
    }

    @Override
    public boolean editProperty(String id, AgPropertyBo propertyBo) {
        AgProperty entity = getById(id);
        if (entity == null) {
            return false;
        }
        if (propertyBo.getAgentCompanyId() != null) {
            entity.setAgentCompanyId(propertyBo.getAgentCompanyId());
        }
        if (propertyBo.getPropertyCompanyId() != null) {
            entity.setPropertyCompanyId(propertyBo.getPropertyCompanyId());
        }
        if (propertyBo.getIsAudited() != null) {
            entity.setIsAudited(propertyBo.getIsAudited());
        }
        if (propertyBo.getIsEnabled() != null) {
            entity.setIsEnabled(propertyBo.getIsEnabled());
        }
        return updateById(entity);
    }
}
