package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrAccountBalance;
import org.sdkj.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 个人账户余额 Mapper
 */
@OrgPermission
public interface PrAccountBalanceMapper extends BaseMapperPlus<PrAccountBalance, PrAccountBalanceVo> {

    /** 查询已开户的账户列表 */
    List<PrAccountBalanceVo> selectAccountList(
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode,
        @Param("search") String search,
        @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode);

    /** 查询未开户的房屋列表 */
    List<PrAccountBalanceVo> selectNoAccountList(
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode,
        @Param("search") String search,
        @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode);

    /** 查询用户账户余额 */
    BigDecimal selectBalanceByUser(
        @Param("orgId") String orgId,
        @Param("userId") Long userId);

    /** 更新账户余额 */
    int updateBalance(
        @Param("userId") Long userId,
        @Param("houseId") Long houseId,
        @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode,
        @Param("amount") BigDecimal amount);

    /** 查询房屋押金信息 */
    Map<String, Object> selectHouseDeposit(
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode,
        @Param("search") String search);

    /** 删除导入临时数据 */
    int deleteImportStagingData();
}
