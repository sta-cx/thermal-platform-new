package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrOptions;

import java.util.List;

/**
 * 系统选项 Service 接口
 */
public interface IPrOptionsService extends IService<PrOptions> {

    /**
     * 根据组织ID查询系统选项列表
     */
    List<PrOptions> selectByOrgId(String orgId);

    /**
     * 初始化系统选项
     */
    boolean initOptions(String orgId);

    /**
     * 检查房屋是否禁止购电/购水
     */
    boolean forbiddenToBuyCheck(String houseId);
}
