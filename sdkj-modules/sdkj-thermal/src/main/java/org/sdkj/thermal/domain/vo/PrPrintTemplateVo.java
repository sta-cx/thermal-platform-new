package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrPrintTemplate;

/**
 * 打印模板 VO
 */
@Data
@AutoMapper(target = PrPrintTemplate.class)
public class PrPrintTemplateVo {
    private Long id;
    private String name;
    private String templateContent;
    private String serialNum;
    private String orgId;
}
