package org.sdkj.system.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TenantUserBindBo {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "租户ID不能为空")
    private String tenantId;
}
