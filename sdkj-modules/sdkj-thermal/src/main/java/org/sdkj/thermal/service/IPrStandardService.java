package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrExpenseItem;
import org.sdkj.thermal.domain.PrStandard;
import org.sdkj.thermal.domain.vo.PrStandardVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收费标准 Service 接口
 * 迁移自旧系统 PrStandardService
 */
public interface IPrStandardService extends IService<PrStandard> {

    /**
     * 分页查询收费标准列表
     */
    TableDataInfo<PrStandardVo> selectPageList(String orgId, String type, PageQuery pageQuery);

    /**
     * 新增收费标准（含子表单价）
     */
    boolean insertData(PrStandard standard);

    /**
     * 修改收费标准（含子表单价）
     */
    boolean updateData(PrStandard standard);

    /**
     * 删除收费标准
     */
    boolean deleteData(String id);

    /**
     * 查询收费标准详情（含子表单价）
     */
    PrStandard selectDetailById(String id);

    /**
     * 根据 itemCode 查询收费标准
     */
    List<PrStandardVo> selectByItemCode(String itemCode, String orgId);

    /**
     * 查询电表收费标准
     */
    List<PrStandardVo> findEleStandard(String companyId, String orgId);

    /**
     * 查询水表收费标准
     */
    List<PrStandardVo> findWaterStandard(String companyId, String orgId);

    /**
     * 查询热力收费标准
     */
    List<PrStandardVo> findHeatStandard(String companyId, String orgId);

    /**
     * 检查标准名称是否重复
     */
    boolean isName(String companyId, String orgId, String itemGroup, String name, String id);

    /**
     * 购买限额校验
     * @return true=超出限额，data中有msg；false=未超出
     */
    PurchaseCheckResult checkPurchase(String meterId, String standardId, String companyId, String orgId, BigDecimal amount);

    /**
     * 根据标准 ID 查询关联的费用项目
     */
    PrExpenseItem getExpenseItemByStandardId(String companyId, String orgId, String standardId);

    /**
     * 按项目名称分页查询收费标准
     */
    TableDataInfo<PrStandardVo> selectByItemName(String orgIdCopy, String itemName, PageQuery pageQuery);

    /**
     * 购买限额检查结果
     */
    record PurchaseCheckResult(boolean exceeded, String message) {}
}
