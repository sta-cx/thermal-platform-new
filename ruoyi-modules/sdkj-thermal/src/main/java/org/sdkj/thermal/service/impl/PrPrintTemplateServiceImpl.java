package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.thermal.domain.PrPrintTemplate;
import org.sdkj.thermal.mapper.PrPrintTemplateMapper;
import org.sdkj.thermal.service.IPrPrintTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 打印模板 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrPrintTemplateServiceImpl extends ServiceImpl<PrPrintTemplateMapper, PrPrintTemplate>
        implements IPrPrintTemplateService {

    private final PrPrintTemplateMapper baseMapper;

    @Override
    public List<PrPrintTemplate> listByOrgAndCompany(String companyId, String orgId, String name) {
        LambdaQueryWrapper<PrPrintTemplate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrPrintTemplate::getCompanyId, companyId)
                .eq(StringUtils.isNotBlank(orgId), PrPrintTemplate::getOrgId, orgId)
                .like(StringUtils.isNotBlank(name), PrPrintTemplate::getName, name);
        return baseMapper.selectList(lqw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateTemplate(String companyId, String orgId, String name, String templateContent) {
        LambdaQueryWrapper<PrPrintTemplate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrPrintTemplate::getCompanyId, companyId)
                .eq(PrPrintTemplate::getOrgId, orgId)
                .eq(PrPrintTemplate::getName, name);
        PrPrintTemplate existing = baseMapper.selectOne(lqw);
        if (existing != null) {
            existing.setTemplateContent(templateContent);
            return updateById(existing);
        }
        PrPrintTemplate template = new PrPrintTemplate();
        template.setCompanyId(companyId);
        template.setOrgId(orgId);
        template.setName(name);
        template.setTemplateContent(templateContent);
        return save(template);
    }

    @Override
    public PrPrintTemplate getBySerialNum(String serialNum) {
        LambdaQueryWrapper<PrPrintTemplate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrPrintTemplate::getSerialNum, serialNum)
                .last("LIMIT 1");
        return baseMapper.selectOne(lqw);
    }
}
