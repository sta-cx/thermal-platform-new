package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.service.IPrCompanyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrCompanyServiceImpl extends ServiceImpl<PrCompanyMapper, PrCompany>
        implements IPrCompanyService {

    private final PrCompanyMapper prCompanyMapper;

    @Override
    public List<PrCompany> listCompanies() {
        return prCompanyMapper.selectList(null);
    }

    @Override
    public List<SysOrganization> getOrganizationsByCompanyId(String companyId) {
        return prCompanyMapper.selectOrganizationsByCompanyId(companyId);
    }
}
