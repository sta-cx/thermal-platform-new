package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtMeterVendor;
import org.dromara.meter.domain.vo.MtMeterVendorVo;
import org.dromara.meter.mapper.MtMeterVendorMapper;
import org.dromara.meter.service.IMtMeterVendorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仪表厂商 Service 实现
 * 迁移自旧系统 MtMeterVendorServiceImpl
 */
@Service
@RequiredArgsConstructor
public class MtMeterVendorServiceImpl extends ServiceImpl<MtMeterVendorMapper, MtMeterVendor> implements IMtMeterVendorService {

    private final MtMeterVendorMapper baseMapper;

    @Override
    public TableDataInfo<MtMeterVendorVo> selectPageList(LambdaQueryWrapper<MtMeterVendor> lqw, PageQuery pageQuery) {
        Page<MtMeterVendorVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public int verifyName(String name, String id) {
        return baseMapper.verifyName(name, id);
    }

    @Override
    public int countByVendorId(String vendorId) {
        return baseMapper.countByVendorId(vendorId);
    }

    @Override
    public List<MtMeterVendorVo> selectAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

}
