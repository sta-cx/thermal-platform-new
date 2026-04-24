package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtStrategy;

import java.util.List;

/**
 * 控制策略主表视图对象
 * 迁移自旧系统 HtStrategy
 */
@Data
@AutoMapper(target = HtStrategy.class)
public class HtStrategyVo {

    private String id;

    /** 策略名称 */
    private String name;

    /** 策略类型 */
    private Integer type;

    /** 公司ID */
    private String companyId;

    /** 备注 */
    private String remark;

    /** 子表记录列表 */
    private List<HtStrategySubVo> subList;

}
