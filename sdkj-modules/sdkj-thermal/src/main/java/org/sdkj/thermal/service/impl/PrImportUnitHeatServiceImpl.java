package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrImportUnitHeat;
import org.sdkj.thermal.mapper.PrImportUnitHeatMapper;
import org.sdkj.thermal.service.IPrImportUnitHeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrImportUnitHeatServiceImpl extends ServiceImpl<PrImportUnitHeatMapper, PrImportUnitHeat>
    implements IPrImportUnitHeatService {

    private final PrImportUnitHeatMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<PrImportUnitHeat> objects) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        Date date = new Date();
        List<PrImportUnitHeat> lists = new ArrayList<>();
        if (!objects.isEmpty()) {
            for (PrImportUnitHeat item : objects) {
                item.setCreateBy(create);
                item.setCompanyId(companyId);
                item.setCreateTime(date);
                item.setType(0);

                Integer dtuType = item.getDtuType();
                if (dtuType == null || dtuType < 0 || dtuType > 2) {
                    item.setDtuType(0);
                }
                if (item.getStartReading() == null) {
                    item.setStartReading(BigDecimal.ZERO);
                }
                if (item.getCurrentReading() == null) {
                    item.setCurrentReading(BigDecimal.ZERO);
                }
                if (item.getTotalUsed() == null) {
                    item.setTotalUsed(BigDecimal.ZERO);
                }
                if (item.getTradeTimes() == null) {
                    item.setTradeTimes(0);
                }
                if (item.getTotalMoney() == null) {
                    item.setTotalMoney(BigDecimal.ZERO);
                }
                if (item.getTotalRecharge() == null) {
                    item.setTotalRecharge("0");
                }
                if (item.getCurrentBalance() == null) {
                    item.setCurrentBalance(BigDecimal.ZERO);
                }

                lists.add(item);
            }
            mapper.insert(lists);
        }
        return lists.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIds() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.updateOrgId(create, companyId);
        mapper.updateBuildingId(create, companyId);
        mapper.updateUnitId(create, companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.updateMeter(create, companyId);
        mapper.updateStandard(create, companyId);
    }

    @Override
    public String getNull(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();

        List<PrImportUnitHeat> noOrgIds = mapper.queryNoOrgIds(create, companyId);
        if (!noOrgIds.isEmpty()) {
            return "下列小区信息无法匹配，请核对信息！";
        }
        List<PrImportUnitHeat> noBuildingIds = mapper.queryNoBuildingIds(create, companyId);
        if (!noBuildingIds.isEmpty()) {
            return "下列楼宇信息无法匹配，请核对信息！";
        }
        List<PrImportUnitHeat> noUnitIds = mapper.queryNoUnitIds(create, companyId);
        if (!noUnitIds.isEmpty()) {
            return "下列单元信息无法匹配，请核对信息！";
        }
        List<PrImportUnitHeat> noMeterNums = mapper.selectNoMeterNum(create, companyId);
        if (!noMeterNums.isEmpty()) {
            return "下列单元无表号，请核对信息！";
        }
        List<PrImportUnitHeat> repate = mapper.selectRepateMeterNum(create, companyId);
        if (!repate.isEmpty()) {
            return "下列表号重复，请核对信息！";
        }
        List<PrImportUnitHeat> noArchiveId = mapper.findNoArchiveId(create, companyId);
        if (!noArchiveId.isEmpty()) {
            return "下列单元无法匹配仪表名称，请核对信息！";
        }
        List<PrImportUnitHeat> noStandardId = mapper.findNoStandardId(create, companyId);
        if (!noStandardId.isEmpty()) {
            return "下列单元无法匹配单价，请核对信息！";
        }
        List<PrImportUnitHeat> errorMoney = mapper.findErrorMoney(create, companyId);
        if (!errorMoney.isEmpty()) {
            return "下列金额核对不上，请核对信息！";
        }
        List<PrImportUnitHeat> meterSerialErrors = mapper.selectMeterSerial(create, companyId);
        if (!meterSerialErrors.isEmpty()) {
            return "下列子表号设置错误，请核对信息！";
        }
        List<PrImportUnitHeat> numericalErrors = mapper.selectNumericalErrors(create, companyId);
        if (!numericalErrors.isEmpty()) {
            return "下列当前读数小于起始读数，请核对信息！";
        }
        List<PrImportUnitHeat> hasAllUnit = mapper.hasAllUnit(create, companyId);
        if (!hasAllUnit.isEmpty()) {
            return "下列单元已绑定热表，请核对信息！";
        }
        List<PrImportUnitHeat> hasAllReadly = mapper.hasAllReadly(create, companyId);
        if (!hasAllReadly.isEmpty()) {
            return "下列表号系统中已存在，请核对信息！";
        }

        return "成功导入" + r + "条记录！\n请提交数据！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.deleteData(create, companyId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.submitData(create, companyId);
        mapper.deleteImportUnitHeatData(create, companyId);
        return true;
    }

    @Override
    public Integer select() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return mapper.select(create, companyId);
    }

    @Override
    public List<PrImportUnitHeat> selectByCompanyIdOrgId(String companyId, String orgId) {
        return mapper.selectByCompanyIdOrgId(companyId, orgId);
    }
}
