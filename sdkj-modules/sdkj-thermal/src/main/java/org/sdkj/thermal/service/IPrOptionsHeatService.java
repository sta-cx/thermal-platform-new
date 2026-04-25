package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrOptionsHeat;

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

    /**
     * 根据ID查询配置（兼容旧系统方法）
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @param level 配置级别
     * @return 配置信息
     */
    default PrOptionsHeat getDataById(String orgId, String companyId, String level) {
        return getByOrgAndCompany(orgId, companyId, level);
    }
}
