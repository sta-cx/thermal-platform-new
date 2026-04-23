package org.dromara.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.meter.domain.MtTcValve;
import org.dromara.meter.domain.vo.MtTcValveVo;
import org.dromara.meter.mapper.MtTcValveMapper;
import org.dromara.meter.service.IMtTcValveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 阀门档案 Service 实现
 * 迁移自旧系统 MtTcValveServiceImpl
 */
@Service
@RequiredArgsConstructor
public class MtTcValveServiceImpl extends ServiceImpl<MtTcValveMapper, MtTcValve> implements IMtTcValveService {

    private final MtTcValveMapper baseMapper;

    @Override
    public TableDataInfo<MtTcValveVo> selectPageList(LambdaQueryWrapper<MtTcValve> lqw, PageQuery pageQuery) {
        Page<MtTcValveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<MtTcValveVo> selectValvesByUserCompany() {
        return baseMapper.selectValvesByUserCompany(LoginHelper.getUserId());
    }

    @Override
    public List<MtTcValveVo> selectList(LambdaQueryWrapper<MtTcValve> lqw) {
        return baseMapper.selectVoList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MtTcValve entity) {
        boolean saved = super.save(entity);
        if (saved) {
            int inserted = baseMapper.insertMeterToAgent(entity);
            if (inserted == 0) {
                throw new RuntimeException("未找到默认代理商公司，无法自动分配");
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        int count = baseMapper.countAllocatedToOtherCompany((String) id);
        if (count > 0) {
            throw new RuntimeException("该阀门已分配给其他公司，无法删除");
        }
        baseMapper.deleteMeterMatch((String) id);
        return super.removeById(id);
    }

}
