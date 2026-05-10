package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrImportUnitValve;

import java.util.List;

/**
 * 单元阀门导入服务接口
 * 迁移自旧系统 PrImportUnitValveService
 */
public interface IPrImportUnitValveService extends IService<PrImportUnitValve> {

    /**
     * 导入数据
     */
    Integer importData(List<PrImportUnitValve> objects);

    /**
     * 更新单元/小区ID
     */
    void updateHouseId();

    /**
     * 校验数据
     */
    void check(Integer r);

    /**
     * 获取校验结果
     */
    String getNull(Integer r);

    /**
     * 删除导入数据
     */
    boolean deleteData();

    /**
     * 提交数据
     */
    boolean submitData();

    /**
     * 查询待提交记录数
     */
    Integer select();

    /**
     * 按公司和小区查询单元列表
     */
    List<PrImportUnitValve> selectByOrgId(String orgId);
}
