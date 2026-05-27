package org.sdkj.thermal.service.impl;

import org.sdkj.common.core.exception.ServiceException;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.constant.ThermalTaskConstants;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.*;
import org.sdkj.thermal.domain.dto.PrHeatVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;

import java.util.Arrays;
import org.sdkj.thermal.mapper.*;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatArchiveService;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 房屋热表配表 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrHeatArchiveServiceImpl extends ServiceImpl<PrHeatArchiveMapper, PrHeatArchive> implements IPrHeatArchiveService {

    private final PrHeatArchiveMapper baseMapper;
    private final IHtTasksPerformService htTasksPerformService;
    private final IPrOptionsHeatService prOptionsHeatService;
    private final PrTransactionRecordMapper prTransactionRecordMapper;
    private final PrTransactionRecordSubMapper prTransactionRecordSubMapper;

    @Override
    public PrHeatArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatArchiveVo> selectPageList(String orgId, String buildingId,
                                                          String unitCode, String search, String archiveId,
                                                          PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        // buildingId and unitCode are not direct fields on PrHeatArchive;
        // they are used for frontend filtering and may be resolved through house/building relations
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(StringUtils.isNotBlank(archiveId), PrHeatArchive::getArchiveId, archiveId);
        // isDeleted is handled by @TableLogic automatically, no need to filter
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<PrHeatArchiveVo> queryCompanyHeat() {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        // isDeleted handled by @TableLogic
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatArchive entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> ids) {
        return super.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replaceHeatMeter(PrHeatArchive newHeatArchive) {
        // 获取旧表信息
        PrHeatArchive oldHeatArchive = getById(newHeatArchive.getId());
        if (oldHeatArchive == null) {
            throw new ServiceException("旧表信息不存在");
        }

        Long creater = LoginHelper.getUserId();
        Date date = new Date();
        BigDecimal zero = BigDecimal.ZERO;

        // 更新旧表信息
        oldHeatArchive.setUpdateBy(creater);
        oldHeatArchive.setCurrentReading(newHeatArchive.getCurrentReading());
        oldHeatArchive.setTotalUsed(newHeatArchive.getTotalUsed());
        oldHeatArchive.setTotalMoney(newHeatArchive.getTotalMoney());
        oldHeatArchive.setIsChanged(1);

        boolean type = false;
        // 当前余额
        if (newHeatArchive.getCurrentBalance().compareTo(zero) >= 0) {
            oldHeatArchive.setCurrentBalance(zero);
            newHeatArchive.setTotalRecharge(newHeatArchive.getCurrentBalance());
            type = true;
        } else {
            oldHeatArchive.setCurrentBalance(newHeatArchive.getCurrentBalance());
            newHeatArchive.setTotalRecharge(zero);
            newHeatArchive.setCurrentBalance(zero);
        }

        // 更新旧表
        updateById(oldHeatArchive);

        // 新表信息
        newHeatArchive.setCurrentReading(zero);
        newHeatArchive.setTotalMoney(zero);
        newHeatArchive.setTotalUsed(zero);
        newHeatArchive.setCreateBy(creater);
        newHeatArchive.setCreateTime(date);

        // 保存新表
        save(newHeatArchive);

        // 余额转移生成交易记录
        if (type && newHeatArchive.getCurrentBalance() != null
                && newHeatArchive.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
            String serialNum = DateUtil.format(date, "yyyyMMddHHmmss")
                + String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
            PrTransactionRecord record = new PrTransactionRecord();
            record.setSerialNum(serialNum);
            record.setTransactionType(3);
            record.setPaymentType(4);
            record.setAmount(newHeatArchive.getCurrentBalance());
            record.setPaidAmount(newHeatArchive.getCurrentBalance());
            record.setStatus(0);
            record.setHouseId(oldHeatArchive.getHouseId());
            record.setOrgId(oldHeatArchive.getOrgId());
            record.setOperatorId(String.valueOf(creater));
            record.setTransactionTime(date);
            record.setNotes("换表余额转移");
            record.setCreateBy(creater);
            record.setCreateTime(date);
            prTransactionRecordMapper.insert(record);

            PrTransactionRecordSub sub = new PrTransactionRecordSub();
            sub.setMainId(record.getId());
            sub.setAmount(newHeatArchive.getCurrentBalance());
            sub.setBalanceBefore(BigDecimal.ZERO);
            sub.setBalanceAfter(newHeatArchive.getCurrentBalance());
            sub.setHouseId(oldHeatArchive.getHouseId());
            sub.setCreateBy(creater);
            sub.setCreateTime(date);
            prTransactionRecordSubMapper.insert(sub);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object recharge(PrHeatArchive heatArchive, String paymentMethod) {
        Long userId = LoginHelper.getUserId();
        Date now = new Date();

        PrHeatArchive archive = getById(heatArchive.getId());
        if (archive == null) {
            throw new ServiceException("配表记录不存在");
        }

        String serialNum = DateUtil.format(now, "yyyyMMddHHmmss")
            + String.format("%06d", new Random().nextInt(1000000));

        BigDecimal rechargeAmount = heatArchive.getCurrentBalance() != null
            ? heatArchive.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal currentBalance = (archive.getCurrentBalance() != null ? archive.getCurrentBalance() : BigDecimal.ZERO)
            .add(rechargeAmount);

        archive.setTotalRecharge((archive.getTotalRecharge() != null ? archive.getTotalRecharge() : BigDecimal.ZERO)
            .add(rechargeAmount));
        archive.setCurrentBalance(currentBalance);
        archive.setUpdateBy(userId);
        updateById(archive);

        PrTransactionRecord record = new PrTransactionRecord();
        record.setSerialNum(serialNum);
        record.setTransactionType(1);
        record.setPaymentType(Integer.parseInt(paymentMethod != null ? paymentMethod : "1"));
        record.setAmount(rechargeAmount);
        record.setPaidAmount(rechargeAmount);
        record.setStatus(0);
        record.setHouseId(archive.getHouseId());
        record.setOrgId(archive.getOrgId());
        record.setOperatorId(String.valueOf(userId));
        record.setTransactionTime(now);
        record.setCreateBy(userId);
        record.setCreateTime(now);
        prTransactionRecordMapper.insert(record);

        PrTransactionRecordSub sub = new PrTransactionRecordSub();
        sub.setMainId(record.getId());
        sub.setAmount(rechargeAmount);
        sub.setBalanceBefore(archive.getCurrentBalance().subtract(rechargeAmount));
        sub.setBalanceAfter(currentBalance);
        sub.setHouseId(archive.getHouseId());
        sub.setCreateBy(userId);
        sub.setCreateTime(now);
        prTransactionRecordSubMapper.insert(sub);

        log.info("仪表充值完成，表号：{}，金额：{}，流水号：{}", archive.getMeterNum(), rechargeAmount, serialNum);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean manualControl(List<PrHeatVo> prHeatVoList, boolean switch1, Integer scale, String adjust,
                                  String orgId, String intervall, String unit, String duration) {
        PrOptionsHeat prOptionsHeat = prOptionsHeatService.getDataById(orgId, "2");
        if (prOptionsHeat == null) {
            throw new ServiceException("未找到调控配置");
        }

        Integer min = prOptionsHeat.getControlMin();
        Integer max = prOptionsHeat.getControlMax();
        Long create = LoginHelper.getUserId();

        List<HtTasksPerform> htTasksPerformList = new ArrayList<>();
        Date now = new Date();

        // 按设备类型分组
        List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives = new ArrayList<>();
        List<org.sdkj.thermal.domain.PrHeatCommandValveArchive> commandValveArchives = new ArrayList<>();
        List<org.sdkj.thermal.domain.PrHeatHotArchive> hotArchives = new ArrayList<>();
        List<org.sdkj.thermal.domain.PrHeatDtuArchive> dtuArchives = new ArrayList<>();

        for (PrHeatVo vo : prHeatVoList) {
            if ("7".equals(adjust)) {
                if (vo.getPrHeatHotArchive() != null) {
                    PrHeatHotArchive entity = MapstructUtils.convert(vo.getPrHeatHotArchive(), PrHeatHotArchive.class);
                    hotArchives.add(entity);
                }
            } else {
                if (vo.getPrHeatValveArchive() != null) {
                    PrHeatValveArchive entity = MapstructUtils.convert(vo.getPrHeatValveArchive(), PrHeatValveArchive.class);
                    valveArchives.add(entity);
                }
                if (vo.getPrHeatCommandValveArchive() != null) {
                    PrHeatCommandValveArchive entity = MapstructUtils.convert(vo.getPrHeatCommandValveArchive(), PrHeatCommandValveArchive.class);
                    commandValveArchives.add(entity);
                }
                if (vo.getPrHeatDtuArchive() != null) {
                    PrHeatDtuArchive entity = MapstructUtils.convert(vo.getPrHeatDtuArchive(), PrHeatDtuArchive.class);
                    dtuArchives.add(entity);
                }
            }
        }

        // adjust=1: 开度调节（加档/减档）
        if ("1".equals(adjust)) {
            if (switch1) { // 调大
                processValveControl(valveArchives, commandValveArchives, orgId, min, max, create, now,
                    prOptionsHeat, htTasksPerformList, true, scale);
            } else { // 调小
                processValveControl(valveArchives, commandValveArchives, orgId, min, max, create, now,
                    prOptionsHeat, htTasksPerformList, false, scale);
            }
        }
        // adjust=2: 开关控制
        else if ("2".equals(adjust)) {
            if (switch1) { // 开阀
                processSwitchControl(valveArchives, commandValveArchives, orgId, create, now,
                    prOptionsHeat, htTasksPerformList, true);
            } else { // 关阀
                processSwitchControl(valveArchives, commandValveArchives, orgId, create, now,
                    prOptionsHeat, htTasksPerformList, false);
            }
        }
        // adjust=3: 开度设定
        else if ("3".equals(adjust)) {
            processSetOpening(valveArchives, commandValveArchives, orgId, create, now,
                prOptionsHeat, htTasksPerformList, scale);
        }
        // adjust=4: 状态查询
        else if ("4".equals(adjust)) {
            processStatusQuery(valveArchives, commandValveArchives, orgId, create, now,
                prOptionsHeat, htTasksPerformList);
        }
        // adjust=5: 制动
        else if ("5".equals(adjust)) {
            processBrake(valveArchives, commandValveArchives, orgId, create, now,
                prOptionsHeat, htTasksPerformList);
        }
        // adjust=6: 上报周期调整
        else if ("6".equals(adjust)) {
            processReportCycle(valveArchives, commandValveArchives, orgId, create, now,
                prOptionsHeat, htTasksPerformList, intervall, unit, duration);
        }
        // adjust=7: 热表巡测
        else if ("7".equals(adjust)) {
            processHeatMeterQuery(hotArchives, orgId, create, now, prOptionsHeat, htTasksPerformList);
        }
        // adjust=28-1: 修改设备信道（阀门）
        else if ("28-1".equals(adjust)) {
            processModifyChannel(valveArchives, orgId, create, now, prOptionsHeat, htTasksPerformList, scale);
        }
        // adjust=28-2: 修改设备信道（DTU）
        else if ("28-2".equals(adjust)) {
            processModifyDtuChannel(dtuArchives, orgId, create, now, prOptionsHeat, htTasksPerformList, scale);
        }
        // adjust=27: 读取信道
        else if ("27".equals(adjust)) {
            processReadChannel(dtuArchives, orgId, create, now, prOptionsHeat, htTasksPerformList, scale);
        }
        // adjust=29: 打开网关
        else if ("29".equals(adjust)) {
            processOpenGateway(dtuArchives, orgId, create, now, prOptionsHeat, htTasksPerformList, scale);
        }
        // adjust=30: 关闭网关
        else if ("30".equals(adjust)) {
            processCloseGateway(dtuArchives, orgId, create, now, prOptionsHeat, htTasksPerformList, scale);
        }
        // adjust=51: 特殊制动
        else if ("51".equals(adjust)) {
            processSpecialBrake(valveArchives, orgId, create, now, prOptionsHeat, htTasksPerformList);
        }

        if (!htTasksPerformList.isEmpty()) {
            htTasksPerformService.saveBatchTasks(htTasksPerformList);

            try {
                if ("7".equals(adjust)) {
                    htTasksPerformService.executeHeatMeterTasks(htTasksPerformList);
                } else if ("28-2".equals(adjust) || "27".equals(adjust) || "29".equals(adjust) || "30".equals(adjust)) {
                    htTasksPerformService.executeDtuControlTasks(htTasksPerformList);
                } else {
                    htTasksPerformService.executeValveControlTasks(htTasksPerformList);
                    htTasksPerformService.insertValveOCLog(htTasksPerformList);
                }
            } catch (Exception e) {
                log.error("执行调控指令失败, adjust={}", adjust, e);
                return false;
            }
        }

        return true;
    }

    // ========== 私有辅助方法 ==========

    private void processValveControl(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                                     List<org.sdkj.thermal.domain.PrHeatCommandValveArchive> commandValveArchives,
                                     String orgId, Integer min, Integer max, Long create, Date now,
                                     PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                     boolean increase, Integer scale) {
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(3);
            int actualStatus = archive.getActualStatus() != null ? archive.getActualStatus() : 0;
            if (increase) {
                task.setInstruction(actualStatus + scale >= max ? max : actualStatus + scale);
            } else {
                task.setInstruction(actualStatus - scale <= min ? min : actualStatus - scale);
            }
            setValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
        for (org.sdkj.thermal.domain.PrHeatCommandValveArchive archive : commandValveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(3);
            int actualStatus = archive.getActualStatus() != null ? archive.getActualStatus() : 0;
            if (increase) {
                task.setInstruction(actualStatus + scale >= max ? max : actualStatus + scale);
            } else {
                task.setInstruction(actualStatus - scale <= min ? min : actualStatus - scale);
            }
            setCommandValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
    }

    private void processSwitchControl(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                                      List<org.sdkj.thermal.domain.PrHeatCommandValveArchive> commandValveArchives,
                                      String orgId, Long create, Date now,
                                      PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                      boolean open) {
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(open ? 1 : 2);
            task.setInstruction(open ? 100 : 0);
            setValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
        for (org.sdkj.thermal.domain.PrHeatCommandValveArchive archive : commandValveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(open ? 1 : 2);
            task.setInstruction(open ? 100 : 0);
            setCommandValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
    }

    private void processSetOpening(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                                   List<org.sdkj.thermal.domain.PrHeatCommandValveArchive> commandValveArchives,
                                   String orgId, Long create, Date now,
                                   PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                   Integer scale) {
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(3);
            task.setInstruction(scale);
            setValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
        for (org.sdkj.thermal.domain.PrHeatCommandValveArchive archive : commandValveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(3);
            task.setInstruction(scale);
            setCommandValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
    }

    private void processStatusQuery(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                                    List<org.sdkj.thermal.domain.PrHeatCommandValveArchive> commandValveArchives,
                                    String orgId, Long create, Date now,
                                    PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList) {
        String[] supportedCodes = {"04310401", "04310402", "04310502", "04310501", "04310503",
            "04310403", "04310601", "04310404", "04310801", "04310802", "04310803"};
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            if (archive.getMeterArcCode() != null && Arrays.asList(supportedCodes).contains(archive.getMeterArcCode())) {
                HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
                task.setInstructionType(4);
                task.setInstruction(0);
                setValveTaskFields(task, archive);
                htTasksPerformList.add(task);
            }
        }
        for (org.sdkj.thermal.domain.PrHeatCommandValveArchive archive : commandValveArchives) {
            if (archive.getMeterArcCode() != null && Arrays.asList(supportedCodes).contains(archive.getMeterArcCode())) {
                HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
                task.setInstructionType(4);
                task.setInstruction(0);
                setCommandValveTaskFields(task, archive);
                htTasksPerformList.add(task);
            }
        }
    }

    private void processBrake(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                             List<org.sdkj.thermal.domain.PrHeatCommandValveArchive> commandValveArchives,
                             String orgId, Long create, Date now,
                             PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList) {
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(5);
            task.setInstruction(0);
            setValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
        for (org.sdkj.thermal.domain.PrHeatCommandValveArchive archive : commandValveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(5);
            task.setInstruction(0);
            setCommandValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
    }

    private void processReportCycle(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                                    List<org.sdkj.thermal.domain.PrHeatCommandValveArchive> commandValveArchives,
                                    String orgId, Long create, Date now,
                                    PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                    String intervall, String unit, String duration) {
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(6);
            task.setInstruction(0);
            if (intervall != null) task.setIntervall(Integer.parseInt(intervall));
            if (unit != null) task.setUnit(Integer.parseInt(unit));
            if (duration != null) task.setDuration(Integer.parseInt(duration));
            setValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
        for (org.sdkj.thermal.domain.PrHeatCommandValveArchive archive : commandValveArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(6);
            task.setInstruction(0);
            if (intervall != null) task.setIntervall(Integer.parseInt(intervall));
            if (unit != null) task.setUnit(Integer.parseInt(unit));
            if (duration != null) task.setDuration(Integer.parseInt(duration));
            setCommandValveTaskFields(task, archive);
            htTasksPerformList.add(task);
        }
    }

    private void processHeatMeterQuery(List<org.sdkj.thermal.domain.PrHeatHotArchive> hotArchives,
                                       String orgId, Long create, Date now,
                                       PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList) {
        String[] supportedCodes = {"040303", "04030301", "04030302", "04030303", "04030304", "090301", "09030101"};
        for (org.sdkj.thermal.domain.PrHeatHotArchive archive : hotArchives) {
            if (archive.getMeterArcCode() != null && Arrays.asList(supportedCodes).contains(archive.getMeterArcCode())) {
                HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
                task.setInstructionType(4);
                task.setInstruction(0);
                setHotArchiveTaskFields(task, archive);
                htTasksPerformList.add(task);
            }
        }
    }

    private void processModifyChannel(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                                      String orgId, Long create, Date now,
                                      PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                      Integer scale) {
        String[] supportedCodes = {"04310401", "04310402", "04310502", "04310501", "04310503", "04310403", "04310404"};
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            if (archive.getMeterArcCode() != null && Arrays.asList(supportedCodes).contains(archive.getMeterArcCode())) {
                HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
                task.setInstructionType(28);
                task.setInstruction(scale);
                setValveTaskFields(task, archive);
                htTasksPerformList.add(task);
            }
        }
    }

    private void processModifyDtuChannel(List<org.sdkj.thermal.domain.PrHeatDtuArchive> dtuArchives,
                                         String orgId, Long create, Date now,
                                         PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                         Integer scale) {
        for (org.sdkj.thermal.domain.PrHeatDtuArchive archive : dtuArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(28);
            task.setInstruction(scale);
            task.setDtuNum(archive.getDtuNum());
            htTasksPerformList.add(task);
        }
    }

    private void processReadChannel(List<org.sdkj.thermal.domain.PrHeatDtuArchive> dtuArchives,
                                    String orgId, Long create, Date now,
                                    PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                    Integer scale) {
        for (org.sdkj.thermal.domain.PrHeatDtuArchive archive : dtuArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(27);
            task.setInstruction(scale);
            task.setDtuNum(archive.getDtuNum());
            htTasksPerformList.add(task);
        }
    }

    private void processOpenGateway(List<org.sdkj.thermal.domain.PrHeatDtuArchive> dtuArchives,
                                    String orgId, Long create, Date now,
                                    PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                    Integer scale) {
        for (org.sdkj.thermal.domain.PrHeatDtuArchive archive : dtuArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(29);
            task.setInstruction(scale);
            task.setDtuNum(archive.getDtuNum());
            htTasksPerformList.add(task);
        }
    }

    private void processCloseGateway(List<org.sdkj.thermal.domain.PrHeatDtuArchive> dtuArchives,
                                     String orgId, Long create, Date now,
                                     PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList,
                                     Integer scale) {
        for (org.sdkj.thermal.domain.PrHeatDtuArchive archive : dtuArchives) {
            HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
            task.setInstructionType(30);
            task.setInstruction(scale);
            task.setDtuNum(archive.getDtuNum());
            htTasksPerformList.add(task);
        }
    }

    private void processSpecialBrake(List<org.sdkj.thermal.domain.PrHeatValveArchive> valveArchives,
                                     String orgId, Long create, Date now,
                                     PrOptionsHeat prOptionsHeat, List<HtTasksPerform> htTasksPerformList) {
        String[] supportedCodes = {"04310801", "04310802", "04310803"};
        for (org.sdkj.thermal.domain.PrHeatValveArchive archive : valveArchives) {
            if (archive.getMeterArcCode() != null && Arrays.asList(supportedCodes).contains(archive.getMeterArcCode())) {
                HtTasksPerform task = createBaseTask(orgId, create, now, prOptionsHeat);
                task.setInstructionType(51);
                task.setInstruction(0);
                setValveTaskFields(task, archive);
                htTasksPerformList.add(task);
            }
        }
    }

    private HtTasksPerform createBaseTask(String orgId, Long create, Date now,
                                           PrOptionsHeat prOptionsHeat) {
        HtTasksPerform task = new HtTasksPerform();
        task.setOrgId(orgId);
        task.setCreateBy(create);
        task.setCreateTime(now);
        task.setNumber(0);
        task.setStatus(ThermalTaskConstants.PERFORM_SENT);
        task.setInstructionStatus(ThermalTaskConstants.PERFORM_PENDING);
        return task;
    }

    private void setValveTaskFields(HtTasksPerform task, org.sdkj.thermal.domain.PrHeatValveArchive archive) {
        task.setDeviceId(archive.getDeviceId());
        task.setMeterArcCode(archive.getMeterArcCode());
        task.setMeterId(archive.getId());
        task.setMeterNum(archive.getMeterNum());
        task.setImei(archive.getImeiNum());
        task.setDtuNum(archive.getDtuNum());
        task.setConcentratorCode(archive.getConcentratorCode());
        task.setChanNum(archive.getChanNum());
    }

    private void setCommandValveTaskFields(HtTasksPerform task, org.sdkj.thermal.domain.PrHeatCommandValveArchive archive) {
        task.setDeviceId(archive.getDeviceId());
        task.setMeterArcCode(archive.getMeterArcCode());
        task.setMeterId(archive.getId());
        task.setMeterNum(archive.getMeterNum());
        task.setImei(archive.getImeiNum());
        task.setDtuNum(archive.getDtuNum());
        task.setConcentratorCode(archive.getConcentratorCode());
        task.setChanNum(archive.getChanNum());
    }

    private void setHotArchiveTaskFields(HtTasksPerform task, org.sdkj.thermal.domain.PrHeatHotArchive archive) {
        task.setDeviceId(archive.getDeviceId());
        task.setMeterArcCode(archive.getMeterArcCode());
        task.setMeterId(archive.getId());
        task.setMeterNum(archive.getMeterNum());
        task.setImei(archive.getImeiNum());
        task.setDtuNum(archive.getDtuNum());
        task.setConcentratorCode(archive.getConcentratorCode());
        task.setChanNum(archive.getChanNum());
    }

    @Override
    public TableDataInfo<PrHeatArchiveVo> realTimeData(String orgId, String buildingId,
                                                        String unitCode, String search, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHeatArchiveVo> zonghe(String orgId, String buildingId,
                                                  String unitCode, String search, String moneyType,
                                                  String valveStatus, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        Page<PrHeatArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean xunce(List<PrHeatVo> prHeatVoList, String orgId) {
        if (prHeatVoList.isEmpty()) {
            return true;
        }
        Long create = LoginHelper.getUserId();
        List<HtTasksPerform> tasks = new ArrayList<>();
        Date now = new Date();
        for (PrHeatVo vo : prHeatVoList) {
            HtTasksPerform task = new HtTasksPerform();
            task.setOrgId(orgId);
            task.setCreateBy(create);
            task.setCreateTime(now);
            if (vo.getPrHeatValveArchive() != null) {
                task.setMeterId(vo.getPrHeatValveArchive().getId());
                task.setMeterNum(vo.getPrHeatValveArchive().getMeterNum());
            }
            task.setNumber(100);
            tasks.add(task);
        }
        htTasksPerformService.saveBatchTasks(tasks);
        try {
            htTasksPerformService.executeHeatMeterTasks(tasks);
        } catch (Exception e) {
            log.error("巡测执行失败", e);
            return false;
        }
        log.info("热表巡测完成，数量：{}", prHeatVoList.size());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setValveGroupParam(List<PrHeatVo> prHeatVoList, String commandParam, String orgId) {
        if (prHeatVoList.isEmpty()) {
            return true;
        }
        Long create = LoginHelper.getUserId();
        List<HtTasksPerform> tasks = new ArrayList<>();
        Date now = new Date();
        for (PrHeatVo vo : prHeatVoList) {
            HtTasksPerform task = new HtTasksPerform();
            task.setOrgId(orgId);
            task.setCreateBy(create);
            task.setCreateTime(now);
            if (vo.getPrHeatValveArchive() != null) {
                task.setMeterId(vo.getPrHeatValveArchive().getId());
                task.setMeterNum(vo.getPrHeatValveArchive().getMeterNum());
            }
            task.setNumber(Integer.valueOf(commandParam));
            tasks.add(task);
        }
        htTasksPerformService.saveBatchTasks(tasks);
        try {
            htTasksPerformService.executeValveControlTasks(tasks);
            htTasksPerformService.insertValveOCLog(tasks);
        } catch (Exception e) {
            log.error("设置阀门组号失败", e);
            return false;
        }
        log.info("设置阀门组号完成，组号：{}，数量：{}", commandParam, prHeatVoList.size());
        return true;
    }

    @Override
    public List<PrHeatArchiveVo> findMeter(String search) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        lqw.last("limit 20");
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public BigDecimal calculate(String id) {
        PrHeatArchive archive = getById(id);
        if (archive == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal current = archive.getCurrentReading() != null ? archive.getCurrentReading() : BigDecimal.ZERO;
        BigDecimal start = archive.getStartReading() != null ? BigDecimal.valueOf(archive.getStartReading()) : BigDecimal.ZERO;
        if (current.compareTo(start) < 0) {
            return BigDecimal.ZERO;
        }
        return current.subtract(start);
    }

    @Override
    public List<PrHeatArchiveVo> exportAll(String orgId) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean importData(String uuid) {
        String create = LoginHelper.getUserIdStr();
        log.info("导入配表数据，批次号：{}，用户：{}", uuid, create);
        baseMapper.importData(create);
        return true;
    }

    @Override
    public List<PrHeatArchiveVo> selectReport(String orgId, String buildingId,
                                              String unitCode, String startTime, String endTime, String search) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatArchiveVo> selectMeterReport(String orgId, String buildingId,
                                                   String unitCode, String startTime, String endTime, String search) {
        LambdaQueryWrapper<PrHeatArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatArchive::getOrgId, orgId);
        lqw.eq(PrHeatArchive::getIsChanged, 0);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatArchive::getMeterNum, search.trim())
                .or().like(PrHeatArchive::getMeterArcName, search.trim()));
        }
        lqw.orderByDesc(PrHeatArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }
}
