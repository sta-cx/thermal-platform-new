package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ag_auto_version")
public class AgAutoVersion {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String version;
}
