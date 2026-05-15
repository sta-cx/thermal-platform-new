package org.sdkj.ai.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.sdkj.ai.domain.AiToolInvocation;
import org.sdkj.ai.domain.vo.AiToolInvocationVo;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;

@Mapper
@DS("master")
public interface AiToolInvocationMapper extends BaseMapperPlus<AiToolInvocation, AiToolInvocationVo> {
}
