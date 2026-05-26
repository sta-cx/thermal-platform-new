package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrStrategy;
import org.sdkj.thermal.domain.bo.PrStrategyBo;

public interface IPrStrategyService extends IService<PrStrategy> {

    boolean saveStrategy(PrStrategyBo bo);

    boolean updateStrategy(PrStrategyBo bo);
}
