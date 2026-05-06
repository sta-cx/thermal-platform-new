package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.bo.PrHeatValveArchiveBo;
import org.sdkj.thermal.domain.dto.*;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.domain.vo.PrHouseVo;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.thermal.service.IPrHouseService;
import org.sdkj.thermal.utils.YunGuUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 户间阀门配表管理
 * 迁移自旧系统 PrHeatValveArchiveController
 * 旧端点: /ht/valveArchive/* -> 新端点: /thermal/ht/valve-archive/*
 */
@Validated
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/thermal/ht/valve-archive")
public class PrHeatValveArchiveController extends BaseController {

    private final IPrHeatValveArchiveService valveArchiveService;
    private final IHtTasksPerformService tasksPerformService;
    private final IPrHouseService houseService;
    private final PrHeatHotArchiveMapper hotArchiveMapper;

    @Value("${thermal.third-party.yungu.app-code:sdkjApp}")
    private String yunguAppCode;

    @Value("${thermal.third-party.yungu.app-secret:[REMOVED]}")
    private String yunguAppSecret;

    @Value("${thermal.third-party.xinao.expected-token:[REMOVED]}")
    private String xinaoExpectedToken;

    /**
     * 分页查询户间阀门配表列表
     * 旧端点: GET /ht/valveArchive/pageList
     * 新端点: GET /thermal/ht/valve-archive/list
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatValveArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            PageQuery pageQuery) {
        return valveArchiveService.selectPageList(companyId, orgId, buildingId, unit, search, parentId, pageQuery);
    }

    /**
     * 查询户间阀门配表详情
     * 旧端点: GET /ht/valveArchive/getDataById/{id}
     * 新端点: GET /thermal/ht/valve-archive/{id}
     */
    @SaCheckPermission("thermal:ht:valve-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatValveArchiveVo> getById(@PathVariable String id) {
        return R.ok(valveArchiveService.selectById(id));
    }

    /**
     * 新增户间阀门配表
     * 旧端点: POST /ht/valveArchive/insertData
     * 新端点: POST /thermal/ht/valve-archive
     */
    @SaCheckPermission("thermal:ht:valve-archive:add")
    @SaCheckLogin
    @Log(title = "户间阀门配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatValveArchiveBo bo) {
        long count = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatValveArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatValveArchive entity = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        return toAjax(valveArchiveService.save(entity));
    }

    /**
     * 修改户间阀门配表
     * 旧端点: POST /ht/valveArchive/updateData
     * 新端点: PUT /thermal/ht/valve-archive
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatValveArchiveBo bo) {
        long count = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatValveArchive::getIsChanged, 0)
            .ne(PrHeatValveArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatValveArchive entity = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        return toAjax(valveArchiveService.updateById(entity));
    }

    /**
     * 删除户间阀门配表
     * 旧端点: POST /ht/valveArchive/deleteData/{id}
     * 新端点: DELETE /thermal/ht/valve-archive/{id}
     */
    @SaCheckPermission("thermal:ht:valve-archive:remove")
    @SaCheckLogin
    @Log(title = "户间阀门配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(valveArchiveService.removeById(id));
    }

