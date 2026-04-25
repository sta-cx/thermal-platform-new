package org.sdkj.thermal.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;
import org.sdkj.thermal.mapper.HtTasksPerformMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 调控执行记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HtTasksPerformServiceImpl extends ServiceImpl<HtTasksPerformMapper, HtTasksPerform> implements IHtTasksPerformService {

    private final HtTasksPerformMapper baseMapper;

    @Value("${collect.ipPort:}")
    private String ipPort;
    @Value("${collect.username:}")
    private String username;
    @Value("${collect.password:}")
    private String password;

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
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInstructionStatus(String performId, Integer status) {
        HtTasksPerform perform = new HtTasksPerform();
        perform.setId(performId);
        perform.setStatus(status);
        return updateById(perform);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatchTasks(List<HtTasksPerform> htTasksPerformList) {
        return saveBatch(htTasksPerformList);
    }

    @Override
    public void executeValveControlTasks(List<HtTasksPerform> htTasksPerformList) throws Exception {
        // Phase 6: 调用中国电信IoT采集平台接口发送阀门调控指令
        log.info("执行阀门调控指令（Phase 6 IoT集成），数量：{}", htTasksPerformList.size());
    }

    @Override
    public void executeHeatMeterTasks(List<HtTasksPerform> htTasksPerformList) throws Exception {
        // Phase 6: 调用中国电信IoT采集平台接口发送热表调控指令
        log.info("执行热表调控指令（Phase 6 IoT集成），数量：{}", htTasksPerformList.size());
    }

    @Override
    public void executeDtuControlTasks(List<HtTasksPerform> htTasksPerformList) throws Exception {
        // Phase 6: 调用中国电信IoT采集平台接口发送DTU调控指令
        log.info("执行DTU调控指令（Phase 6 IoT集成），数量：{}", htTasksPerformList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateValveScopeStatusList(List<HtTasksPerform> htTasksPerformList) {
        Date now = new Date();
        for (HtTasksPerform perform : htTasksPerformList) {
            perform.setValveOpen(perform.getValveOpen());
            perform.setUpdateTime(now);
            updateById(perform);
        }
        log.info("更新阀门开度状态完成，数量：{}", htTasksPerformList.size());
    }

    @Override
    public void insertValveOCLog(List<HtTasksPerform> htTasksPerformList) {
        // Phase 6: 创建 pr_valve_oc_log 表后实现阀门开关日志记录
        log.info("阀门开关日志（Phase 6 待实现），数量：{}", htTasksPerformList.size());
    }
}
