package org.sdkj.thermal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
import org.sdkj.thermal.domain.PrHeatDtuArchive;
import org.sdkj.thermal.domain.bo.PrHeatDtuArchiveBo;
import org.sdkj.thermal.domain.dto.PrHeatDtuArchiveDto;
import org.sdkj.thermal.domain.vo.PrHeatDtuArchiveVo;
import org.sdkj.thermal.mapper.PrHeatDtuArchiveMapper;
import org.sdkj.thermal.service.IPrHeatDtuArchiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * DTU采集器配表 Service 实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrHeatDtuArchiveServiceImpl extends ServiceImpl<PrHeatDtuArchiveMapper, PrHeatDtuArchive> implements IPrHeatDtuArchiveService {

    private final PrHeatDtuArchiveMapper baseMapper;

    @Override
    public PrHeatDtuArchiveVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHeatDtuArchiveVo> selectPageList(String orgId, String search,
                                                             String status, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatDtuArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatDtuArchive::getOrgId, orgId);
        if (StringUtils.isNotBlank(search)) {
            lqw.like(PrHeatDtuArchive::getDtuNum, search.trim());
        }
        lqw.eq(StringUtils.isNotBlank(status), PrHeatDtuArchive::getStatus, status);
        // DTU has no isChanged field, no filter needed
        lqw.orderByDesc(PrHeatDtuArchive::getCreateTime);
        Page<PrHeatDtuArchiveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean queryMeter(PrHeatDtuArchiveBo bo) {
        String createBy = StpUtil.getLoginIdAsString();
        String orgId = bo.getOrgId();
        String dtuNum = bo.getDtuNum();

        boolean result = true;

        // 生成户阀查询指令
        result = baseMapper.setTasksPerformValve(orgId, dtuNum, createBy) && result;

        // 生成单元阀查询指令
        result = baseMapper.setTasksPerformUnitValve(orgId, dtuNum, createBy) && result;

        // 生成热表查询指令
        result = baseMapper.setTasksPerformHot(orgId, dtuNum, createBy) && result;

        // 生成单元热表查询指令
        result = baseMapper.setTasksPerformUnitHot(orgId, dtuNum, createBy) && result;

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatDtuArchive entity) {
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
    public List<PrHeatDtuArchiveVo> listAll(String orgId) {
        LambdaQueryWrapper<PrHeatDtuArchive> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHeatDtuArchive::getOrgId, orgId);
        lqw.orderByDesc(PrHeatDtuArchive::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> importDtuArchive(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }

        List<PrHeatDtuArchiveDto> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream())
                .head(PrHeatDtuArchiveDto.class)
                .sheet(0)
                .headRowNumber(1)
                .doReadSync();
        } catch (Exception e) {
            log.error("PrHeatDtuArchiveServiceImpl operation failed", e);
            return R.fail("文件解析失败: " + e.getMessage());
        }

        if (dataList == null || dataList.isEmpty()) {
            return R.fail("导入数据为空");
        }

        int successCount = 0;
        int failCount = 0;
        for (PrHeatDtuArchiveDto dto : dataList) {
            if (StringUtils.isBlank(dto.getDtuNum())) {
                failCount++;
                continue;
            }
            // 检查是否已存在
            long count = count(new LambdaQueryWrapper<PrHeatDtuArchive>()
                .eq(PrHeatDtuArchive::getDtuNum, dto.getDtuNum()));
            if (count > 0) {
                failCount++;
                continue;
            }
            PrHeatDtuArchive entity = new PrHeatDtuArchive();
            entity.setDtuNum(dto.getDtuNum());
            entity.setInstallSite(dto.getInstallSite());
            entity.setStatus(dto.getStatus());
            entity.setIp(dto.getIp());
            entity.setChanNum(dto.getChanNum());
            entity.setChannelNum(dto.getChannelNum());
            entity.setChannelNumTime(dto.getChannelNumTime());
            entity.setLatestTime(dto.getLatestTime());
            entity.setLastTime(dto.getLastTime());
            entity.setOrgId(dto.getOrgId());
            save(entity);
            successCount++;
        }
        log.info("DTU采集器配表导入完成, success={}, fail={}", successCount, failCount);
        return R.ok();
    }
}
