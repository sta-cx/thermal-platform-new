package org.sdkj.thermal.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PrHouseChangeOwnerBo {

    @NotNull(message = "houseId 不能为空")
    private Long houseId;

    @NotBlank(message = "userId 不能为空")
    private String userId;

    @NotBlank(message = "userName 不能为空")
    private String userName;

    private String phone;

    @NotBlank(message = "变更原因不能为空")
    @Size(max = 500)
    private String reason;

    /** 默认 "1" 业主 */
    private String relationType;
}
