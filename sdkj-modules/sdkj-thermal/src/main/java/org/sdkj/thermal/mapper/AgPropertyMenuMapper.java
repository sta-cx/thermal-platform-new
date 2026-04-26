package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.AgPropertyMenu;
import org.sdkj.thermal.domain.vo.AgPropertyMenuVo;

import java.util.List;

/**
 * 代理商物业菜单 Mapper
 * 基础 CRUD 使用 BaseMapperPlus 提供，仅保留复杂查询
 */
public interface AgPropertyMenuMapper extends BaseMapperPlus<AgPropertyMenu, AgPropertyMenuVo> {

    /**
     * 查询已分配的菜单列表
     */
    List<AgPropertyMenuVo> selectAssignedMenus(@Param("companyId") String companyId);

    /**
     * 查询未分配的菜单列表
     */
    List<AgPropertyMenuVo> selectUnassignedMenus(@Param("companyId") String companyId);

    /**
     * 删除物业菜单关联
     */
    int deletePropertyMenus(@Param("companyId") String companyId);

    /**
     * 批量插入物业菜单关联
     */
    int insertPropertyMenus(@Param("companyId") String companyId, @Param("menuIds") String[] menuIds);
}
