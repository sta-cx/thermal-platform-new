package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatStation;
import org.sdkj.thermal.domain.PrHeatStationPartition;
import org.sdkj.thermal.mapper.PrHeatStationMapper;
import org.sdkj.thermal.mapper.PrHeatStationPartitionMapper;
import org.sdkj.thermal.service.IPrHeatStationPartitionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrHeatStationPartitionServiceImpl extends ServiceImpl<PrHeatStationPartitionMapper, PrHeatStationPartition>
        implements IPrHeatStationPartitionService {

    private final PrHeatStationPartitionMapper baseMapper;
    private final PrHeatStationMapper stationMapper;

    @Override
    public TableDataInfo<PrHeatStationPartition> selectPageList(String search, String stationId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHeatStationPartition> lqw = new LambdaQueryWrapper<>();
        lqw.like(search != null && !search.isEmpty(), PrHeatStationPartition::getName, search);
        if (stationId != null && !stationId.isEmpty()) {
            Long parsedStationId = parseStationId(stationId);
            if (!canAccessStation(parsedStationId)) {
                return TableDataInfo.build(List.of());
            }
            lqw.eq(PrHeatStationPartition::getStationId, parsedStationId);
        } else {
            List<Long> stationIds = accessibleStationIds();
            if (stationIds.isEmpty()) {
                return TableDataInfo.build(List.of());
            }
            lqw.in(PrHeatStationPartition::getStationId, stationIds);
        }
        lqw.orderByAsc(PrHeatStationPartition::getSeq);
        Page<PrHeatStationPartition> page = pageQuery.build();
        baseMapper.selectPage(page, lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public PrHeatStationPartition getById(Serializable id) {
        PrHeatStationPartition partition = super.getById(id);
        if (partition == null) {
            return null;
        }
        return canAccessStation(partition.getStationId()) ? partition : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHeatStationPartition entity) {
        assertStationAccessible(entity.getStationId());
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(PrHeatStationPartition entity) {
        assertExistingPartitionAccessible(entity.getId());
        if (entity.getStationId() != null) {
            assertStationAccessible(entity.getStationId());
        }
        return super.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        assertExistingPartitionAccessible(id);
        return super.removeById(id);
    }

    @Override
    public List<PrHeatStationPartition> selectByStationId(String stationId) {
        Long parsedStationId = parseStationId(stationId);
        if (!canAccessStation(parsedStationId)) {
            return List.of();
        }
        return baseMapper.selectList(
            new LambdaQueryWrapper<PrHeatStationPartition>()
                .eq(PrHeatStationPartition::getStationId, parsedStationId)
                .orderByAsc(PrHeatStationPartition::getSeq));
    }

    private Long parseStationId(String stationId) {
        try {
            return Long.valueOf(stationId.trim());
        } catch (NumberFormatException e) {
            throw new ServiceException("换热站ID格式错误");
        }
    }

    private List<Long> accessibleStationIds() {
        return stationMapper.selectList(new LambdaQueryWrapper<PrHeatStation>()
                .select(PrHeatStation::getId))
            .stream()
            .map(PrHeatStation::getId)
            .toList();
    }

    private boolean canAccessStation(Long stationId) {
        return stationId != null && stationMapper.selectById(stationId) != null;
    }

    private void assertStationAccessible(Long stationId) {
        if (!canAccessStation(stationId)) {
            throw new ServiceException("无权操作换热站数据");
        }
    }

    private void assertExistingPartitionAccessible(Serializable id) {
        PrHeatStationPartition existing = baseMapper.selectById(id);
        if (existing == null) {
            throw new ServiceException("换热站分区不存在");
        }
        assertStationAccessible(existing.getStationId());
    }
}
