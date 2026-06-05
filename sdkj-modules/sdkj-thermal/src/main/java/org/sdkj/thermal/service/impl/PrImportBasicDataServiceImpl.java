package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.utils.IdGeneratorUtil;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrImportBasicData;
import org.sdkj.thermal.domain.PrImportBasicDataByCode;
import org.sdkj.thermal.domain.PrUser;
import org.sdkj.thermal.domain.PrHouseExpense;
import org.sdkj.thermal.mapper.PrImportBasicDataMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IPrImportBasicDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PrImportBasicDataServiceImpl extends ServiceImpl<PrImportBasicDataMapper, PrImportBasicData>
    implements IPrImportBasicDataService {

    private final PrImportBasicDataMapper mapper;
    private final PrHouseMapper houseMapper;

    @Override
    public Integer importData(List<PrImportBasicData> objects) {
        String create = LoginHelper.getUserIdStr();
        Date date = new Date();
        List<PrImportBasicData> lists = new ArrayList<>(objects.size());
        for (PrImportBasicData item : objects) {
            if (item.getOrgName() != null) {
                item.setOrgName(item.getOrgName().trim());
            }
            item.setCreateTime(date);
            item.setCreateBy(create);
            if (item.getUserName() != null && !item.getUserName().isEmpty()) {
                item.setUserId(String.valueOf(IdGeneratorUtil.nextLongId()));
            }
            lists.add(item);
        }
        mapper.insertList(lists);
        mapper.updateOrgId(create);
        return lists.size();
    }

    @Override
    public R check(long size) {
        String create = LoginHelper.getUserIdStr();

        List<PrImportBasicData> noOrgIds = mapper.selectNoOrgIds(create);
        if (!noOrgIds.isEmpty()) {
            return R.fail("下列" + noOrgIds.size() + "条数据小区名称错误或不存在！");
        }

        List<PrImportBasicData> noRoomNum = mapper.selectNoRoomNum(create);
        if (!noRoomNum.isEmpty()) {
            return R.fail("下列" + noRoomNum.size() + "条数据没有房号！");
        }

        List<PrImportBasicData> repeatHouse = mapper.selectRepeatHouse(create);
        if (!repeatHouse.isEmpty()) {
            return R.fail("下列" + repeatHouse.size() + "条数据房号重复！");
        }

        List<PrImportBasicData> noDate = mapper.selectError1(create);
        if (!noDate.isEmpty()) {
            return R.fail("下列" + noDate.size() + "条数据需填写建费日期！");
        }

        List<PrImportBasicData> hasAlready = mapper.selectHasAlready(create);
        if (!hasAlready.isEmpty()) {
            return R.fail("下列房屋已存在，请勿重复导入。");
        }

        mapper.updateBuildId(create);
        List<PrImportBasicData> noBuildId = mapper.selectNoBuildId(create);
        if (!noBuildId.isEmpty()) {
            return R.fail("下列" + noBuildId.size() + "条数据楼宇信息无法匹配，请核对信息！");
        }

        mapper.updateUnitId(create);
        mapper.updateStationId(create);
        List<PrImportBasicData> noStation = mapper.selectStationId(create);
        if (!noStation.isEmpty()) {
            return R.fail("下列数据无换热站信息，请填写完整");
        }

        mapper.updateSubstationId(create);
        List<PrImportBasicData> noSubstation = mapper.selectNoSubstationId(create);
        if (!noSubstation.isEmpty()) {
            return R.fail("下列数据无换热分站信息，请填写完整");
        }

        mapper.updateHouseId(create);
        mapper.updateStandardId(create);
        List<PrImportBasicData> noStandardId = mapper.selectNoStandardId(create);
        if (!noStandardId.isEmpty()) {
            return R.fail("下列数据项目名称或标准名称不匹配，请检查后重新填写");
        }

        return R.ok("成功导入" + size + "条记录\n请提交数据！");
    }

    @Override
    public boolean deleteData() {
        String create = LoginHelper.getUserIdStr();
        return mapper.deleteData(create);
    }

    @Override
    public long count() {
        String create = LoginHelper.getUserIdStr();
        LambdaQueryWrapper<PrImportBasicData> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrImportBasicData::getCreateBy, create);
        lqw.eq(PrImportBasicData::getType, 0);
        return count(lqw);
    }

    @Override
    public boolean submitData() {
        String create = LoginHelper.getUserIdStr();
        List<String> orgIds = mapper.groupOrgId(create);
        if (orgIds != null && !orgIds.isEmpty()) {
            mapper.insertUsers(create, "e10adc3949ba59abbe56e057f20f883e");
            mapper.insertUserHouse(create);
            mapper.insertHouseChange(create);
            mapper.insertHouseExpense(create);
            mapper.deleteImportBasicData(create);
            mapper.deleteUserHouseDataByNoHouseId(orgIds);
            return true;
        }
        return false;
    }

    @Override
    public R<String> importDataByHeatCode(List<PrImportBasicDataByCode> objects) {
        String createBy = LoginHelper.getUserIdStr();
        Date now = new Date();

        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {
            PrImportBasicDataByCode row = objects.get(i);
            int rowNum = i + 2; // Excel row number (header starts at row 1)

            if (row.getCode() == null || row.getCode().trim().isEmpty()) {
                failCount++;
                errors.add("第" + rowNum + "行：房屋编码为空");
                continue;
            }
            String code = row.getCode().trim();

            // 1. Find house by code
            PrHouse house = houseMapper.selectOne(
                new LambdaQueryWrapper<PrHouse>()
                    .eq(PrHouse::getCode, code)
                    .last("LIMIT 1")
            );

            if (house == null) {
                failCount++;
                errors.add("第" + rowNum + "行：房屋编码[" + code + "]不存在");
                continue;
            }

            try {
                Integer normalizedPayStatus = normalizePaymentStatus(row.getPayStatus());

                // 2. Update house fields if provided
                LambdaUpdateWrapper<PrHouse> houseUpdate = new LambdaUpdateWrapper<>();
                houseUpdate.eq(PrHouse::getId, house.getId());
                boolean needUpdate = false;

                if (row.getNfloorArea() != null) {
                    houseUpdate.set(PrHouse::getNfloorArea, row.getNfloorArea());
                    needUpdate = true;
                }
                if (row.getGfloorArea() != null) {
                    houseUpdate.set(PrHouse::getGfloorArea, row.getGfloorArea());
                    needUpdate = true;
                }
                if (row.getHeatingArea() != null) {
                    houseUpdate.set(PrHouse::getHeatingArea, row.getHeatingArea());
                    needUpdate = true;
                }
                if (row.getNature() != null && !row.getNature().isEmpty()) {
                    houseUpdate.set(PrHouse::getNature, row.getNature().trim());
                    needUpdate = true;
                }
                if (row.getSiteType() != null && !row.getSiteType().isEmpty()) {
                    houseUpdate.set(PrHouse::getSiteType, row.getSiteType().trim());
                    needUpdate = true;
                }
                if (row.getEstablishTime() != null) {
                    houseUpdate.set(PrHouse::getEstablishTime, row.getEstablishTime());
                    needUpdate = true;
                }
                if (row.getOtherCode() != null && !row.getOtherCode().isEmpty()) {
                    houseUpdate.set(PrHouse::getOtherCode, row.getOtherCode().trim());
                    needUpdate = true;
                }

                if (needUpdate) {
                    houseUpdate.set(PrHouse::getUpdateBy, createBy);
                    houseUpdate.set(PrHouse::getUpdateTime, now);
                    houseMapper.update(null, houseUpdate);
                }
                if (normalizedPayStatus != null) {
                    houseMapper.updatePayStatusBatch(List.of(house.getId()), normalizedPayStatus);
                }

                // 3. Handle user info (if userName and phone are both provided)
                Long userId = null;
                if (row.getUserName() != null && !row.getUserName().isEmpty()
                    && row.getPhone() != null && !row.getPhone().isEmpty()) {

                    // Insert user
                    PrUser newUser = new PrUser();
                    newUser.setName(row.getUserName().trim());
                    newUser.setPhone(row.getPhone().trim());
                    if (row.getIdNo() != null && !row.getIdNo().isEmpty()) {
                        newUser.setIdNo(row.getIdNo().trim());
                        newUser.setIdType(1); // ID card
                    }
                    newUser.setOrgId(house.getOrgId());
                    newUser.setPassword("e10adc3949ba59abbe56e057f20f883e"); // default password
                    mapper.insertUserDirect(newUser);
                    userId = newUser.getId();
                }

                // 4. Link user to house
                if (userId != null) {
                    mapper.insertUserHouseRelation(userId, house.getId(), createBy, now);
                }

                // 5. Handle house expense (if standardName is provided)
                if (row.getStandardName() != null && !row.getStandardName().isEmpty()) {
                    mapper.insertHouseExpenseByCode(house.getId(), row.getStandardName(),
                        row.getStandardPrice(), row.getItemName(), createBy, now);
                }

                // 6. Handle account balance if provided
                if (row.getAccount() != null && row.getAccount().compareTo(new java.math.BigDecimal("0.00")) != 0) {
                    mapper.updateAccountBalance(house.getId(), userId, row.getAccount(),
                        row.getItemName(), createBy, now);
                }

                successCount++;
            } catch (Exception e) {
                log.error("Failed to import row {}: code={}", rowNum, code, e);
                failCount++;
                errors.add("第" + rowNum + "行：房屋编码[" + code + "]处理异常: " + e.getMessage());
            }
        }

        StringBuilder result = new StringBuilder();
        result.append("导入完成：成功 ").append(successCount).append(" 条");
        if (failCount > 0) {
            result.append("，失败 ").append(failCount).append(" 条");
            if (!errors.isEmpty()) {
                result.append("\n失败详情：");
                int maxErrors = Math.min(errors.size(), 20);
                for (int i = 0; i < maxErrors; i++) {
                    result.append("\n").append(errors.get(i));
                }
                if (errors.size() > 20) {
                    result.append("\n... 还有 ").append(errors.size() - 20).append(" 条错误");
                }
            }
        }

        if (failCount > 0) {
            return R.fail(result.toString());
        }
        return R.ok(result.toString());
    }

    @Override
    public boolean isCheckHouse(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        PrHouse house = houseMapper.selectOne(
            new LambdaQueryWrapper<PrHouse>()
                .eq(PrHouse::getCode, code.trim())
                .last("LIMIT 1")
        );
        return house != null;
    }

    static Integer normalizePaymentStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return switch (value.trim()) {
            case "1", "已缴费", "已收费", "已交费" -> 1;
            case "0", "未缴费", "未收费", "欠费" -> 0;
            case "2", "停供" -> 2;
            case "3", "空置" -> 3;
            default -> throw new IllegalArgumentException("缴费状态只能填写：已缴费、未缴费、停供、空置");
        };
    }
}
