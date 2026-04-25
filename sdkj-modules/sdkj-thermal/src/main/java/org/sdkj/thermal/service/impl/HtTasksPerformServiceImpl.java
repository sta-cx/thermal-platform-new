package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;
import org.sdkj.thermal.mapper.HtTasksPerformMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 调控执行记录服务实现
 */
@Service
@RequiredArgsConstructor
public class HtTasksPerformServiceImpl extends ServiceImpl<HtTasksPerformMapper, HtTasksPerform> implements IHtTasksPerformService {

    private final HtTasksPerformMapper baseMapper;

    @Override
    public TableDataInfo<HtTasksPerformVo> selectPageList(LambdaQueryWrapper<HtTasksPerform> lqw, PageQuery pageQuery) {
        Page<HtTasksPerformVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtTasksPerformVo> selectByMeterId(String meterId) {
        return baseMapper.selectByMeterId(meterId);
    }

    @Override
    public List<HtTasksPerformVo> selectByMeterIdDetail(String meterId) {
        return baseMapper.selectByMeterIdDetail(meterId);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean updateInstructionStatus(String performId, Integer status) {
        HtTasksPerform perform = new HtTasksPerform();
        perform.setId(performId);
        perform.setStatus(status);
        return updateById(perform);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateInstructionStatus(List<String> performIds, Integer status) {
        return lambdaUpdate()
            .in(HtTasksPerform::getId, performIds)
            .set(HtTasksPerform::getStatus, status)
            .update();
    }

    @Override
    public List<HtTasksPerformVo> selectPendingByTaskId(String taskId) {
        return lambdaQuery()
            .eq(HtTasksPerform::getTasksId, taskId)
            .in(HtTasksPerform::getStatus, 0, 3)
            .orderByAsc(HtTasksPerform::getOrderr)
            .list()
            .stream()
            .map(perform -> {
                HtTasksPerformVo vo = new HtTasksPerformVo();
                org.springframework.beans.BeanUtils.copyProperties(perform, vo);
                return vo;
            })
            .toList();
    }

    @Override
    public Map<String, Object> selectPerformStats(String taskId) {
        long total = lambdaQuery().eq(HtTasksPerform::getTasksId, taskId).count();
        long pending = lambdaQuery().eq(HtTasksPerform::getTasksId, taskId)
            .eq(HtTasksPerform::getStatus, 0).count();
        long sending = lambdaQuery().eq(HtTasksPerform::getTasksId, taskId)
            .eq(HtTasksPerform::getStatus, 1).count();
        long success = lambdaQuery().eq(HtTasksPerform::getTasksId, taskId)
            .eq(HtTasksPerform::getStatus, 2).count();
        long failed = lambdaQuery().eq(HtTasksPerform::getTasksId, taskId)
            .eq(HtTasksPerform::getStatus, 3).count();

        double rate = total > 0 ? Math.round(success * 10000.0 / total) / 100.0 : 0.0;
        return Map.of(
            "total", total,
            "pending", pending,
            "sending", sending,
            "success", success,
            "failed", failed,
            "successRate", rate
        );
    }

    @Override
    public Map<String, Object> selectGlobalStatusSummary() {
        long total = lambdaQuery().count();
        long pending = lambdaQuery().eq(HtTasksPerform::getStatus, 0).count();
        long sending = lambdaQuery().eq(HtTasksPerform::getStatus, 1).count();
        long success = lambdaQuery().eq(HtTasksPerform::getStatus, 2).count();
        long failed = lambdaQuery().eq(HtTasksPerform::getStatus, 3).count();

        double rate = total > 0 ? Math.round(success * 10000.0 / total) / 100.0 : 0.0;
        return Map.of(
            "total", total,
            "pending", pending,
            "sending", sending,
            "success", success,
            "failed", failed,
            "successRate", rate
        );
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean batchCreateValveControlTasks(List<ValveArchiveInfo> archives, String orgId, String companyId, int instruction) {
        List<HtTasksPerform> tasks = archives.stream().map(info -> {
            HtTasksPerform task = new HtTasksPerform();
            task.setId(UUID.randomUUID().toString().replace("-", ""));
            task.setInstructionType(3);
            task.setInstruction(instruction);
            task.setNumber(0);
            task.setOrgId(orgId);
            task.setCompanyId(companyId);
            task.setDeviceId(info.deviceId());
            task.setMeterArcCode(info.meterArcCode());
            task.setMeterId(info.meterId());
            task.setMeterNum(info.meterNum());
            task.setStatus(0);
            task.setInstructionStatus(0);
            task.setImei(info.imei());
            task.setConcentratorCode(info.concentratorCode());
            task.setDtuNum(info.dtuNum());
            task.setChanNum(info.chanNum());
            return task;
        }).toList();
        return saveBatch(tasks);
    }
}
