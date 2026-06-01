package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.vo.PrBuildingVo;

import java.io.Serializable;
import java.util.List;

/**
 * 楼宇信息 Service 接口
 * 迁移自旧系统 PrBuildingService
 */
public interface IPrBuildingService extends IService<PrBuilding> {

    /**
     * 根据ID查询楼宇详情
     */
    PrBuildingVo selectById(Serializable id);

    /**
     * 分页查询楼宇列表
     * @param search 搜索关键字（名称/编码）
     * @param used 用途
     * @param orgId 小区ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrBuildingVo> selectPageList(String search, String used, String orgId, PageQuery pageQuery);

    /**
     * 校验楼宇名称是否重复
     * @param name 楼宇名称
     * @param id 排除的ID（编辑时传入）
     * @return true=名称已存在
     */
    boolean validateName(String name, String id);

    /**
     * 根据小区ID查询楼宇列表
     * @param orgId 小区ID
     * @return 楼宇列表
     */
    List<PrBuildingVo> selectByOrgId(String orgId);

    /**
     * 根据楼宇ID查询单元编码列表
     * @param buildingId 楼宇ID
     * @return 单元编码列表
     */
    List<String> selectUnitCodesByBuildingId(String buildingId);

    /**
     * 根据单元编码查询房间号列表
     * @param unitCode 单元编码
     * @return 房间号列表
     */
    List<String> selectRoomNumsByUnitCode(String unitCode);

    /**
     * 按楼宇查询其下房屋实际使用的单元编码（DISTINCT pr_house.unit_code）。
     * 与 selectUnitCodesByBuildingId 区别：后者取 pr_unit.code，本方法取 pr_house.unit_code，
     * 供「按单元筛选房屋/户阀」的下拉用，保证下拉值与 pr_house.unit_code 筛选键同源。
     */
    List<String> selectHouseUnitCodesByBuildingId(String buildingId);

    /**
     * 根据热力站ID查询楼宇列表
     * @param stationId 热力站ID
     * @return 楼宇列表
     */
    List<PrBuildingVo> selectByStationId(String stationId);

}
