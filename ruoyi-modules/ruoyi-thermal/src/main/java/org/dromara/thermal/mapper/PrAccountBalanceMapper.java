package org.dromara.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.thermal.domain.PrAccountBalance;
import org.dromara.thermal.domain.vo.PrAccountBalanceVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 个人账户余额 Mapper
 */
public interface PrAccountBalanceMapper extends BaseMapperPlus<PrAccountBalance, PrAccountBalanceVo> {

    /** 查询已开户的账户列表 */
    List<PrAccountBalanceVo> selectAccountList(
        @Param("companyId") String companyId,
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode,
        @Param("search") String search,
        @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode);

    /** 查询未开户的房屋列表 */
    List<PrAccountBalanceVo> selectNoAccountList(
        @Param("companyId") String companyId,
        @Param("orgId") String orgId,
        @Param("buildingId") String buildingId,
        @Param("unitCode") String unitCode,
        @Param("search") String search,
        @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode);

    /** 查询用户账户余额 */
    BigDecimal selectBalanceByUser(
        @Param("companyId") String companyId,
        @Param("orgId") String orgId,
        @Param("userId") String userId);

    /** 更新账户余额 */
    int updateBalance(
        @Param("userId") String userId,
        @Param("houseId") String houseId,
        @Param("itemGroup") String itemGroup,
        @Param("itemCode") String itemCode,
        @Param("amount") BigDecimal amount);
}
