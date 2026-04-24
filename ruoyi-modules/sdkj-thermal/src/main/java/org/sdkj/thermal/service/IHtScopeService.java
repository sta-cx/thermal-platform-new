package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.HtScope;
import org.sdkj.thermal.domain.vo.HtScopeVo;

import java.util.List;

/**
 * 控制范围服务接口
 */
public interface IHtScopeService extends IService<HtScope> {

    /**
     * 根据任务ID获取控制范围内的房屋列表
     */
    List<HtScopeVo> getHouseListByTaskId(String orgId, String taskId, Integer scopeType);
}
