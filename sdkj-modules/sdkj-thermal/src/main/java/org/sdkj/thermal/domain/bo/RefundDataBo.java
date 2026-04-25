package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class RefundDataBo {

    @NotEmpty(message = "退费房屋不能为空")
    private Map<String, String> houses;

    @NotBlank(message = "费项分组不能为空")
    private String itemGroup;

    @NotBlank(message = "费项编码不能为空")
    private String itemCode;

    @DecimalMin(value = "0.01", message = "退费金额必须大于0")
    private BigDecimal amount;
}
