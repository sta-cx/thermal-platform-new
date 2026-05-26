package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrStrategy;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrStrategy.class, reverseConvertGenerate = false)
public class PrStrategyBo extends BaseEntity {

    private Long id;

    @NotBlank(message = "策略名称不能为空")
    @Size(max = 128)
    private String name;

    @NotBlank(message = "策略类型不能为空")
    @Size(max = 32)
    private String type;

    private String content;

    @NotBlank(message = "小区ID不能为空")
    private String orgId;
}
