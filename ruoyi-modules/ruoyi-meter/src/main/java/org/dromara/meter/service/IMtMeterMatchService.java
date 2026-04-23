package org.dromara.meter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.meter.domain.MtMeterMatch;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仪表分配服务接口
 */
public interface IMtMeterMatchService extends IService<MtMeterMatch> {

    /**
     * 批量分配仪表给公司（原子操作：先删后插）
     */
    @Transactional(rollbackFor = Exception.class)
    void batchAllocate(String companyId, List<String> archiveIds, String meterType);
}
