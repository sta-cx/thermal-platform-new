package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrFamily;
import org.sdkj.thermal.domain.vo.PrFamilyVo;

/**
 * 家庭成员信息 Service 接口
 * 迁移自旧系统 PrFamilyService
 */
public interface IPrFamilyService extends IService<PrFamily> {

    /**
     * 分页查询家庭成员列表
     * @param search 搜索关键字
     * @param houseId 房屋ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrFamilyVo> selectPageList(String search, String houseId, PageQuery pageQuery);

}
