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
import org.sdkj.thermal.domain.vo.PrHeatReadingVo;
import org.sdkj.thermal.service.IPrHeatReadingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 热表抄表记录管理
 * 旧端点: /ht/heatReading/* -> 新端点: /thermal/ht/heat-reading/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/heat-reading")
public class PrHeatReadingController extends BaseController {

    private final IPrHeatReadingService heatReadingService;

    /**
     * 分页查询抄表数据
     * 旧端点: GET /ht/heatReading/pageList
     * 新端点: GET /thermal/ht/heat-reading/list
     */
    @SaCheckPermission("thermal:ht:heat-reading:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatReadingVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String meterArcCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String valveType,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return heatReadingService.selectPageList(companyId, orgId, buildingId, unitCode, meterArcCode, search, startTime, endTime, type, valveType, parentId, pageQuery);
    }

    /**
     * 查询抄表详情
     * 旧端点: GET /ht/heatReading/getDataById/{id}
     * 新端点: GET /thermal/ht/heat-reading/{id}
     */
    @SaCheckPermission("thermal:ht:heat-reading:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatReadingVo> getById(@PathVariable String id) {
        return R.ok(heatReadingService.selectById(id));
    }

    /**
     * 查询阀门趋势数据
     * 旧端点: POST /ht/heatReading/pageListTrend
     * 新端点: POST /thermal/ht/heat-reading/trend
     */
    @SaCheckPermission("thermal:ht:heat-reading:query")
    @SaCheckLogin
    @Log(title = "热表抄表-阀门趋势", businessType = BusinessType.OTHER)
    @PostMapping("/trend")
    public R<List<PrHeatReadingVo>> trend(@RequestBody List<String> meterNums,
                                          @RequestParam(required = false) String startTime,
                                          @RequestParam(required = false) String endTime,
                                          @RequestParam(required = false) String companyId,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String orgId,
                                          @RequestParam(required = false) String parentId) {
        return R.ok(heatReadingService.selectTrendList(meterNums, startTime, endTime, companyId, status, orgId, parentId));
    }

    /**
     * 首页户间阀门趋势图
     * 旧端点: POST /ht/heatReading/pageListTrendS
     * 新端点: GET /thermal/ht/heat-reading/trend-home
     */
    @SaCheckPermission("thermal:ht:heat-reading:query")
    @SaCheckLogin
    @GetMapping("/trend-home")
    public R<List<PrHeatReadingVo>> trendHome(@RequestParam(required = false) String stationId,
                                              @RequestParam(required = false) String stationPartitionId,
                                              @RequestParam(required = false) String companyId) {
        return R.ok(heatReadingService.selectHomeTrendList(stationId, stationPartitionId, companyId));
    }

    /**
     * 导出抄表Excel
     * 旧端点: GET /ht/heatReading/exportAll
     * 新端点: GET /thermal/ht/heat-reading/exportAll
     */
    @SaCheckPermission("thermal:ht:heat-reading:list")
    @SaCheckLogin
    @Log(title = "热表抄表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/exportAll")
    public void exportAll(HttpServletResponse response,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String meterArcCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String valveType,
            @RequestParam(required = false) String parentId) throws IOException {
        var list = heatReadingService.selectPageList(companyId, orgId, buildingId, unitCode, meterArcCode, search, startTime, endTime, type, valveType, parentId, null).getRows();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("热表抄表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatReadingVo.class).sheet("抄表").doWrite(list);
    }
}
