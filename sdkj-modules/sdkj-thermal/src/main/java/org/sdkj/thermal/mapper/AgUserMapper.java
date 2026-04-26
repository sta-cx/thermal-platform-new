package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.AgUser;
import org.sdkj.thermal.domain.vo.AgUserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 代理商用户 Mapper
 * 基础 CRUD 使用 BaseMapperPlus 提供，仅保留复杂查询
 */
public interface AgUserMapper extends BaseMapperPlus<AgUser, AgUserVo> {

    /**
     * 分页查询代理商用户列表
     */
    IPage<AgUserVo> selectUserPage(Page<?> page, @Param("name") String name, @Param("companyId") String companyId);

    /**
     * 检查手机号是否存在
     */
    int checkPhone(@Param("phone") String phone);

    /**
     * 检查用户是否有角色关联
     */
    int hasUserRole(@Param("userId") String userId);

    /**
     * 删除用户角色关联
     */
    int deleteUserRole(@Param("userId") String userId);

    /**
     * 保存用户角色关联
     */
    int saveUserRole(@Param("userId") String userId, @Param("roleIds") String[] roleIds);
}
