package org.sdkj.thermal.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.idev.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtTasksPerform;
import org.sdkj.thermal.domain.PrFamily;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.dto.PrHeatValveArchiveDto;
import org.sdkj.thermal.domain.dto.PrHouseByPayVo;
import org.sdkj.thermal.domain.dto.ValveArchiveInfo;
import org.sdkj.thermal.domain.dto.LtValveDataResponse;
import org.sdkj.thermal.domain.dto.YunGuDataResponse;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.mapper.PrFamilyMapper;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class PrHeatValveArchiveServiceImpl extends ServiceImpl<PrHeatValveArchiveMapper, PrHeatValveArchive> implements IPrHeatValveArchiveService {

    private final PrHeatValveArchiveMapper baseMapper;
    private final PrHeatHotArchiveMapper hotArchiveMapper;
    private final IHtTasksPerformService htTasksPerformService;
    private final PrHouseMapper houseMapper;
    private final PrFamilyMapper familyMapper;

    @Value("${collect.ipPort:}")
    private String ipPort;
    @Value("${collect.username:}")
    private String username;
    @Value("${collect.password:}")
    private String password;

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

    // ========== 卡表管理实现 ==========

    @Override
    public TableDataInfo<PrHeatValveArchiveVo> pageListHeatCard(String companyId, String orgId, String buildingId,
                                                                 String unit, String meterArcCode, String payStatus,
                                                                 String search, String parentId, String writeCardStatus,
                                                                 PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatValveArchive::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatValveArchive::getHouseId, parentId);
        // buildingId / unit 需通过 house 关联，这里先按 archive 的直接字段过滤
        if (StringUtils.isNotBlank(meterArcCode)) {
            lqw.eq(PrHeatValveArchive::getMeterArcCode, meterArcCode);
        }
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatValveArchive::getMeterNum, search.trim())
                .or().like(PrHeatValveArchive::getCardNum, search.trim())
                .or().like(PrHeatValveArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        // writeCardStatus 筛选: 0=未写卡, 1=已写卡（基于 isOpen 字段映射）
        if (StringUtils.isNotBlank(writeCardStatus)) {
            lqw.eq(PrHeatValveArchive::getIsOpen, Integer.parseInt(writeCardStatus));
        }
        // payStatus 筛选需要 JOIN pr_house，在简单查询中暂不支持；预留参数位置
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        Page<PrHeatValveArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateValveStatus(String id) {
        PrHeatValveArchive archive = getById(id);
        if (archive == null) {
            return false;
        }
        archive.setIsOpen(archive.getIsOpen() == null || archive.getIsOpen() == 0 ? 1 : 0);
        return updateById(archive);
    }

    // ========== 设备查询实现 ==========

    @Override
    public List<PrHeatValveArchiveVo> queryMeterByMeterNum(String meterNum, String orgId, String code) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatValveArchive::getMeterNum, meterNum);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatValveArchive::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(code), PrHeatValveArchive::getMeterArcCode, code);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.last("LIMIT 100");
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatValveArchiveVo> queryValveByMeterNum(String meterNum) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatValveArchive::getMeterNum, meterNum);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatValveArchiveVo> queryCardMeterByHouseId(String houseId) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatValveArchive::getHouseId, houseId);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatValveArchiveVo> queryCardMeterByRoomNum(String orgId, String buildingId, String unitCode, String search) {
        // 1. 根据 buildingId + unitCode + search 找到 house
        LambdaQueryWrapper<PrHouse> houseLqw = new LambdaQueryWrapper<>();
        houseLqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        houseLqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        if (StringUtils.isNotBlank(unitCode)) {
            houseLqw.eq(PrHouse::getUnitCode, unitCode);
        }
        if (StringUtils.isNotBlank(search)) {
            houseLqw.and(w -> w.like(PrHouse::getRoomNum, search.trim())
                .or().like(PrHouse::getCode, search.trim()));
        }
        houseLqw.last("LIMIT 500");
        List<PrHouse> houses = houseMapper.selectList(houseLqw);
        if (houses.isEmpty()) {
            return List.of();
        }
        // 2. 按房屋ID列表查卡阀
        List<String> houseIds = houses.stream().map(PrHouse::getId).toList();
        LambdaQueryWrapper<PrHeatValveArchive> valveLqw = new LambdaQueryWrapper<>();
        valveLqw.in(PrHeatValveArchive::getHouseId, houseIds);
        valveLqw.eq(PrHeatValveArchive::getIsChanged, 0);
        return baseMapper.selectVoList(valveLqw);
    }

    @Override
    public PrHeatValveArchiveVo getValveDataByHouseId(String houseId) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatValveArchive::getHouseId, houseId);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.last("LIMIT 1");
        return baseMapper.selectVoOne(lqw);
    }

    // ========== 信息同步实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean valveInformationSynchronization(String orgId, String companyId) {
        // 1. 查询该小区下所有有效阀门档案
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatValveArchive::getOrgId, orgId);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        List<PrHeatValveArchive> archives = list(lqw);
        if (archives.isEmpty()) {
            log.info("同步-未找到阀门档案数据, orgId={}, companyId={}", orgId, companyId);
            return false;
        }

        // 2. 构建同步 JSON
        JSONArray dataArray = new JSONArray();
        for (PrHeatValveArchive archive : archives) {
            JSONObject item = new JSONObject();
            item.set("meterNum", archive.getMeterNum());
            item.set("meterArcCode", archive.getMeterArcCode());
            item.set("meterArcName", archive.getMeterArcName());
            item.set("concentratorCode", archive.getConcentratorCode());
            item.set("imeiNum", archive.getImeiNum());
            item.set("deviceId", archive.getDeviceId());
            item.set("dtuNum", archive.getDtuNum());
            item.set("chanNum", archive.getChanNum());
            item.set("orgId", archive.getOrgId());
            item.set("companyId", archive.getCompanyId());
            dataArray.add(item);
        }

        JSONObject payload = new JSONObject();
        payload.set("type", "valve");
        payload.set("data", dataArray);

        // 3. 调用采集平台同步
        boolean result = CollectPlatformUtil.informationSynchronization(payload, ipPort, username, password);
        log.info("同步户阀信息到采集平台, orgId={}, companyId={}, count={}, result={}",
            orgId, companyId, archives.size(), result);
        return result;
    }

    @Override
    public List<PrHeatValveArchiveVo> listSyncData(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatValveArchive::getOrgId, orgId);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatValveArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    // ========== 蓝牙控制日志实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertValveControlLogByBluetooth(String meterNum, String type, String opening) {
        // 查找阀门档案
        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHeatValveArchive::getMeterNum, meterNum);
        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
        lqw.last("LIMIT 1");
        PrHeatValveArchive archive = getOne(lqw);
        if (archive == null) {
            log.warn("蓝牙控制日志-未找到阀门档案, meterNum={}", meterNum);
            return;
        }

        // 创建蓝牙控制任务
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

        int instructionType = "1".equals(type) ? 3 : 3; // 默认开阀
        int instruction = StringUtils.isNotBlank(opening) ? Integer.parseInt(opening) : 0;

        List<HtTasksPerform> tasks = buildTasks(List.of(info), archive.getOrgId(), archive.getCompanyId(),
            instructionType, instruction);
        htTasksPerformService.saveBatchTasks(tasks);
        log.info("蓝牙控制日志-指令下发, meterNum={}, type={}, opening={}", meterNum, type, opening);
    }

    // ========== 一键新增实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertUserAndValveInfo(String companyId, String orgId, String orgName,
                                          String buildingId, String buildingName, String unitCode,
                                          String roomNum, String floor, String otherCode, String payStatus,
                                          String userName, String phone,
                                          String gfloorArea, String nfloorArea, String heatingArea,
                                          String meterNum) {
        // 1. 创建房屋
        PrHouse house = new PrHouse();
        house.setRoomNum(roomNum);
        house.setBuildingId(buildingId);
        house.setBuildingName(buildingName);
        house.setUnitCode(unitCode);
        house.setFloor(StringUtils.isNotBlank(floor) ? Integer.parseInt(floor) : null);
        house.setOtherCode(otherCode);
        house.setOrgId(orgId);
        house.setCompanyId(companyId);
        house.setGfloorArea(StringUtils.isNotBlank(gfloorArea) ? new BigDecimal(gfloorArea) : null);
        house.setNfloorArea(StringUtils.isNotBlank(nfloorArea) ? new BigDecimal(nfloorArea) : null);
        house.setHeatingArea(StringUtils.isNotBlank(heatingArea) ? new BigDecimal(heatingArea) : null);
        house.setStatus("1");
        houseMapper.insert(house);

        // 2. 创建家庭成员（关联用户）
        if (StringUtils.isNotBlank(userName)) {
            PrFamily family = new PrFamily();
            family.setName(userName);
            family.setHouseId(house.getId());
            family.setContactAddr(phone);
            familyMapper.insert(family);
        }

        // 3. 创建阀门配表
        PrHeatValveArchive archive = new PrHeatValveArchive();
        archive.setMeterNum(meterNum);
        archive.setHouseId(house.getId());
        archive.setOrgId(orgId);
        archive.setCompanyId(companyId);
        archive.setIsChanged(0);
        archive.setIsOpen(0);
        save(archive);

        return house.getId();
    }

    // ========== NB/MBus 数据接收实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHouseTemperature(String houseId, BigDecimal inTemperature,
                                       BigDecimal outTemperature, Integer actualOpen) {
        baseMapper.updateHouse(houseId, inTemperature, outTemperature, actualOpen);
        log.debug("温度反写完成, houseId={}, inTemp={}, outTemp={}, actualOpen={}",
            houseId, inTemperature, outTemperature, actualOpen);
    }

    @Override
    public List<PrHeatValveArchive> queryPaidClosedValves(String companyId, String orgId) {
        // TODO: Implement - query valves where house is paid but valve is closed (from old queryValveStatusGetByPayK)
        // SQL logic: JOIN pr_house on houseId WHERE pr_house.payStatus='1' AND pr_heat_valve_archive.valveStatus != '1'
        log.warn("queryPaidClosedValves not yet implemented for companyId={}, orgId={}", companyId, orgId);
        return List.of();
    }

    @Override
    public List<PrHeatValveArchive> queryUnpaidOpenValves(String companyId, String orgId) {
        // TODO: Implement - query valves where house is unpaid but valve is open (from old queryValveStatusGetByPayG)
        // SQL logic: JOIN pr_house on houseId WHERE pr_house.payStatus!='1' AND pr_heat_valve_archive.valveStatus == '1'
        log.warn("queryUnpaidOpenValves not yet implemented for companyId={}, orgId={}", companyId, orgId);
        return List.of();
    }
}
