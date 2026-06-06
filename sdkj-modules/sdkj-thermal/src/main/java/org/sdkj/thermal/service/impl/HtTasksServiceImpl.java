package org.sdkj.thermal.service.impl;

import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.mybatis.utils.IdGeneratorUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.constant.ThermalTaskConstants;
import org.sdkj.thermal.domain.*;
import org.sdkj.thermal.domain.vo.*;
import org.sdkj.thermal.mapper.*;
import org.sdkj.thermal.service.IHtTasksService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.sdkj.thermal.quartz.ThermalJobManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 调控任务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HtTasksServiceImpl extends OrgScopedServiceImpl<HtTasksMapper, HtTasks> implements IHtTasksService {

    private final HtTasksMapper baseMapper;
    private final HtScopeMapper scopeMapper;
    private final HtStrategySubMapper strategySubMapper;
    private final HtInstructionMapper instructionMapper;
    private final PrHouseMapper prHouseMapper;
    private final ThermalJobManager jobManager;
    private final HtStrategyMapper htStrategyMapper;
    private final HtStrategyPerformMapper htStrategyPerformMapper;
    private final HtScopeDtuMapper htScopeDtuMapper;
    private final HtTasksPerformMapper htTasksPerformMapper;

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
    public boolean saveWithScope(HtTasks entity, List<Long> scopeIds) {
        boolean saved = save(entity);
        if (saved && entity.getCronExpression() != null && !entity.getCronExpression().isEmpty()) {
            addToScheduler(entity.getId());
        }
        if (saved && scopeIds != null && !scopeIds.isEmpty()) {
            for (Long scopeId : scopeIds) {
                if (scopeId == null) {
                    continue;
                }
                HtScope scope = new HtScope();
                scope.setTasksId(entity.getId());
                scope.setId(scopeId);
                scopeMapper.insert(scope);
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithScope(HtTasks entity, List<Long> scopeIds) {
        // 在事务内检查状态，防止 TOCTOU 竞争
        HtTasks existing = getById(entity.getId());
        if (existing != null && existing.getStatus() != null && existing.getStatus() == ThermalTaskConstants.TASK_RUNNING) {
            throw new ServiceException("修改之前请先停止任务！");
        }
        boolean updated = updateById(entity);
        if (updated) {
            baseMapper.deleteScopeByTaskId(entity.getId());
            if (scopeIds != null && !scopeIds.isEmpty()) {
                for (Long scopeId : scopeIds) {
                    if (scopeId == null) {
                        continue;
                    }
                    HtScope scope = new HtScope();
                    scope.setTasksId(entity.getId());
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
        HtTasks existing = getById(id);
        if (existing != null && existing.getStatus() != null && existing.getStatus() == ThermalTaskConstants.TASK_RUNNING) {
            throw new ServiceException("删除前请先停止任务！");
        }
        removeFromScheduler((Long) id);
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> idList) {
        for (Object id : idList) {
            HtTasks existing = getById((java.io.Serializable) id);
            if (existing != null && existing.getStatus() != null && existing.getStatus() == ThermalTaskConstants.TASK_RUNNING) {
                throw new ServiceException("任务[ID:" + id + "]正在运行，删除前请先停止！");
            }
        }
        return super.removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(Long id, Integer status) {
        HtTasks task = getById(id);
        if (task == null) return false;

        try {
            if (status == ThermalTaskConstants.TASK_RUNNING) {
                jobManager.resumeJob(id);
            } else {
                jobManager.pauseJob(id);
            }
        } catch (Exception e) {
            throw new ServiceException("调度器操作失败: " + e.getMessage(), e);
        }

        return baseMapper.updateTaskStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean runTask(Long id) {
        HtTasks task = getById(id);
        if (task == null) return false;
        if (task.getStatus() == null || task.getStatus() != ThermalTaskConstants.TASK_RUNNING) {
            throw new ServiceException("请先启动任务！");
        }

        try {
            jobManager.triggerJob(id);
        } catch (Exception e) {
            throw new ServiceException("触发任务失败: " + e.getMessage(), e);
        }

        baseMapper.updateLastTime(id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markSpecial(List<Long> scopeIds, String val, String remark) {
        return baseMapper.markScopeAsSpecial(scopeIds, val, remark) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markSpecialUnit(List<Long> scopeIds, String val, String remark) {
        return baseMapper.markUnitAsSpecial(scopeIds, val, remark) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markPayStatus(List<Long> scopeIds, String val) {
        return baseMapper.markPayStatus(scopeIds, val) > 0;
    }

    @Override
    public double refreshBalanceRate(Long taskId) {
        double rate = baseMapper.queryBalanceRate(taskId);
        return Math.round(rate * 100.0) / 100.0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveValveAngle(Long taskId, String scopeType) {
        HtTasks task = getById(taskId);
        if (task == null) throw new ServiceException("任务不存在");

        // 终止条件检查：天数/平衡率/次数
        if (checkTerminationConditions(task)) {
            return true; // 任务已自动停止，不再生成指令
        }

        if (task.getStrategyId() == null) {
            throw new ServiceException("该任务未关联策略，无法生成指令");
        }

        List<HtStrategySubVo> subList = strategySubMapper.selectByStrategyId(task.getStrategyId());
        if (subList == null || subList.isEmpty()) {
            throw new ServiceException("关联策略下无指令明细，无法生成开度");
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
                record.setGroupId(task.getCuGroupId() != null && !task.getCuGroupId().isBlank() ? Long.valueOf(task.getCuGroupId()) : null);
                record.setCommandIndex(commandIndex++);
                record.setOrderr(sub.getSort());
                record.setMeterId(row.get("meter_id") != null ? Long.valueOf(row.get("meter_id").toString()) : null);
                record.setMeterNum(row.get("meter_num") != null ? row.get("meter_num").toString() : null);
                record.setDeviceId(row.get("device_id") != null ? row.get("device_id").toString() : null);
                record.setImei(row.get("imei") != null ? row.get("imei").toString() : null);
                record.setDtuNum(row.get("dtu_num") != null ? row.get("dtu_num").toString() : null);
                record.setChanNum(row.get("chan_num") != null ? row.get("chan_num").toString() : null);
                record.setMeterArcCode(row.get("meter_arc_code") != null ? row.get("meter_arc_code").toString() : null);
                record.setOrgId(row.get("org_id") != null ? row.get("org_id").toString() : null);
                record.setStatus(ThermalTaskConstants.PERFORM_PENDING);
                record.setInstructionStatus(ThermalTaskConstants.PERFORM_PENDING);
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
    public boolean addToScheduler(Long id) {
        try {
            return jobManager.addJob(id);
        } catch (Exception e) {
            throw new ServiceException("添加调度任务失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromScheduler(Long id) {
        try {
            return jobManager.deleteJob(id);
        } catch (Exception e) {
            throw new ServiceException("删除调度任务失败: " + e.getMessage(), e);
        }
    }

    @Override
    public TableDataInfo<HtTasksVo> selectDeviceList(String orgId, String deviceType, PageQuery pageQuery) {
        LambdaQueryWrapper<HtTasks> lqw = new LambdaQueryWrapper<>();
        lqw.eq(orgId != null && !orgId.isEmpty(), HtTasks::getOrgId, orgId);
        lqw.orderByDesc(HtTasks::getCreateTime);
        Page<HtTasksVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateValveAngleLog(String logId, String scopeType) {
        Long parsedLogId = Long.valueOf(logId);
        Long newMainId = IdGeneratorUtil.nextLongId();
        Long userId = LoginHelper.getUserId();
        String createBy = userId != null ? userId.toString() : null;

        if ("1".equals(scopeType)) {
            baseMapper.insertSettingLogMainH(parsedLogId, newMainId, createBy);
            baseMapper.insertSettingLogItemH(parsedLogId, newMainId);
            return baseMapper.updateValveSettingStatusH(newMainId) > 0;
        } else if ("2".equals(scopeType)) {
            baseMapper.insertSettingLogMainD(parsedLogId, newMainId, createBy);
            baseMapper.insertSettingLogItemD(parsedLogId, newMainId);
            return baseMapper.updateValveSettingStatusD(newMainId) > 0;
        }
        return false;
    }

    @Override
    public String getHouseOtherCode(String houseId) {
        return prHouseMapper.selectOtherCodeById(houseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHouseOtherCode(String houseId, String otherCode) {
        return prHouseMapper.updateOtherCodeById(houseId, otherCode) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertHtTasksPerformDtu(HtTasks htTasks) {
        boolean result = false;

        // 终止条件检查：天数/平衡率/次数
        if (checkTerminationConditions(htTasks)) {
            return true; // 任务已自动停止，不再生成指令
        }

        // 调控依据平均回水温度
        if (htTasks.getAdjustBasis() == ThermalTaskConstants.ADJUST_AVG_RETURN_WATER) {

            // 检查回报率
            if (htTasks.getIsUseReportRate() != null && htTasks.getIsUseReportRate() == ThermalTaskConstants.FLAG_ON && htTasks.getNumber() != null && htTasks.getNumber() > 0) {
                Integer reportRate = htTasksPerformMapper.getTaskLastPerformReportRate(htTasks.getId(), htTasks.getCuGroupId());
                if (reportRate != null && reportRate < htTasks.getReportRate()) {
                    log.info("回报率 {}% 小于设定值 {}% 跳过指令生成！", reportRate, htTasks.getReportRate());
                    return true;
                } else {
                    log.info("回报率 {}% 大于等于设定值 {}% 继续指令生成！", reportRate, htTasks.getReportRate());
                }
            }

            List<PJHSVo> pjhsVoList = new ArrayList<>();
            // 判断控制范围类型--3 户阀
            if (htTasks.getScopeType() == ThermalTaskConstants.SCOPE_DTU_BROADCAST) {
                // 根据组号计算调控范围内的平均回水温度（不包括特殊户）
                pjhsVoList = baseMapper.queryHtTasksPJHSDTU(htTasks.getId());
                if (!pjhsVoList.isEmpty()) {
                    baseMapper.queryHtTasksPJHSDTU(htTasks.getId());
                }
            }

            Map<String, Double> pjhsMap = pjhsVoList.stream()
                .filter(pj -> pj.getChanNum() != null && !pj.getChanNum().isEmpty())
                .collect(Collectors.toMap(PJHSVo::getChanNum, PJHSVo::getOutTempPJ));

            // 必须有策略
            if (htTasks.getStrategyId() != null) {

                HtStrategy htStrategy = htStrategyMapper.selectById(htTasks.getStrategyId());

                if (htStrategy == null) {
                    log.error("未获取到策略！");
                    return false;
                }

                int commandIndex = -1;
                HtTasksPerform lastPerform = htTasksPerformMapper.queryLatestTasksPerform(htTasks.getId(), htTasks.getCuGroupId());

                if (lastPerform != null) {
                    commandIndex = lastPerform.getCommandIndex();
                }
                commandIndex = commandIndex + 1;

                HtStrategyPerform htStrategyPerform = htStrategyPerformMapper.queryStrategyPerformListByTasksIdAndIndex(
                    htTasks.getId(), commandIndex);

                if (htStrategyPerform == null) {
                    log.error("未获取到指令或指令序列已完成！");
                    return false;
                }

                List<HtScopeDtu> htScopeDtuList = htScopeDtuMapper.queryHtScopeDtuListByTasksId(htTasks.getId());

                if (htScopeDtuList == null || htScopeDtuList.isEmpty()) {
                    log.error("未获取到调控范围数据！");
                    return false;
                }

                List<HtTasksPerform> htTasksPerformList = new ArrayList<>();
                HtScopeDtu htScopeDtu;

                for (HtScopeDtu scopeDtu : htScopeDtuList) {
                    htScopeDtu = scopeDtu;
                    HtTasksPerform htTasksPerform = new HtTasksPerform();
                    htTasksPerform.setGroupId(htTasks.getCuGroupId() != null ? Long.valueOf(htTasks.getCuGroupId()) : null);
                    htTasksPerform.setStrategyId(htTasks.getStrategyId());
                    htTasksPerform.setInstructionId(htStrategyPerform.getInstructionId());
                    htTasksPerform.setInstructionType(htStrategyPerform.getType());
                    try {
                        htTasksPerform.setInstruction(Integer.parseInt(htStrategyPerform.getInstruction()));
                    } catch (NumberFormatException e) {
                        htTasksPerform.setInstruction(0);
                    }
                    htTasksPerform.setNumber(commandIndex);
                    htTasksPerform.setIntervall(htStrategyPerform.getIntervall());
                    htTasksPerform.setUnit(htStrategyPerform.getUnit());
                    htTasksPerform.setDuration(htStrategyPerform.getDuration());
                    htTasksPerform.setOrgId(htScopeDtu.getOrgId());
                    htTasksPerform.setConcentratorCode(htScopeDtu.getConcentratorCode());
                    htTasksPerform.setMeterNum("AAAAAAAAAAAAAAAA");
                    htTasksPerform.setMeterArcCode(htScopeDtu.getMeterArcCode());
                    htTasksPerform.setStatus(ThermalTaskConstants.PERFORM_PENDING);
                    htTasksPerform.setMeterId(null);
                    htTasksPerform.setInstructionStatus(ThermalTaskConstants.PERFORM_PENDING);
                    htTasksPerform.setTasksId(htTasks.getId());
                    htTasksPerform.setOrderr(htStrategyPerform.getOrderr());
                    htTasksPerform.setForeStart(htStrategyPerform.getXunhuan());
                    htTasksPerform.setCreateTime(new Date());
                    htTasksPerform.setCreateBy(htTasks.getCreateBy());
                    htTasksPerform.setDtuNum(htScopeDtu.getDtuNum());
                    htTasksPerform.setChanNum(htScopeDtu.getChanNums());
                    htTasksPerform.setCommandIndex(commandIndex);

                    // 设置分组数据
                    List<GroupData> groupDataList = createGroupDatas(htScopeDtu.getChanNums(), pjhsMap, htStrategy);
                    if (!groupDataList.isEmpty()) {
                        // 这里可以根据需要将 groupDataList 转换为 JSON 字符串或其他格式存储
                        // 如果 HtTasksPerform 有 groupData 字段，可以在这里设置
                        htTasksPerformList.add(htTasksPerform);
                    } else {
                        log.error("未获取到指令相关调控数据，跳过指令生成！");
                    }
                }

                if (!htTasksPerformList.isEmpty()) {
                    // 下发控制指令
                    baseMapper.insertTasksPerformBatch(htTasksPerformList);
                    // 更新执行次数
                    baseMapper.updateHtTasks(htTasks.getId());
                    // 插入指令表每个阀门信息（不下发，仅作为界面查询展示用）
                    baseMapper.insertHtTasksPerformByRadio(htTasks.getId(), commandIndex);
                } else {
                    log.error("无可下发指令，跳过调控次数累计！");
                }

                result = true;
            }
        }

        return result;
    }

    /**
     * 检查任务是否满足终止条件。满足任一条件则自动停止任务：
     * 1. 调控天数已超出设定天数
     * 2. 平衡率已达到或超过达标值
     * 3. 调控次数已达上限
     *
     * @param task 调控任务实体
     * @return true 表示任务应终止，false 表示继续执行
     */
    private boolean checkTerminationConditions(HtTasks task) {
        if (task == null) return true;
        Long taskId = task.getId();

        // ---- 条件1: 调控天数检查 ----
        if (task.getDays() != null && task.getDays() > 0 && task.getLastTime() != null) {
            LocalDate lastTimeDate = task.getLastTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
            long elapsedDays = ChronoUnit.DAYS.between(lastTimeDate, LocalDate.now());
            if (elapsedDays >= task.getDays()) {
                stopTask(taskId, "调控天数已达上限（已调控 " + elapsedDays + " 天，上限 " + task.getDays() + " 天）");
                return true;
            }
        }

        // ---- 条件2: 平衡率达标检查 ----
        if (task.getStandard() != null && task.getStandard() > 0) {
            double currentBalanceRate = baseMapper.queryBalanceRate(taskId);
            if (currentBalanceRate >= task.getStandard()) {
                stopTask(taskId, "平衡率已达标（当前 " + currentBalanceRate + "%，目标 " + task.getStandard() + "%）");
                return true;
            }
        }

        // ---- 条件3: 调控次数上限检查 ----
        if (task.getNums() != null && task.getNums() > 0 && task.getNumber() != null) {
            if (task.getNumber() >= task.getNums()) {
                stopTask(taskId, "调控次数已达上限（已调控 " + task.getNumber() + " 次，上限 " + task.getNums() + " 次）");
                return true;
            }
        }

        return false;
    }

    /**
     * 停止调控任务：更新状态为停止、移除 Quartz 调度、记录结束时间
     *
     * @param taskId 任务ID
     * @param reason 停止原因（用于日志）
     */
    private void stopTask(Long taskId, String reason) {
        // 更新状态为停止
        lambdaUpdate().eq(HtTasks::getId, taskId)
            .set(HtTasks::getStatus, ThermalTaskConstants.TASK_STOPPED)
            .set(HtTasks::getEndTime, new Date())
            .update();

        // 移除 Quartz 调度任务
        try {
            jobManager.deleteJob(taskId);
        } catch (Exception e) {
            log.warn("停止任务 {} 时删除 Quartz 调度失败: {}", taskId, e.getMessage());
        }

        log.info("任务 {} 已自动停止: {}", taskId, reason);
    }

    /**
     * 创建分组数据
     */
    private List<GroupData> createGroupDatas(String chanNums, Map<String, Double> pjhsMap, HtStrategy htStrategy) {
        List<GroupData> result = new ArrayList<>();
        if (chanNums == null || chanNums.isEmpty()) {
            return result;
        }
        if (pjhsMap == null || pjhsMap.isEmpty()) {
            return result;
        }
        if (htStrategy == null) {
            return result;
        }

        String[] chanNumsArray = chanNums.split(",");
        for (String chanNum : chanNumsArray) {
            if (!pjhsMap.containsKey(chanNum)) {
                continue;
            }

            GroupData groupData = new GroupData();
            groupData.setChanNum(chanNum);
            groupData.setOutTempPJ((int) Math.round(pjhsMap.get(chanNum) * 100));
            groupData.setStride(htStrategy.getStride());
            groupData.setOutTempDeviation(htStrategy.getOutTempDeviation());
            result.add(groupData);
        }

        return result;
    }
}
