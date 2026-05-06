package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserOrgBo {

    @NotNull(message = "userId不能为空")
    private Long userId;

    @NotBlank(message = "companyId不能为空")
    private String companyId;

    @NotNull(message = "orgIds不能为空")
    private List<String> orgIds;
}
