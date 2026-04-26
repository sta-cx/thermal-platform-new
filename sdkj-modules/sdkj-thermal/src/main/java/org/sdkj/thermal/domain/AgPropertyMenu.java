package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.AgPropertyMenuVo;

/**
 * 代理商物业菜单关联表
 * 对应 ag_property_menu 表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ag_property_menu")
@AutoMapper(target = AgPropertyMenuVo.class)
public class AgPropertyMenu extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String companyId;

    private String menuId;
}
