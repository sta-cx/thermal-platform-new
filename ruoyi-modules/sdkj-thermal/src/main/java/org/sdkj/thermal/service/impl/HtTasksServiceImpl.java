package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.domain.HtScope;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.vo.HtStrategySubVo;
import org.sdkj.thermal.domain.vo.HtTasksVo;
import org.sdkj.thermal.mapper.HtTasksMapper;
import org.sdkj.thermal.mapper.HtScopeMapper;
import org.sdkj.thermal.mapper.HtStrategySubMapper;
import org.sdkj.thermal.mapper.HtInstructionMapper;
import org.sdkj.thermal.service.IHtTasksService;
import org.sdkj.thermal.quartz.ThermalJobManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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
    private final HtStrategySubMapper strategySubMapper;
    private final HtInstructionMapper instructionMapper;
    private final ThermalJobManager jobManager;

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
        if (saved && entity.getCronExpression() != null && !entity.getCronExpression().isEmpty()) {
            addToScheduler(entity.getId());
        }
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
        // 在事务内检查状态，防止 TOCTOU 竞争
        HtTasks existing = getById(entity.getId());
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            throw new RuntimeException("修改之前请先停止任务！");
        }
        boolean updated = updateById(entity);
        if (updated) {
            baseMapper.deleteScopeByTaskId(entity.getId());
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
    public boolean removeById(java.io.Serializable id) {
        HtTasks existing = getById((Integer) id);
        if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
            throw new RuntimeException("删除前请先停止任务！");
        }
        removeFromScheduler((Integer) id);
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> idList) {
        for (Object id : idList) {
            HtTasks existing = getById((Integer) id);
            if (existing != null && existing.getStatus() != null && existing.getStatus() == 1) {
                throw new RuntimeException("任务[ID:" + id + "]正在运行，删除前请先停止！");
            }
        }
        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(Integer id, Integer status) {
        HtTasks task = getById(id);
        if (task == null) return false;

        try {
            if (status == 1) {
                jobManager.resumeJob(id);
            } else {
                jobManager.pauseJob(id);
            }
        } catch (Exception e) {
            throw new RuntimeException("调度器操作失败: " + e.getMessage(), e);
        }

        return baseMapper.updateTaskStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean runTask(Integer id) {
        HtTasks task = getById(id);
        if (task == null) return false;
        if (task.getStatus() == null || task.getStatus() != 1) {
            throw new RuntimeException("请先启动任务！");
        }

        try {
            jobManager.triggerJob(id);
        } catch (Exception e) {
            throw new RuntimeException("触发任务失败: " + e.getMessage(), e);
        }

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
        double rate = baseMapper.queryBalanceRate(taskId);
        return Math.round(rate * 100.0) / 100.0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveValveAngle(String taskId, String scopeType) {
        HtTasks task = getById(Integer.parseInt(taskId));
        if (task == null) throw new RuntimeException("任务不存在");
        if (task.getStrategyId() == null || task.getStrategyId().isEmpty()) {
            throw new RuntimeException("该任务未关联策略，无法生成指令");
        }

        List<HtStrategySubVo> subList = strategySubMapper.selectByStrategyId(task.getStrategyId());
        if (subList == null || subList.isEmpty()) {
            throw new RuntimeException("关联策略下无指令明细，无法生成开度");
        }

        List<Map<String, Object>> scopeData = "2".equals(scopeType)
            ? baseMapper.selectScopeForAngleD(taskId)
            : baseMapper.selectScopeForAngleH(taskId);

        if (scopeData == null || scopeData.isEmpty()) {
            return baseMapper.updateScopeStatus(taskId, 9) > 0;
        }

        List<HtTasksPerform> records = new ArrayList<>();
        int commandIndex = 0;
        Date now = new Date();
        Long userId = LoginHelper.getUserId();
        double avgTemp = baseMapper.queryAvgReturnTemp(taskId);

        for (Map<String, Object> row : scopeData) {
            for (HtStrategySubVo sub : subList) {
                HtTasksPerform record = new HtTasksPerform();
                record.setTasksId(taskId);
                record.setStrategyId(task.getStrategyId());
                record.setInstructionId(sub.getInstructionId());
                record.setGroupId(task.getCuGroupId());
                record.setCommandIndex(commandIndex++);
                record.setOrderr(sub.getSort());
                record.setMeterId(row.get("meter_id") != null ? row.get("meter_id").toString() : null);
                record.setMeterNum(row.get("meter_num") != null ? row.get("meter_num").toString() : null);
                record.setDeviceId(row.get("device_id") != null ? row.get("device_id").toString() : null);
                record.setImei(row.get("imei") != null ? row.get("imei").toString() : null);
                record.setDtuNum(row.get("dtu_num") != null ? row.get("dtu_num").toString() : null);
                record.setChanNum(row.get("chan_num") != null ? row.get("chan_num").toString() : null);
                record.setMeterArcCode(row.get("meter_arc_code") != null ? row.get("meter_arc_code").toString() : null);
                record.setOrgId(row.get("org_id") != null ? row.get("org_id").toString() : null);
                record.setCompanyId(row.get("company_id") != null ? row.get("company_id").toString() : null);
                record.setStatus(0);
                record.setInstructionStatus(0);
                record.setSendTime(now);
                record.setCreateTime(now);
                record.setCreateBy(userId);

                if (sub.getValveAngle() != null && !sub.getValveAngle().isEmpty()) {
                    try {
                        record.setValveOpen(Integer.parseInt(sub.getValveAngle()));
                    } catch (NumberFormatException e) {
                        record.setValveOpen(0);
                    }
                }
                if (avgTemp != 0.0) {
                    record.setOutTempPj(BigDecimal.valueOf(avgTemp).setScale(2, RoundingMode.HALF_UP));
                }

                records.add(record);
            }
        }

        if (!records.isEmpty()) {
            baseMapper.insertTasksPerformBatch(records);
        }

        return baseMapper.updateScopeStatus(taskId, 9) > 0;
    }

    @Override
    public Map<String, Object> querySummary(String orgId, String buildingId, String unitCode) {
        Map<String, Object> result = baseMapper.querySummary(orgId, buildingId, unitCode);
        if (result == null) return Map.of();

        double balanceRate = result.get("balanceRate") != null
            ? ((Number) result.get("balanceRate")).doubleValue() : 0.0;
        double avgReturnTemp = result.get("avgReturnTemp") != null
            ? ((Number) result.get("avgReturnTemp")).doubleValue() : 0.0;

        result.put("balanceRate", Math.round(balanceRate * 100.0) / 100.0);
        result.put("avgReturnTemp", BigDecimal.valueOf(avgReturnTemp)
            .setScale(2, RoundingMode.HALF_UP).doubleValue());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addToScheduler(Integer id) {
        try {
            return jobManager.addJob(id);
        } catch (Exception e) {
            throw new RuntimeException("添加调度任务失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromScheduler(Integer id) {
        try {
            return jobManager.deleteJob(id);
        } catch (Exception e) {
            throw new RuntimeException("删除调度任务失败: " + e.getMessage(), e);
        }
    }
}
