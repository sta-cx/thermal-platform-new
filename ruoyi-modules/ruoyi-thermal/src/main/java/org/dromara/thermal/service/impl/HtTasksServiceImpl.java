package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtTasks;
import org.dromara.thermal.domain.HtScope;
import org.dromara.thermal.domain.vo.HtTasksVo;
import org.dromara.thermal.mapper.HtTasksMapper;
import org.dromara.thermal.mapper.HtScopeMapper;
import org.dromara.thermal.service.IHtTasksService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 调控任务服务实现
 */
@Service
@RequiredArgsConstructor
public class HtTasksServiceImpl extends ServiceImpl<HtTasksMapper, HtTasks> implements IHtTasksService {

    private final HtTasksMapper baseMapper;
    private final HtScopeMapper scopeMapper;

    @Override
    public TableDataInfo<HtTasksVo> selectPageList(LambdaQueryWrapper<HtTasks> lqw, PageQuery pageQuery) {
        Page<HtTasksVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtTasks> selectAllByOrgId(String orgId) {
        return baseMapper.selectAllByOrgId(orgId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithScope(HtTasks entity, List<String> scopeIds) {
        boolean saved = save(entity);
        if (saved && scopeIds != null && !scopeIds.isEmpty()) {
            for (String scopeId : scopeIds) {
                if (scopeId == null || scopeId.isBlank()) continue;
                HtScope scope = new HtScope();
                scope.setTasksId(String.valueOf(entity.getId()));
                scope.setId(scopeId);
                scopeMapper.insert(scope);
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithScope(HtTasks entity, List<String> scopeIds) {
        boolean updated = updateById(entity);
        if (updated) {
            // 删除旧的范围记录
            baseMapper.deleteScopeByTaskId(entity.getId());
            // 插入新的范围记录
            if (scopeIds != null && !scopeIds.isEmpty()) {
                for (String scopeId : scopeIds) {
                    if (scopeId == null || scopeId.isBlank()) continue;
                    HtScope scope = new HtScope();
                    scope.setTasksId(String.valueOf(entity.getId()));
                    scope.setId(scopeId);
                    scopeMapper.insert(scope);
                }
            }
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(Integer id, Integer status) {
        // TODO: Quartz/snail-job集成 - 实际启动/停止调度任务
        // 当前仅更新数据库状态
        return baseMapper.updateTaskStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean runTask(Integer id) {
        // TODO: Quartz/snail-job集成 - 立即触发任务执行
        baseMapper.updateLastTime(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markSpecial(List<String> scopeIds, String val, String remark) {
        return baseMapper.markScopeAsSpecial(scopeIds, val, remark) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markSpecialUnit(List<String> scopeIds, String val, String remark) {
        return baseMapper.markUnitAsSpecial(scopeIds, val, remark) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markPayStatus(List<String> scopeIds, String val) {
        return baseMapper.markPayStatus(scopeIds, val) > 0;
    }

    @Override
    public double refreshBalanceRate(String taskId) {
        return baseMapper.queryBalanceRate(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveValveAngle(String taskId, String scopeType) {
        // TODO: 指令生成管线 - Phase 4c-3 完整实现
        // 当前标记范围为调控完成状态
        return baseMapper.updateScopeStatus(taskId, 9) > 0;
    }

    @Override
    public Map<String, Object> querySummary(String orgId, String buildingId, String unitCode) {
        return baseMapper.querySummary(orgId, buildingId, unitCode);
    }
}
