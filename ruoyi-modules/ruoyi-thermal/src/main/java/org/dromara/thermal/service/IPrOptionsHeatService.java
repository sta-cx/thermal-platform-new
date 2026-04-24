package org.dromara.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.thermal.domain.PrOptionsHeat;

/**
 * 供热系统选项 Service 接口
 */
public interface IPrOptionsHeatService extends IService<PrOptionsHeat> {

    /**
     * 根据 orgId 和 companyId 查询配置
     */
    PrOptionsHeat getByOrgAndCompany(String orgId, String companyId, String level);

    /**
     * 初始化默认配置
     */
    boolean initData(String orgId, String companyId);
}
