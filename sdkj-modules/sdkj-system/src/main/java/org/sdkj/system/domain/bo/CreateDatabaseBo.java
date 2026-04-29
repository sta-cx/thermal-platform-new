package org.sdkj.system.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateDatabaseBo extends DbConnectionBo {

    @NotBlank(message = "数据库名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "数据库名称只允许字母、数字和下划线")
    private String dbName;

    private Boolean initSchema = true;
}
