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
import org.sdkj.thermal.domain.vo.PrHeatDailyVo;
import org.sdkj.thermal.service.IPrHeatDailyService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 热表日表管理
 * 旧端点: /ht/heatDaily/* -> 新端点: /thermal/ht/heat-daily/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping({"/thermal/ht/heat-daily", "/thermal/ht/heat-usage"})
public class PrHeatDailyController extends BaseController {

    private final IPrHeatDailyService heatDailyService;

    /**
     * 分页查询日表数据
     * 旧端点: GET /ht/heatDaily/pageList
     * 新端点: GET /thermal/ht/heat-daily/list
     */
    @SaCheckPermission("thermal:ht:heat-daily:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatDailyVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer isCharged,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            PageQuery pageQuery) {
        return heatDailyService.selectPageList(companyId, orgId, buildingId, unitCode, search, isCharged, startTime, endTime, pageQuery);
    }

    /**
     * 查询日表详情
     * 旧端点: GET /ht/heatDaily/getDataById/{id}
     * 新端点: GET /thermal/ht/heat-daily/{id}
     */
    @SaCheckPermission("thermal:ht:heat-daily:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatDailyVo> getById(@PathVariable String id) {
        return R.ok(heatDailyService.selectById(id));
    }

    /**
     * 生成日表数据
     * 旧端点: POST /ht/heatDaily/setHeat
     * 新端点: POST /thermal/ht/heat-daily/setHeat
     * 5步流程：setIsValid -> setHeatDaily -> setSteps -> setQtyStepsN -> setCurrentReading
     */
    @SaCheckPermission("thermal:ht:heat-daily:edit")
    @SaCheckLogin
    @Log(title = "热表日表-生成", businessType = BusinessType.UPDATE)
    @PostMapping("/setHeat")
    public R<Void> setHeat(@RequestParam String companyId, @RequestParam String orgId) {
        boolean result = heatDailyService.generateHeatDaily(companyId, orgId);
        return result ? R.ok() : R.fail("日表生成失败");
    }

    /**
     * 导出日表Excel
     * 旧端点: GET /ht/heatDaily/exportAll
     * 新端点: GET /thermal/ht/heat-daily/exportAll
     */
    @SaCheckPermission("thermal:ht:heat-daily:list")
    @SaCheckLogin
    @Log(title = "热表日表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/exportAll")
    public void exportAll(HttpServletResponse response,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer isCharged,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) throws IOException {
        var list = heatDailyService.selectPageList(companyId, orgId, buildingId, unitCode, search, isCharged, startTime, endTime, null).getRows();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("热表日表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatDailyVo.class).sheet("日表").doWrite(list);
    }
}
