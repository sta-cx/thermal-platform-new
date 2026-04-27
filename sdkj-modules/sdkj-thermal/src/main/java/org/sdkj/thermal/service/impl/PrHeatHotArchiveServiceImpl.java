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
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.service.IPrHeatHotArchiveService;
import org.sdkj.thermal.utils.CollectPlatformUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TableDataInfo<PrHeatHotArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                             String unit, String search, String parentId,
                                                             PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatHotArchive::getCompanyId, companyId);
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
    public boolean valveInformationSynchronization(String orgId, String companyId) {
        // 1. 查询该小区下所有有效热量表档案
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatHotArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        List<PrHeatHotArchive> archives = list(lqw);
        if (archives.isEmpty()) {
            log.info("同步-未找到热量表档案数据, orgId={}, companyId={}", orgId, companyId);
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
            payload.set("type", 2);
            payload.set("data", batch);
            boolean batchResult = CollectPlatformUtil.informationSynchronization(payload, ipPort, username, password);
            if (!batchResult) {
                result = false;
            }
        }

        log.info("同步户热表信息到采集平台, orgId={}, companyId={}, count={}, result={}",
            orgId, companyId, archives.size(), result);
        return result;
    }

    @Override
    public List<PrHeatHotArchiveVo> listSyncData(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatHotArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatHotArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHeatHotArchiveVo> listAll(String companyId, String orgId) {
        LambdaQueryWrapper<PrHeatHotArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHeatHotArchive::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatHotArchive::getOrgId, orgId);
        lqw.eq(PrHeatHotArchive::getIsChanged, 0);
        lqw.orderByDesc(PrHeatHotArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }
}
