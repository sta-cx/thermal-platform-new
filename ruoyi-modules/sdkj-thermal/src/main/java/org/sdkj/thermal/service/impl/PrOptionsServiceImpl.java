package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrOptions;
import org.sdkj.thermal.mapper.PrOptionsMapper;
import org.sdkj.thermal.service.IPrOptionsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统选项 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrOptionsServiceImpl extends ServiceImpl<PrOptionsMapper, PrOptions>
        implements IPrOptionsService {

    private final PrOptionsMapper baseMapper;

    @Override
    public List<PrOptions> selectByOrgId(String orgId) {
        return lambdaQuery()
            .eq(PrOptions::getOrgId, orgId)
            .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initOptions(String orgId) {
        PrOptions defaultOption = new PrOptions();
        defaultOption.setOrgId(orgId);
        return save(defaultOption);
    }

    @Override
    public boolean forbiddenToBuyCheck(String houseId) {
        // TODO: 根据房屋关联的组织查询选项，判断是否禁止购买
        return false;
    }
}
