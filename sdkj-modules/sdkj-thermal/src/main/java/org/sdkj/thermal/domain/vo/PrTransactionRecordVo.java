package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrTransactionRecord;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 交易记录视图对象
 */
@Data
@AutoMapper(target = PrTransactionRecord.class)
public class PrTransactionRecordVo {

    private Long id;
    private String serialNum;
    private Integer transactionType;
    private Integer paymentType;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private Integer status;
    private Long houseId;
    private String userId;
    private String orgId;
    private String companyId;
    private String itemGroup;
    private String itemCode;
    private Date transactionTime;
    private String operatorId;
    private String notes;
    private Long originalRecordId;
    private String invoiceNo;

    /** 房间号（关联查询） */
    private String roomNum;

    /** 用户名称（关联查询） */
    private String userName;

    /** 费用名称（关联查询） */
    private String itemName;

    /** 子记录列表 */
    private List<PrTransactionRecordSubVo> subList;
}
