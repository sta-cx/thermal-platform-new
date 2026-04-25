package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrImportValve;
import org.sdkj.thermal.mapper.PrImportValveMapper;
import org.sdkj.thermal.service.IPrImportValveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阀门导入服务实现
 */
@Service
@RequiredArgsConstructor
public class PrImportValveServiceImpl extends ServiceImpl<PrImportValveMapper, PrImportValve>
    implements IPrImportValveService {

    private final PrImportValveMapper prImportValveMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<Object> objects) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        Date date = new Date();
        List<PrImportValve> lists = new ArrayList<>();
        if (!objects.isEmpty()) {
            for (Object object : objects) {
                PrImportValve valve = (PrImportValve) object;
                valve.setCreateBy(create);
                valve.setCompanyId(companyId);
                valve.setCreateTime(date);
                valve.setType(0);

                Integer dtuType = valve.getDtuType();
                if (dtuType == null || dtuType < 0 || dtuType > 2) {
                    valve.setDtuType(0);
                }

                if (!StringUtils.hasText(valve.getCaliber())) {
                    valve.setCaliber("65");
                }

                lists.add(valve);
            }
            prImportValveMapper.insert(lists);
        }
        return lists.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHouseId() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        prImportValveMapper.updateHouseId(create, companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        prImportValveMapper.updateMeter(create, companyId);
        prImportValveMapper.updateCommandMeter(create, companyId);
    }

    @Override
    public String getNull(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();

        List<PrImportValve> noHouseIds = prImportValveMapper.selectNoHouseId(create, companyId);
        if (!noHouseIds.isEmpty()) {
            return "下列房屋信息无法匹配，请核对信息！";
        }

        List<PrImportValve> noMeterNums = prImportValveMapper.selectNoMeterNum(create, companyId);
        if (!noMeterNums.isEmpty()) {
            return "下列房屋无表号，请核对信息！";
        }

        List<PrImportValve> list = prImportValveMapper.getRepateMeter2(create, companyId);
        if (!list.isEmpty()) {
            return "下列表号重复，请核对信息！";
        }

        List<PrImportValve> noArchiveId = prImportValveMapper.findNoArchiveId(create, companyId);
        if (!noArchiveId.isEmpty()) {
            return "下列房屋无法匹配仪表名称，请核对信息！";
        }

        List<PrImportValve> hasAllHouse = prImportValveMapper.selectHasAllHouse(create, companyId);
        if (!hasAllHouse.isEmpty()) {
            return "下列房屋已绑定阀门，请核对信息！";
        }

        List<PrImportValve> hasAllReadly = prImportValveMapper.selectHasAllReadly(create, companyId);
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
        prImportValveMapper.deleteData(create, companyId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        prImportValveMapper.deleteHeatValveByNoHouseId(create, companyId);
        prImportValveMapper.submitData(create, companyId);
        prImportValveMapper.deleteImportValveData(create, companyId);
        return true;
    }

    @Override
    public Integer select() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return prImportValveMapper.select(create, companyId);
    }

    @Override
    public List<PrImportValve> selectByCompanyIdOrgId(String companyId, String orgId) {
        return prImportValveMapper.selectByCompanyIdOrgId(companyId, orgId);
    }
}
