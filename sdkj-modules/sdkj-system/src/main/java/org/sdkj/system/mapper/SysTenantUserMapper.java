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
            .last("LIMIT 1"));
    }
}
