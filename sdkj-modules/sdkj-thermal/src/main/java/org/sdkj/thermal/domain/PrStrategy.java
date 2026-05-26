package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrStrategyVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_strategy")
@AutoMapper(target = PrStrategyVo.class)
public class PrStrategy extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String name;
    private String type;
    private String content;
    private String orgId;
}
