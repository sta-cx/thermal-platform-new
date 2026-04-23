package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtRepair;
import org.dromara.thermal.domain.vo.HtRepairVo;

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
     * @param companyId 公司ID
     * @return 类型统计列表 [{repairType, count}]
     */
    List<Map<String, Object>> selectTypeCount(String companyId);

    /**
     * 根据房间ID查询报修记录
     * @param roomId 房屋ID
     * @return 报修记录列表
     */
    List<HtRepairVo> selectByRoomId(String roomId);

    /**
     * 逻辑删除报修记录
     * @param repairNo 报修编号
     * @param companyId 公司ID
     * @return 影响行数
     */
    int markAsDeleted(String repairNo, String companyId);

}
