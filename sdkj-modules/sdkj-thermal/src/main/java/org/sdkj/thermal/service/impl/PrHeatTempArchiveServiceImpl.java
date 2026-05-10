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
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.dto.PrHeatTempArchiveDto;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.mapper.PrHeatTempArchiveMapper;
import org.sdkj.thermal.service.IPrHeatTempArchiveService;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 温采器配表 Service 实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrHeatTempArchiveServiceImpl extends ServiceImpl<PrHeatTempArchiveMapper, PrHeatTempArchive> implements IPrHeatTempArchiveService {

    private final PrHeatTempArchiveMapper baseMapper;

    @Value("${collect.ipPort:}")
    private String ipPort;
    @Value("${collect.username:}")
    private String username;
    @Value("${collect.password:}")
    private String password;

    @Override
    public PrHeatTempArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatTempArchiveVo> selectPageList(String orgId, String buildingId,
                                                              String unit, String search, String parentId,
                                                              PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        // parentId maps to houseId for this entity
        lqw.eq(StringUtils.isNotBlank(parentId), PrHeatTempArchive::getHouseId, parentId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHeatTempArchive::getMeterNum, search.trim())
                .or().like(PrHeatTempArchive::getMeterArcName, search.trim()));
        }
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        Page<PrHeatTempArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatTempArchive entity) {
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
        // 1. 查询该小区下所有有效温采器档案
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        List<PrHeatTempArchive> archives = list(lqw);
        if (archives.isEmpty()) {
            log.info("同步-未找到温采器档案数据, orgId={}", orgId);
            return false;
        }

        // 2. 构建同步 JSON（type=3 代表温采器）
        JSONArray dataArray = new JSONArray();
        for (PrHeatTempArchive archive : archives) {
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
            payload.set("type", 3);
            payload.set("data", batch);
            boolean batchResult = CollectPlatformUtil.informationSynchronization(payload, ipPort, username, password);
            if (!batchResult) {
                result = false;
            }
        }

        log.info("同步户温采器信息到采集平台, orgId={}, count={}, result={}",
            orgId, archives.size(), result);
        return result;
    }

    @Override
    public List<PrHeatTempArchiveVo> listSyncData(String orgId) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatTempArchiveVo> listAll(String orgId) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> importTempArchive(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }

        List<PrHeatTempArchiveDto> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream())
                .head(PrHeatTempArchiveDto.class)
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
        for (PrHeatTempArchiveDto dto : dataList) {
            if (StringUtils.isBlank(dto.getMeterNum()) || StringUtils.isBlank(dto.getMeterArcCode())) {
                failCount++;
                continue;
            }
            // 检查是否已存在
            long count = count(new LambdaQueryWrapper<PrHeatTempArchive>()
                .eq(PrHeatTempArchive::getMeterNum, dto.getMeterNum())
                .eq(PrHeatTempArchive::getMeterArcCode, dto.getMeterArcCode())
                .eq(PrHeatTempArchive::getIsChanged, 0));
            if (count > 0) {
                failCount++;
                continue;
            }
            PrHeatTempArchive entity = new PrHeatTempArchive();
            entity.setMeterNum(dto.getMeterNum());
            entity.setMeterArcCode(dto.getMeterArcCode());
            entity.setMeterArcName(dto.getMeterArcName());
            entity.setCardNum(dto.getCardNum());
            entity.setConcentratorCode(dto.getConcentratorCode());
            entity.setImeiNum(dto.getImeiNum());
            entity.setDeviceId(dto.getDeviceId());
            entity.setHouseId(dto.getHouseId());
            entity.setOrgId(dto.getOrgId());
            entity.setIsChanged(0);
            entity.setIsStop(0);
            entity.setValveStatus(dto.getValveStatus());
            entity.setTemper(dto.getTemper());
            entity.setHumidity(dto.getHumidity());
            entity.setVoltage(dto.getVoltage());
            entity.setSignalStrength(dto.getSignalStrength());
            entity.setReportingInterval(dto.getReportingInterval());
            entity.setIntervalUnit(dto.getIntervalUnit());
            entity.setValidTime(dto.getValidTime());
            entity.setCollectInterval(dto.getCollectInterval());
            entity.setCollectUnit(dto.getCollectUnit());
            entity.setCollectNum(dto.getCollectNum());
            entity.setMovPlace(dto.getMovPlace());
            save(entity);
            successCount++;
        }
        log.info("温采器导入完成, success={}, fail={}", successCount, failCount);
        return R.ok();
    }
}
