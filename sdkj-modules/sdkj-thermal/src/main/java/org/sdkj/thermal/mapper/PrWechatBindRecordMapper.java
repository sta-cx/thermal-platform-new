package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrWechatBindRecord;
import org.sdkj.thermal.domain.vo.PrWechatBindRecordVo;

/**
 * 微信绑定记录 Mapper
 */
public interface PrWechatBindRecordMapper extends BaseMapperPlus<PrWechatBindRecord, PrWechatBindRecordVo> {

    int getCountByHouseId(@Param("houseId") String houseId, @Param("createBy") String createBy);

    int insertData(@Param("houseId") String houseId, @Param("heatPayCode") String heatPayCode,
                   @Param("wxOpenId") String wxOpenId, @Param("orgId") String orgId,
                   @Param("orgName") String orgName, @Param("buildingId") String buildingId,
                   @Param("buildingName") String buildingName, @Param("unitCode") String unitCode,
                   @Param("roomNum") String roomNum, @Param("companyId") String companyId,
                   @Param("createBy") String createBy);
}
