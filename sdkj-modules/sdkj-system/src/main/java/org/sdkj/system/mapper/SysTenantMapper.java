package org.sdkj.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.sdkj.system.domain.SysTenant;
import org.sdkj.system.domain.vo.SysTenantVo;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 租户Mapper接口
 *
 * @author Michelle.Chung
 */
@DS("master")
public interface SysTenantMapper extends BaseMapperPlus<SysTenant, SysTenantVo> {

}
