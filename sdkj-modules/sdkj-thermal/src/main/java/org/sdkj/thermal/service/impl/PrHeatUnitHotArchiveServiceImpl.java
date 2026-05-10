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
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.dto.PrHeatUnitHotArchiveDto;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.sdkj.thermal.mapper.PrHeatUnitHotArchiveMapper;
import org.sdkj.thermal.service.IPrHeatUnitHotArchiveService;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 单元热表配表 Service 实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrHeatUnitHotArchiveServiceImpl extends ServiceImpl<PrHeatUnitHotArchiveMapper, PrHeatUnitHotArchive> implements IPrHeatUnitHotArchiveService {

    private final PrHeatUnitHotArchiveMapper baseMapper;

    @Value("${collect.ipPort:}")
    private String ipPort;
    @Value("${collect.username:}")
    private String username;
    @Value("${collect.password:}")
    private String password;

    @Override
    public PrHeatUnitHotArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatUnitHotArchiveVo> selectPageList(String orgId, String buildingId,
                                                                 String unit, String search, String parentId,
                                                                 PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitHotArchive::getOrgId, orgId);
        // parentId maps to unitId for this entity (uses unitId, not houseId)
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatUnitHotArchive::getUnitId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatUnitHotArchive::getMeterNum, search.trim())
                .or().like(PrHeatUnitHotArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatUnitHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitHotArchive::getCreateTime);
        Page<PrHeatUnitHotArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatUnitHotArchive entity) {
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

    // ========== 批量操作实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean valveInformationSynchronization(String orgId) {
        // 1. 查询该小区下所有有效单元热表档案
        LambdaQueryWrapper<PrHeatUnitHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatUnitHotArchive::getIsChanged, 0);
        List<PrHeatUnitHotArchive> archives = list(lqw);
        if (archives.isEmpty()) {
            log.info("同步-未找到单元热表档案数据, orgId={}", orgId);
            return false;
        }

        // 2. 构建同步 JSON（type=5 代表单元热表）
        JSONArray dataArray = new JSONArray();
        for (PrHeatUnitHotArchive archive : archives) {
            JSONObject item = new JSONObject();
            item.set("meterNum", archive.getMeterNum());
            item.set("meterArcCode", archive.getMeterArcCode());
            item.set("meterArcName", archive.getMeterArcName());
            item.set("cardNum", archive.getCardNum());
            item.set("concentratorCode", archive.getConcentratorCode() != null ? archive.getConcentratorCode() : "00000000");
            item.set("imeiNum", archive.getImeiNum());
            item.set("deviceId", archive.getDeviceId());
            item.set("orgId", archive.getOrgId());
            dataArray.add(item);
        }

        // 分批同步（每批最多300条）
        boolean result = true;
        int batchSize = 300;
        for (int i = 0; i < dataArray.size(); i += batchSize) {
            JSONArray batch = new JSONArray();
            for (int j = i; j < Math.min(i + batchSize, dataArray.size()); j++) {
                batch.add(dataArray.get(j));
            }
            JSONObject payload = new JSONObject();
            payload.set("type", 5);
            payload.set("data", batch);
            boolean batchResult = CollectPlatformUtil.informationSynchronization(payload, ipPort, username, password);
            if (!batchResult) {
                result = false;
            }
        }

        log.info("同步单元热表信息到采集平台, orgId={}, count={}, result={}",
            orgId, archives.size(), result);
        return result;
    }

    @Override
    public List<PrHeatUnitHotArchiveVo> listSyncData(String orgId) {
        LambdaQueryWrapper<PrHeatUnitHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatUnitHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitHotArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatUnitHotArchiveVo> listAll(String orgId) {
        LambdaQueryWrapper<PrHeatUnitHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatUnitHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitHotArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> importUnitHotArchive(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }

        List<PrHeatUnitHotArchiveDto> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream())
                .head(PrHeatUnitHotArchiveDto.class)
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
        for (PrHeatUnitHotArchiveDto dto : dataList) {
            if (StringUtils.isBlank(dto.getMeterNum()) || StringUtils.isBlank(dto.getMeterArcCode())) {
                failCount++;
                continue;
            }
            // 检查是否已存在
            long count = count(new LambdaQueryWrapper<PrHeatUnitHotArchive>()
                .eq(PrHeatUnitHotArchive::getMeterNum, dto.getMeterNum())
                .eq(PrHeatUnitHotArchive::getMeterArcCode, dto.getMeterArcCode())
                .eq(PrHeatUnitHotArchive::getIsChanged, 0));
            if (count > 0) {
                failCount++;
                continue;
            }
            PrHeatUnitHotArchive entity = new PrHeatUnitHotArchive();
            entity.setMeterNum(dto.getMeterNum());
            entity.setMeterArcCode(dto.getMeterArcCode());
            entity.setMeterArcName(dto.getMeterArcName());
            entity.setCardNum(dto.getCardNum());
            entity.setConcentratorCode(dto.getConcentratorCode());
            entity.setImeiNum(dto.getImeiNum());
            entity.setDeviceId(dto.getDeviceId());
            entity.setDtuNum(dto.getDtuNum());
            entity.setChanNum(dto.getChanNum());
            entity.setUnitId(dto.getUnitId());
            entity.setOrgId(dto.getOrgId());
            entity.setIsChanged(0);
            entity.setIsStop(0);
            entity.setMeterSerial(dto.getMeterSerial());
            entity.setInstallSite(dto.getInstallSite());
            entity.setDtuType(dto.getDtuType());
            entity.setValveStatus(dto.getValveStatus());
            entity.setInTemperature(dto.getInTemperature());
            entity.setOutTemperature(dto.getOutTemperature());
            entity.setVoltage(dto.getVoltage());
            entity.setSignalStrength(dto.getSignalStrength());
            entity.setHoardLimit(dto.getHoardLimit());
            entity.setAlarmValue(dto.getAlarmValue());
            entity.setCloseValue(dto.getCloseValue());
            entity.setMeasurement(dto.getMeasurement());
            entity.setStandardId(dto.getStandardId());
            entity.setStandardPrice(dto.getStandardPrice());
            entity.setIsSteps(dto.getIsSteps());
            entity.setStartReading(dto.getStartReading());
            entity.setCurrentReading(dto.getCurrentReading());
            entity.setTotalUsed(dto.getTotalUsed());
            entity.setTradeTimes(dto.getTradeTimes());
            entity.setTotalFlow(dto.getTotalFlow());
            entity.setCurFlow(dto.getCurFlow());
            entity.setTotalWorktime(dto.getTotalWorktime());
            entity.setThermalPower(dto.getThermalPower());
            save(entity);
            successCount++;
        }
        log.info("单元热表导入完成, success={}, fail={}", successCount, failCount);
        return R.ok();
    }
}
