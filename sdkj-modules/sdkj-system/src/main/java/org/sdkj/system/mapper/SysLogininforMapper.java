package org.sdkj.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.system.domain.SysLogininfor;
import org.sdkj.system.domain.vo.SysLogininforVo;

/**
 * 系统访问日志情况信息 数据层
 *
 * sys_logininfor 物理表存在 master 库。类级 @DS("master") 让任何调用方
 * (含 @Async 线程继承到 tenant DS 的场景)都自动路由到 master。
 *
 * @author Lion Li
 */
@DS("master")
public interface SysLogininforMapper extends BaseMapperPlus<SysLogininfor, SysLogininforVo> {

}
