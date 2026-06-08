package org.sdkj.thermal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
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
import org.sdkj.thermal.domain.PrValveOperationLog;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.vo.HtTasksPerformVo;
import org.sdkj.thermal.mapper.HtTasksPerformMapper;
import org.sdkj.thermal.mapper.PrValveOperationLogMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final PrValveOperationLogMapper valveOperationLogMapper;

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
            // NB 上报周期设定（instructionType=6，对齐旧系统 getNonExecutionNew1）
            if (task.getInstructionType() != null && task.getInstructionType() == 6) {
                item.set("reportInterval", task.getIntervall())
                    .set("intervalUnit", task.getUnit())
                    .set("validTime", task.getDuration());
            }
            // NB 采集周期设定（instructionType=9）
            if (task.getInstructionType() != null && task.getInstructionType() == 9) {
                item.set("collectInterval", task.getIntervall())
                    .set("collectUnit", task.getUnit())
                    .set("collectValidTime", task.getDuration());
            }
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
    @Transactional(rollbackFor = Exception.class)
    public void insertValveOCLog(List<HtTasksPerform> htTasksPerformList) {
        if (CollUtil.isEmpty(htTasksPerformList)) {
            return;
        }
        // 阀门操作审计日志：复刻旧系统 insertValveOCStatusLog → pr_use_card_log（阀门视角）。
        // 写入后由 PrValveOperationLogController(/list, isNotNull(valveStatus)) 展示；
        // 覆盖缴费自动开阀 Job / 档案 controlValve / 设阀门组号三条下发路径。
        Date now = new Date();
        for (HtTasksPerform task : htTasksPerformList) {
            PrValveOperationLog record = new PrValveOperationLog();
            record.setMeterId(task.getMeterId());
            record.setMeterNum(task.getMeterNum());
            // valveStatus 取指令值；组号设置等无 instruction 的场景回退取 number，保证日志页可见（isNotNull 过滤）。
            record.setValveStatus(task.getInstruction() != null ? task.getInstruction() : task.getNumber());
            record.setOrgId(task.getOrgId());
            record.setOperationTime(now);
            if (task.getCreateBy() != null) {
                record.setOperatorId(String.valueOf(task.getCreateBy()));
            }
            record.setContent(buildValveOpDesc(task));
            // 逐条 insert 而非 Db.saveBatch，确保动态数据源正确路由到租户库
            valveOperationLogMapper.insert(record);
        }
        log.info("阀门操作日志写入完成，数量：{}", htTasksPerformList.size());
    }

    /**
     * 阀门操作描述（写入 pr_use_card_log.content，便于审计可读）。
     */
    private String buildValveOpDesc(HtTasksPerform task) {
        Integer type = task.getInstructionType();
        Integer val = task.getInstruction();
        if (type != null) {
            switch (type) {
                case 1:
                    return "开阀";
                case 2:
                    return "关阀";
                case 3:
                    return "设置开度" + (val != null ? " " + val : "");
                case 5:
                    return "制动";
                case 6:
                    return "NB上报周期设定";
                case 9:
                    return "采集周期设定";
                default:
                    break;
            }
        }
        if (val == null && task.getNumber() != null) {
            return "设置阀门组号 " + task.getNumber();
        }
        return "阀门指令" + (type != null ? " type=" + type : "") + (val != null ? " value=" + val : "");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executePendingCommands(String orgId) {
        if (StrUtil.isBlank(orgId)) {
            log.warn("executePendingCommands: orgId 为空，跳过");
            return;
        }
        // 1. 捞取该小区待发指令（status=0 待执行）。
        //    覆盖调控引擎(insertHtTasksPerform 写 status='0')、批量阀控、蓝牙、云谷等"只写不发"入口；
        //    缴费控阀/状态查询 Job 自产自销时用 status=1(PERFORM_SENT)+立即下发，不会被重复捞取。
        List<HtTasksPerform> pending = lambdaQuery()
            .eq(HtTasksPerform::getOrgId, orgId)
            .eq(HtTasksPerform::getStatus, ThermalTaskConstants.PERFORM_PENDING)
            .list();
        if (CollUtil.isEmpty(pending)) {
            return;
        }

        // 2. 按 meterArcCode 分组（复刻旧系统 getNonExecutionNew：热表 vs 阀门两路）。
        List<HtTasksPerform> heatMeterTasks = new ArrayList<>();
        List<HtTasksPerform> valveTasks = new ArrayList<>();
        for (HtTasksPerform task : pending) {
            if (isHeatMeter(task.getMeterArcCode())) {
                heatMeterTasks.add(task);
            } else {
                valveTasks.add(task);
            }
        }

        // 3. 分组下发（executeXxxTasks 内部完成 sendControlMsg + 状态回写 status=2/3 + sendTime）。
        //    每组独立 try/catch，单组失败不影响另一组。
        if (CollUtil.isNotEmpty(valveTasks)) {
            try {
                executeValveControlTasks(valveTasks);
            } catch (Exception e) {
                log.error("待发阀门指令下发失败: orgId={}, 数量={}", orgId, valveTasks.size(), e);
            }
        }
        if (CollUtil.isNotEmpty(heatMeterTasks)) {
            try {
                executeHeatMeterTasks(heatMeterTasks);
            } catch (Exception e) {
                log.error("待发热表指令下发失败: orgId={}, 数量={}", orgId, heatMeterTasks.size(), e);
            }
        }
        log.info("待发指令下发完成: orgId={}, 阀门={}, 热表={}", orgId, valveTasks.size(), heatMeterTasks.size());
    }

    /**
     * 热表类档案号判断（meter_arc_code，复刻旧系统 getNonExecutionNew 分组规则）。
     */
    private boolean isHeatMeter(String meterArcCode) {
        return "040303".equals(meterArcCode) || "04030301".equals(meterArcCode)
            || "090301".equals(meterArcCode) || "09030101".equals(meterArcCode);
    }
}
