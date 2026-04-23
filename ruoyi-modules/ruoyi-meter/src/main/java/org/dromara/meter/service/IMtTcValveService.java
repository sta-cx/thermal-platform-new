package org.dromara.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtTcValve;
import org.dromara.meter.domain.vo.MtTcValveVo;

import java.util.List;

/**
 * 阀门档案 Service 接口
 * 迁移自旧系统 MtTcValveService
 */
public interface IMtTcValveService extends IService<MtTcValve> {

    /**
     * 分页查询阀门档案列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MtTcValveVo> selectPageList(LambdaQueryWrapper<MtTcValve> lqw, PageQuery pageQuery);

    /**
     * 根据当前用户所属公司查询阀门列表
     * @return 阀门列表
     */
    List<MtTcValveVo> selectValvesByUserCompany();

    /**
     * 条件查询阀门列表
     * @param lqw 查询条件
     * @return 阀门列表
     */
    List<MtTcValveVo> selectList(LambdaQueryWrapper<MtTcValve> lqw);

}
