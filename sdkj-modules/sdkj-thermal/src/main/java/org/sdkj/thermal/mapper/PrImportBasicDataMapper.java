package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportBasicData;

import java.util.List;

public interface PrImportBasicDataMapper extends BaseMapperPlus<PrImportBasicData, PrImportBasicData> {

    void insertList(@Param("list") List<PrImportBasicData> lists);

    void updateOrgId(@Param("companyId") String companyId, @Param("create") String create);

    void updateBuildId(@Param("companyId") String companyId, @Param("create") String create);

    void updateUnitId(@Param("companyId") String companyId, @Param("create") String create);

    void updateStationId(@Param("companyId") String companyId, @Param("create") String create);

    void updateSubstationId(@Param("companyId") String companyId, @Param("create") String create);

    void updateHouseId(@Param("companyId") String companyId, @Param("create") String create);

    void updateStandardId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectNoOrgIds(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectNoRoomNum(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectRepeatHouse(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectError1(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectHasAlready(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectNoBuildId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectNoUnitId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectStationId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectNoSubstationId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectNoStandardId(@Param("companyId") String companyId, @Param("create") String create);

    boolean deleteData(@Param("companyId") String companyId, @Param("create") String create);

    void submitData(@Param("companyId") String companyId, @Param("create") String create);

    void deleteImportBasicData(@Param("companyId") String companyId, @Param("create") String create);

    List<String> groupOrgId(@Param("companyId") String companyId, @Param("create") String create);

    List<PrImportBasicData> selectBuildings(@Param("companyId") String companyId, @Param("create") String create, @Param("orgId") String orgId);

    List<PrImportBasicData> selectUnits(@Param("companyId") String companyId, @Param("create") String create, @Param("orgId") String orgId);

    List<PrImportBasicData> selectHouses(@Param("companyId") String companyId, @Param("create") String create, @Param("orgId") String orgId);

    void insertUsers(@Param("companyId") String companyId, @Param("create") String create, @Param("password") String password);

    void insertUserHouse(@Param("companyId") String companyId, @Param("create") String create);

    void insertHouseChange(@Param("companyId") String companyId, @Param("create") String create);

    void insertHouseExpense(@Param("companyId") String companyId, @Param("create") String create);

    void deleteUserHouseDataByNoHouseId(@Param("companyId") String companyId, @Param("orgIds") List<String> orgIds);

    // ========== 按房屋编码导入相关方法 ==========

    /**
     * 直接插入用户
     */
    void insertUserDirect(@Param("user") org.sdkj.thermal.domain.PrUser user);

    /**
     * 插入用户-房屋关联
     */
    void insertUserHouseRelation(@Param("userId") Long userId,
                                  @Param("houseId") Long houseId,
                                  @Param("companyId") String companyId,
                                  @Param("createBy") String createBy,
                                  @Param("createTime") java.util.Date createTime);

    /**
     * 按标准名称插入房屋费用绑定
     */
    void insertHouseExpenseByCode(@Param("houseId") Long houseId,
                                   @Param("standardName") String standardName,
                                   @Param("standardPrice") java.math.BigDecimal standardPrice,
                                   @Param("itemName") String itemName,
                                   @Param("companyId") String companyId,
                                   @Param("createBy") String createBy,
                                   @Param("createTime") java.util.Date createTime);

    /**
     * 更新账户余额（按房屋/用户/费项）
     */
    void updateAccountBalance(@Param("houseId") Long houseId,
                               @Param("userId") Long userId,
                               @Param("account") java.math.BigDecimal account,
                               @Param("itemName") String itemName,
                               @Param("companyId") String companyId,
                               @Param("createBy") String createBy,
                               @Param("createTime") java.util.Date createTime);
}
