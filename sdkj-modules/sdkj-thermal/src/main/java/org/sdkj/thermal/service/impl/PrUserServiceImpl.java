package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrUser;
import org.sdkj.thermal.domain.vo.PrUserVo;
import org.sdkj.thermal.mapper.PrUserMapper;
import org.sdkj.thermal.service.IPrUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户档案 Service 实现
 * 迁移自旧系统 PrUserServiceImpl
 */
@Service
@RequiredArgsConstructor
public class PrUserServiceImpl extends ServiceImpl<PrUserMapper, PrUser> implements IPrUserService {

    private final PrUserMapper baseMapper;

    @Override
    public TableDataInfo<PrUserVo> selectPageList(String companyId, PageQuery pageQuery) {
        List<PrUserVo> list = baseMapper.selectPageList(companyId);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrUserVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrUserVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
        return TableDataInfo.build(page);
    }

    @Override
    public PrUserVo selectDetailById(String userId) {
        return baseMapper.selectDetailById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertData(PrUserVo vo) {
        // TODO: OSS 头像上传（Phase 6）- 旧系统将 Base64 头像上传到 OSS
        PrUser user = new PrUser();
        org.springframework.beans.BeanUtils.copyProperties(vo, user);
        return save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateData(PrUserVo vo) {
        // TODO: OSS 头像上传（Phase 6）
        PrUser user = new PrUser();
        org.springframework.beans.BeanUtils.copyProperties(vo, user);
        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteData(String id, String idNo) {
        // TODO: 清理 OSS 头像（Phase 6）- 旧系统删除 pi/{idNo}.jpg
        return removeById(id);
    }

    @Override
    public boolean hasUser(String houseId) {
        return baseMapper.countByHouseId(houseId) > 0;
    }

    @Override
    public PrUserVo selectByPhone(String phone) {
        return baseMapper.selectByPhone(phone);
    }
}
