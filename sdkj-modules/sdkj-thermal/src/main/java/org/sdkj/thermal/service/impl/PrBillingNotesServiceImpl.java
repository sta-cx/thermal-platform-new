package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrBillingNotes;
import org.sdkj.thermal.mapper.PrBillingNotesMapper;
import org.sdkj.thermal.service.IPrBillingNotesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 票据备注 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrBillingNotesServiceImpl extends ServiceImpl<PrBillingNotesMapper, PrBillingNotes>
        implements IPrBillingNotesService {

    private final PrBillingNotesMapper baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSerialNum(String serialNum, String notes) {
        PrBillingNotes existing = baseMapper.selectBySerialNum(serialNum);
        PrBillingNotes bn = new PrBillingNotes();
        if (existing != null) {
            bn.setId(existing.getId());
            bn.setNotes(notes);
            return updateById(bn);
        }
        bn.setSerialNum(serialNum);
        bn.setNotes(notes);
        return save(bn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reprint(String recordId, String serialNum) {
        return saveSerialNum(serialNum, "重开票据 recordId=" + recordId);
    }
}
