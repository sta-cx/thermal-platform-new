package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.SysColumn;
import org.dromara.system.domain.vo.SysColumnVo;
import org.dromara.system.service.ISysColumnService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户自定义表格列
 * 迁移自旧系统 SysColumnController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/sysColumn")
public class SysColumnController extends BaseController {

    private final ISysColumnService sysColumnService;

    /**
     * 根据页面名称查找自定义列
     * 旧端点: GET /property/sysColumn/pageList
     * 新端点: GET /thermal/sysColumn/pageList
     */
    @SaCheckLogin
    @GetMapping("/pageList")
    public R<String[]> pageList(@RequestParam @NotBlank String tableName) {
        Long userId = LoginHelper.getUserId();
        SysColumnVo vo = sysColumnService.getByUserIdAndPageName(userId, tableName);
        if (vo != null && vo.getColumnName() != null) {
            return R.ok(vo.getColumnName().split(","));
        }
        return R.ok(new String[0]);
    }

    /**
     * 新增/更新自定义列
     * 旧端点: POST /property/sysColumn/insertData
     * 新端点: POST /thermal/sysColumn/saveOrUpdate
     */
    @SaCheckLogin
    @Log(title = "自定义列", businessType = BusinessType.UPDATE)
    @PostMapping("/saveOrUpdate")
    public R<Void> saveOrUpdate(
            @RequestParam @NotBlank String tableName,
            @RequestParam String columnName) {
        Long userId = LoginHelper.getUserId();
        SysColumn sysColumn = new SysColumn();
        sysColumn.setUserId(userId);
        sysColumn.setPageName(tableName);
        sysColumn.setColumnName(columnName);
        return toAjax(sysColumnService.saveOrUpdate(sysColumn));
    }

    /**
     * 删除自定义列（校验用户归属）
     */
    @SaCheckLogin
    @Log(title = "自定义列", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        Long userId = LoginHelper.getUserId();
        sysColumnService.deleteById(id, userId);
        return R.ok();
    }
}
