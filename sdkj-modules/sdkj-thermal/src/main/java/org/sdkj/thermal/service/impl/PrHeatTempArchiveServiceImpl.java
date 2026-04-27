package org.sdkj.thermal.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;
import org.sdkj.thermal.mapper.PrHeatTempArchiveMapper;
import org.sdkj.thermal.service.IPrHeatTempArchiveService;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TableDataInfo<PrHeatTempArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                              String unit, String search, String parentId,
                                                              PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatTempArchive::getCompanyId, companyId);
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
    public boolean valveInformationSynchronization(String orgId, String companyId) {
        // 1. 查询该小区下所有有效温采器档案
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatTempArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        List<PrHeatTempArchive> archives = list(lqw);
        if (archives.isEmpty()) {
            log.info("同步-未找到温采器档案数据, orgId={}, companyId={}", orgId, companyId);
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
            payload.set("type", 3);
            payload.set("data", batch);
            boolean batchResult = CollectPlatformUtil.informationSynchronization(payload, ipPort, username, password);
            if (!batchResult) {
                result = false;
            }
        }

        log.info("同步户温采器信息到采集平台, orgId={}, companyId={}, count={}, result={}",
            orgId, companyId, archives.size(), result);
        return result;
    }

    @Override
    public List<PrHeatTempArchiveVo> listSyncData(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatTempArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatTempArchiveVo> listAll(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatTempArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatTempArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatTempArchive::getOrgId, orgId);
        lqw.eq(PrHeatTempArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatTempArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }
}
