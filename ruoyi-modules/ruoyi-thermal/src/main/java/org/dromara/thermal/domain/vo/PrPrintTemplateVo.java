package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrPrintTemplate;

/**
 * 打印模板 VO
 */
@Data
@AutoMapper(target = PrPrintTemplate.class)
public class PrPrintTemplateVo {
    private String id;
    private String name;
    private String templateContent;
    private String serialNum;
    private String companyId;
    private String orgId;
}
