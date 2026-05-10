package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.vo.HtRepairVo;

import java.util.List;
import java.util.Map;

/**
 * 报修记录 Service 接口
 * 迁移自旧系统 HtRepairService
 */
public interface IHtRepairService extends IService<HtRepair> {

    /**
     * 分页查询报修记录列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<HtRepairVo> selectPageList(LambdaQueryWrapper<HtRepair> lqw, PageQuery pageQuery);

    /**
     * 按报修类型统计数量
     * @return 类型统计列表 [{repairType, count}]
     */
    List<Map<String, Object>> selectTypeCount();

    /**
     * 根据房间ID查询报修记录
     * @param roomId 房屋ID
     * @return 报修记录列表
     */
    List<HtRepairVo> selectByRoomId(String roomId);

    /**
     * 逻辑删除报修记录
     * @param repairNo 报修编号
     * @return 影响行数
     */
    int markAsDeleted(String repairNo);

    /**
     * 生成报修编号
     * 格式: BX + yyyyMMdd + 4位序号 (如 BX202604250001)
     * @return 报修编号
     */
    String generateRepairNo();

}
