package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrUnit;
import org.sdkj.thermal.domain.vo.PrUnitVo;

import java.util.List;

/**
 * 单元信息 Service 接口
 * 迁移自旧系统 PrUnitService
 */
public interface IPrUnitService extends IService<PrUnit> {

    /**
     * 分页查询单元列表
     * @param search 搜索关键字
     * @param buildingId 楼宇ID
     * @param orgId 小区ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrUnitVo> selectPageList(String search, String buildingId, String orgId, PageQuery pageQuery);

    /**
     * 校验单元名称是否重复
     * @param name 单元名称
     * @param id 排除的ID（编辑时传入）
     * @return true=名称已存在
     */
    boolean validateName(String name, String id);

    /**
     * 根据楼宇ID查询单元列表
     * @param buildingId 楼宇ID
     * @return 单元列表
     */
    List<PrUnitVo> selectByBuildingId(String buildingId);

}
