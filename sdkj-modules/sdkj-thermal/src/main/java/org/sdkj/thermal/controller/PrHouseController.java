package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.bo.PrHouseBo;
import org.sdkj.thermal.domain.bo.PrHouseChangeOwnerBo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.domain.vo.PrUserHouseVo;
import org.sdkj.thermal.service.IPrHouseService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 房屋信息管理
 * 迁移自旧系统 PrHouseController
 * 旧端点: /property/prHouse/* -> 新端点: /thermal/property/house/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/property/house")
public class PrHouseController extends BaseController {

    private final IPrHouseService houseService;

    /**
     * 分页查询房屋列表
     * 旧端点: GET /property/prHouse/pageList
     * 新端点: GET /thermal/property/house/list
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHouseVo> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String status,
            PageQuery pageQuery) {
        return houseService.selectPageList(search, buildingId, orgId, status, pageQuery);
    }

    /**
     * 查询房屋详情
     * 旧端点: GET /property/prHouse/getDataById/{id}
     * 新端点: GET /thermal/property/house/{id}
     */
    @SaCheckPermission("thermal:property:house:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHouseVo> getById(@PathVariable String id) {
        return R.ok(houseService.selectById(id));
    }

    /**
     * 新增房屋
     * 旧端点: POST /property/prHouse/insertData
     * 新端点: POST /thermal/property/house
     */
    @SaCheckPermission("thermal:property:house:add")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHouseBo bo) {
        PrHouse house = MapstructUtils.convert(bo, PrHouse.class);
        return toAjax(houseService.save(house));
    }

    /**
     * 修改房屋
     * 旧端点: POST /property/prHouse/updateData
     * 新端点: PUT /thermal/property/house
     */
    @SaCheckPermission("thermal:property:house:edit")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHouseBo bo) {
        PrHouse house = MapstructUtils.convert(bo, PrHouse.class);
        return toAjax(houseService.updateById(house));
    }

    /**
     * 删除房屋
     * 旧端点: POST /property/prHouse/deleteData/{id}
     * 新端点: DELETE /thermal/property/house/{id}
     */
    @SaCheckPermission("thermal:property:house:remove")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(houseService.removeById(id));
    }

    /**
     * 批量删除房屋
     * 旧端点: POST /property/prHouse/deleteMulData
     * 新端点: DELETE /thermal/property/house/batch
     */
    @SaCheckPermission("thermal:property:house:remove")
    @SaCheckLogin
    @Log(title = "房屋信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public R<Void> removeBatch(@RequestBody List<String> ids) {
        return toAjax(houseService.removeByIds(ids));
    }

    /**
     * 校验房间号是否唯一
     * 旧端点: GET /property/prHouse/validate
     * 新端点: GET /thermal/property/house/validate
     */
    @SaCheckPermission("thermal:property:house:query")
    @SaCheckLogin
    @GetMapping("/validate")
    public R<Boolean> validate(
            @RequestParam String roomNum,
            @RequestParam Long buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) Long id) {
        return R.ok(houseService.validateRoomNum(roomNum, buildingId, unitCode, id));
    }

    /**
     * 根据楼宇+单元查询房屋列表
     * 旧端点: GET /property/prHouse/getDataByBuildingUnit
     * 新端点: GET /thermal/property/house/byUnit
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/byUnit")
    public R<List<PrHouseVo>> listByUnit(
            @RequestParam String buildingId,
            @RequestParam(required = false) String unitCode) {
        return R.ok(houseService.selectByUnit(buildingId, unitCode));
    }

    /**
     * 根据小区查询房屋列表
     * 旧端点: GET /property/prHouse/getDataByOrgId
     * 新端点: GET /thermal/property/house/byOrg
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/byOrg")
    public R<List<PrHouseVo>> listByOrg(@RequestParam(required = false) String orgId) {
        return R.ok(houseService.selectByOrg(orgId));
    }

    /**
     * 按类型筛选房屋列表（特殊户、收费状态等）
     * 旧端点: GET /property/prHouse/getHouseListByUnitAndType
     * 新端点: GET /thermal/property/house/byType
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/byType")
    public R<List<PrHouseVo>> listByType(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false) List<String> types) {
        return R.ok(houseService.selectByType(orgId, buildingId, unitCode, stationId, types));
    }

    /**
     * 按阀门和供热类型筛选房屋列表
     * 旧端点: GET /property/prHouse/getHouseListByValveAndHotType
     * 新端点: GET /thermal/property/house/valveAndHot
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/valveAndHot")
    public R<List<PrHouseVo>> listByValveAndHot(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false) List<String> types) {
        return R.ok(houseService.selectByValveAndHotType(orgId, buildingId, unitCode, stationId, types));
    }

    /**
     * 导出房屋Excel
     * 旧端点: POST /property/prHouse/exportAll
     * 新端点: POST /thermal/property/house/export
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @Log(title = "房屋信息-导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String orgId,
                       @RequestParam(required = false) String buildingId,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String search) throws IOException {
        List<PrHouseVo> list = houseService.selectAllForExport(orgId, buildingId, status, search);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("房屋基础数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHouseVo.class).sheet("房屋数据").doWrite(list);
    }

    /**
     * 导入房屋Excel
     * 新端点: POST /thermal/property/house/import
     */
    @SaCheckPermission("thermal:property:house:import")
    @SaCheckLogin
    @Log(title = "房屋信息-导入", businessType = BusinessType.IMPORT)
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<String> importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("文件为空");
        }
        try {
            List<PrHouseBo> list = EasyExcel.read(file.getInputStream())
                .head(PrHouseBo.class)
                .sheet(0)
                .headRowNumber(1)
                .doReadSync();
            if (list == null || list.isEmpty()) {
                return R.fail("文件中无有效数据");
            }
            int imported = houseService.importAll(list);
            return R.ok("成功导入 " + imported + " 条房屋数据");
        } catch (Exception e) {
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    /**
     * 查询供热编码（通过外部编码查询房屋）
     * 旧端点: GET /property/prHouse/getHouseByOtherCode
     * 新端点: GET /thermal/property/house/byOtherCode
     */
    @SaCheckPermission("thermal:property:house:query")
    @SaCheckLogin
    @GetMapping("/byOtherCode")
    public R<List<PrHouseVo>> getByOtherCode(@RequestParam String otherCode) {
        List<PrHouseVo> list = houseService.selectByOtherCode(otherCode);
        if (list == null || list.isEmpty()) {
            return R.fail("该编码未查询到房屋信息");
        }
        return R.ok(list);
    }

    /**
     * 设置供热编码
     * 旧端点: PUT /property/prHouse/setGDH (部分功能)
     * 新端点: PUT /thermal/property/house/heatingCode
     */
    @SaCheckPermission("thermal:property:house:edit")
    @SaCheckLogin
    @Log(title = "房屋信息-设置供热编码", businessType = BusinessType.UPDATE)
    @PutMapping("/heatingCode")
    public R<Void> setHeatingCode(@RequestParam String id, @RequestParam String heatingCode) {
        return toAjax(houseService.updateOtherCode(id, heatingCode));
    }

    /**
     * 查询房屋供热编码
     * 旧端点: GET /property/prHouse/queryGDH (部分功能)
     * 新端点: GET /thermal/property/house/heatingCode
     */
    @SaCheckPermission("thermal:property:house:query")
    @SaCheckLogin
    @GetMapping("/heatingCode")
    public R<String> getHeatingCode(@RequestParam String id) {
        return R.ok(houseService.queryOtherCode(id));
    }

    /**
     * 按缴费状态查询房屋
     * 旧端点: GET /property/prHouse/getDataByPay
     * 新端点: GET /thermal/property/house/payStatus
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/payStatus")
    public TableDataInfo<PrHouseVo> listByPayStatus(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String payStatus,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return houseService.selectByPayStatus(orgId, buildingId, status, payStatus, search, pageQuery);
    }

    /**
     * 多条件综合搜索房屋
     * 旧端点: GET /property/prHouse/getDataByMulSearch
     * 新端点: GET /thermal/property/house/multiSearch
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/multiSearch")
    public TableDataInfo<PrHouseVo> multiSearch(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return houseService.selectByMultiSearch(orgId, buildingId, type, search, pageQuery);
    }

    /**
     * 查询用户关联的房屋数量
     * 旧端点: GET /property/prHouse/countByUserId
     * 新端点: GET /thermal/property/house/count
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/count")
    public R<Long> countByUser(@RequestParam String userId) {
        return R.ok(houseService.countByUser(userId));
    }

    /**
     * 查询用户关联的房屋总面积
     * 旧端点: GET /property/prHouse/areaByUserId
     * 新端点: GET /thermal/property/house/area
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/area")
    public R<BigDecimal> areaByUser(@RequestParam String userId) {
        return R.ok(houseService.areaByUser(userId));
    }

    // ========== 孤岛户功能 ==========

    /**
     * 查询孤岛户列表
     * 旧端点: GET /property/prHouse/queryGDH
     * 新端点: GET /thermal/property/house/isolated
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/isolated")
    public R<List<PrHouseVo>> queryIsolatedHouses(@RequestParam String orgId,
            @RequestParam(required = false) String buildingId) {
        return R.ok(houseService.queryIsolatedHouses(orgId, buildingId));
    }

    /**
     * 设置孤岛户标记
     * 旧端点: POST /property/prHouse/setGDH
     * 新端点: PUT /thermal/property/house/isolated
     */
    @SaCheckPermission("thermal:property:house:edit")
    @SaCheckLogin
    @Log(title = "房屋信息-设置孤岛户", businessType = BusinessType.UPDATE)
    @PutMapping("/isolated")
    public R<Void> setIsolatedHouses(
            @RequestBody List<PrHouse> houseList,
            @RequestParam String orgId,
            @RequestParam(required = false) String buildingId) {
        return toAjax(houseService.setIsolatedHouses(houseList, orgId, buildingId));
    }

    // ========== B-01 D-05 新增 4 个高价值端点 ==========

    /**
     * 变更房屋业主
     * 旧端点: POST /property/prHouse/updateUserByHouse
     * 新端点: POST /thermal/property/house/changeOwner
     */
    @SaCheckPermission("thermal:property:house:edit")
    @SaCheckLogin
    @Log(title = "房屋管理-变更业主", businessType = BusinessType.UPDATE)
    @PostMapping("/changeOwner")
    public R<Void> changeOwner(@Validated @RequestBody PrHouseChangeOwnerBo bo) {
        return toAjax(houseService.changeOwner(bo));
    }

    /**
     * 查询房屋变更记录
     * 旧端点: GET /property/prHouse/getHouseChangeList
     * 新端点: GET /thermal/property/house/changeList
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/changeList")
    public R<List<PrUserHouseVo>> changeList(
            @RequestParam Long houseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return R.ok(houseService.changeList(houseId, startDate, endDate));
    }

    /**
     * 按单元+类型+阀门可控性筛选房屋
     * 旧端点: GET /property/prHouse/getHouseListByUnitAndTypeAndCon
     * 新端点: GET /thermal/property/house/byTypeAndValve
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/byTypeAndValve")
    public R<Map<String, Object>> listByTypeAndValve(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String stationId,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) String treeTypeValve) {
        return R.ok(houseService.selectByTypeAndValve(orgId, buildingId, unitCode, stationId, types, treeTypeValve));
    }

    /**
     * 入住率统计
     * 旧端点: GET /property/prHouse/getOccupancy
     * 新端点: GET /thermal/property/house/occupancy
     */
    @SaCheckPermission("thermal:property:house:list")
    @SaCheckLogin
    @GetMapping("/occupancy")
    public R<BigDecimal> occupancy(@RequestParam(required = false) String orgId) {
        return R.ok(houseService.calcOccupancy(orgId));
    }
}
