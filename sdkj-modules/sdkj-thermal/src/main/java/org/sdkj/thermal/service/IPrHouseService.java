package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHouse;
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
     * @param companyId 公司ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHouseVo> selectPageList(String search, String buildingId, String orgId,
                                            String status, String companyId, PageQuery pageQuery);

    /**
     * 校验房间号是否唯一
     * @param roomNum 房间号
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param id 排除的ID（编辑时传入）
     * @return true=房间号已存在
     */
    boolean validateRoomNum(String roomNum, String buildingId, String unitCode, String id);

    /**
     * 根据楼宇+单元查询房屋列表
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码（可选）
     * @return 房屋列表
     */
    List<PrHouseVo> selectByUnit(String buildingId, String unitCode);

    /**
     * 根据小区查询房屋列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 房屋列表
     */
    List<PrHouseVo> selectByOrg(String companyId, String orgId);

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

    // ========== 孤岛户功能 ==========

    /**
     * 查询孤岛户列表
     * 通过分析相邻房号的阀门状态来判断孤岛户
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @return 孤岛户列表
     */
    List<PrHouseVo> queryIsolatedHouses(String companyId, String orgId, String buildingId);

    /**
     * 设置孤岛户标记
     * 先重置楼宇下所有房屋的位置属性，再更新传入的孤岛户列表
     * @param houseList 房屋列表（包含 siteType 和 siteTypeOld）
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @return 是否成功
     */
    boolean setIsolatedHouses(List<PrHouse> houseList, String companyId, String orgId, String buildingId);
}
