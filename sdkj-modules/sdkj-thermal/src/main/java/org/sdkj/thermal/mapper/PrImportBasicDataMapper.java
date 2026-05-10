package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrImportBasicData;

import java.util.List;

public interface PrImportBasicDataMapper extends BaseMapperPlus<PrImportBasicData, PrImportBasicData> {

    void insertList(@Param("list") List<PrImportBasicData> lists);

    void updateOrgId(@Param("create") String create);

    void updateBuildId(@Param("create") String create);

    void updateUnitId(@Param("create") String create);

    void updateStationId(@Param("create") String create);

    void updateSubstationId(@Param("create") String create);

    void updateHouseId(@Param("create") String create);

    void updateStandardId(@Param("create") String create);

    List<PrImportBasicData> selectNoOrgIds(@Param("create") String create);

    List<PrImportBasicData> selectNoRoomNum(@Param("create") String create);

    List<PrImportBasicData> selectRepeatHouse(@Param("create") String create);

    List<PrImportBasicData> selectError1(@Param("create") String create);

    List<PrImportBasicData> selectHasAlready(@Param("create") String create);

    List<PrImportBasicData> selectNoBuildId(@Param("create") String create);

    List<PrImportBasicData> selectNoUnitId(@Param("create") String create);

    List<PrImportBasicData> selectStationId(@Param("create") String create);

    List<PrImportBasicData> selectNoSubstationId(@Param("create") String create);

    List<PrImportBasicData> selectNoStandardId(@Param("create") String create);

    boolean deleteData(@Param("create") String create);

    void submitData(@Param("create") String create);

    void deleteImportBasicData(@Param("create") String create);

    List<String> groupOrgId(@Param("create") String create);

    List<PrImportBasicData> selectBuildings(@Param("create") String create, @Param("orgId") String orgId);

    List<PrImportBasicData> selectUnits(@Param("create") String create, @Param("orgId") String orgId);

    List<PrImportBasicData> selectHouses(@Param("create") String create, @Param("orgId") String orgId);

    void insertUsers(@Param("create") String create, @Param("password") String password);

    void insertUserHouse(@Param("create") String create);

    void insertHouseChange(@Param("create") String create);

    void insertHouseExpense(@Param("create") String create);

    void deleteUserHouseDataByNoHouseId(@Param("orgIds") List<String> orgIds);

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
                                  @Param("createBy") String createBy,
                                  @Param("createTime") java.util.Date createTime);

    /**
     * 按标准名称插入房屋费用绑定
     */
    void insertHouseExpenseByCode(@Param("houseId") Long houseId,
                                   @Param("standardName") String standardName,
                                   @Param("standardPrice") java.math.BigDecimal standardPrice,
                                   @Param("itemName") String itemName,
                                   @Param("createBy") String createBy,
                                   @Param("createTime") java.util.Date createTime);

    /**
     * 更新账户余额（按房屋/用户/费项）
     */
    void updateAccountBalance(@Param("houseId") Long houseId,
                               @Param("userId") Long userId,
                               @Param("account") java.math.BigDecimal account,
                               @Param("itemName") String itemName,
                               @Param("createBy") String createBy,
                               @Param("createTime") java.util.Date createTime);
}
