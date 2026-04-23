package org.dromara.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtMeterSort;
import org.dromara.meter.domain.vo.MtMeterSortVo;

import java.util.List;

/**
 * 仪表分类服务接口
 */
public interface IMtMeterSortService extends IService<MtMeterSort> {

    /**
     * 分页查询仪表分类
     */
    TableDataInfo<MtMeterSortVo> selectPageList(LambdaQueryWrapper<MtMeterSort> lqw, PageQuery pageQuery);

    int verifyName(String name, String id);

    int countBySortId(String sortId, String meterType);

    /**
     * 条件查询仪表分类
     */
    List<MtMeterSortVo> selectList(LambdaQueryWrapper<MtMeterSort> lqw);
}
