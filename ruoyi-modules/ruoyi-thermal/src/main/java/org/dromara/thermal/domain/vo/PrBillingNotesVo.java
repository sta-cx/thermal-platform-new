package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrBillingNotes;

/**
 * 票据备注 VO
 */
@Data
@AutoMapper(target = PrBillingNotes.class)
public class PrBillingNotesVo {
    private String id;
    private String serialNum;
    private String notes;
}
