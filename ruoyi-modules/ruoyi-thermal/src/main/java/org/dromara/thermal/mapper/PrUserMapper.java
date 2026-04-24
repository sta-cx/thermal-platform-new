package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrUser;
import org.dromara.thermal.domain.vo.PrUserVo;

import java.util.List;

/**
 * 客户档案 Mapper
 * 迁移自旧系统 PrUserMapper
 */
public interface PrUserMapper extends BaseMapperPlus<PrUser, PrUserVo> {

    /**
     * 分页查询客户列表（按公司）
     */
    List<PrUserVo> selectPageList(@Param("companyId") String companyId);

    /**
     * 查询客户详情（含房屋关联信息）
     */
    PrUserVo selectDetailById(@Param("userId") String userId);

    /**
     * 查询房屋是否有业主
     */
    int countByHouseId(@Param("houseId") String houseId);

    /**
     * 根据手机号查询客户
     */
    PrUserVo selectByPhone(@Param("phone") String phone);
}
