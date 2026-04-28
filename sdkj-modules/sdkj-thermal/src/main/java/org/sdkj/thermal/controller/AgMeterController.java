package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.service.IAgMeterService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 代理商仪表分配管理
 * 迁移自旧系统 AgentMeterController
 * 提供3个接口：查询可分配仪表、查询已分配仪表、分配仪表给公司
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/meter")
public class AgMeterController extends BaseController {

    private final IAgMeterService agMeterService;

    /**
     * 分页查询已分配的仪表列表（按公司和仪表类型查询）
     * meterType: 01=电表, 02=水表, 03=热力表, 04=燃气表
     */
    @SaCheckPermission("thermal:agent:meter:list")
    @SaCheckLogin
    @GetMapping("/allocated")
    public TableDataInfo<Map<String, Object>> allocated(
            @RequestParam String companyId,
            @RequestParam String meterType,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return agMeterService.queryAllocatedMeters(companyId, meterType, search, pageQuery);
    }

    /**
     * 查询所有可分配的仪表（全量，不分页）
     * meterType: 01=电表, 02=水表, 03=热力表, 04=燃气表
     */
    @SaCheckPermission("thermal:agent:meter:list")
    @SaCheckLogin
    @GetMapping("/all")
    public R<List<Map<String, Object>>> all(
            @RequestParam String meterType,
            @RequestParam(required = false) String search) {
        return R.ok(agMeterService.queryAllMeters(meterType, search));
    }

    /**
     * 批量分配仪表给公司（事务操作：先删后插）
     * archiveIds 逗号分隔的档案ID
     */
    @SaCheckPermission("thermal:agent:meter:add")
    @SaCheckLogin
    @Log(title = "仪表分配", businessType = BusinessType.INSERT)
    @PostMapping("/allocate")
    public R<Void> allocate(
            @RequestParam String companyId,
            @RequestParam String archiveIds,
            @RequestParam String meterType) {
        agMeterService.allocateMeters(companyId, archiveIds, meterType);
        return R.ok();
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(java.util.Collections.emptyList());
    }
}
