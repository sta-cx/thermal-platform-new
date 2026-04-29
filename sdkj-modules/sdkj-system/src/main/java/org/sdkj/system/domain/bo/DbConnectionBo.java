package org.sdkj.system.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DbConnectionBo {

    @NotBlank(message = "数据库主机不能为空")
    private String dbHost;

    @NotNull(message = "数据库端口不能为空")
    private Integer dbPort;

    @NotBlank(message = "数据库用户名不能为空")
    private String dbUsername;

    @NotBlank(message = "数据库密码不能为空")
    private String dbPassword;
}
