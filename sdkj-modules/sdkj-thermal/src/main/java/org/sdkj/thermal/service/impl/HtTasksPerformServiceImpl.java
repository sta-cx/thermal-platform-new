package org.sdkj.thermal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.constant.ThermalTaskConstants;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;
import org.sdkj.thermal.mapper.HtTasksPerformMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.sdkj.thermal.utils.CollectPlatformUtil;
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
public class HtTasksPerformServiceImpl extends OrgScopedServiceImpl<HtTasksPerformMapper, HtTasksPerform> implements IHtTasksPerformService {

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
    public boolean updateInstructionStatus(Long performId, Integer status) {
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
    public boolean batchCreateValveControlTasks(List<ValveArchiveInfo> archives, String orgId, int instruction) {
        List<HtTasksPerform> tasks = archives.stream().map(info -> {
            HtTasksPerform task = new HtTasksPerform();
            task.setInstructionType(3);
            task.setInstruction(instruction);
            task.setNumber(0);
            task.setOrgId(orgId);
            task.setDeviceId(info.deviceId());
            task.setMeterArcCode(info.meterArcCode());
            task.setMeterId(info.meterId());
            task.setMeterNum(info.meterNum());
            task.setStatus(ThermalTaskConstants.PERFORM_PENDING);
            task.setInstructionStatus(ThermalTaskConstants.PERFORM_PENDING);
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
    @Transactional(rollbackFor = Exception.class)
    public void executeValveControlTasks(List<HtTasksPerform> htTasksPerformList) throws Exception {
        if (CollUtil.isEmpty(htTasksPerformList)) {
            return;
        }
        JSONArray dataArray = new JSONArray();
        for (HtTasksPerform task : htTasksPerformList) {
            JSONObject item = new JSONObject();
            item.set("meterNum", task.getMeterNum())
                .set("dtuNum", task.getDtuNum())
                .set("conNum", task.getConcentratorCode())
                .set("chanNum", task.getChanNum())
                .set("command", task.getInstructionType())
                .set("guid", task.getId())
                .set("meterInfo", task.getMeterArcCode())
                .set("meterCode", task.getMeterArcCode())
                .set("valveStatus", task.getInstruction());
            dataArray.add(item);
        }
        JSONObject payload = new JSONObject();
        payload.set("type", 1);
        payload.set("data", dataArray);

        String result = CollectPlatformUtil.sendControlMsg(payload, ipPort, username, password);
        Date now = new Date();
        for (HtTasksPerform task : htTasksPerformList) {
            task.setStatus("1".equals(result) ? 2 : 3);
            task.setSendTime(now);
        }
        updateBatchById(htTasksPerformList);
        log.info("阀门调控指令下发完成，数量：{}，结果：{}", htTasksPerformList.size(), result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeHeatMeterTasks(List<HtTasksPerform> htTasksPerformList) throws Exception {
        if (CollUtil.isEmpty(htTasksPerformList)) {
            return;
        }
        JSONArray dataArray = new JSONArray();
        for (HtTasksPerform task : htTasksPerformList) {
            JSONObject item = new JSONObject();
            item.set("meterNum", task.getMeterNum())
                .set("dtuNum", task.getDtuNum())
                .set("conNum", task.getConcentratorCode())
                .set("chanNum", task.getChanNum())
                .set("command", task.getInstructionType())
                .set("guid", task.getId())
                .set("meterInfo", task.getMeterArcCode())
                .set("meterCode", task.getMeterArcCode());
            dataArray.add(item);
        }
        JSONObject payload = new JSONObject();
        payload.set("type", 2);
        payload.set("data", dataArray);

        String result = CollectPlatformUtil.sendControlMsg(payload, ipPort, username, password);
        Date now = new Date();
        for (HtTasksPerform task : htTasksPerformList) {
            task.setStatus("1".equals(result) ? 2 : 3);
            task.setSendTime(now);
        }
        updateBatchById(htTasksPerformList);
        log.info("热表调控指令下发完成，数量：{}，结果：{}", htTasksPerformList.size(), result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeDtuControlTasks(List<HtTasksPerform> htTasksPerformList) throws Exception {
        if (CollUtil.isEmpty(htTasksPerformList)) {
            return;
        }
        JSONArray dataArray = new JSONArray();
        for (HtTasksPerform task : htTasksPerformList) {
            JSONObject item = new JSONObject();
            item.set("meterNum", task.getMeterNum())
                .set("dtuNum", task.getDtuNum())
                .set("conNum", task.getConcentratorCode())
                .set("chanNum", task.getChanNum())
                .set("command", task.getInstructionType())
                .set("guid", task.getId())
                .set("meterInfo", task.getMeterArcCode())
                .set("meterCode", task.getMeterArcCode());
            if (task.getIntervall() != null) {
                item.set("reportInterval", task.getIntervall());
            }
            if (task.getUnit() != null) {
                item.set("intervalUnit", task.getUnit());
            }
            if (task.getDuration() != null) {
                item.set("validTime", task.getDuration());
            }
            dataArray.add(item);
        }
        JSONObject payload = new JSONObject();
        payload.set("type", 5);
        payload.set("data", dataArray);

        String result = CollectPlatformUtil.sendControlMsg(payload, ipPort, username, password);
        Date now = new Date();
        for (HtTasksPerform task : htTasksPerformList) {
            task.setStatus("1".equals(result) ? 2 : 3);
            task.setSendTime(now);
        }
        updateBatchById(htTasksPerformList);
        log.info("DTU调控指令下发完成，数量：{}，结果：{}", htTasksPerformList.size(), result);
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

    @Override
    public void executePendingCommands(String orgId) {
        // 等外部 IoT 平台(DTU/NB/MBus)API 就绪后从旧 getNonExecutionNew 移植:
        // 1. Query all pending (status=1, instructionStatus=0) HtTasksPerform records
        // 2. Group by concentrator/device
        // 3. Send via DTU/NB/MBus depending on device type
        // 4. Update instructionStatus after send
        log.warn("executePendingCommands not yet implemented for orgId={}", orgId);
    }
}
