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
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.dto.PrHeatHotArchiveDto;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.service.IPrHeatHotArchiveService;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 房屋热量表配表 Service 实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrHeatHotArchiveServiceImpl extends ServiceImpl<PrHeatHotArchiveMapper, PrHeatHotArchive> implements IPrHeatHotArchiveService {

    private final PrHeatHotArchiveMapper baseMapper;

    @Value("${collect.ipPort:}")
    private String ipPort;
    @Value("${collect.username:}")
    private String username;
    @Value("${collect.password:}")
    private String password;

    @Override
    public PrHeatHotArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatHotArchiveVo> selectPageList(String orgId, String buildingId,
                                                             String unit, String search, String parentId,
                                                             PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        // parentId maps to houseId for this entity
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatHotArchive::getHouseId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatHotArchive::getMeterNum, search.trim())
                .or().like(PrHeatHotArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatHotArchive::getCreateTime);
        Page<PrHeatHotArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatHotArchive entity) {
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
        // 1. 查询该小区下所有有效热量表档案
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        List<PrHeatHotArchive> archives = list(lqw);
        if (archives.isEmpty()) {
            log.info("同步-未找到热量表档案数据, orgId={}", orgId);
            return false;
        }

        // 2. 构建同步 JSON（type=2 代表热表）
        JSONArray dataArray = new JSONArray();
        for (PrHeatHotArchive archive : archives) {
            JSONObject item = new JSONObject();
            item.set("meterNum", archive.getMeterNum());
            item.set("meterArcCode", archive.getMeterArcCode());
            item.set("meterArcName", archive.getMeterArcName());
            item.set("cardNum", archive.getCardNum());
            item.set("concentratorCode", archive.getConcentratorCode());
            item.set("imeiNum", archive.getImeiNum());
            item.set("deviceId", archive.getDeviceId());
            item.set("dtuNum", archive.getDtuNum());
            item.set("chanNum", archive.getChanNum());
            item.set("dtuType", archive.getDtuType() != null ? archive.getDtuType() : 0);
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
            payload.set("type", 2);
            payload.set("data", batch);
            boolean batchResult = CollectPlatformUtil.informationSynchronization(payload, ipPort, username, password);
            if (!batchResult) {
                result = false;
            }
        }

        log.info("同步户热表信息到采集平台, orgId={}, count={}, result={}",
            orgId, archives.size(), result);
        return result;
    }

    @Override
    public List<PrHeatHotArchiveVo> listSyncData(String orgId) {
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatHotArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatHotArchiveVo> listAll(String orgId) {
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatHotArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> importHotArchive(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }

        List<PrHeatHotArchiveDto> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream())
                .head(PrHeatHotArchiveDto.class)
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
        for (PrHeatHotArchiveDto dto : dataList) {
            if (StringUtils.isBlank(dto.getMeterNum()) || StringUtils.isBlank(dto.getMeterArcCode())) {
                failCount++;
                continue;
            }
            // 检查是否已存在
            long count = count(new LambdaQueryWrapper<PrHeatHotArchive>()
                .eq(PrHeatHotArchive::getMeterNum, dto.getMeterNum())
                .eq(PrHeatHotArchive::getMeterArcCode, dto.getMeterArcCode())
                .eq(PrHeatHotArchive::getIsChanged, 0));
            if (count > 0) {
                failCount++;
                continue;
            }
            PrHeatHotArchive entity = new PrHeatHotArchive();
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
            entity.setIsChanged(0);
            entity.setIsStop(0);
            entity.setMeterSerial(dto.getMeterSerial());
            entity.setInstallSite(dto.getInstallSite());
            entity.setValveStatus(dto.getValveStatus());
            entity.setInTemperature(dto.getInTemperature());
            entity.setOutTemperature(dto.getOutTemperature());
            entity.setVoltage(StringUtils.isNotBlank(dto.getVoltage()) ? new java.math.BigDecimal(dto.getVoltage()) : null);
            entity.setSignalStrength(dto.getSignalStrength());
            entity.setTradeTimes(dto.getTradeTimes());
            save(entity);
            successCount++;
        }
        log.info("热量表导入完成, success={}, fail={}", successCount, failCount);
        return R.ok();
    }
}
