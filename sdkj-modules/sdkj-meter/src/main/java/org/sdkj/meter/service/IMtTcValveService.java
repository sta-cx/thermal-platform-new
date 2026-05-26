package org.sdkj.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtTcValve;
import org.sdkj.meter.domain.vo.MtTcValveVo;

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
     * 查询所有已建立 mt_meter_match 关联的阀门
     * @return 阀门列表
     */
    List<MtTcValveVo> selectAllMatchedValves();

    /**
     * 条件查询阀门列表
     * @param lqw 查询条件
     * @return 阀门列表
     */
    List<MtTcValveVo> selectList(LambdaQueryWrapper<MtTcValve> lqw);

}
