package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.AgCompany;
import org.sdkj.thermal.domain.bo.AgCompanyBo;

import java.util.List;

/**
 * 代理商公司 Service
 */
public interface IAgCompanyService extends IService<AgCompany> {

    /**
     * 查询所有公司列表（树形结构）
     */
    List<AgCompany> listCompanies();

    /**
     * 根据ID查询公司详情
     */
    AgCompany getCompanyById(String id);

    /**
     * 删除公司
     */
    boolean deleteCompany(String id);

    /**
     * 校验手机号是否已被使用
     */
    boolean verifyTele(String tele);

    /**
     * 校验编码是否已被使用
     */
    boolean verifyCode(String code);

    /**
     * 校验名称是否重复
     */
    boolean verifyName(String name);

    /**
     * 新增公司（包含创建管理员用户、角色、权限等）
     */
    boolean insertCompany(AgCompanyBo companyBo);

    /**
     * 启用公司
     */
    boolean enableCompany(String id);

    /**
     * 禁用公司
     */
    boolean disableCompany(String id);

    /**
     * 更新公司
     */
    boolean updateCompany(AgCompanyBo companyBo);
}
