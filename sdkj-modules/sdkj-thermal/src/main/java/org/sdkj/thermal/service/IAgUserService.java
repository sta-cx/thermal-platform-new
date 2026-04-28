package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.AgUser;
import org.sdkj.thermal.domain.bo.AgUserBo;
import org.sdkj.thermal.domain.vo.AgUserVo;

/**
 * 代理商用户 Service
 */
public interface IAgUserService extends IService<AgUser> {

    /**
     * 分页查询代理商用户列表
     */
    IPage<AgUserVo> selectUserPage(Page<?> page, String name, String companyId);

    /**
     * 检查手机号是否已存在
     */
    boolean checkPhone(String phone);

    /**
     * 新增用户
     */
    boolean insertUser(AgUserBo userBo);

    /**
     * 修改用户
     */
    boolean updateUser(AgUserBo userBo);

    /**
     * 删除用户
     */
    boolean deleteUser(String id);

    /**
     * 启用用户
     */
    boolean enableUser(String id);

    /**
     * 禁用用户
     */
    boolean disableUser(String id);

    /**
     * 分配角色
     */
    boolean assignRoles(String userId, String roleIds);

    /**
     * 根据ID查询用户
     */
    AgUserVo selectUserById(String id);

    /**
     * 重置密码
     */
    boolean resetPassword(String id, String password);
}
