package org.sdkj.thermal.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.AgUser;
import org.sdkj.thermal.domain.bo.AgUserBo;
import org.sdkj.thermal.domain.vo.AgUserVo;
import org.sdkj.thermal.mapper.AgUserMapper;
import org.sdkj.thermal.service.IAgUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 代理商用户 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AgUserServiceImpl extends ServiceImpl<AgUserMapper, AgUser> implements IAgUserService {

    private final AgUserMapper userMapper;

    @Override
    public IPage<AgUserVo> selectUserPage(Page<?> page, String name, String companyId) {
        return userMapper.selectUserPage(page, name, companyId);
    }

    @Override
    public boolean checkPhone(String phone) {
        return userMapper.checkPhone(phone) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertUser(AgUserBo userBo) {
        AgUser user = new AgUser();
        BeanUtils.copyProperties(userBo, user);
        user.setIsDeleted("0");
        user.setIsEnabled(0);
        user.setIsSuper(0);
        user.setIsRealname(1);

        if (StrUtil.isNotBlank(user.getUserPwd())) {
            user.setUserPwd(BCrypt.hashpw(userBo.getUserPwd().trim()));
        }
        return save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(AgUserBo userBo) {
        AgUser user = new AgUser();
        BeanUtils.copyProperties(userBo, user);

        if (StrUtil.isNotBlank(userBo.getUserPwd())) {
            user.setUserPwd(BCrypt.hashpw(userBo.getUserPwd().trim()));
        } else {
            user.setUserPwd(null);
        }
        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String id) {
        boolean deleted = removeById(id);
        if (deleted) {
            int roleCount = userMapper.hasUserRole(id);
            if (roleCount > 0) {
                userMapper.deleteUserRole(id);
            }
        }
        return deleted;
    }

    @Override
    public boolean enableUser(String id) {
        return lambdaUpdate()
            .eq(AgUser::getId, id)
            .set(AgUser::getIsEnabled, 1)
            .update();
    }

    @Override
    public boolean disableUser(String id) {
        return lambdaUpdate()
            .eq(AgUser::getId, id)
            .set(AgUser::getIsEnabled, 0)
            .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(String userId, String roleIds) {
        userMapper.deleteUserRole(userId);
        if (StrUtil.isBlank(roleIds)) {
            return true;
        }
        String[] roleIdArray = roleIds.split(",");
        return userMapper.saveUserRole(userId, roleIdArray) > 0;
    }
}
