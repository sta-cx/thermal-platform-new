package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.AgCompany;
import org.sdkj.thermal.domain.AgRole;
import org.sdkj.thermal.domain.AgUser;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.PrDataGrant;
import org.sdkj.thermal.domain.bo.AgCompanyBo;
import org.sdkj.thermal.mapper.AgCompanyMapper;
import org.sdkj.thermal.mapper.AgRoleMapper;
import org.sdkj.thermal.mapper.AgUserMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrDataGrantMapper;
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
    private final PrCompanyMapper prCompanyMapper;
    private final PrDataGrantMapper prDataGrantMapper;


    @Override
    public List<AgCompany> listCompanies() {
        return lambdaQuery()
            .eq(AgCompany::getIsAudited, 1)
            .orderByDesc(AgCompany::getCreateTime)
            .list();
    }

    @Override
    public AgCompany getCompanyById(String id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCompany(String id) {
        // 级联删除数据权限
        prDataGrantMapper.delete(
            new LambdaQueryWrapper<PrDataGrant>().eq(PrDataGrant::getCompanyId, id)
        );
        // 级联删除组织机构
        List<SysOrganization> orgs = prCompanyMapper.selectOrganizationsByCompanyId(id);
        for (SysOrganization org : orgs) {
            prCompanyMapper.deleteOrgById(org.getId());
        }
        SysOrganization root = prCompanyMapper.getCompany(id);
        if (root != null) {
            prCompanyMapper.deleteOrgById(root.getId());
        }
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
        adminUser.setDelFlag("0");
        adminUser.setIsEnabled(1);
        adminUser.setUserPwd(BCrypt.hashpw("123456"));
        userMapper.insert(adminUser);

        // 关联用户与角色
        userMapper.saveUserRole(adminUser.getId(), new String[]{role.getId()});

        // 创建组织根节点
        createOrgRoot(String.valueOf(company.getId()), company.getName(), company.getCode());

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerCompany(AgCompanyBo companyBo) {
        // 1. 创建公司
        AgCompany company = new AgCompany();
        BeanUtils.copyProperties(companyBo, company);
        company.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
        save(company);

        // 2. 创建管理员用户
        AgUser adminUser = new AgUser();
        adminUser.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
        adminUser.setUserName(companyBo.getTele());
        adminUser.setRealName(companyBo.getPrincipal());
        adminUser.setPhone(companyBo.getTele());
        adminUser.setUserPwd(BCrypt.hashpw("123456"));
        adminUser.setCompanyId(company.getId());
        adminUser.setIsSuper(1);
        adminUser.setIsEnabled(1);
        userMapper.insert(adminUser);

        // 创建组织根节点
        createOrgRoot(String.valueOf(company.getId()), company.getName(), company.getCode());

        return true;
    }

    @Override
    public boolean canDeleteCompany(String id) {
        return true;
    }

    @Override
    public boolean editDetails(AgCompanyBo companyBo) {
        AgCompany company = new AgCompany();
        BeanUtils.copyProperties(companyBo, company);
        return updateById(company);
    }

    @Override
    public String getCompanyDetails(String id) {
        AgCompany company = getById(id);
        return company != null ? company.getDescription() : null;
    }

    private void updateAgAdminUserStatus(String companyId, boolean enabled) {
        LambdaUpdateWrapper<AgUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AgUser::getCompanyId, companyId)
               .eq(AgUser::getIsSuper, 1)
               .set(AgUser::getIsEnabled, enabled ? 1 : 0);
        userMapper.update(null, wrapper);
    }

    private void createOrgRoot(String companyId, String name, String code) {
        SysOrganization root = new SysOrganization();
        root.setId(IdWorker.getIdStr(root));
        root.setName(name);
        root.setCode(code);
        root.setCompanyId(companyId);
        root.setParentId("-1");
        root.setLevel("0");
        prCompanyMapper.insertOrganization(root);
    }
}
