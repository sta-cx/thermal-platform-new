package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.vo.HtRepairVo;

import java.util.List;
import java.util.Map;

/**
 * 报修记录 Mapper
 * 迁移自旧系统 HtRepairMapper
 */
@OrgPermission
public interface HtRepairMapper extends BaseMapperPlus<HtRepair, HtRepairVo> {

    /**
     * 按报修类型统计数量
     * @return 类型统计列表
     */
    List<Map<String, Object>> selectTypeCount();

    /**
     * 根据房间ID查询报修记录
     * @param roomId 房屋ID
     * @return 报修记录列表
     */
    List<HtRepairVo> selectByRoomId(@Param("roomId") String roomId);

    /**
     * 逻辑删除报修记录
     * @param repairNo 报修编号
     * @return 影响行数
     */
    int markAsDeleted(@Param("repairNo") String repairNo);

    @Select("SELECT MAX(repair_no) FROM ht_repair WHERE repair_no LIKE CONCAT(#{prefix}, '%')")
    String selectMaxRepairNoByPrefix(@Param("prefix") String prefix);

}
