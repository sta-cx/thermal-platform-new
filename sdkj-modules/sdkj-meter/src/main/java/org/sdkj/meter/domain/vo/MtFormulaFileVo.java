package org.sdkj.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.meter.domain.MtFormulaFile;

/**
 * 公式档案视图对象
 */
@Data
@AutoMapper(target = MtFormulaFile.class)
public class MtFormulaFileVo {

    private String id;
    private String name;
    private String type;
    private String cformula;
    private String eformula;
    private String seq;
    private String isEnabled;
}
