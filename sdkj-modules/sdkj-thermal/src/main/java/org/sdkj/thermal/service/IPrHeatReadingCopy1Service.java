package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.sdkj.thermal.domain.vo.PrHeatReadingCopy1Vo;
import org.sdkj.thermal.domain.vo.PrHeatReadingLabelVo;

import java.util.List;

/**
 * 热表抄表数据副本 Service 接口
 * 手工远传抄表数据（携带物业公司及小区信息）
 */
public interface IPrHeatReadingCopy1Service {

    /**
     * 根据标签列表分页查询抄表数据
     *
     * @param labels   标签列表
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param page      分页参数
     * @return 分页结果
     */
    Page<PrHeatReadingCopy1Vo> pageList(List<PrHeatReadingLabelVo> labels, String startTime, String endTime, Page<?> page);

    /**
     * 热表配表读数情况分页查询
     *
     * @param companyId   公司ID
     * @param orgId       小区ID
     * @param buildingId  楼栋ID
     * @param unitCode    单元编码
     * @param meterArcCode 热表档案编号
     * @param search      搜索关键字
     * @param page        分页参数
     * @return 分页结果
     */
    Page<PrHeatReadingCopy1Vo> pageHeatReadingList(String companyId, String orgId, String buildingId,
                                                    String unitCode, String meterArcCode, String search, Page<?> page);
}
