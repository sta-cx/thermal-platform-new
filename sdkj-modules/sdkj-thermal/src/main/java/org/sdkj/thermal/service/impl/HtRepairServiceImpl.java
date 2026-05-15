package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.dto.CreatedRepairResult;
import org.sdkj.thermal.domain.vo.HtRepairVo;
import org.sdkj.thermal.mapper.HtRepairMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IHtRepairService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报修记录 Service 实现
 * 迁移自旧系统 HtRepairServiceImpl
 */
@Service
@RequiredArgsConstructor
public class HtRepairServiceImpl extends ServiceImpl<HtRepairMapper, HtRepair> implements IHtRepairService {

    private final HtRepairMapper baseMapper;
    private final PrHouseMapper houseMapper;
    private final PrCompanyMapper companyMapper;

    @Override
    public TableDataInfo<HtRepairVo> selectPageList(LambdaQueryWrapper<HtRepair> lqw, PageQuery pageQuery) {
        Page<HtRepairVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<Map<String, Object>> selectTypeCount() {
        return baseMapper.selectTypeCount();
    }

    @Override
    public List<HtRepairVo> selectByRoomId(String roomId) {
        return baseMapper.selectByRoomId(roomId);
    }

    @Override
    public int markAsDeleted(String repairNo) {
        return baseMapper.markAsDeleted(repairNo);
    }

    @Override
    public String generateRepairNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "BX" + date;
        String maxNo = baseMapper.selectMaxRepairNoByPrefix(prefix);
        int nextSeq = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            try {
                nextSeq = Integer.parseInt(maxNo.substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return prefix + String.format("%04d", nextSeq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreatedRepairResult createFromAi(Long houseId, String repairInfo,
                                             String userName, String userPhone,
                                             Long operatorId) {
        // 1. 参数校验
        if (houseId == null) {
            throw new IllegalArgumentException("房屋 ID 不能为空");
        }
        if (repairInfo == null || repairInfo.isBlank()) {
            throw new IllegalArgumentException("报修描述不能为空");
        }

        // 2. 查房屋，确认存在并带出关联字段
        PrHouse house = houseMapper.selectById(houseId);
        if (house == null) {
            throw new IllegalArgumentException("房屋 ID " + houseId + " 不存在，请核实后重试");
        }

        // 3. 构建完整实体
        HtRepair repair = new HtRepair();
        repair.setHouseId(houseId);
        repair.setBuildingId(house.getBuildingId());
        repair.setBuildingName(house.getBuildingName());
        repair.setUnitCode(house.getUnitCode());
        repair.setRoomNum(house.getRoomNum());
        repair.setOrgId(house.getOrgId());

        // 4. 查组织名称（PrHouse.orgName 是 @TableField(exist=false)，需额外查）
        if (house.getOrgId() != null) {
            SysOrganization org = companyMapper.selectOrgById(house.getOrgId());
            if (org != null) {
                repair.setOrgName(org.getName());
            }
        }

        // 5. 业务编号
        repair.setRepairNo(generateRepairNo());

        // 6. 默认值
        repair.setRepairType(0);
        repair.setRepairTime(new Date());
        repair.setRepairStatus(0);
        repair.setUrgentType(0);
        repair.setServiceType(0);

        // 7. 用户传入值
        repair.setRepairInfo(repairInfo);
        repair.setUserName(userName);
        repair.setUserPhone(userPhone);

        // 8. 审计字段显式设置
        repair.setCreateBy(operatorId);
        repair.setUpdateBy(operatorId);

        // 9. 保存
        save(repair);

        String summary = String.format("为 %s %s 室创建报修工单：%s",
            house.getBuildingName() != null ? house.getBuildingName() : "未知楼宇",
            house.getRoomNum() != null ? house.getRoomNum() : "未知",
            repairInfo.length() > 30 ? repairInfo.substring(0, 30) + "…" : repairInfo);

        return new CreatedRepairResult(repair.getId(), houseId, summary, "PENDING");
    }

}
