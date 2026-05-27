package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.constant.ThermalTaskConstants;
import org.sdkj.thermal.domain.PrImportHeat;
import org.sdkj.thermal.mapper.PrImportHeatMapper;
import org.sdkj.thermal.service.IPrImportHeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrImportHeatServiceImpl extends ServiceImpl<PrImportHeatMapper, PrImportHeat>
    implements IPrImportHeatService {

    private final PrImportHeatMapper prImportHeatMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<PrImportHeat> objects) {
        String create = LoginHelper.getUserIdStr();
        Date date = new Date();
        List<PrImportHeat> lists = new ArrayList<>();
        if (!objects.isEmpty()) {
            for (PrImportHeat heat : objects) {
                heat.setCreateBy(create);
                heat.setCreateTime(date);
                heat.setType(ThermalTaskConstants.IMPORT_TYPE_DEFAULT);

                Integer dtuType = heat.getDtuType();
                if (dtuType == null || dtuType < 0 || dtuType > 2) {
                    heat.setDtuType(0);
                }

                BigDecimal startReading = heat.getStartReading();
                if (startReading == null) {
                    heat.setStartReading(BigDecimal.ZERO);
                }
                BigDecimal currentReading = heat.getCurrentReading();
                if (currentReading == null) {
                    heat.setCurrentReading(BigDecimal.ZERO);
                }
                if (heat.getTotalUsed() == null) {
                    heat.setTotalUsed(BigDecimal.ZERO);
                }
                if (heat.getTradeTimes() == null) {
                    heat.setTradeTimes(0);
                }
                if (heat.getTotalMoney() == null) {
                    heat.setTotalMoney(BigDecimal.ZERO);
                }
                if (heat.getTotalRecharge() == null) {
                    heat.setTotalRecharge("0");
                }
                if (heat.getCurrentBalance() == null) {
                    heat.setCurrentBalance(BigDecimal.ZERO);
                }

                lists.add(heat);
            }
            prImportHeatMapper.insert(lists);
        }
        return lists.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHouseId() {
        String create = LoginHelper.getUserIdStr();
        prImportHeatMapper.updateHouseId(create);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void check(Integer r) {
        String create = LoginHelper.getUserIdStr();
        prImportHeatMapper.updateMeter(create);
        prImportHeatMapper.updateStandard(create);
    }

    @Override
    public String getNull(Integer r) {
        String create = LoginHelper.getUserIdStr();

        List<PrImportHeat> noHouseIds = prImportHeatMapper.selectNoHouseId(create);
        if (!noHouseIds.isEmpty()) {
            return "下列房屋信息无法匹配，请核对信息！";
        }

        List<PrImportHeat> noMeterNums = prImportHeatMapper.selectNoMeterNum(create);
        if (!noMeterNums.isEmpty()) {
            return "下列房屋无表号，请核对信息！";
        }

        List<PrImportHeat> repate = prImportHeatMapper.selectRepateMeterNum(create);
        if (!repate.isEmpty()) {
            return "下列表号重复，请核对信息！";
        }

        List<PrImportHeat> noArchiveId = prImportHeatMapper.findNoArchiveId(create);
        if (!noArchiveId.isEmpty()) {
            return "下列房屋无法匹配仪表名称，请核对信息！";
        }

        List<PrImportHeat> noStandardId = prImportHeatMapper.findNoStandardId(create);
        if (!noStandardId.isEmpty()) {
            return "下列房屋无法匹配单价，请核对信息！";
        }

        List<PrImportHeat> errorMoney = prImportHeatMapper.findErrorMoney(create);
        if (!errorMoney.isEmpty()) {
            return "下列金额核对不上，请核对信息！";
        }

        List<PrImportHeat> meterSerialErrors = prImportHeatMapper.selectMeterSerial(create);
        if (!meterSerialErrors.isEmpty()) {
            return "下列子表号设置错误，请核对信息！";
        }

        List<PrImportHeat> numericalErrors = prImportHeatMapper.selectNumericalErrors(create);
        if (!numericalErrors.isEmpty()) {
            return "下列当前读数小于起始读数，请核对信息！";
        }

        List<PrImportHeat> hasAllHouse = prImportHeatMapper.hasAllHouse(create);
        if (!hasAllHouse.isEmpty()) {
            return "下列房屋已绑定热表，请核对信息！";
        }

        List<PrImportHeat> hasAllReadly = prImportHeatMapper.hasAllReadly(create);
        if (!hasAllReadly.isEmpty()) {
            return "下列表号系统中已存在，请核对信息！";
        }

        return "成功导入" + r + "条记录！\n请提交数据！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData() {
        String create = LoginHelper.getUserIdStr();
        prImportHeatMapper.deleteData(create);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        prImportHeatMapper.deleteHeatByNoHouseId(create);
        prImportHeatMapper.submitData(create);
        prImportHeatMapper.deleteImportHeatData(create);
        return true;
    }

    @Override
    public Integer select() {
        String create = LoginHelper.getUserIdStr();
        return prImportHeatMapper.select(create);
    }

    @Override
    public List<PrImportHeat> selectByOrgId(String orgId) {
        return prImportHeatMapper.selectByOrgId(orgId);
    }
}
