package org.sdkj.job.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillDto {

    private Long billId;
    private String billChannel;
    private String billDate;
    private BigDecimal billAmount;
}
