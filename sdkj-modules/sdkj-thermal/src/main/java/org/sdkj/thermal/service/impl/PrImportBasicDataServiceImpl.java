package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrImportBasicData;
import org.sdkj.thermal.mapper.PrImportBasicDataMapper;
import org.sdkj.thermal.service.IPrImportBasicDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PrImportBasicDataServiceImpl extends ServiceImpl<PrImportBasicDataMapper, PrImportBasicData>
    implements IPrImportBasicDataService {

    private final PrImportBasicDataMapper mapper;

    @Override
    public Integer importData(List<Object> objects) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        Date date = new Date();
        List<PrImportBasicData> lists = new ArrayList<>(objects.size());
        for (Object object : objects) {
            if (object instanceof PrImportBasicData) {
                PrImportBasicData item = (PrImportBasicData) object;
                if (item.getOrgName() != null) {
                    item.setOrgName(item.getOrgName().trim());
                }
                item.setCreateTime(date);
                item.setCreateBy(create);
                item.setCompanyId(companyId);
                if (item.getUserName() != null && !item.getUserName().isEmpty()) {
                    item.setUserId(UUID.randomUUID().toString().replace("-", ""));
                }
                lists.add(item);
            }
        }
        mapper.insertList(lists);
        mapper.updateOrgId(companyId, create);
        return lists.size();
    }

    @Override
    public R check(long size) {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();

        List<PrImportBasicData> noOrgIds = mapper.selectNoOrgIds(companyId, create);
        if (!noOrgIds.isEmpty()) {
            return R.fail("下列" + noOrgIds.size() + "条数据小区名称错误或不存在！");
        }

        List<PrImportBasicData> noRoomNum = mapper.selectNoRoomNum(companyId, create);
        if (!noRoomNum.isEmpty()) {
            return R.fail("下列" + noRoomNum.size() + "条数据没有房号！");
        }

        List<PrImportBasicData> repeatHouse = mapper.selectRepeatHouse(companyId, create);
        if (!repeatHouse.isEmpty()) {
            return R.fail("下列" + repeatHouse.size() + "条数据房号重复！");
        }

        List<PrImportBasicData> noDate = mapper.selectError1(companyId, create);
        if (!noDate.isEmpty()) {
            return R.fail("下列" + noDate.size() + "条数据需填写建费日期！");
        }

        List<PrImportBasicData> hasAlready = mapper.selectHasAlready(companyId, create);
        if (!hasAlready.isEmpty()) {
            return R.fail("下列房屋已存在，请勿重复导入。");
        }

        mapper.updateBuildId(companyId, create);
        List<PrImportBasicData> noBuildId = mapper.selectNoBuildId(companyId, create);
        if (!noBuildId.isEmpty()) {
            return R.fail("下列" + noBuildId.size() + "条数据楼宇信息无法匹配，请核对信息！");
        }

        mapper.updateUnitId(companyId, create);
        mapper.updateStationId(companyId, create);
        List<PrImportBasicData> noStation = mapper.selectStationId(companyId, create);
        if (!noStation.isEmpty()) {
            return R.fail("下列数据无换热站信息，请填写完整");
        }

        mapper.updateSubstationId(companyId, create);
        List<PrImportBasicData> noSubstation = mapper.selectNoSubstationId(companyId, create);
        if (!noSubstation.isEmpty()) {
            return R.fail("下列数据无换热分站信息，请填写完整");
        }

        mapper.updateHouseId(companyId, create);
        mapper.updateStandardId(companyId, create);
        List<PrImportBasicData> noStandardId = mapper.selectNoStandardId(companyId, create);
        if (!noStandardId.isEmpty()) {
            return R.fail("下列数据项目名称或标准名称不匹配，请检查后重新填写");
        }

        return R.ok("成功导入" + size + "条记录\n请提交数据！");
    }

    @Override
    public boolean deleteData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        return mapper.deleteData(companyId, create);
    }

    @Override
    public long count() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        LambdaQueryWrapper<PrImportBasicData> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrImportBasicData::getCompanyId, companyId);
        lqw.eq(PrImportBasicData::getCreateBy, create);
        lqw.eq(PrImportBasicData::getType, 0);
        return count(lqw);
    }

    @Override
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        String companyId = LoginHelper.getTenantId();
        List<String> orgIds = mapper.groupOrgId(companyId, create);
        if (orgIds != null && !orgIds.isEmpty()) {
            mapper.insertUsers(companyId, create, "e10adc3949ba59abbe56e057f20f883e");
            mapper.insertUserHouse(companyId, create);
            mapper.insertHouseChange(companyId, create);
            mapper.insertHouseExpense(companyId, create);
            mapper.deleteImportBasicData(companyId, create);
            mapper.deleteUserHouseDataByNoHouseId(companyId, orgIds);
            return true;
        }
        return false;
    }
}
