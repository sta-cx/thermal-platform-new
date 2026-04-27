package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.vo.PrHeatReadingCopy1Vo;
import org.sdkj.thermal.domain.vo.PrHeatReadingLabelVo;
import org.sdkj.thermal.mapper.PrHeatReadingMapper;
import org.sdkj.thermal.service.IPrHeatReadingCopy1Service;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 热表抄表数据副本 Service 实现
 * 远传抄表数据一般不分物业公司，手工抄表的数据需要携带物业公司及小区
 * 此类远传抄表数据不能更新，每次都要记录
 */
@Service
@RequiredArgsConstructor
public class PrHeatReadingCopy1ServiceImpl implements IPrHeatReadingCopy1Service {

    private final PrHeatReadingMapper baseMapper;

    @Override
    public Page<PrHeatReadingCopy1Vo> pageList(List<PrHeatReadingLabelVo> labels, String startTime,
                                                String endTime, Page<?> page) {
        List<PrHeatReadingCopy1Vo> list = baseMapper.selectPageListCopy1(labels, startTime, endTime);
        // 聚合查询使用手动分页（GROUP BY + 聚合函数不适合自动分页）
        long total = list.size();
        long current = page.getCurrent();
        long size = page.getSize();
        long fromIndex = (current - 1) * size;
        long toIndex = Math.min(fromIndex + size, total);
        if (fromIndex < total) {
            list = list.subList((int) fromIndex, (int) toIndex);
        } else {
            list = List.of();
        }
        Page<PrHeatReadingCopy1Vo> result = new Page<>(current, size, total);
        result.setRecords(list);
        return result;
    }

    @Override
    public Page<PrHeatReadingCopy1Vo> pageHeatReadingList(String companyId, String orgId, String buildingId,
                                                           String unitCode, String meterArcCode, String search,
                                                           Page<?> page) {
        // 旧系统使用 MyBatis-Plus 自动分页，此处保持一致
        return baseMapper.selectPageHeatReadingList(page, companyId, orgId, buildingId,
            unitCode, meterArcCode, search);
    }
}
