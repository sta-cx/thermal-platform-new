package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.HtRepair;
import org.dromara.thermal.domain.vo.HtRepairVo;

import java.util.List;
import java.util.Map;

/**
 * 报修记录 Mapper
 * 迁移自旧系统 HtRepairMapper
 */
public interface HtRepairMapper extends BaseMapperPlus<HtRepair, HtRepairVo> {

    /**
     * 按报修类型统计数量
     * @param companyId 公司ID
     * @return 类型统计列表
     */
    List<Map<String, Object>> selectTypeCount(@Param("companyId") String companyId);

    /**
     * 根据房间ID查询报修记录
     * @param roomId 房屋ID
     * @return 报修记录列表
     */
    List<HtRepairVo> selectByRoomId(@Param("roomId") String roomId);

    /**
     * 逻辑删除报修记录
     * @param repairNo 报修编号
     * @param companyId 公司ID
     * @return 影响行数
     */
    int markAsDeleted(@Param("repairNo") String repairNo, @Param("companyId") String companyId);

}
