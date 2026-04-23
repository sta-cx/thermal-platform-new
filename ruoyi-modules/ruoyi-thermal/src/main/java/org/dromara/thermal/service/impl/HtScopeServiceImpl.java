package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.thermal.domain.HtScope;
import org.dromara.thermal.domain.vo.HtScopeVo;
import org.dromara.thermal.mapper.HtScopeMapper;
import org.dromara.thermal.service.IHtScopeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 控制范围服务实现
 */
@Service
@RequiredArgsConstructor
public class HtScopeServiceImpl extends ServiceImpl<HtScopeMapper, HtScope> implements IHtScopeService {

    private final HtScopeMapper baseMapper;

    @Override
    public List<HtScopeVo> getHouseListByTaskId(String orgId, String taskId, Integer scopeType) {
        LambdaQueryWrapper<HtScope> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HtScope::getTasksId, taskId);
        lqw.eq(orgId != null && !orgId.isEmpty(), HtScope::getOrgId, orgId);
        // scopeType 参数用于过滤执行状态（0=正常 9=完成调控）
        if (scopeType != null) {
            lqw.eq(HtScope::getStatus, scopeType);
        }
        return baseMapper.selectVoList(lqw);
    }
}
