package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.vo.HtRepairVo;
import org.sdkj.thermal.mapper.HtRepairMapper;
import org.sdkj.thermal.service.IHtRepairService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 报修记录 Service 实现
 * 迁移自旧系统 HtRepairServiceImpl
 */
@Service
@RequiredArgsConstructor
public class HtRepairServiceImpl extends ServiceImpl<HtRepairMapper, HtRepair> implements IHtRepairService {

    private final HtRepairMapper baseMapper;

    @Override
    public TableDataInfo<HtRepairVo> selectPageList(LambdaQueryWrapper<HtRepair> lqw, PageQuery pageQuery) {
        Page<HtRepairVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<Map<String, Object>> selectTypeCount() {
        return baseMapper.selectTypeCount();
    }

    @Override
    public List<HtRepairVo> selectByRoomId(String roomId) {
        return baseMapper.selectByRoomId(roomId);
    }

    @Override
    public int markAsDeleted(String repairNo) {
        return baseMapper.markAsDeleted(repairNo);
    }

    @Override
    public String generateRepairNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "BX" + date;
        String maxNo = baseMapper.selectMaxRepairNoByPrefix(prefix);
        int nextSeq = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            try {
                nextSeq = Integer.parseInt(maxNo.substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return prefix + String.format("%04d", nextSeq);
    }

}
