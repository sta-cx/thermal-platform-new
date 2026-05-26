package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrStrategy;

import java.util.Date;

@Data
@AutoMapper(target = PrStrategy.class)
public class PrStrategyVo {

    private Long id;
    private String name;
    private String type;
    private String content;
    private String orgId;
    private Date createTime;
}
