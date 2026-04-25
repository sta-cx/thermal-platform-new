package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrRepairPerson;
import org.sdkj.thermal.mapper.PrRepairPersonMapper;
import org.sdkj.thermal.service.IPrRepairPersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrRepairPersonServiceImpl extends ServiceImpl<PrRepairPersonMapper, PrRepairPerson>
        implements IPrRepairPersonService {

    private final PrRepairPersonMapper baseMapper;

    @Override
    public List<PrRepairPerson> selectByCompanyId(String companyId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<PrRepairPerson>()
                .eq(PrRepairPerson::getCompanyId, companyId));
    }

    @Override
    public List<PrRepairPerson> selectByOrgId(String orgId, String companyId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<PrRepairPerson>()
                .eq(PrRepairPerson::getOrgId, orgId)
                .eq(PrRepairPerson::getCompanyId, companyId));
    }
}
