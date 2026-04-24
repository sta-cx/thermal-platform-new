package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

/**
 * 房屋信息 Service 实现
 * 迁移自旧系统 PrHouseServiceImpl
 */
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
}
