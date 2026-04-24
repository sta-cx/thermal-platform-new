package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtMeterVendor;
import org.sdkj.meter.domain.vo.MtMeterVendorVo;
import org.sdkj.meter.mapper.MtMeterVendorMapper;
import org.sdkj.meter.service.IMtMeterVendorService;
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
