package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.AgCompany;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.AgUser;
import org.sdkj.thermal.domain.bo.AgCompanyBo;
import org.sdkj.thermal.mapper.AgCompanyMapper;
import org.sdkj.thermal.mapper.AgRoleMapper;
import org.sdkj.thermal.mapper.AgUserMapper;
import org.sdkj.thermal.service.IAgCompanyService;
import org.springframework.beans.BeanUtils;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 代理商公司 Service 实现
 */
@Service
@RequiredArgsConstructor
public class AgCompanyServiceImpl extends ServiceImpl<AgCompanyMapper, AgCompany> implements IAgCompanyService {

    private final AgRoleMapper roleMapper;
    private final AgUserMapper userMapper;


    @Override
    public List<AgCompany> listCompanies() {
        return lambdaQuery()
            .eq(AgCompany::getIsAudited, 1)
            .orderByAsc(AgCompany::getSeq)
            .list();
    }

    @Override
    public AgCompany getCompanyById(String id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCompany(String id) {
        return removeById(id);
    }

    @Override
    public boolean verifyTele(String tele) {
        return lambdaQuery()
            .eq(AgCompany::getTele, tele)
            .exists();
    }

    @Override
    public boolean verifyCode(String code) {
        return lambdaQuery()
            .eq(AgCompany::getCode, code)
            .exists();
    }

    @Override
    public boolean verifyName(String name) {
        return lambdaQuery()
            .eq(AgCompany::getName, name)
            .exists();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertCompany(AgCompanyBo companyBo) {
        AgCompany company = new AgCompany();
        BeanUtils.copyProperties(companyBo, company);
        boolean saved = save(company);
        if (!saved) return false;

        // 创建超管角色
        AgRole role = new AgRole();
        role.setName("代理商超管");
        role.setIdentifying("ROLE_SUPER_AGENT");
        role.setNature("1");
        role.setCompanyId(company.getId());
        role.setIsSuper(1);
        roleMapper.insert(role);

        // 创建管理员用户
        AgUser adminUser = new AgUser();
        adminUser.setUserName(company.getTele());
        adminUser.setRealName(company.getPrincipal());
        adminUser.setPhone(company.getTele());
        adminUser.setCompanyId(company.getId());
        adminUser.setIsSuper(1);
        adminUser.setIsRealname(1);
        adminUser.setIsDeleted("0");
        adminUser.setIsEnabled(1);
        adminUser.setUserPwd(BCrypt.hashpw("123456"));
        userMapper.insert(adminUser);

        // 关联用户与角色
        userMapper.saveUserRole(adminUser.getId(), new String[]{role.getId()});

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableCompany(String id) {
        boolean updated = lambdaUpdate()
            .eq(AgCompany::getId, id)
            .set(AgCompany::getIsEnabled, 1)
            .update();

        if (updated) {
            updateAgAdminUserStatus(id, true);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableCompany(String id) {
        boolean updated = lambdaUpdate()
            .eq(AgCompany::getId, id)
            .set(AgCompany::getIsEnabled, 0)
            .update();

        if (updated) {
            updateAgAdminUserStatus(id, false);
        }
        return updated;
    }

    @Override
    public boolean updateCompany(AgCompanyBo companyBo) {
        AgCompany company = new AgCompany();
        BeanUtils.copyProperties(companyBo, company);
        return updateById(company);
    }

    private void updateAgAdminUserStatus(String companyId, boolean enabled) {
        LambdaUpdateWrapper<AgUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AgUser::getCompanyId, companyId)
               .eq(AgUser::getIsSuper, 1)
               .set(AgUser::getIsEnabled, enabled ? 1 : 0);
        userMapper.update(null, wrapper);
    }
}
