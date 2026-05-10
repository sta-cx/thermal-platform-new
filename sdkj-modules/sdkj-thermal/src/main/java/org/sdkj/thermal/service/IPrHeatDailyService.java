package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatDaily;
import org.sdkj.thermal.domain.vo.PrHeatDailyVo;

import java.io.Serializable;

/**
 * 热表日记录 Service 接口
 */
public interface IPrHeatDailyService extends IService<PrHeatDaily> {

    /**
     * 根据ID查询日记录详情
     */
    PrHeatDailyVo selectById(Serializable id);

    /**
     * 分页查询热表日记录列表
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param search 搜索关键字（表号/档案名称）
     * @param isCharged 是否计费
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatDailyVo> selectPageList(String orgId, String buildingId,
                                                 String unitCode, String search, Integer isCharged,
                                                 String startTime, String endTime, PageQuery pageQuery);

    /**
     * 生成热表日表数据（5步流程）
     * 1. setIsValid - 标记有效的抄表记录
     * 2. setHeatDaily - 生成日表记录
     * 3. setSteps - 更新日表单价及金额
     * 4. setQtyStepsN - 计算日表用量和金额
     * 5. setCurrentReading - 更新配表档案的当前读数
     * @param orgId 小区ID
     * @return 是否成功
     */
    boolean generateHeatDaily(String orgId);
}
