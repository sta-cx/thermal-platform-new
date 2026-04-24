package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrUser;
import org.dromara.thermal.domain.vo.PrUserVo;

/**
 * 客户档案 Service 接口
 * 迁移自旧系统 PrUserService
 */
public interface IPrUserService extends IService<PrUser> {

    /**
     * 分页查询客户列表
     */
    TableDataInfo<PrUserVo> selectPageList(String companyId, PageQuery pageQuery);

    /**
     * 查询客户详情
     */
    PrUserVo selectDetailById(String userId);

    /**
     * 新增客户
     */
    boolean insertData(PrUserVo vo);

    /**
     * 修改客户
     */
    boolean updateData(PrUserVo vo);

    /**
     * 删除客户（含头像清理）
     */
    boolean deleteData(String id, String idNo);

    /**
     * 查询房屋是否有业主
     */
    boolean hasUser(String houseId);

    /**
     * 根据手机号查询客户
     */
    PrUserVo selectByPhone(String phone);
}
