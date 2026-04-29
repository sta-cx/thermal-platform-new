package org.sdkj.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.sdkj.system.domain.SysTenantUser;

/**
 * 用户-租户关联 Mapper
 */
public interface SysTenantUserMapper extends BaseMapper<SysTenantUser> {

    default SysTenantUser selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapper<SysTenantUser>()
            .eq(SysTenantUser::getUserId, userId)
            .orderByAsc(SysTenantUser::getId)
            .last("LIMIT 1"));
    }

    default SysTenantUser selectByUserIdAndTenantId(Long userId, String tenantId) {
        return selectOne(new LambdaQueryWrapper<SysTenantUser>()
            .eq(SysTenantUser::getUserId, userId)
            .eq(SysTenantUser::getTenantId, tenantId)
            .last("LIMIT 1"));
    }
}
