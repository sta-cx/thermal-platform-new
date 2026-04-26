package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.vo.PrHouseVo;

import java.util.List;

/**
 * 房屋 Mapper
 */
public interface PrHouseMapper extends BaseMapperPlus<PrHouse, PrHouseVo> {

    /**
     * 查询房屋其他编码
     */
    String selectOtherCodeById(@Param("id") String id);

    /**
     * 更新房屋其他编码
     */
    int updateOtherCodeById(@Param("id") String id, @Param("otherCode") String otherCode);

    // ========== 孤岛户功能 ==========

    /**
     * 查询孤岛户列表
     * 通过分析相邻房号的阀门状态来判断孤岛户
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @return 孤岛户列表
     */
    List<PrHouse> queryIsolatedHouses(@Param("companyId") String companyId,
                                       @Param("orgId") String orgId,
                                       @Param("buildingId") String buildingId);

    /**
     * 批量更新房屋位置属性（孤岛户标记）
     * @param houseList 房屋列表
     * @return 更新行数
     */
    int updateSiteTypeBatch(@Param("list") List<PrHouse> houseList);

    /**
     * 重置楼宇下所有房屋的位置属性
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @return 更新行数
     */
    int resetSiteTypeByBuilding(@Param("companyId") String companyId,
                                 @Param("orgId") String orgId,
                                 @Param("buildingId") String buildingId);
}
