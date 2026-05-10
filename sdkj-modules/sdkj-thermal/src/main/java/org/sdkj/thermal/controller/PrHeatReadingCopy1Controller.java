package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.vo.PrHeatReadingCopy1Vo;
import org.sdkj.thermal.domain.vo.PrHeatReadingLabelVo;
import org.sdkj.thermal.service.IPrHeatReadingCopy1Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 热表抄表数据副本（手工远传抄表）
 * 旧端点: /property/prHeatReadingCopy1/* -> 新端点: /thermal/ht/heat-reading-copy1/*
 * 远传抄表数据一般不分物业公司，手工抄表的数据需要携带物业公司及小区
 * 此类远传抄表数据不能更新，每次都要记录
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/ht/heat-reading-copy1")
public class PrHeatReadingCopy1Controller extends BaseController {

    private final IPrHeatReadingCopy1Service copy1Service;

    /**
     * 根据标签列表分页查询抄表数据
     * 旧端点: POST /property/prHeatReadingCopy1/pageList
     * 新端点: POST /thermal/ht/heat-reading-copy1/pageList
     *
     * @param labels   标签列表（PrHeatReadingLabel）
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageQuery 分页参数
     * @return 分页抄表数据
     */
    @SaCheckPermission("thermal:ht:reading:query")
    @SaCheckLogin
    @PostMapping("/pageList")
    public TableDataInfo<PrHeatReadingCopy1Vo> pageList(
            @RequestBody List<PrHeatReadingLabelVo> labels,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            PageQuery pageQuery) {
        Page<?> page = pageQuery.build();
        return TableDataInfo.build(copy1Service.pageList(labels, startTime, endTime, page));
    }

    /**
     * 热表配表读数情况分页查询
     * 旧端点: GET /property/prHeatReadingCopy1/pageHeatReadingList
     * 新端点: GET /thermal/ht/heat-reading-copy1/pageHeatReadingList
     *
     * @param orgId       小区ID
     * @param buildingId  楼栋ID
     * @param unitCode    单元编码
     * @param meterArcCode 热表档案编号
     * @param search      搜索关键字（房间号/手机号/表号）
     * @param pageQuery   分页参数
     * @return 分页配表读数数据
     */
    @SaCheckPermission("thermal:ht:reading:query")
    @SaCheckLogin
    @GetMapping("/pageHeatReadingList")
    public TableDataInfo<PrHeatReadingCopy1Vo> pageHeatReadingList(@RequestParam(required = false) String orgId,
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String unitCode,
            @RequestParam(required = false) String meterArcCode,
            @RequestParam(required = false) String search,
            PageQuery pageQuery) {
        Page<?> page = pageQuery.build();
        return TableDataInfo.build(copy1Service.pageHeatReadingList(
            orgId, buildingId, unitCode, meterArcCode, search, page));
    }
}
