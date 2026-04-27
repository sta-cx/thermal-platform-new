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
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.dto.PrHeatUnitValveArchiveDto;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;
import org.sdkj.thermal.mapper.PrHeatUnitValveArchiveMapper;
import org.sdkj.thermal.service.IPrHeatUnitValveArchiveService;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 单元阀门配表 Service 实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrHeatUnitValveArchiveServiceImpl extends ServiceImpl<PrHeatUnitValveArchiveMapper, PrHeatUnitValveArchive> implements IPrHeatUnitValveArchiveService {

    private final PrHeatUnitValveArchiveMapper baseMapper;

    @Value("${collect.ipPort:}")
    private String ipPort;
    @Value("${collect.username:}")
    private String username;
    @Value("${collect.password:}")
    private String password;

    @Override
    public PrHeatUnitValveArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatUnitValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                                   String unit, String search, String parentId,
                                                                   PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatUnitValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitValveArchive::getOrgId, orgId);
        // parentId maps to unitId for this entity (uses unitId, not houseId)
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatUnitValveArchive::getUnitId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatUnitValveArchive::getMeterNum, search.trim())
                .or().like(PrHeatUnitValveArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatUnitValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitValveArchive::getCreateTime);
        Page<PrHeatUnitValveArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatUnitValveArchive entity) {
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
    public boolean valveInformationSynchronization(String orgId, String companyId) {
        // 1. 查询该小区下所有有效单元阀门档案
        LambdaQueryWrapper<PrHeatUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatUnitValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitValveArchive::getOrgId, orgId);
        lqw.eq(PrHeatUnitValveArchive::getIsChanged, 0);
        List<PrHeatUnitValveArchive> archives = list(lqw);
        if (archives.isEmpty()) {
            log.info("同步-未找到单元阀门档案数据, orgId={}, companyId={}", orgId, companyId);
            return false;
        }

        // 2. 构建同步 JSON（type=6 代表单元阀门）
        JSONArray dataArray = new JSONArray();
        for (PrHeatUnitValveArchive archive : archives) {
            JSONObject item = new JSONObject();
            item.set("meterNum", archive.getMeterNum());
            item.set("meterArcCode", archive.getMeterArcCode());
            item.set("meterArcName", archive.getMeterArcName());
            item.set("concentratorCode", archive.getConcentratorCode() != null ? archive.getConcentratorCode() : "00000000");
            item.set("imeiNum", archive.getImeiNum());
            item.set("deviceId", archive.getDeviceId());
            item.set("dtuNum", archive.getDtuNum());
            item.set("chanNum", archive.getChanNum());
            item.set("orgId", archive.getOrgId());
            item.set("companyId", archive.getCompanyId());
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
            payload.set("type", 6);
            payload.set("data", batch);
            boolean batchResult = CollectPlatformUtil.informationSynchronization(payload, ipPort, username, password);
            if (!batchResult) {
                result = false;
            }
        }

        log.info("同步单元阀门信息到采集平台, orgId={}, companyId={}, count={}, result={}",
            orgId, companyId, archives.size(), result);
        return result;
    }

    @Override
    public List<PrHeatUnitValveArchiveVo> listSyncData(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatUnitValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitValveArchive::getOrgId, orgId);
        lqw.eq(PrHeatUnitValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitValveArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatUnitValveArchiveVo> listAll(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatUnitValveArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatUnitValveArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatUnitValveArchive::getOrgId, orgId);
        lqw.eq(PrHeatUnitValveArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatUnitValveArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> importUnitValveArchive(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }

        List<PrHeatUnitValveArchiveDto> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream())
                .head(PrHeatUnitValveArchiveDto.class)
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
        for (PrHeatUnitValveArchiveDto dto : dataList) {
            if (StringUtils.isBlank(dto.getMeterNum()) || StringUtils.isBlank(dto.getMeterArcCode())) {
                failCount++;
                continue;
            }
            // 检查是否已存在
            long count = count(new LambdaQueryWrapper<PrHeatUnitValveArchive>()
                .eq(PrHeatUnitValveArchive::getMeterNum, dto.getMeterNum())
                .eq(PrHeatUnitValveArchive::getMeterArcCode, dto.getMeterArcCode())
                .eq(PrHeatUnitValveArchive::getIsChanged, 0));
            if (count > 0) {
                failCount++;
                continue;
            }
            PrHeatUnitValveArchive entity = new PrHeatUnitValveArchive();
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
            entity.setCompanyId(dto.getCompanyId());
            entity.setIsChanged(0);
            entity.setIsStop(0);
            entity.setMeterSerial(dto.getMeterSerial());
            entity.setInstallSite(dto.getInstallSite());
            entity.setCaliber(dto.getCaliber());
            entity.setValveStatus(dto.getValveStatus());
            entity.setSettingStatus(dto.getSettingStatus());
            entity.setActualStatus(dto.getActualStatus());
            entity.setInTemperature(dto.getInTemperature());
            entity.setOutTemperature(dto.getOutTemperature());
            entity.setVoltage(dto.getVoltage());
            entity.setSignalStrength(dto.getSignalStrength());
            entity.setDtuType(dto.getDtuType());
            save(entity);
            successCount++;
        }
        log.info("单元阀门导入完成, success={}, fail={}", successCount, failCount);
        return R.ok();
    }
}
