package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrBillingNotes;

/**
 * 票据备注 Service 接口
 */
public interface IPrBillingNotesService extends IService<PrBillingNotes> {

    /**
     * 保存票据流水号/备注（存在则更新，不存在则新增）
     */
    boolean saveSerialNum(String serialNum, String notes);

    /**
     * 重开票据
     */
    boolean reprint(String recordId, String serialNum);
}
