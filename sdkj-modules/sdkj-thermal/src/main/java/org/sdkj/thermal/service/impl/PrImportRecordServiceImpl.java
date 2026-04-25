package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrImportRecord;
import org.sdkj.thermal.mapper.PrImportRecordMapper;
import org.sdkj.thermal.service.IPrImportRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrImportRecordServiceImpl extends ServiceImpl<PrImportRecordMapper, PrImportRecord>
    implements IPrImportRecordService {

    private final PrImportRecordMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<Object> objects) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        List<PrImportRecord> lists = new ArrayList<>();
        for (Object obj : objects) {
            if (obj instanceof PrImportRecord) {
                PrImportRecord item = (PrImportRecord) obj;
                item.setCreateBy(create);
                item.setCompanyId(companyId);
                item.setCreateTime(new Date());
                item.setType(0);
                lists.add(item);
            }
        }
        if (!lists.isEmpty()) {
            mapper.insertList(lists);
        }
        return lists.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIds() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.updateHouseId(companyId, create);
        mapper.updateItemId(companyId, create);
        mapper.updateMeterInfo(companyId, create);
    }

    @Override
    public String check(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();

        List<PrImportRecord> noOrgIds = mapper.selectNoOrgId(companyId, create);
        if (!noOrgIds.isEmpty()) return "下列小区信息无法匹配！";

        List<PrImportRecord> noHouseIds = mapper.selectNoHouseId(companyId, create);
        if (!noHouseIds.isEmpty()) return "下列房屋信息无法匹配！";

        List<PrImportRecord> noItemIds = mapper.selectNoItemId(companyId, create);
        if (!noItemIds.isEmpty()) return "下列费项信息无法匹配！";

        List<PrImportRecord> noMeterNums = mapper.selectNoMeterNum(companyId, create);
        if (!noMeterNums.isEmpty()) return "下列记录无法匹配仪表信息！";

        List<PrImportRecord> amountErrors = mapper.checkAmountError(companyId, create);
        if (!amountErrors.isEmpty()) return "下列金额核对不上！";

        return "成功导入" + r + "条记录！\n请提交数据！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return mapper.deleteData(companyId, create);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.submitData(companyId, create);
        mapper.deleteImportRecordData(companyId, create);
        return true;
    }
}
