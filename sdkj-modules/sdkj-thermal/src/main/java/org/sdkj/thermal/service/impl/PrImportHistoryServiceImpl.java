package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrImportHistory;
import org.sdkj.thermal.mapper.PrImportHistoryMapper;
import org.sdkj.thermal.service.IPrImportHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrImportHistoryServiceImpl extends ServiceImpl<PrImportHistoryMapper, PrImportHistory>
    implements IPrImportHistoryService {

    private final PrImportHistoryMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer importData(List<PrImportHistory> objects) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        List<PrImportHistory> lists = new ArrayList<>();
        for (PrImportHistory item : objects) {
            item.setCreateBy(create);
            item.setCompanyId(companyId);
            item.setCreateTime(new Date());
            item.setType(0);
            lists.add(item);
        }
        if (!lists.isEmpty()) {
            mapper.insert(lists);
        }
        return lists.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIds() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        mapper.updateHouseId(create, companyId);
        mapper.updateStandardId(create, companyId);
    }

    @Override
    public String check(Integer r) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();

        if (!mapper.selectNoHouseId(create, companyId).isEmpty())
            return "下列房屋信息无法匹配！";
        if (!mapper.selectNoStandardId(create, companyId).isEmpty())
            return "下列标准名称无法匹配！";

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
        mapper.deleteImportHistoryData(create, companyId);
        return true;
    }

    @Override
    public Integer select() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return mapper.select(create, companyId);
    }
}
