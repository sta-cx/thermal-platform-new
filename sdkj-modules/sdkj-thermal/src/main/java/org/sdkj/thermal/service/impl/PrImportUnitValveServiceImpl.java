package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrImportUnitValve;
import org.sdkj.thermal.mapper.PrImportUnitValveMapper;
import org.sdkj.thermal.service.IPrImportUnitValveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单元阀门导入服务实现
 * 迁移自旧系统 PrImportUnitValveServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrImportUnitValveServiceImpl extends ServiceImpl<PrImportUnitValveMapper, PrImportUnitValve>
    implements IPrImportUnitValveService {

    private final PrImportUnitValveMapper prImportUnitValveMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<PrImportUnitValve> objects) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        Date date = new Date();
        List<PrImportUnitValve> lists = new ArrayList<>();
        if (!objects.isEmpty()) {
            for (PrImportUnitValve prImportUnitValve : objects) {
                prImportUnitValve.setCreateBy(create);
                prImportUnitValve.setCompanyId(companyId);
                prImportUnitValve.setCreateTime(date);
                prImportUnitValve.setType(0);

                Integer dtuType = prImportUnitValve.getDtuType();
                if (dtuType == null || dtuType < 0 || dtuType > 2) {
                    prImportUnitValve.setDtuType(0);
                }

                if (!StringUtils.hasText(prImportUnitValve.getCaliber())) {
                    prImportUnitValve.setCaliber("65");
                }

                lists.add(prImportUnitValve);
            }
            prImportUnitValveMapper.insert(lists);
        }
        return lists.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHouseId() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        // 更新单元ID及小区ID
        prImportUnitValveMapper.updateOrgId(create, companyId);
        prImportUnitValveMapper.updateBuildingId(create, companyId);
        prImportUnitValveMapper.updateUnitId(create, companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        // 匹配阀门档案信息
        prImportUnitValveMapper.updateMeter(create, companyId);
        // 匹配有阀门控制档案信息
        prImportUnitValveMapper.updateCommandMeter(create, companyId);
    }

    @Override
    public String getNull(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();

        // 小区ID匹配不到的记录
        List<PrImportUnitValve> noOrgIds = prImportUnitValveMapper.selectNoOrgIds(create, companyId);
        if (!noOrgIds.isEmpty()) {
            return "下列小区信息无法匹配，请核对信息！";
        }

        List<PrImportUnitValve> noBuildingIds = prImportUnitValveMapper.selectNoBuildingIds(create, companyId);
        if (!noBuildingIds.isEmpty()) {
            return "下列楼宇信息无法匹配，请核对信息！";
        }

        // 单元ID匹配不到的记录
        List<PrImportUnitValve> lists = prImportUnitValveMapper.selectNoUnitId(create, companyId);
        if (!lists.isEmpty()) {
            return "下列单元信息无法匹配，请核对信息！";
        }

        // 没有表号的记录
        List<PrImportUnitValve> noMeterNums = prImportUnitValveMapper.selectNoMeterNum(create, companyId);
        if (!noMeterNums.isEmpty()) {
            return "下列单元无表号，请核对信息！";
        }

        // 表号重复的记录
        List<PrImportUnitValve> list = prImportUnitValveMapper.getRepateUnitMeter2(create, companyId);
        if (!list.isEmpty()) {
            return "下列单元表号重复，请核对信息！";
        }

        // 查询出没有仪表ID的记录
        List<PrImportUnitValve> noArchiveId = prImportUnitValveMapper.findNoArchiveId(create, companyId);
        if (!noArchiveId.isEmpty()) {
            return "下列单元无法匹配仪表名称，请核对信息！";
        }

        List<PrImportUnitValve> hasAllUnit = prImportUnitValveMapper.selectHasAllByUnit(create, companyId);
        if (!hasAllUnit.isEmpty()) {
            return "下列单元已绑定阀门，请核对信息！";
        }

        List<PrImportUnitValve> hasAllReadly = prImportUnitValveMapper.selectHasAllReadly(create, companyId);
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
        prImportUnitValveMapper.deleteData(create, companyId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        prImportUnitValveMapper.submitData(create, companyId);
        prImportUnitValveMapper.deleteImportUnitValveData(create, companyId);
        return true;
    }

    @Override
    public Integer select() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return prImportUnitValveMapper.select(create, companyId);
    }

    @Override
    public List<PrImportUnitValve> selectByCompanyIdOrgId(String companyId, String orgId) {
        return prImportUnitValveMapper.selectByCompanyIdOrgId(companyId, orgId);
    }
}
