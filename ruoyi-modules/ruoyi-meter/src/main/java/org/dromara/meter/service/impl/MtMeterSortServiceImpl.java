package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtMeterSort;
import org.dromara.meter.domain.vo.MtMeterSortVo;
import org.dromara.meter.mapper.MtMeterSortMapper;
import org.dromara.meter.service.IMtMeterSortService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仪表分类服务实现
 */
@Service
@RequiredArgsConstructor
public class MtMeterSortServiceImpl extends ServiceImpl<MtMeterSortMapper, MtMeterSort> implements IMtMeterSortService {

    private final MtMeterSortMapper baseMapper;

    @Override
    public TableDataInfo<MtMeterSortVo> selectPageList(LambdaQueryWrapper<MtMeterSort> lqw, PageQuery pageQuery) {
        Page<MtMeterSortVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public int verifyName(String name, String id) {
        LambdaQueryWrapper<MtMeterSort> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MtMeterSort::getName, name);
        if (id != null && !id.isEmpty()) {
            lqw.ne(MtMeterSort::getId, id);
        }
        return Math.toIntExact(this.count(lqw));
    }

    @Override
    public int countBySortId(String sortId, String meterType) {
        return baseMapper.countBySortId(sortId, meterType);
    }

    @Override
    public List<MtMeterSortVo> selectList(LambdaQueryWrapper<MtMeterSort> lqw) {
        return baseMapper.selectVoList(lqw);
    }
}
