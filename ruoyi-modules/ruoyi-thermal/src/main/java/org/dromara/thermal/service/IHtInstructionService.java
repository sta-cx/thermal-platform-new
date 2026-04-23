package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.HtInstruction;
import org.dromara.thermal.domain.vo.HtInstructionVo;

import java.util.List;

/**
 * 控制指令 Service 接口
 * 迁移自旧系统 HtInstructionService
 */
public interface IHtInstructionService extends IService<HtInstruction> {

    /**
     * 分页查询控制指令列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<HtInstructionVo> selectPageList(LambdaQueryWrapper<HtInstruction> lqw, PageQuery pageQuery);

    /**
     * 统计指令被策略子表引用的次数
     * @param instructionId 指令ID
     * @return 引用次数
     */
    int countByStrategySub(String instructionId);

    /**
     * 查询所有指令列表
     * @return 指令列表
     */
    List<HtInstruction> selectAllList();

}
