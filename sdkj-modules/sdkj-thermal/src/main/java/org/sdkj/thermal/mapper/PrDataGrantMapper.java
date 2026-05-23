package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrDataGrant;

import java.util.Date;

public interface PrDataGrantMapper extends BaseMapperPlus<PrDataGrant, PrDataGrant> {

    /**
     * 统计早于阈值的逻辑删墓碑数量。
     * 用原生 SQL 绕过 MyBatis-Plus 的 @TableLogic 自动过滤(否则永远查不到 del_flag='1' 的行)。
     */
    @Select("SELECT COUNT(*) FROM pr_data_grant WHERE del_flag = '1' AND update_time < #{threshold}")
    long countTombstones(@Param("threshold") Date threshold);

    /**
     * 物理删除一批早于阈值的逻辑删墓碑,LIMIT 控制单次事务大小。
     * 注意:原生 @Delete SQL 不受 @TableLogic 拦截,直接物理删行。
     */
    @Delete("DELETE FROM pr_data_grant WHERE del_flag = '1' AND update_time < #{threshold} LIMIT #{batchSize}")
    int deleteTombstoneBatch(@Param("threshold") Date threshold, @Param("batchSize") int batchSize);
}
