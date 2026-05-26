package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrStrategy;
import org.sdkj.thermal.domain.vo.PrStrategyVo;

@OrgPermission
public interface PrStrategyMapper extends BaseMapperPlus<PrStrategy, PrStrategyVo> {

    @InterceptorIgnore(dataPermission = "true")
    @Select("SELECT id FROM pr_strategy WHERE org_id = #{orgId} AND type = 'base' AND del_flag = '0' LIMIT 1")
    Long selectBaseStrategyId(@Param("orgId") String orgId);
}
