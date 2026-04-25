package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrImportHeatTemp;
import org.sdkj.thermal.mapper.PrImportHeatTempMapper;
import org.sdkj.thermal.service.IPrImportHeatTempService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrImportHeatTempServiceImpl extends ServiceImpl<PrImportHeatTempMapper, PrImportHeatTemp>
    implements IPrImportHeatTempService {

    private final PrImportHeatTempMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<Object> objects) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        Date date = new Date();
        List<PrImportHeatTemp> lists = new ArrayList<>();
        for (Object object : objects) {
            PrImportHeatTemp item = (PrImportHeatTemp) object;
            item.setCreateBy(create);
            item.setCompanyId(companyId);
            item.setCreateTime(date);
            item.setType(0);

            if (item.getDtuType() == null || item.getDtuType() < 0 || item.getDtuType() > 2) {
                item.setDtuType(0);
            }
            if (!StringUtils.hasText(item.getCaliber())) {
                item.setCaliber("65");
            }
            lists.add(item);
        }
        if (!lists.isEmpty()) {
            mapper.insert(lists);
        }
        return lists.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHouseId() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.updateHouseId(create, companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.updateMeter(create, companyId);
    }

    @Override
    public String getNull(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();

        if (!mapper.selectNoHouseId(create, companyId).isEmpty())
            return "下列房屋信息无法匹配！";
        if (!mapper.selectNoMeterNum(create, companyId).isEmpty())
            return "下列无表号！";
        if (!mapper.selectRepateMeterNum(create, companyId).isEmpty())
            return "下列表号重复！";
        if (!mapper.findNoArchiveId(create, companyId).isEmpty())
            return "下列无法匹配仪表名称！";

        return "成功导入" + r + "条记录！\n请提交数据！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return mapper.deleteData(create, companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.submitData(create, companyId);
        mapper.deleteImportHeatTempData(create, companyId);
        return true;
    }

    @Override
    public Integer select() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return mapper.select(create, companyId);
    }

    @Override
    public List<PrImportHeatTemp> selectByCompanyIdOrgId(String companyId, String orgId) {
        return mapper.selectByCompanyIdOrgId(companyId, orgId);
    }
}
