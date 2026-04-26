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
import org.sdkj.thermal.domain.bo.PrHeatValveArchiveBo;
import org.sdkj.thermal.domain.dto.*;
import org.sdkj.thermal.domain.vo.PrHeatValveArchiveVo;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrHeatValveArchiveService;
import org.sdkj.thermal.utils.YunGuUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        if (!"sdkjApp".equals(appCode) || StringUtils.isBlank(appCode)) {
            log.info("云谷控制-请求参数错误1(AppCode)");
            return YunGuApiResponse.fail("请求参数错误1");
        }
        if (StringUtils.isBlank(appToken)) {
            log.info("云谷控制-请求参数错误2(AppToken)");
            return YunGuApiResponse.fail("请求参数错误2");
        }
        if (StringUtils.isBlank(timestamp) || !timestamp.matches("^\\d{13}$")) {
            log.info("云谷控制-请求参数错误3(Timestamp)");
            return YunGuApiResponse.fail("请求参数错误3");
        }

        long targetTime = Long.parseLong(timestamp.trim());
        long timeDiff = Math.abs(targetTime - System.currentTimeMillis());
        if (timeDiff >= 5 * 60 * 1000) {
            log.info("云谷控制-请求参数错误4(Timestamp过期)");
            return YunGuApiResponse.fail("请求参数错误4");
        }

        String expectedToken = YunGuUtils.generateToken("sdkjApp", "[REMOVED]", timestamp).toUpperCase();
        if (!expectedToken.equals(appToken.toUpperCase())) {
            log.info("云谷控制-请求参数错误5(token不匹配)");
            return YunGuApiResponse.fail("请求参数错误5");
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
        if (!"sdkjApp".equals(appCode) || StringUtils.isBlank(appCode)) {
            log.info("云谷同步-请求参数错误1(AppCode)");
            return YunGuApiResponse.fail("请求参数错误1");
        }
        if (StringUtils.isBlank(appToken)) {
            log.info("云谷同步-请求参数错误2(AppToken)");
            return YunGuApiResponse.fail("请求参数错误2");
        }
        if (StringUtils.isBlank(timestamp) || !timestamp.matches("^\\d{13}$")) {
            log.info("云谷同步-请求参数错误3(Timestamp)");
            return YunGuApiResponse.fail("请求参数错误3");
        }

        long targetTime = Long.parseLong(timestamp.trim());
        long timeDiff = Math.abs(targetTime - System.currentTimeMillis());
        if (timeDiff >= 5 * 60 * 1000) {
            log.info("云谷同步-请求参数错误4(Timestamp过期)");
            return YunGuApiResponse.fail("请求参数错误4");
        }

        String expectedToken = YunGuUtils.generateToken("sdkjApp", "[REMOVED]", timestamp).toUpperCase();
        if (!expectedToken.equals(appToken.toUpperCase())) {
            log.info("云谷同步-请求参数错误5(token不匹配)");
            return YunGuApiResponse.fail("请求参数错误5");
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
        if (!"ltrlApp".equals(appCode) || StringUtils.isBlank(appCode)) {
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
        if (!"[REMOVED]".equals(checkResult)) {
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
}
