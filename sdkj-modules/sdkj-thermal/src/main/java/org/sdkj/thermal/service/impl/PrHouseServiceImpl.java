package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IPrHouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 房屋信息 Service 实现
 * 迁移自旧系统 PrHouseServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrHouseServiceImpl extends ServiceImpl<PrHouseMapper, PrHouse> implements IPrHouseService {

    private final PrHouseMapper baseMapper;

    @Override
    public PrHouseVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHouseVo> selectPageList(String search, String buildingId, String orgId,
                                                   String status, String companyId, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHouse::getRoomNum, search.trim())
                .or().like(PrHouse::getCode, search.trim()));
        }
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(status), PrHouse::getStatus, status);
        lqw.eq(StringUtils.isNotBlank(companyId), PrHouse::getCompanyId, companyId);
        lqw.orderByAsc(PrHouse::getSeq).orderByDesc(PrHouse::getCreateTime);
        Page<PrHouseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public boolean validateRoomNum(String roomNum, String buildingId, String unitCode, String id) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHouse::getRoomNum, roomNum);
        lqw.eq(PrHouse::getBuildingId, buildingId);
        if (StringUtils.isNotBlank(unitCode)) {
            lqw.eq(PrHouse::getUnitCode, unitCode);
        }
        if (StringUtils.isNotBlank(id)) {
            lqw.ne(PrHouse::getId, id);
        }
        return baseMapper.selectCount(lqw) > 0;
    }

    @Override
    public List<PrHouseVo> selectByUnit(String buildingId, String unitCode) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHouse::getBuildingId, buildingId);
        if (StringUtils.isNotBlank(unitCode)) {
            lqw.eq(PrHouse::getUnitCode, unitCode);
        }
        lqw.orderByAsc(PrHouse::getSeq, PrHouse::getRoomNum);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHouseVo> selectByOrg(String companyId, String orgId) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(companyId), PrHouse::getCompanyId, companyId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.orderByAsc(PrHouse::getSeq, PrHouse::getRoomNum);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public long countByUser(String userId) {
        // 当前阶段通过 userId 查询关联房屋数量
        // 后续阶段通过 PrFamily 表关联后可实现精确查询
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        // 暂返回0，等 Stage 2D PrFamily 关联完善后实现
        return 0L;
    }

    @Override
    public BigDecimal areaByUser(String userId) {
        // 当前阶段通过 userId 查询关联房屋总面积
        // 后续阶段通过 PrFamily 表关联后可实现精确查询
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PrHouse entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(java.util.Collection<?> ids) {
        return super.removeByIds(ids);
    }

    // ========== 孤岛户功能实现 ==========

    @Override
    public List<PrHouseVo> queryIsolatedHouses(String companyId, String orgId, String buildingId) {
        // 获取楼宇下所有房屋
        List<PrHouse> allHouses = baseMapper.queryIsolatedHouses(companyId, orgId, buildingId);
        List<PrHouse> isolatedHouses = new ArrayList<>();

        if (allHouses.isEmpty()) {
            return List.of();
        }

        // 获取所有房屋ID
        List<String> houseIds = allHouses.stream().map(PrHouse::getId).toList();

        // TODO: 获取阀门状态，暂时跳过
        // 这里需要注入 PrHeatValveArchiveMapper 来查询
        // 暂时跳过，后续可通过注入 Mapper 来实现

        // 房号格式: XX-XX-XXXX (楼栋-单元-房号)
        Pattern roomPattern = Pattern.compile("^(\\d+)-(\\d+)-(\\d+)$");

        for (PrHouse house : allHouses) {
            if (StringUtils.isBlank(house.getRoomNum())) {
                continue;
            }

            Matcher matcher = roomPattern.matcher(house.getRoomNum());
            if (!matcher.matches()) {
                continue;
            }

            String building = matcher.group(1);
            String unit = matcher.group(2);
            String roomNum = matcher.group(3);

            // 房号左补齐4位
            String roomNumPadded = String.format("%04s", roomNum).replace(' ', '0');
            String floor = roomNumPadded.substring(0, roomNumPadded.length() - 2);
            String room = roomNumPadded.substring(roomNumPadded.length() - 2);

            try {
                int floorNum = Integer.parseInt(floor);
                int roomNumInt = Integer.parseInt(room);

                // 构建相邻房号
                String leftRoomNum = building + "-" + unit + "-" + floor + String.format("%02d", roomNumInt - 1);
                String rightRoomNum = building + "-" + unit + "-" + floor + String.format("%02d", roomNumInt + 1);
                String upperRoomNum = building + "-" + unit + "-" + (floorNum + 1) + String.format("%02d", roomNumInt);

                // 检查相邻房号是否有开阀的房屋（简化判断：暂不关联阀门状态）
                boolean hasNeighborWithValve = checkNeighborHasValve(allHouses, leftRoomNum, rightRoomNum, upperRoomNum);

                if (!hasNeighborWithValve) {
                    isolatedHouses.add(house);
                }
            } catch (NumberFormatException e) {
                log.warn("房号格式解析失败: {}", house.getRoomNum());
            }
        }

        // 转换为 VO
        return MapstructUtils.convert(isolatedHouses, PrHouseVo.class);
    }

    /**
     * 检查相邻房号是否有开阀的房屋
     * 简化实现：仅检查相邻房号是否存在，不关联阀门状态
     * 完整实现需要关联 PrHeatValveArchive 表查询阀门状态
     */
    private boolean checkNeighborHasValve(List<PrHouse> allHouses, String... neighborRoomNums) {
        for (String roomNum : neighborRoomNums) {
            for (PrHouse house : allHouses) {
                if (roomNum.equals(house.getRoomNum())) {
                    // TODO: 后续需要关联阀门档案表检查阀门状态
                    // 暂时认为存在相邻房号即非孤岛
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setIsolatedHouses(List<PrHouse> houseList, String companyId, String orgId, String buildingId) {
        // 1. 重置楼宇下所有房屋的位置属性
        baseMapper.resetSiteTypeByBuilding(companyId, orgId, buildingId);

        // 2. 批量更新孤岛户的位置属性
        if (houseList != null && !houseList.isEmpty()) {
            // 设置 siteType 和 siteTypeOld
            for (PrHouse house : houseList) {
                house.setSiteType("1"); // 孤岛户标记
                house.setSiteTypeOld(house.getSiteType());
            }
            baseMapper.updateSiteTypeBatch(houseList);
        }

        log.info("设置孤岛户完成, companyId={}, orgId={}, buildingId={}, count={}",
            companyId, orgId, buildingId, houseList != null ? houseList.size() : 0);
        return true;
    }
}