    /**
     * 开阀
     * 旧端点: POST /ht/valveArchive/openValve
     * 新端点: POST /thermal/ht/valve-archive/openValve
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-开阀", businessType = BusinessType.UPDATE)
    @PostMapping("/openValve")
    public R<Void> openValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatValveArchive> archives = valveArchiveService.listByIds(ids);
        if (archives.isEmpty()) {
            return R.fail("未找到对应的阀门配表记录");
        }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), a.getDtuNum(), a.getChanNum()))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 100));
    }

    /**
     * 关阀
     * 旧端点: POST /ht/valveArchive/closeValve
     * 新端点: POST /thermal/ht/valve-archive/closeValve
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-关阀", businessType = BusinessType.UPDATE)
    @PostMapping("/closeValve")
    public R<Void> closeValve(@RequestParam String orgId, @RequestParam String companyId, @RequestBody List<String> ids) {
        List<PrHeatValveArchive> archives = valveArchiveService.listByIds(ids);
        if (archives.isEmpty()) {
            return R.fail("未找到对应的阀门配表记录");
        }
        var infos = archives.stream()
            .map(a -> new ValveArchiveInfo(a.getId(), a.getMeterArcCode(), a.getMeterNum(), a.getDeviceId(), a.getConcentratorCode(), a.getImeiNum(), a.getDtuNum(), a.getChanNum()))
            .toList();
        return toAjax(tasksPerformService.batchCreateValveControlTasks(infos, orgId, companyId, 0));
    }

    /**
     * 换表
     * 旧端点: POST /ht/valveArchive/exchangeMeter
     * 新端点: POST /thermal/ht/valve-archive/exchangeMeter/{oldId}
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-换表", businessType = BusinessType.UPDATE)
    @PostMapping("/exchangeMeter/{oldId}")
    public R<Void> exchangeMeter(@Validated @RequestBody PrHeatValveArchiveBo bo, @PathVariable String oldId) {
        // 1. 查出旧记录，标记为已变更
        PrHeatValveArchive oldArchive = valveArchiveService.getById(oldId);
        if (oldArchive == null) {
            return R.fail("未找到原阀门配表记录");
        }
        oldArchive.setIsChanged(1);
        valveArchiveService.updateById(oldArchive);
        // 2. 创建新记录
        PrHeatValveArchive newArchive = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        newArchive.setIsChanged(0);
        newArchive.setIsOpen(0);
        return toAjax(valveArchiveService.save(newArchive));
    }

    /**
     * 根据房屋ID新增阀门配表（校验房屋唯一性）
     * 旧端点: POST /ht/valveArchive/insertByHouseId
     * 新端点: POST /thermal/ht/valve-archive/insertByHouseId
     */
    @SaCheckPermission("thermal:ht:valve-archive:add")
    @SaCheckLogin
    @Log(title = "户间阀门配表-按房屋新增", businessType = BusinessType.INSERT)
    @PostMapping("/insertByHouseId")
    public R<Void> insertByHouseId(@Validated @RequestBody PrHeatValveArchiveBo bo) {
        // 校验 houseId 唯一性
        long houseCount = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getHouseId, bo.getHouseId())
            .eq(PrHeatValveArchive::getIsChanged, 0));
        if (houseCount > 0) {
            return R.fail("该房屋已绑定阀门配表");
        }
        // 校验设备编号唯一性
        long count = valveArchiveService.count(new LambdaQueryWrapper<PrHeatValveArchive>()
            .eq(PrHeatValveArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatValveArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatValveArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatValveArchive entity = MapstructUtils.convert(bo, PrHeatValveArchive.class);
        return toAjax(valveArchiveService.save(entity));
    }

    // ========== 批量操作端点 ==========

    /**
     * 批量设置阀门开关/查询/制动状态
     * 旧端点: POST /ht/valveArchive/setValveOCStatus
     * 新端点: POST /thermal/ht/valve-archive/batch-status
     *
     * @param houseList   房屋列表（含 meterNum + meterArcCode）
     * @param valveStatus 阀门状态: "1"→开(100), "2"→关(0), "4"→查询, "5"→制动, "51"→特殊制动
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-批量开关", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-status")
    public R<Void> batchSetValveStatus(@RequestBody List<PrHouseByPayVo> houseList,
                                        @RequestParam String valveStatus) {
        if (houseList == null || houseList.isEmpty()) {
            return R.fail("参数为空");
        }
        return toAjax(valveArchiveService.batchSetValveStatus(houseList, valveStatus));
    }

    /**
     * 批量设置阀门开度
     * 旧端点: POST /ht/valveArchive/setValveOCOpening
     * 新端点: POST /thermal/ht/valve-archive/batch-opening
     *
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param valveStatus 开度值 (0-100)
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-批量开度", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-opening")
    public R<Void> batchSetValveOpening(@RequestBody List<PrHouseByPayVo> houseList,
                                         @RequestParam String valveStatus) {
        if (houseList == null || houseList.isEmpty()) {
            return R.fail("参数为空");
        }
        return toAjax(valveArchiveService.batchSetValveOpening(houseList, valveStatus));
    }

    /**
     * 批量设置上报周期
     * 旧端点: POST /ht/valveArchive/setValveCycle
     * 新端点: POST /thermal/ht/valve-archive/batch-cycle
     *
     * @param houseList 房屋列表（含 meterNum + meterArcCode）
     * @param interval  上报间隔
     * @param unit      间隔单位
     * @param valid     有效时间
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-批量周期", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-cycle")
    public R<Void> batchSetValveCycle(@RequestBody List<PrHouseByPayVo> houseList,
                                       @RequestParam String interval,
                                       @RequestParam String unit,
                                       @RequestParam String valid) {
        if (houseList == null || houseList.isEmpty()) {
            return R.fail("参数为空");
        }
        return toAjax(valveArchiveService.batchSetValveCycle(houseList, interval, unit, valid));
    }

    /**
     * 导出阀门配表 Excel
     * 旧端点: GET /ht/valveArchive/exportAll
     * 新端点: GET /thermal/ht/valve-archive/export
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @Log(title = "户间阀门配表-导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void exportAll(HttpServletResponse response,
                           @RequestParam(required = false) String companyId,
                           @RequestParam(required = false) String orgId) throws IOException {
        List<PrHeatValveArchiveVo> list = valveArchiveService.listAll(companyId, orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("阀门配表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatValveArchiveVo.class).sheet("阀门配表").doWrite(list);
    }

    /**
     * 导入阀门配表 Excel
     * 旧端点: POST /ht/valveArchive/importValveArchive
     * 新端点: POST /thermal/ht/valve-archive/import
     */
    @SaCheckPermission("thermal:ht:valve-archive:add")
    @SaCheckLogin
    @Log(title = "户间阀门配表-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public R<Void> importValveArchive(@RequestParam("file") MultipartFile file) throws IOException {
        return valveArchiveService.importValveArchive(file);
    }

    // ========== 卡表管理端点 ==========

    /**
     * 卡表分页查询
     * 旧端点: GET /ht/valveArchive/heatCardPageList
     * 新端点: GET /thermal/ht/valve-archive/heat-card
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/heat-card")
    public TableDataInfo<PrHeatValveArchiveVo> pageListHeatCard(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String meterArcCode,
            @RequestParam(required = false) String payStatus,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String writeCardStatus,
            PageQuery pageQuery) {
        return valveArchiveService.pageListHeatCard(companyId, orgId, buildingId, unit, meterArcCode,
            payStatus, search, parentId, writeCardStatus, pageQuery);
    }

    /**
     * 更新写卡状态
     * 旧端点: PUT /ht/valveArchive/updateCardStatus/{id}
     * 新端点: PUT /thermal/ht/valve-archive/{id}/card-status
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-更新写卡状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/card-status")
    public R<Void> updateValveStatus(@PathVariable String id) {
        return toAjax(valveArchiveService.updateValveStatus(id));
    }

    // ========== 设备查询端点 ==========

    /**
     * 按表号查询设备
     * 旧端点: GET /ht/valveArchive/queryMeterByMeterNum
     * 新端点: GET /thermal/ht/valve-archive/query-by-meter
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/query-by-meter")
    public R<List<PrHeatValveArchiveVo>> queryMeterByMeterNum(
            @RequestParam String meterNum,
            @RequestParam String orgId,
            @RequestParam String code) {
        return R.ok(valveArchiveService.queryMeterByMeterNum(meterNum, orgId, code));
    }

    /**
     * 按阀门号查询
     * 旧端点: GET /ht/valveArchive/queryValveByMeterNum
     * 新端点: GET /thermal/ht/valve-archive/query-by-valve
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/query-by-valve")
    public R<List<PrHeatValveArchiveVo>> queryValveByMeterNum(@RequestParam String meterNum) {
        return R.ok(valveArchiveService.queryValveByMeterNum(meterNum));
    }

    /**
     * 按阀门号查房屋
     * 旧端点: GET /ht/valveArchive/queryHouseByMeterNum
     * 新端点: GET /thermal/ht/valve-archive/query-house
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/query-house")
    public R<PrHouseVo> queryHouseByMeterNum(@RequestParam String meterNum) {
        List<PrHeatValveArchiveVo> valves = valveArchiveService.queryValveByMeterNum(meterNum);
        if (valves.isEmpty()) {
            return R.fail("未找到对应的阀门档案");
        }
        Long houseId = valves.get(0).getHouseId();
        if (houseId == null) {
            return R.fail("该阀门未关联房屋");
        }
        return R.ok(houseService.selectById(houseId));
    }

    /**
     * 按房屋ID查卡阀
     * 旧端点: GET /ht/valveArchive/queryCardMeterByHouseId
     * 新端点: GET /thermal/ht/valve-archive/query-card-by-house
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/query-card-by-house")
    public R<List<PrHeatValveArchiveVo>> queryCardMeterByHouseId(@RequestParam String houseId) {
        return R.ok(valveArchiveService.queryCardMeterByHouseId(houseId));
    }

    /**
     * 按房号查卡阀
     * 旧端点: GET /ht/valveArchive/queryCardMeterByRoomNum
     * 新端点: GET /thermal/ht/valve-archive/query-card-by-room
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/query-card-by-room")
    public R<List<PrHeatValveArchiveVo>> queryCardMeterByRoomNum(
            @RequestParam String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam String search) {
        return R.ok(valveArchiveService.queryCardMeterByRoomNum(orgId, buildingId, unitCode, search));
    }

    /**
     * 按房屋ID获取阀门数据
     * 旧端点: GET /ht/valveArchive/getValveDataByHouseId/{houseId}
     * 新端点: GET /thermal/ht/valve-archive/valve-data/{houseId}
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @GetMapping("/valve-data/{houseId}")
    public R<PrHeatValveArchiveVo> getValveDataByHouseId(@PathVariable String houseId) {
        return R.ok(valveArchiveService.getValveDataByHouseId(houseId));
    }

    // ========== 信息同步端点 ==========

    /**
     * 同步户阀信息到采集平台
     * 旧端点: POST /ht/valveArchive/valveInformationSynchronization
     * 新端点: POST /thermal/ht/valve-archive/sync
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-同步采集平台", businessType = BusinessType.UPDATE)
    @PostMapping("/sync")
    public R<Boolean> valveInformationSynchronization(
            @RequestParam String orgId,
            @RequestParam String companyId) {
        boolean result = valveArchiveService.valveInformationSynchronization(orgId, companyId);
        return result ? R.ok(true) : R.fail("同步失败，请检查采集平台配置");
    }

    /**
     * 下载同步信息Excel
     * 旧端点: GET /ht/valveArchive/downloadInfoSync
     * 新端点: GET /thermal/ht/valve-archive/sync-download
     */
    @SaCheckPermission("thermal:ht:valve-archive:list")
    @SaCheckLogin
    @Log(title = "户间阀门配表-同步信息下载", businessType = BusinessType.EXPORT)
    @GetMapping("/sync-download")
    public void downloadInfoSync(HttpServletResponse response,
                                  @RequestParam String companyId,
                                  @RequestParam String orgId) throws IOException {
        List<PrHeatValveArchiveVo> list = valveArchiveService.listSyncData(companyId, orgId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("户阀同步信息", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), PrHeatValveArchiveVo.class).sheet("同步信息").doWrite(list);
    }

    // ========== 蓝牙控制日志端点 ==========

    /**
     * 蓝牙阀门控制日志
     * 旧端点: POST /ht/valveArchive/insertValveControlLogByBluetooth
     * 新端点: POST /thermal/ht/valve-archive/bluetooth-log
     */
    @SaCheckPermission("thermal:ht:valve-archive:edit")
    @SaCheckLogin
    @Log(title = "户间阀门配表-蓝牙控制", businessType = BusinessType.UPDATE)
    @PostMapping("/bluetooth-log")
    public R<Void> insertValveControlLogByBluetooth(
            @RequestParam String meterNum,
            @RequestParam String type,
            @RequestParam String opening) {
        valveArchiveService.insertValveControlLogByBluetooth(meterNum, type, opening);
        return R.ok();
    }

    // ========== 一键新增端点 ==========

    /**
     * 新增用户和阀门信息
     * 旧端点: POST /ht/valveArchive/insertUserAndValveInfo
     * 新端点: POST /thermal/ht/valve-archive/user-valve
     */
    @SaCheckPermission("thermal:ht:valve-archive:add")
    @SaCheckLogin
    @Log(title = "户间阀门配表-一键新增", businessType = BusinessType.INSERT)
    @PostMapping("/user-valve")
    public R<String> insertUserAndValveInfo(
            @RequestParam String companyId,
            @RequestParam String orgId,
            @RequestParam(required = false) String orgName,
            @RequestParam Long buildingId,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) String unitCode,
            @RequestParam String roomNum,
            @RequestParam(required = false) String floor,
            @RequestParam(required = false) String otherCode,
            @RequestParam(required = false) String payStatus,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String gfloorArea,
            @RequestParam(required = false) String nfloorArea,
            @RequestParam(required = false) String heatingArea,
            @RequestParam String meterNum) {
        String houseId = valveArchiveService.insertUserAndValveInfo(
            companyId, orgId, orgName, buildingId, buildingName, unitCode,
            roomNum, floor, otherCode, payStatus, userName, phone,
            gfloorArea, nfloorArea, heatingArea, meterNum);
        return R.ok(houseId, "创建成功");
    }

    // ========== 第三方 API（云谷/新奥） — 不需要 @SaCheckLogin ==========

    /**
     * 云谷阀门控制 API
     * 旧端点: POST /ht/valveArchive/api/enopt/valve/control
     * 新端点: POST /thermal/ht/valve-archive/api/enopt/valve/control
     *
     * Header: AppCode=sdkjApp, AppToken, Timestamp(13位毫秒)
     * Body: { ManuId, ControlMode(11), Value(0-100) }
     */
    @PostMapping("/api/enopt/valve/control")
    public YunGuApiResponse<Boolean> yunguControl(
            @RequestHeader("AppCode") String appCode,
            @RequestHeader("Timestamp") String timestamp,
            @RequestHeader("AppToken") String appToken,
            @RequestBody YunGuControlRequest request) {

        // --- Header 校验 ---
        String headerError = validateYunGuHeaders(appCode, timestamp, appToken);
        if (headerError != null) {
            log.info("云谷控制-{}", headerError);
            return YunGuApiResponse.fail(headerError);
        }

        // --- Body 校验 ---
        if (StringUtils.isBlank(request.getManuId())) {
            log.info("云谷控制-设备编码为空");
            return YunGuApiResponse.fail("设备编码为空");
        }
        if (request.getControlMode() == null || request.getControlMode() != 11) {
            log.info("云谷控制-ControlMode错误，设备只支持手动模式");
            return YunGuApiResponse.fail("设备只支持手动模式");
        }
        if (StringUtils.isBlank(request.getValue())) {
            log.info("云谷控制-Value值错误1");
            return YunGuApiResponse.fail("Value值错误1");
        }
        if (!request.getValue().trim().matches("^\\d+$")) {
            log.info("云谷控制-Value值错误2(非数字)");
            return YunGuApiResponse.fail("Value值错误2");
        }
        int num = Integer.parseInt(request.getValue().trim());
        if (num < 0 || num > 100) {
            log.info("云谷控制-Value值错误3(超出范围)");
            return YunGuApiResponse.fail("Value值错误3");
        }

        // --- 业务处理 ---
        try {
            boolean success = valveArchiveService.yunguValveControl(request.getManuId().trim(), num);
            if (!success) {
                log.info("云谷控制-设备编码错误(未找到记录)");
                return YunGuApiResponse.fail("设备编码错误");
            }
        } catch (Exception e) {
            log.info("云谷控制-指令下发失败: {}", e.getMessage());
            return YunGuApiResponse.fail("指令下发失败");
        }

        log.info("云谷控制-指令下发成功, manuId={}, value={}", request.getManuId(), num);
        return YunGuApiResponse.success(true);
    }

    /**
     * 云谷批量数据同步 API
     * 旧端点: POST /ht/valveArchive/api/enopt/rtdata/batchsync
     * 新端点: POST /thermal/ht/valve-archive/api/enopt/rtdata/batchsync
     *
     * Header: AppCode=sdkjApp, AppToken, Timestamp(13位毫秒)
     * Body: { ManuIds: "num1,num2,..." } (逗号分隔，最多100个)
     */
    @PostMapping("/api/enopt/rtdata/batchsync")
    public YunGuApiResponse<List<YunGuDataResponse>> yunguBatchSync(
            @RequestHeader("AppCode") String appCode,
            @RequestHeader("Timestamp") String timestamp,
            @RequestHeader("AppToken") String appToken,
            @RequestBody YunGuBatchSyncRequest request) {

        // --- Header 校验（与云谷控制共用逻辑） ---
        String headerError = validateYunGuHeaders(appCode, timestamp, appToken);
        if (headerError != null) {
            log.info("云谷同步-{}", headerError);
            return YunGuApiResponse.fail(headerError);
        }

        // --- Body 校验 ---
        if (request == null || StringUtils.isBlank(request.getManuIds())) {
            log.info("云谷同步-设备编码为空");
            return YunGuApiResponse.fail("设备编码为空");
        }

        List<String> manuIdList = Arrays.stream(request.getManuIds().split(","))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());

        if (manuIdList.size() > 100) {
            log.info("云谷同步-参数ManuIds最多支持100个设备编码");
            return YunGuApiResponse.fail("参数ManuIds最多支持100个设备编码");
        }

        // --- 业务处理 ---
        List<YunGuDataResponse> dataList = valveArchiveService.yunguBatchSync(manuIdList);
        if (dataList.isEmpty()) {
            log.info("云谷同步-未查询到当前设备数据");
            return YunGuApiResponse.fail("未查询到当前设备数据");
        }

        log.info("云谷同步-查询完成, count={}", dataList.size());
        return YunGuApiResponse.success(dataList);
    }

    /**
     * 新奥阀门数据查询 API
     * 旧端点: GET /ht/valveArchive/api/xaltrl/getLTValveData
     * 新端点: GET /thermal/ht/valve-archive/api/xaltrl/getLTValveData
     *
     * Header: AppCode=ltrlApp, AppToken, Timestamp(10位秒级)
     * 参数: meterNums (逗号分隔，最多50个)
     */
    @GetMapping("/api/xaltrl/getLTValveData")
    public R<List<LtValveDataResponse>> getLTValveData(
            @RequestHeader("AppCode") String appCode,
            @RequestHeader("Timestamp") String timestamp,
            @RequestHeader("AppToken") String appToken,
            @RequestParam String meterNums) {

        // --- Header 校验 ---
        if (StringUtils.isBlank(appCode) || !appCode.equals(yunguAppCode)) {
            log.info("新奥-请求参数错误1(AppCode)");
            return R.fail("请求参数错误1");
        }
        if (StringUtils.isBlank(appToken)) {
            log.info("新奥-请求参数错误2(AppToken)");
            return R.fail("请求参数错误2");
        }
        if (StringUtils.isBlank(timestamp) || !timestamp.matches("^\\d{10}$")) {
            log.info("新奥-请求参数错误3(Timestamp)");
            return R.fail("请求参数错误3");
        }

        long targetTime = Long.parseLong(timestamp.trim());
        long currentTime = System.currentTimeMillis() / 1000;
        long timeDiff = Math.abs(targetTime - currentTime);
        if (timeDiff >= 3 * 60) {
            log.info("新奥-请求参数错误4(Timestamp过期)");
            return R.fail("请求参数错误4");
        }

        String checkResult = YunGuUtils.checkToken(appToken, targetTime);
        if (!xinaoExpectedToken.equals(checkResult)) {
            log.info("新奥-请求参数错误5(token不匹配)");
            return R.fail("请求参数错误5");
        }

        // --- 参数校验 ---
        if (StringUtils.isBlank(meterNums)) {
            log.info("新奥-输入参数错误1(meterNums=null)");
            return R.fail("输入参数错误1");
        }

        List<String> meterList = Arrays.stream(meterNums.split(","))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());

        if (meterList.isEmpty()) {
            log.info("新奥-输入参数错误2(meterList为空)");
            return R.fail("输入参数错误2");
        }
        if (meterList.size() > 50) {
            log.info("新奥-输入查询阀门数量大于50个");
            return R.fail("输入查询阀门数量不能大于50个");
        }

        // --- 业务处理 ---
        List<LtValveDataResponse> valveList = valveArchiveService.getLTValveData(meterList);
        if (valveList == null || valveList.isEmpty()) {
            return R.fail("未查询到数据");
        }

        log.info("新奥-查询完成, count={}", valveList.size());
        return R.ok(valveList);
    }

    // ========== 云谷 Header 校验共用方法 ==========

    private String validateYunGuHeaders(String appCode, String timestamp, String appToken) {
        if (StringUtils.isBlank(appCode) || !appCode.equals(yunguAppCode)) {
            return "请求参数错误1";
        }
        if (StringUtils.isBlank(appToken)) {
            return "请求参数错误2";
        }
        if (StringUtils.isBlank(timestamp) || !timestamp.matches("^\\d{13}$")) {
            return "请求参数错误3";
        }
        long targetTime = Long.parseLong(timestamp.trim());
        long timeDiff = Math.abs(targetTime - System.currentTimeMillis());
        if (timeDiff >= 5 * 60 * 1000) {
            return "请求参数错误4";
        }
        String expectedToken = YunGuUtils.generateToken(yunguAppCode, yunguAppSecret, timestamp).toUpperCase();
        if (!expectedToken.equals(appToken.toUpperCase())) {
            return "请求参数错误5";
        }
        return null;
    }

    // ========== NB/MBus 阀门数据接收端点 ==========

    /**
     * 接收电信 NB 阀门数据
     * 旧端点: POST /ht/valveArchive/insertDataNbValve
     * 新端点: POST /thermal/ht/valve-archive/nb-data
     *
     * 接收格式: JSON payload with Base64 encoded data
     * 接收后反写温度到 pr_house 表
     */
    @PostMapping("/nb-data")
    public R<Integer> insertDataNbValve(@RequestBody String msg) {
        try {
            cn.hutool.json.JSONObject jsonObject = cn.hutool.json.JSONUtil.parseObj(msg);
            log.info("NB阀门接收电信平台数据:{}", jsonObject);

            // 解析基础字段
            long timestamp = Long.decode(jsonObject.getStr("timestamp"));
            String date = cn.hutool.core.date.DateUtil.format(new Date(timestamp), "yyyy-MM-dd HH:mm:ss");
            String imei = jsonObject.getStr("IMEI");
            String deviceId = jsonObject.getStr("deviceId");

            // 解析 payload
            String payload = jsonObject.getJSONObject("payload").getStr("APPdata");
            String text = new String(java.util.Base64.getDecoder().decode(payload.trim()),
                java.nio.charset.StandardCharsets.UTF_8);

            if (text.startsWith("0")) {
                text = text.substring(1);
                // 阀门编号
                String meterNum = text.substring(4, 20);
                // 阀门状态
                String valveStatus = text.substring(28, 30);
                // 设定开度
                int settingStatus = Integer.parseInt(text.substring(30, 32), 16);
                // 实际开度
                int actualOpen = Integer.parseInt(text.substring(32, 34), 16);
                // 进水温度
                BigDecimal inTemperature = new BigDecimal(Integer.parseInt(text.substring(34, 38), 16))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                // 回水温度
                BigDecimal outTemperature = new BigDecimal(Integer.parseInt(text.substring(38, 42), 16))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                // 电压
                BigDecimal voltage = new BigDecimal(Integer.parseInt(text.substring(42, 46), 16))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                if ("00".equals(valveStatus)) {
                    valveStatus = "02";
                }

                // 查找阀门档案并反写温度到房屋
                LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
                lqw.eq(PrHeatValveArchive::getMeterNum, meterNum);
                lqw.eq(PrHeatValveArchive::getIsChanged, 0);
                lqw.last("LIMIT 1");
                PrHeatValveArchive archive = valveArchiveService.getOne(lqw);

                if (archive != null && archive.getHouseId() != null) {
                    valveArchiveService.updateHouseTemperature(
                        archive.getHouseId(), inTemperature, outTemperature, actualOpen);
                }

                log.info("NB阀门数据接收完成, meterNum={}, inTemp={}, outTemp={}",
                    meterNum, inTemperature, outTemperature);
            }
            return R.ok(200);
        } catch (Exception e) {
            log.error("NB阀门数据接收失败", e);
            return R.fail("数据解析失败");
        }
    }

    /**
     * 接收 MBus 阀门数据（大连世达）
     * 旧端点: POST /ht/valveArchive/insertDataMBusValve
     * 新端点: POST /thermal/ht/valve-archive/mbus-data
     *
     * 接收格式: JSON array of valve information
     * 接收后反写温度到 pr_house 表
     */
    @PostMapping("/mbus-data")
    public R<Integer> insertDataMBusValve(@RequestBody String args) {
        try {
            cn.hutool.json.JSONObject jsonObject = cn.hutool.json.JSONUtil.parseObj(args);
            log.info("接收MBus阀门数据:{}", jsonObject);

            if (jsonObject.containsKey("data")) {
                java.util.List<cn.hutool.json.JSONObject> objects = jsonObject.getJSONArray("data")
                    .stream()
                    .map(obj -> (cn.hutool.json.JSONObject) obj)
                    .toList();

                for (cn.hutool.json.JSONObject obj : objects) {
                    String type = obj.getStr("type");
                    String meterArcCode;
                    BigDecimal supplyTemp;
                    BigDecimal returnTemp;
                    String valveNo;
                    String valveOpening;
                    String valveStatus;

                    if ("2".equals(type)) {
                        // 热表
                        meterArcCode = "04030301";
                        supplyTemp = obj.getBigDecimal("rbSupplyTemp");
                        returnTemp = obj.getBigDecimal("rbReturnTemp");
                        valveNo = obj.getStr("valveNo");

                        // 查找热表档案并反写温度到房屋
                        PrHeatHotArchive hotArchive = hotArchiveMapper.selectOne(
                            new LambdaQueryWrapper<PrHeatHotArchive>()
                                .eq(PrHeatHotArchive::getMeterNum, valveNo)
                                .eq(PrHeatHotArchive::getIsChanged, 0)
                                .last("LIMIT 1")
                        );

                        if (hotArchive != null && hotArchive.getHouseId() != null) {
                            valveArchiveService.updateHouseTemperature(
                                hotArchive.getHouseId(), supplyTemp, returnTemp, null);
                        }

                    } else if ("1".equals(type)) {
                        // 阀门
                        meterArcCode = "04310401";
                        supplyTemp = obj.getBigDecimal("supplyTemp");
                        returnTemp = obj.getBigDecimal("returnTemp");
                        valveNo = obj.getStr("valveNo");
                        valveStatus = obj.getStr("valveStatus");
                        valveOpening = obj.getStr("valveOpening");

                        // 转换阀门状态
                        if ("0".equals(valveStatus)) {
                            valveStatus = "02";
                        } else if ("1".equals(valveStatus)) {
                            valveStatus = "01";
                        }

                        // 开度处理
                        if (StringUtils.isNotBlank(valveOpening)) {
                            try {
                                int opening = Integer.parseInt(valveOpening);
                                if (opening > 100) {
                                    valveOpening = "100";
                                }
                            } catch (NumberFormatException ignored) {
                                log.debug("数值解析失败, 使用默认值", ignored);
                                valveOpening = "0";
                            }
                        }

                        // 查找阀门档案并反写温度到房屋
                        LambdaQueryWrapper<PrHeatValveArchive> lqw = new LambdaQueryWrapper<>();
                        lqw.eq(PrHeatValveArchive::getMeterNum, valveNo);
                        lqw.eq(PrHeatValveArchive::getMeterArcCode, meterArcCode);
                        lqw.eq(PrHeatValveArchive::getIsChanged, 0);
                        lqw.last("LIMIT 1");
                        PrHeatValveArchive archive = valveArchiveService.getOne(lqw);

                        if (archive != null && archive.getHouseId() != null) {
                            Integer actualOpen = StringUtils.isNotBlank(valveOpening)
                                ? Integer.parseInt(valveOpening) : null;
                            valveArchiveService.updateHouseTemperature(
                                archive.getHouseId(), supplyTemp, returnTemp, actualOpen);
                        }
                    }
                }

                log.info("MBus阀门数据接收完成, count={}", objects.size());
            }
            return R.ok(200);
        } catch (Exception e) {
            log.error("MBus阀门数据接收失败", e);
            return R.fail("数据解析失败");
        }
    }
}
