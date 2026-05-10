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
import cn.idev.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.sdkj.thermal.domain.vo.PrHeatMonthVo;
import org.sdkj.thermal.service.IPrHeatMonthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 热表月表管理
 * 旧端点: /ht/heatMonth/* -> 新端点: /thermal/ht/heat-month/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/heat-month")
public class PrHeatMonthController extends BaseController {

    private final IPrHeatMonthService heatMonthService;

    /**
     * 分页查询月表数据
     * 旧端点: GET /ht/heatMonth/pageList
     * 新端点: GET /thermal/ht/heat-month/list
     */
    @SaCheckPermission("thermal:ht:heatMonth:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatMonthVo> list(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String isCharged,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            PageQuery pageQuery) {
        return heatMonthService.selectPageList(orgId, buildingId, unitCode, search, isCharged, startTime, endTime, pageQuery);
    }

    /**
     * 查询月表详情
     * 旧端点: GET /ht/heatMonth/getDataById/{id}
     * 新端点: GET /thermal/ht/heat-month/{id}
     */
    @SaCheckPermission("thermal:ht:heatMonth:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatMonthVo> getById(@PathVariable String id) {
        return R.ok(heatMonthService.selectById(id));
    }

    /**
     * 生成月表数据（从日表聚合）
     * 默认生成上月数据，force=true 时重算当月
     * 旧端点: POST /ht/heatMonth/setHeat
     * 新端点: POST /thermal/ht/heat-month/setHeat
     */
    @SaCheckPermission("thermal:ht:heatMonth:edit")
    @SaCheckLogin
    @Log(title = "热表月表-生成", businessType = BusinessType.UPDATE)
    @PostMapping("/setHeat")
    public R<Void> setHeat(@RequestParam String orgId,
                           @RequestParam(required = false, defaultValue = "false") Boolean force) {
        heatMonthService.setHeat(orgId, force);
        return R.ok();
    }

    /**
     * 导出月表Excel
     * 旧端点: GET /ht/heatMonth/exportAll
     * 新端点: GET /thermal/ht/heat-month/exportAll
     */
    @SaCheckPermission("thermal:ht:heatMonth:list")
    @SaCheckLogin
    @Log(title = "热表月表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/exportAll")
    public void exportAll(HttpServletResponse response,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String isCharged,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) throws IOException {
        var list = heatMonthService.selectPageList(orgId, buildingId, unitCode, search, isCharged, startTime, endTime, null).getRows();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("热表月表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatMonthVo.class).sheet("月表").doWrite(list);
    }
}
