package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.bo.PrHouseBo;
import org.sdkj.thermal.domain.vo.PrHouseVo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 房屋信息 Service 接口
 * 迁移自旧系统 PrHouseService
 */
public interface IPrHouseService extends IService<PrHouse> {

    /**
     * 根据ID查询房屋详情
     */
    PrHouseVo selectById(Serializable id);

    /**
     * 分页查询房屋列表
     * @param search 搜索关键字（房间号）
     * @param buildingId 楼宇ID
     * @param orgId 小区ID
     * @param status 房屋状态
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHouseVo> selectPageList(String search, String buildingId, String orgId,
                                            String status, PageQuery pageQuery);

    /**
     * 校验房间号是否唯一
     * @param roomNum 房间号
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param id 排除的ID（编辑时传入）
     * @return true=房间号已存在
     */
    boolean validateRoomNum(String roomNum, Long buildingId, String unitCode, Long id);

    /**
     * 根据楼宇+单元查询房屋列表
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码（可选）
     * @return 房屋列表
     */
    List<PrHouseVo> selectByUnit(String buildingId, String unitCode);

    /**
     * 根据小区查询房屋列表
     * @param orgId 小区ID
     * @return 房屋列表
     */
    List<PrHouseVo> selectByOrg(String orgId);

    /**
     * 查询用户关联的房屋数量
     * @param userId 用户ID
     * @return 房屋数量
     */
    long countByUser(String userId);

    /**
     * 查询用户关联的房屋总面积
     * @param userId 用户ID
     * @return 总面积
     */
    BigDecimal areaByUser(String userId);

    // ========== 类型筛选功能 ==========

    /**
     * 按类型筛选房屋列表（特殊户、收费状态等）
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param stationId 换热站ID
     * @param types 类型列表（1=特殊户, 2=非特殊户, 3=已收费, 4=未收费）
     * @return 房屋列表
     */
    List<PrHouseVo> selectByType(String orgId, String buildingId,
                                 String unitCode, String stationId, List<String> types);

    /**
     * 按阀门和供热类型筛选房屋列表
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param stationId 换热站ID
     * @param types 类型列表
     * @return 房屋列表
     */
    List<PrHouseVo> selectByValveAndHotType(String orgId, String buildingId,
                                            String unitCode, String stationId, List<String> types);

    /**
     * 查询所有房屋（用于导出）
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param status 状态
     * @param search 搜索关键字
     * @return 房屋列表
     */
    List<PrHouseVo> selectAllForExport(String orgId, String buildingId,
                                       String status, String search);

    /**
     * 通过其他编码查询房屋
     * @param otherCode 外部缴费编码
     * @return 房屋列表
     */
    List<PrHouseVo> selectByOtherCode(String otherCode);

    /**
     * 通过缴费状态查询房屋
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param status 房屋状态
     * @param payStatus 缴费状态
     * @param search 搜索关键字
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHouseVo> selectByPayStatus(String orgId, String buildingId,
                                               String status, String payStatus, String search, PageQuery pageQuery);

    /**
     * 多条件综合搜索房屋
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param type 类型
     * @param search 搜索关键字
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHouseVo> selectByMultiSearch(String orgId, String buildingId,
                                                 String type, String search, PageQuery pageQuery);

    /**
     * 设置供热编码
     * @param id 房屋ID
     * @param otherCode 外部编码
     * @return 是否成功
     */
    boolean updateOtherCode(String id, String otherCode);

    /**
     * 查询供热编码
     * @param id 房屋ID
     * @return 外部编码
     */
    String queryOtherCode(String id);

    // ========== 孤岛户功能 ==========

    /**
     * 查询孤岛户列表
     * 通过分析相邻房号的阀门状态来判断孤岛户
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @return 孤岛户列表
     */
    List<PrHouseVo> queryIsolatedHouses(String orgId, String buildingId);

    /**
     * 设置孤岛户标记
     * 先重置楼宇下所有房屋的位置属性，再更新传入的孤岛户列表
     * @param houseList 房屋列表（包含 siteType 和 siteTypeOld）
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @return 是否成功
     */
    boolean setIsolatedHouses(List<PrHouse> houseList, String orgId, String buildingId);

    /**
     * 批量导入房屋数据
     * @param houseList 房屋数据列表
     * @return 成功导入的数量
     */
    int importAll(List<PrHouseBo> houseList);

    /**
     * 查询可用于策略绑定的房屋列表
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param search 搜索关键字（房间号）
     * @return 房屋列表
     */
    List<PrHouseVo> selectForStrategyBinding(String orgId, String buildingId, String search);
}
