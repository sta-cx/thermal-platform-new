package org.dromara.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrExpenseItem;
import org.dromara.thermal.domain.vo.PrExpenseItemVo;

import java.util.List;

/**
 * 费用项目 Service 接口
 * 迁移自旧系统 PrExpenseItemService
 */
public interface IPrExpenseItemService extends IService<PrExpenseItem> {

    /**
     * 分页查询费用项目列表
     */
    TableDataInfo<PrExpenseItemVo> selectPageList(String orgId, String itemGroup, PageQuery pageQuery);

    /**
     * 新增费用项目（自动编号）
     */
    boolean insertData(PrExpenseItem item);

    /**
     * 根据 ID 查询费用项目详情
     */
    PrExpenseItemVo selectById(String id);

    /**
     * 按编号查询费用项目
     */
    List<PrExpenseItemVo> selectByItemCode(String companyId, String orgId, String itemGroup, String itemCode);

    /**
     * 按费项分组查询列表
     */
    List<PrExpenseItemVo> selectByItemGroup(String companyId, String orgId, String itemGroup, String userId);

    /**
     * 获取费项分组下的所有 itemCode
     */
    List<PrExpenseItemVo> getItemCodesByItemGroup(String companyId, String orgId, String itemGroup);

    /**
     * 按公司/小区查询费用项目
     */
    List<PrExpenseItemVo> selectByCompanyAndOrg(String companyId, String orgId);

    /**
     * 删除费用项目（检查是否有关联收费标准）
     */
    boolean deleteData(String id, String itemCode, String orgId);

    /**
     * 检查项目名称是否重复
     */
    boolean isItemName(String companyId, String orgId, String itemName, String id);
}
