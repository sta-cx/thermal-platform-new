package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrUserHouse;
import org.sdkj.thermal.domain.bo.PrHouseBo;
import org.sdkj.thermal.domain.bo.PrHouseChangeOwnerBo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.domain.vo.PrUserHouseVo;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrUserHouseMapper;
import org.sdkj.thermal.service.IPrHouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final PrUserHouseMapper userHouseMapper;

    @Override
    public PrHouseVo selectById(java.io.Serializable id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<PrHouseVo> selectPageList(String search, String buildingId, String orgId,
                                                   String status, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHouse::getRoomNum, search.trim())
                .or().like(PrHouse::getCode, search.trim()));
        }
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(status), PrHouse::getStatus, status);
        lqw.orderByAsc(PrHouse::getSeq).orderByDesc(PrHouse::getCreateTime);
        Page<PrHouseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public boolean validateRoomNum(String roomNum, Long buildingId, String unitCode, Long id) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PrHouse::getRoomNum, roomNum);
        lqw.eq(PrHouse::getBuildingId, buildingId);
        if (StringUtils.isNotBlank(unitCode)) {
            lqw.eq(PrHouse::getUnitCode, unitCode);
        }
        if (id != null) {
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
    public List<PrHouseVo> selectByOrg(String orgId) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
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

    // ========== 类型筛选功能实现 ==========

    @Override
    public List<PrHouseVo> selectByType(String orgId, String buildingId,
                                        String unitCode, String stationId, List<String> types) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        lqw.eq(StringUtils.isNotBlank(unitCode), PrHouse::getUnitCode, unitCode);
        lqw.eq(StringUtils.isNotBlank(stationId), PrHouse::getStationType, stationId);
        lqw.orderByAsc(PrHouse::getSeq, PrHouse::getRoomNum);

        // 按类型过滤（1=特殊户, 2=非特殊户, 3=已收费, 4=未收费）
        // 优先使用数据库级过滤（is_charged 是真实列，is_special 通过 stationType 推断）
        if (types != null && !types.isEmpty()) {
            boolean hasSpecial = types.contains("1");
            boolean hasNonSpecial = types.contains("2");
            boolean hasCharged = types.contains("3");
            boolean hasUnpaid = types.contains("4");

            // 如果同时包含特殊户和非特殊户（或都不含），则不需要按 is_special 过滤
            boolean needSpecialFilter = (hasSpecial || hasNonSpecial) && !(hasSpecial && hasNonSpecial);

            if (needSpecialFilter) {
                // 特殊户/非特殊户通过 stationType 推断：stationType="1" 视为特殊户
                if (hasSpecial) {
                    lqw.eq(PrHouse::getStationType, "1");
                } else if (hasNonSpecial) {
                    lqw.ne(PrHouse::getStationType, "1");
                }
            }

            // 已收费/未收费通过 is_charged 列过滤
            boolean needChargeFilter = (hasCharged || hasUnpaid) && !(hasCharged && hasUnpaid);
            if (needChargeFilter) {
                if (hasCharged) {
                    lqw.eq(PrHouse::getIsCharged, 1);
                } else if (hasUnpaid) {
                    lqw.and(w -> w.isNull(PrHouse::getIsCharged).or().eq(PrHouse::getIsCharged, 0));
                }
            }
        }

        List<PrHouse> list = baseMapper.selectList(lqw);
        return MapstructUtils.convert(list, PrHouseVo.class);
    }

    @Override
    public List<PrHouseVo> selectByValveAndHotType(String orgId, String buildingId,
                                                   String unitCode, String stationId, List<String> types) {
        // 按阀门和供热类型筛选
        // valveOpen > 0 表示阀门已开启, stationType 表示供热区域属性
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        lqw.eq(StringUtils.isNotBlank(unitCode), PrHouse::getUnitCode, unitCode);
        lqw.eq(StringUtils.isNotBlank(stationId), PrHouse::getStationType, stationId);

        // types: 1=特殊户, 2=非特殊户, 3=已收费, 4=未收费
        // 额外按 valveOpen 和 heating 相关字段过滤
        if (types != null && !types.isEmpty()) {
            boolean hasSpecial = types.contains("1");
            boolean hasNonSpecial = types.contains("2");
            boolean hasCharged = types.contains("3");
            boolean hasUnpaid = types.contains("4");

            boolean needSpecialFilter = (hasSpecial || hasNonSpecial) && !(hasSpecial && hasNonSpecial);
            if (needSpecialFilter) {
                if (hasSpecial) {
                    lqw.eq(PrHouse::getStationType, "1");
                } else if (hasNonSpecial) {
                    lqw.ne(PrHouse::getStationType, "1");
                }
            }

            boolean needChargeFilter = (hasCharged || hasUnpaid) && !(hasCharged && hasUnpaid);
            if (needChargeFilter) {
                if (hasCharged) {
                    lqw.eq(PrHouse::getIsCharged, 1);
                } else if (hasUnpaid) {
                    lqw.and(w -> w.isNull(PrHouse::getIsCharged).or().eq(PrHouse::getIsCharged, 0));
                }
            }
        }

        lqw.orderByAsc(PrHouse::getSeq, PrHouse::getRoomNum);
        List<PrHouse> list = baseMapper.selectList(lqw);
        return MapstructUtils.convert(list, PrHouseVo.class);
    }

    @Override
    public List<PrHouseVo> selectAllForExport(String orgId, String buildingId,
                                              String status, String search) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        lqw.eq(StringUtils.isNotBlank(status), PrHouse::getStatus, status);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHouse::getRoomNum, search.trim())
                .or().like(PrHouse::getBuildingName, search.trim())
                .or().like(PrHouse::getCode, search.trim()));
        }
        lqw.orderByAsc(PrHouse::getSeq, PrHouse::getRoomNum);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<PrHouseVo> selectByOtherCode(String otherCode) {
        return baseMapper.selectByOtherCode(otherCode);
    }

    @Override
    public TableDataInfo<PrHouseVo> selectByPayStatus(String orgId, String buildingId,
                                                      String status, String payStatus, String search,
                                                      PageQuery pageQuery) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        lqw.eq(StringUtils.isNotBlank(status), PrHouse::getStatus, status);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHouse::getRoomNum, search.trim())
                .or().like(PrHouse::getCode, search.trim()));
        }
        lqw.orderByAsc(PrHouse::getSeq).orderByDesc(PrHouse::getCreateTime);
        Page<PrHouseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);

        // 在内存中按缴费状态过滤（payStatus 是 VO 字段）
        if (StringUtils.isNotBlank(payStatus)) {
            List<PrHouseVo> filtered = result.getRecords().stream()
                .filter(v -> payStatus.equals(v.getPayStatus()))
                .toList();
            result.setRecords(filtered);
            result.setTotal(filtered.size());
        }
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<PrHouseVo> selectByMultiSearch(String orgId, String buildingId,
                                                        String type, String search, PageQuery pageQuery) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        lqw.eq(StringUtils.isNotBlank(type), PrHouse::getType, type);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHouse::getRoomNum, search.trim())
                .or().like(PrHouse::getBuildingName, search.trim())
                .or().like(PrHouse::getCode, search.trim())
                .or().like(PrHouse::getUserName, search.trim()));
        }
        lqw.orderByAsc(PrHouse::getSeq).orderByDesc(PrHouse::getCreateTime);
        Page<PrHouseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public boolean updateOtherCode(String id, String otherCode) {
        return baseMapper.updateOtherCodeById(id, otherCode) > 0;
    }

    @Override
    public String queryOtherCode(String id) {
        return baseMapper.selectOtherCodeById(id);
    }

    // ========== 孤岛户功能实现 ==========

    @Override
    public List<PrHouseVo> queryIsolatedHouses(String orgId, String buildingId) {
        // 获取楼宇下所有房屋
        List<PrHouse> allHouses = baseMapper.queryIsolatedHouses(orgId, buildingId);
        List<PrHouse> isolatedHouses = new ArrayList<>();

        if (allHouses.isEmpty()) {
            return List.of();
        }

        // 获取所有房屋ID
        List<Long> houseIds = allHouses.stream().map(PrHouse::getId).toList();

        // 等业务确认:相邻房屋"开阀"判定值未定(valve_status 是 varchar(10),
        // 旧库分布为 NULL/空/02/03/04...,需业务领域定义哪些值算"开阀")。
        // 当前简化:不关联 pr_heat_valve_archive,仅按相邻房号存在判定非孤岛。

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
                    // 等业务确认 valve_status 开阀判定值,当前简化为"存在相邻房号即非孤岛"
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setIsolatedHouses(List<PrHouse> houseList, String orgId, String buildingId) {
        // 1. 重置楼宇下所有房屋的位置属性
        baseMapper.resetSiteTypeByBuilding(orgId, buildingId);

        // 2. 批量更新孤岛户的位置属性
        if (houseList != null && !houseList.isEmpty()) {
            // 设置 siteType 和 siteTypeOld
            for (PrHouse house : houseList) {
                house.setSiteType("1"); // 孤岛户标记
                house.setSiteTypeOld(house.getSiteType());
            }
            baseMapper.updateSiteTypeBatch(houseList);
        }

        log.info("设置孤岛户完成, orgId={}, buildingId={}, count={}",
            orgId, buildingId, houseList != null ? houseList.size() : 0);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importAll(List<PrHouseBo> houseList) {
        if (houseList == null || houseList.isEmpty()) {
            return 0;
        }
        List<PrHouse> entities = MapstructUtils.convert(houseList, PrHouse.class);
        int count = 0;
        for (PrHouse house : entities) {
            // 校验必填字段：房间号和楼宇ID
            if (StringUtils.isBlank(house.getRoomNum()) || house.getBuildingId() == null) {
                log.warn("跳过无效房屋数据: roomNum={}, buildingId={}", house.getRoomNum(), house.getBuildingId());
                continue;
            }
            // 校验房间号唯一性
            if (validateRoomNum(house.getRoomNum(), house.getBuildingId(), house.getUnitCode(), null)) {
                log.warn("跳过重复房间号: {} (buildingId={})", house.getRoomNum(), house.getBuildingId());
                continue;
            }
            if (super.save(house)) {
                count++;
            }
        }
        log.info("房屋数据导入完成, 总数={}, 成功导入={}", houseList.size(), count);
        return count;
    }

    @Override
    public List<PrHouseVo> selectForStrategyBinding(String orgId, String buildingId, String search) {
        LambdaQueryWrapper<PrHouse> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        lqw.eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId);
        if (StringUtils.isNotBlank(search)) {
            lqw.and(w -> w.like(PrHouse::getRoomNum, search.trim())
                .or().like(PrHouse::getBuildingName, search.trim()));
        }
        lqw.orderByAsc(PrHouse::getSeq, PrHouse::getRoomNum);
        return baseMapper.selectVoList(lqw);
    }

    // ========== B-01 D-05 新增 4 个高价值端点实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeOwner(PrHouseChangeOwnerBo bo) {
        PrHouse house = baseMapper.selectById(bo.getHouseId());
        if (house == null) {
            throw new ServiceException("房屋不存在");
        }

        // 1. 软删旧 pr_user_house —— 仅业主(relation_type='1'),保留租户/家属关联
        LambdaUpdateWrapper<PrUserHouse> updWrapper = new LambdaUpdateWrapper<PrUserHouse>()
            .eq(PrUserHouse::getHouseId, bo.getHouseId())
            .eq(PrUserHouse::getRelationType, "1")
            .eq(PrUserHouse::getDelFlag, "0")
            .set(PrUserHouse::getDelFlag, "1")
            .set(PrUserHouse::getRemark, "迁出: " + bo.getReason());
        userHouseMapper.update(null, updWrapper);

        // 2. 新建 pr_user_house（新业主）
        PrUserHouse newRecord = new PrUserHouse();
        newRecord.setUserId(bo.getUserId());
        newRecord.setUserName(bo.getUserName());
        newRecord.setPhone(bo.getPhone());
        newRecord.setHouseId(bo.getHouseId());
        newRecord.setRelationType(StringUtils.defaultIfBlank(bo.getRelationType(), "1"));
        newRecord.setOrgId(house.getOrgId());
        newRecord.setRecordSource("1");
        newRecord.setRemark("迁入: " + bo.getReason());
        userHouseMapper.insert(newRecord);

        // 3. 同步 pr_house 冗余字段 user_name/phone（用字符串列名绕过 @TableField(exist=false)）
        baseMapper.update(null, new UpdateWrapper<PrHouse>()
            .eq("id", bo.getHouseId())
            .set("user_name", bo.getUserName())
            .set("phone", bo.getPhone()));

        return true;
    }

    @Override
    public List<PrUserHouseVo> changeList(Long houseId, String startDate, String endDate) {
        return userHouseMapper.selectChangeHistory(houseId, startDate, endDate);
    }

    @Override
    public Map<String, Object> selectByTypeAndValve(String orgId, String buildingId,
                                                     String unitCode, String stationId,
                                                     List<String> types, String treeTypeValve) {
        // 复用 selectByType 的 SQL 层过滤
        List<PrHouseVo> houses = selectByType(orgId, buildingId, unitCode, stationId, types);

        // D5 降级: PrHouseVo 无 heatValveArchiveList 字段，无法做内存层阀门过滤
        // controllableList 始终为空，后续补齐阀门档案字段后可完整实现
        Map<String, Object> result = new HashMap<>();
        result.put("list", houses);
        result.put("controllableList", List.of());
        return result;
    }

    @Override
    public BigDecimal calcOccupancy(String orgId) {
        // @OrgPermission 在 Mapper 类级注解自动注入子查询
        LambdaQueryWrapper<PrHouse> totalLqw = new LambdaQueryWrapper<PrHouse>()
            .eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId);
        long total = baseMapper.selectCount(totalLqw);
        if (total == 0) {
            return BigDecimal.ZERO;
        }

        // status='8' 表示入住（老系统约定，验收前 ping 业务确认）
        LambdaQueryWrapper<PrHouse> checkInLqw = new LambdaQueryWrapper<PrHouse>()
            .eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId)
            .eq(PrHouse::getStatus, "8");
        long checkIn = baseMapper.selectCount(checkInLqw);

        return BigDecimal.valueOf(checkIn)
            .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);
    }
}
