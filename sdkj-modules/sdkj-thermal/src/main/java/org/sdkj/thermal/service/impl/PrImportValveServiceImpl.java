package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.constant.ThermalTaskConstants;
import org.sdkj.thermal.domain.PrImportValve;
import org.sdkj.thermal.mapper.PrImportValveMapper;
import org.sdkj.thermal.service.IPrImportValveService;
import org.sdkj.thermal.service.OrgAccessService;
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
    private final OrgAccessService orgAccessService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<PrImportValve> objects) {
        String create = LoginHelper.getUserIdStr();
        Date date = new Date();
        List<PrImportValve> lists = new ArrayList<>();
        if (!objects.isEmpty()) {
            for (PrImportValve valve : objects) {
                valve.setCreateBy(create);
                valve.setCreateTime(date);
                valve.setType(ThermalTaskConstants.IMPORT_TYPE_DEFAULT);

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
        prImportValveMapper.updateHouseId(create);
        orgAccessService.assertCurrentUserCanAccessOrgIds(prImportValveMapper.selectImportedOrgIds(create));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check(Integer r) {
        String create = LoginHelper.getUserIdStr();
        prImportValveMapper.updateMeter(create);
        prImportValveMapper.updateCommandMeter(create);
    }

    @Override
    public String getNull(Integer r) {
        String create = LoginHelper.getUserIdStr();

        List<PrImportValve> noHouseIds = prImportValveMapper.selectNoHouseId(create);
        if (!noHouseIds.isEmpty()) {
            return "下列房屋信息无法匹配，请核对信息！";
        }

        List<PrImportValve> noMeterNums = prImportValveMapper.selectNoMeterNum(create);
        if (!noMeterNums.isEmpty()) {
            return "下列房屋无表号，请核对信息！";
        }

        List<PrImportValve> list = prImportValveMapper.getRepateMeter2(create);
        if (!list.isEmpty()) {
            return "下列表号重复，请核对信息！";
        }

        List<PrImportValve> noArchiveId = prImportValveMapper.findNoArchiveId(create);
        if (!noArchiveId.isEmpty()) {
            return "下列房屋无法匹配仪表名称，请核对信息！";
        }

        List<PrImportValve> hasAllHouse = prImportValveMapper.selectHasAllHouse(create);
        if (!hasAllHouse.isEmpty()) {
            return "下列房屋已绑定阀门，请核对信息！";
        }

        List<PrImportValve> hasAllReadly = prImportValveMapper.selectHasAllReadly(create);
        if (!hasAllReadly.isEmpty()) {
            return "下列表号系统中已存在，请核对信息！";
        }

        return "成功导入" + r + "条记录！\n请提交数据！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData() {
        String create = LoginHelper.getUserIdStr();
        prImportValveMapper.deleteData(create);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        orgAccessService.assertCurrentUserCanAccessOrgIds(prImportValveMapper.selectImportedOrgIds(create));
        prImportValveMapper.deleteHeatValveByNoHouseId(create);
        prImportValveMapper.submitData(create);
        prImportValveMapper.deleteImportValveData(create);
        return true;
    }

    @Override
    public Integer select() {
        String create = LoginHelper.getUserIdStr();
        return prImportValveMapper.select(create);
    }

    @Override
    public List<PrImportValve> selectByOrgId(String orgId) {
        orgAccessService.assertCurrentUserCanAccessOrg(orgId);
        return prImportValveMapper.selectByOrgId(orgId);
    }
}
