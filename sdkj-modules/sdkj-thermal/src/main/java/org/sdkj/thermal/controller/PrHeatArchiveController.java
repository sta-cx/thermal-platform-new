package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.log.annotation.Log;
import org.sdkj.common.log.enums.BusinessType;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.bo.PrHeatArchiveBo;
import org.sdkj.thermal.domain.dto.PrHeatVo;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;
import org.sdkj.thermal.service.IPrHeatArchiveService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 房屋热表配表管理
 * 迁移自旧系统 PrHeatArchiveController
 * 旧端点: /ht/heatArchive/* -> 新端点: /thermal/ht/heat-archive/*
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/heat-archive")
public class PrHeatArchiveController extends BaseController {

    private final IPrHeatArchiveService heatArchiveService;

    /**
     * 分页查询房屋热表配表列表
     * 旧端点: GET /ht/heatArchive/pageList
     * 新端点: GET /thermal/ht/heat-archive/list
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @GetMapping("/list")
    public TableDataInfo<PrHeatArchiveVo> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String archiveId,
            PageQuery pageQuery) {
        return heatArchiveService.selectPageList(companyId, orgId, buildingId, unitCode, search, archiveId, pageQuery);
    }

    /**
     * 查询房屋热表配表详情
     * 旧端点: GET /ht/heatArchive/getDataById/{id}
     * 新端点: GET /thermal/ht/heat-archive/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:query")
    @SaCheckLogin
    @GetMapping("/{id}")
    public R<PrHeatArchiveVo> getById(@PathVariable String id) {
        return R.ok(heatArchiveService.selectById(id));
    }

    /**
     * 新增房屋热表配表
     * 旧端点: POST /ht/heatArchive/insertData
     * 新端点: POST /thermal/ht/heat-archive
     */
    @SaCheckPermission("thermal:ht:heat-archive:add")
    @SaCheckLogin
    @Log(title = "房屋热表配表", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody PrHeatArchiveBo bo) {
        long count = heatArchiveService.count(new LambdaQueryWrapper<PrHeatArchive>()
            .eq(PrHeatArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatArchive::getIsChanged, 0));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatArchive entity = MapstructUtils.convert(bo, PrHeatArchive.class);
        return toAjax(heatArchiveService.save(entity));
    }

    /**
     * 修改房屋热表配表
     * 旧端点: POST /ht/heatArchive/updateData
     * 新端点: PUT /thermal/ht/heat-archive
     */
    @SaCheckPermission("thermal:ht:heat-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热表配表", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody PrHeatArchiveBo bo) {
        long count = heatArchiveService.count(new LambdaQueryWrapper<PrHeatArchive>()
            .eq(PrHeatArchive::getMeterNum, bo.getMeterNum())
            .eq(PrHeatArchive::getMeterArcCode, bo.getMeterArcCode())
            .eq(PrHeatArchive::getIsChanged, 0)
            .ne(PrHeatArchive::getId, bo.getId()));
        if (count > 0) {
            return R.fail("该设备编号已存在");
        }
        PrHeatArchive entity = MapstructUtils.convert(bo, PrHeatArchive.class);
        return toAjax(heatArchiveService.updateById(entity));
    }

    /**
     * 删除房屋热表配表（逻辑删除）
     * 旧端点: POST /ht/heatArchive/deleteData/{id}
     * 新端点: DELETE /thermal/ht/heat-archive/{id}
     * 注意: PrHeatArchive 实体使用 @TableLogic 软删除
     */
    @SaCheckPermission("thermal:ht:heat-archive:remove")
    @SaCheckLogin
    @Log(title = "房屋热表配表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable String id) {
        return toAjax(heatArchiveService.removeById(id));
    }

    /**
     * 查询公司下所有热表档案
     * 旧端点: GET /ht/heatArchive/queryCompanyHeat
     * 新端点: GET /thermal/ht/heat-archive/queryCompanyHeat
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @GetMapping("/queryCompanyHeat")
    public R<List<PrHeatArchiveVo>> queryCompanyHeat(@RequestParam String companyId) {
        return R.ok(heatArchiveService.queryCompanyHeat(companyId));
    }

    /**
     * 停表（设置 isStop=1）
     * 旧端点: POST /ht/heatArchive/stopMeter/{id}
     * 新端点: POST /thermal/ht/heat-archive/stopMeter/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热表配表-停表", businessType = BusinessType.UPDATE)
    @PostMapping("/stopMeter/{id}")
    public R<Void> stopMeter(@PathVariable Long id) {
        PrHeatArchive entity = new PrHeatArchive();
        entity.setId(id);
        entity.setIsStop(1);
        return toAjax(heatArchiveService.updateById(entity));
    }

    /**
     * 开表（设置 isStop=0）
     * 旧端点: POST /ht/heatArchive/startMeter/{id}
     * 新端点: POST /thermal/ht/heat-archive/startMeter/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热表配表-开表", businessType = BusinessType.UPDATE)
    @PostMapping("/startMeter/{id}")
    public R<Void> startMeter(@PathVariable Long id) {
        PrHeatArchive entity = new PrHeatArchive();
        entity.setId(id);
        entity.setIsStop(0);
        return toAjax(heatArchiveService.updateById(entity));
    }

    /**
     * 计算平衡
     * 旧端点: GET /ht/heatArchive/calculate/{id}
     * 新端点: GET /thermal/ht/heat-archive/calculate/{id}
     */
    @SaCheckPermission("thermal:ht:heat-archive:query")
    @SaCheckLogin
    @GetMapping("/calculate/{id}")
    public R<BigDecimal> calculate(@PathVariable String id) {
        return R.ok(heatArchiveService.calculate(id));
    }

    /**
     * 更换仪表
     * 旧端点: POST /ht/heatArchive/replaceHeatMeter
     * 新端点: POST /thermal/ht/heat-archive/replace
     */
    @SaCheckPermission("thermal:ht:heat-archive:edit")
    @SaCheckLogin
    @Log(title = "房屋热表配表-更换仪表", businessType = BusinessType.UPDATE)
    @PostMapping("/replace")
    public R<Void> replaceHeatMeter(@RequestBody PrHeatArchive prHeatArchive) {
        return toAjax(heatArchiveService.replaceHeatMeter(prHeatArchive));
    }

    /**
     * 仪表充值
     * 旧端点: POST /ht/heatArchive/recharge
     * 新端点: POST /thermal/ht/heat-archive/recharge
     */
    @SaCheckPermission("thermal:ht:heat-archive:recharge")
    @SaCheckLogin
    @Log(title = "房屋热表配表-仪表充值", businessType = BusinessType.INSERT)
    @PostMapping("/recharge")
    public R<Object> recharge(@RequestBody PrHeatArchive prHeatArchive,
                              @RequestParam String paymentMethod) {
        Object result = heatArchiveService.recharge(prHeatArchive, paymentMethod);
        if (result != null) {
            return R.ok(result);
        }
        return R.fail("充值失败");
    }

    /**
     * 手动调控
     * 旧端点: POST /ht/heatArchive/manualControl
     * 新端点: POST /thermal/ht/heat-archive/manualControl
     */
    @SaCheckPermission("thermal:ht:heat-archive:control")
    @SaCheckLogin
    @Log(title = "房屋热表配表-手动调控", businessType = BusinessType.UPDATE)
    @PostMapping("/manualControl")
    public R<Void> manualControl(
            @RequestBody List<PrHeatVo> prHeatVoList,
            @RequestParam boolean switch1,
            @RequestParam(required = false) Integer scale,
            @RequestParam String adjust,
            @RequestParam String orgId,
            @RequestParam String companyId,
            @RequestParam(required = false) String intervall,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String duration) {
        return toAjax(heatArchiveService.manualControl(prHeatVoList, switch1, scale, adjust,
                orgId, companyId, intervall, unit, duration));
    }

    /**
     * 实时数据查询
     * 旧端点: POST /ht/heatArchive/realTimeData
     * 新端点: POST /thermal/ht/heat-archive/realTimeData
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @PostMapping("/realTimeData")
    public TableDataInfo<PrHeatArchiveVo> realTimeData(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        return heatArchiveService.realTimeData(companyId, orgId, buildingId, unitCode, search, pageQuery);
    }

    /**
     * 综合查询
     * 旧端点: POST /ht/heatArchive/zonghe
     * 新端点: POST /thermal/ht/heat-archive/zonghe
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @PostMapping("/zonghe")
    public TableDataInfo<PrHeatArchiveVo> zonghe(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String moneyType,
            @RequestParam(required = false) String valveStatus,
            PageQuery pageQuery) {
        return heatArchiveService.zonghe(companyId, orgId, buildingId, unitCode, search, moneyType, valveStatus, pageQuery);
    }

    /**
     * 巡测
     * 旧端点: POST /ht/heatArchive/xunce
     * 新端点: POST /thermal/ht/heat-archive/xunce
     */
    @SaCheckPermission("thermal:ht:heat-archive:control")
    @SaCheckLogin
    @Log(title = "房屋热表配表-巡测", businessType = BusinessType.UPDATE)
    @PostMapping("/xunce")
    public R<Void> xunce(@RequestBody List<PrHeatVo> prHeatVoList,
                         @RequestParam String orgId,
                         @RequestParam String companyId) {
        return toAjax(heatArchiveService.xunce(prHeatVoList, orgId, companyId));
    }

    /**
     * 设置阀门组号
     * 旧端点: POST /ht/heatArchive/setValveGroupParam
     * 新端点: POST /thermal/ht/heat-archive/setValveGroupParam
     */
    @SaCheckPermission("thermal:ht:heat-archive:control")
    @SaCheckLogin
    @Log(title = "房屋热表配表-设置阀门组号", businessType = BusinessType.UPDATE)
    @PostMapping("/setValveGroupParam")
    public R<Void> setValveGroupParam(@RequestBody List<PrHeatVo> prHeatVoList,
                                      @RequestParam String commandParam,
                                      @RequestParam String orgId,
                                      @RequestParam String companyId) {
        return toAjax(heatArchiveService.setValveGroupParam(prHeatVoList, commandParam, orgId, companyId));
    }

    /**
     * 查询仪表
     * 旧端点: GET /ht/heatArchive/findMeter
     * 新端点: GET /thermal/ht/heat-archive/findMeter
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @GetMapping("/findMeter")
    public R<List<PrHeatArchiveVo>> findMeter(@RequestParam String search,
                                              @RequestParam String companyId) {
        return R.ok(heatArchiveService.findMeter(search, companyId));
    }

    /**
     * 导出全部配表数据
     * 旧端点: GET /ht/heatArchive/exportAll
     * 新端点: GET /thermal/ht/heat-archive/export
     */
    @SaCheckPermission("thermal:ht:heat-archive:export")
    @SaCheckLogin
    @Log(title = "房屋热表配表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public R<List<PrHeatArchiveVo>> exportAll(@RequestParam(required = false) String companyId,
                                              @RequestParam(required = false) String orgId) {
        return R.ok(heatArchiveService.exportAll(companyId, orgId));
    }

    /**
     * 收费明细报表
     * 旧端点: POST /ht/heatArchive/selectReport
     * 新端点: POST /thermal/ht/heat-archive/selectReport
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @PostMapping("/selectReport")
    public R<List<PrHeatArchiveVo>> selectReport(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search) {
        return R.ok(heatArchiveService.selectReport(companyId, orgId, buildingId, unitCode, startTime, endTime, search));
    }

    /**
     * 仪表历史数据查询报表
     * 旧端点: POST /ht/heatArchive/selectMeterReport
     * 新端点: POST /thermal/ht/heat-archive/selectMeterReport
     */
    @SaCheckPermission("thermal:ht:heat-archive:list")
    @SaCheckLogin
    @PostMapping("/selectMeterReport")
    public R<List<PrHeatArchiveVo>> selectMeterReport(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String search) {
        return R.ok(heatArchiveService.selectMeterReport(companyId, orgId, buildingId, unitCode, startTime, endTime, search));
    }
}
