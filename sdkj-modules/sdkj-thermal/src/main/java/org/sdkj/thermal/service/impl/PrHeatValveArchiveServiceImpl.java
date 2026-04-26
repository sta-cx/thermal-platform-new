package org.sdkj.thermal.service.impl;

import cn.idev.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.dto.PrHeatValveArchiveDto;
import org.sdkj.thermal.domain.dto.PrHouseByPayVo;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.dto.LtValveDataResponse;
import org.sdkj.thermal.domain.dto.YunGuDataResponse;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 户间阀门配表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrHeatValveArchiveServiceImpl extends ServiceImpl<PrHeatValveArchiveMapper, PrHeatValveArchive> implements IPrHeatValveArchiveService {

    private final PrHeatValveArchiveMapper baseMapper;
    private final PrHeatHotArchiveMapper hotArchiveMapper;
    private final IHtTasksPerformService htTasksPerformService;

    @Override
    public PrHeatValveArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                               String unit, String search, String parentId,
                                                               PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatValveArchive::getOrgId, orgId);
        // parentId maps to houseId for this entity
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatValveArchive::getHouseId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatValveArchive::getMeterNum, search.trim())
                .or().like(PrHeatValveArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        Page<PrHeatValveArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<PrHeatValveArchiveVo> listAll(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatValveArchive::getOrgId, orgId);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSetValveStatus(List<PrHouseByPayVo> houseList, String valveStatus) {
        List<ValveArchiveInfo> infos = new ArrayList<>();
        for (PrHouseByPayVo house : houseList) {
            // 按 meterNum + meterArcCode 查找 PrHeatValveArchive
            LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
            lqw.eq(PrHeatValveArchive::getMeterNum, house.getMeterNum());
            lqw.eq(PrHeatValveArchive::getMeterArcCode, house.getMeterArcCode());
            lqw.eq(PrHeatValveArchive::getIsChanged, 0);
            lqw.last("LIMIT 1");
            PrHeatValveArchive archive = getOne(lqw);
            if (archive == null) {
                continue;
            }
            infos.add(new ValveArchiveInfo(
                archive.getId(),
                archive.getMeterArcCode(),
                archive.getMeterNum(),
                archive.getDeviceId(),
                archive.getConcentratorCode(),
                archive.getImeiNum(),
                archive.getDtuNum(),
                archive.getChanNum()
            ));
        }
        if (infos.isEmpty()) {
            return false;
        }

        // valveStatus 决定 instructionType 和 instruction
        int instructionType;
        int instruction;
        switch (valveStatus) {
            case "1": // 开阀
                instructionType = 3;
                instruction = 100;
                break;
            case "2": // 关阀
                instructionType = 3;
                instruction = 0;
                break;
            case "4": // 查询
                instructionType = 4;
                instruction = 0;
                break;
            case "5": // 制动
                instructionType = 5;
                instruction = 0;
                break;
            case "51": // 特殊制动
                instructionType = 5;
                instruction = 1;
                break;
            default: // 默认按开度处理
                instructionType = 3;
                instruction = 0;
                break;
        }

        List<HtTasksPerform> tasks = buildTasks(infos, houseList.get(0).getOrgId(), houseList.get(0).getCompanyId(),
            instructionType, instruction);
        return htTasksPerformService.saveBatchTasks(tasks);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSetValveOpening(List<PrHouseByPayVo> houseList, String opening) {
        List<ValveArchiveInfo> infos = new ArrayList<>();
        for (PrHouseByPayVo house : houseList) {
            LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
            lqw.eq(PrHeatValveArchive::getMeterNum, house.getMeterNum());
            lqw.eq(PrHeatValveArchive::getMeterArcCode, house.getMeterArcCode());
            lqw.eq(PrHeatValveArchive::getIsChanged, 0);
            lqw.last("LIMIT 1");
            PrHeatValveArchive archive = getOne(lqw);
            if (archive == null) {
                continue;
            }
            infos.add(new ValveArchiveInfo(
                archive.getId(),
                archive.getMeterArcCode(),
                archive.getMeterNum(),
                archive.getDeviceId(),
                archive.getConcentratorCode(),
                archive.getImeiNum(),
                archive.getDtuNum(),
                archive.getChanNum()
            ));
        }
        if (infos.isEmpty()) {
            return false;
        }

        int instructionValue = Integer.parseInt(opening);
        List<HtTasksPerform> tasks = buildTasks(infos, houseList.get(0).getOrgId(), houseList.get(0).getCompanyId(),
            3, instructionValue);
        return htTasksPerformService.saveBatchTasks(tasks);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSetValveCycle(List<PrHouseByPayVo> houseList, String interval, String unit, String valid) {
        List<ValveArchiveInfo> infos = new ArrayList<>();
        for (PrHouseByPayVo house : houseList) {
            LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
            lqw.eq(PrHeatValveArchive::getMeterNum, house.getMeterNum());
            lqw.eq(PrHeatValveArchive::getMeterArcCode, house.getMeterArcCode());
            lqw.eq(PrHeatValveArchive::getIsChanged, 0);
            lqw.last("LIMIT 1");
            PrHeatValveArchive archive = getOne(lqw);
            if (archive == null) {
                continue;
            }
            infos.add(new ValveArchiveInfo(
                archive.getId(),
                archive.getMeterArcCode(),
                archive.getMeterNum(),
                archive.getDeviceId(),
                archive.getConcentratorCode(),
                archive.getImeiNum(),
                archive.getDtuNum(),
                archive.getChanNum()
            ));
        }
        if (infos.isEmpty()) {
            return false;
        }

        // instructionType=6, 设置上报周期
        List<HtTasksPerform> tasks = buildCycleTasks(infos, houseList.get(0).getOrgId(), houseList.get(0).getCompanyId(),
            interval, unit, valid);
        return htTasksPerformService.saveBatchTasks(tasks);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> importValveArchive(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }

        List<PrHeatValveArchiveDto> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream())
                .head(PrHeatValveArchiveDto.class)
                .sheet(0)
                .headRowNumber(1)
                .doReadSync();
        } catch (Exception e) {
            return R.fail("文件解析失败: " + e.getMessage());
        }

        if (dataList == null || dataList.isEmpty()) {
            return R.fail("导入数据为空");
        }

        int successCount = 0;
        int failCount = 0;
        for (PrHeatValveArchiveDto dto : dataList) {
            if (StringUtils.isBlank(dto.getMeterNum()) || StringUtils.isBlank(dto.getMeterArcCode())) {
                failCount++;
                continue;
            }
            // 检查是否已存在
            long count = count(new LambdaQueryWrapper<PrHeatValveArchive>()
                .eq(PrHeatValveArchive::getMeterNum, dto.getMeterNum())
                .eq(PrHeatValveArchive::getMeterArcCode, dto.getMeterArcCode())
                .eq(PrHeatValveArchive::getIsChanged, 0));
            if (count > 0) {
                failCount++;
                continue;
            }
            PrHeatValveArchive entity = new PrHeatValveArchive();
            entity.setMeterNum(dto.getMeterNum());
            entity.setMeterArcCode(dto.getMeterArcCode());
            entity.setMeterArcName(dto.getMeterArcName());
            entity.setCardNum(dto.getCardNum());
            entity.setConcentratorCode(dto.getConcentratorCode());
            entity.setImeiNum(dto.getImeiNum());
            entity.setDeviceId(dto.getDeviceId());
            entity.setDtuNum(dto.getDtuNum());
            entity.setChanNum(dto.getChanNum());
            entity.setHouseId(dto.getHouseId());
            entity.setOrgId(dto.getOrgId());
            entity.setCompanyId(dto.getCompanyId());
            entity.setIsChanged(0);
            entity.setIsOpen(0);
            entity.setMeterSerial(dto.getMeterSerial());
            entity.setInstallSite(dto.getInstallSite());
            entity.setCaliber(dto.getCaliber());
            entity.setInstallType(dto.getInstallType());
            entity.setGroupNum25(dto.getGroupNum25());
            entity.setValveModel(dto.getValveModel());
            entity.setDtuType(dto.getDtuType());
            save(entity);
            successCount++;
        }
        return R.ok();
    }

    // ========== 私有方法 ==========

    /**
     * 构建批量阀门控制任务列表
     */
    private List<HtTasksPerform> buildTasks(List<ValveArchiveInfo> infos, String orgId, String companyId,
                                             int instructionType, int instruction) {
        return infos.stream().map(info -> {
            HtTasksPerform task = new HtTasksPerform();
            task.setId(UUID.randomUUID().toString().replace("-", ""));
            task.setInstructionType(instructionType);
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
    }

    /**
     * 构建批量上报周期设置任务列表（instructionType=6）
     */
    private List<HtTasksPerform> buildCycleTasks(List<ValveArchiveInfo> infos, String orgId, String companyId,
                                                  String interval, String unit, String valid) {
        return infos.stream().map(info -> {
            HtTasksPerform task = new HtTasksPerform();
            task.setId(UUID.randomUUID().toString().replace("-", ""));
            task.setInstructionType(6);
            task.setInstruction(0);
            task.setNumber(0);
            task.setIntervall(StringUtils.isNotBlank(interval) ? Integer.parseInt(interval) : null);
            task.setUnit(StringUtils.isNotBlank(unit) ? Integer.parseInt(unit) : null);
            task.setDuration(StringUtils.isNotBlank(valid) ? Integer.parseInt(valid) : null);
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatValveArchive entity) {
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

    // ========== 第三方 API 实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean yunguValveControl(String manuId, int value) {
        // 按 meterNum 查找阀门配表
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatValveArchive::getMeterNum, manuId);
        lqw.last("LIMIT 1");
        PrHeatValveArchive archive = getOne(lqw);
        if (archive == null) {
            return false;
        }

        // 创建调控任务
        ValveArchiveInfo info = new ValveArchiveInfo(
            archive.getId(),
            archive.getMeterArcCode(),
            archive.getMeterNum(),
            archive.getDeviceId(),
            archive.getConcentratorCode(),
            archive.getImeiNum(),
            archive.getDtuNum(),
            archive.getChanNum()
        );
        List<HtTasksPerform> tasks = buildTasks(List.of(info), archive.getOrgId(), archive.getCompanyId(), 3, value);
        return htTasksPerformService.saveBatchTasks(tasks);
    }

    @Override
    public List<YunGuDataResponse> yunguBatchSync(List<String> manuIdList) {
        List<PrHeatHotArchive> hotList = hotArchiveMapper.getValveHotDataByList(manuIdList);
        if (hotList == null || hotList.isEmpty()) {
            return List.of();
        }

        List<YunGuDataResponse> dataList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (PrHeatHotArchive hot : hotList) {
            YunGuDataResponse resp = new YunGuDataResponse();
            resp.setMeterManuId(hot.getMeterNum());
            resp.setShowName(hot.getMeterNum());
            resp.setLastFlow(hot.getCurFlow() != null
                ? hot.getCurFlow().divide(new BigDecimal("1000000"), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
            resp.setLastForwardT(hot.getInTemperature());
            resp.setLastReturnT(hot.getOutTemperature());
            resp.setLastHeatSum(hot.getTotalUsed() != null
                ? hot.getTotalUsed().divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
            resp.setLastFlowSum(hot.getTotalFlow() != null
                ? hot.getTotalFlow().divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
            resp.setLastHeatPower(hot.getThermalPower() != null
                ? hot.getThermalPower().divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
            resp.setLastRecordTs(hot.getValveTime() != null ? hot.getValveTime().getTime() : currentTime);

            int open = 0;
            if (StringUtils.isNotBlank(hot.getValveStatus())) {
                try {
                    open = Integer.parseInt(hot.getValveStatus());
                } catch (NumberFormatException ignored) {
                    // keep 0
                }
            }
            resp.setLastValveOpenPercent(open);
            resp.setBoltStatus(open > 0 ? 1 : 0);

            resp.setLastFlowMonth(BigDecimal.ZERO);
            resp.setLastHeatMonth(BigDecimal.ZERO);
            resp.setLastRoomTemp(BigDecimal.ZERO);
            resp.setLastRoomTempRecordTs(0L);

            dataList.add(resp);
        }
        return dataList;
    }

    @Override
    public List<LtValveDataResponse> getLTValveData(List<String> meterNums) {
        return baseMapper.getLTValveData(meterNums);
    }
}
