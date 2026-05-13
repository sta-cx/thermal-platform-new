package org.sdkj.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.system.domain.SysOperLog;
import org.sdkj.system.domain.vo.SysOperLogVo;

/**
 * 操作日志 数据层
 *
 * sys_oper_log 物理表存在 master 库。类级 @DS("master") 让任何调用方
 * (含 @Async 线程继承到 tenant DS 的场景)都自动路由到 master。
 *
 * @author Lion Li
 */
@DS("master")
public interface SysOperLogMapper extends BaseMapperPlus<SysOperLog, SysOperLogVo> {

}
