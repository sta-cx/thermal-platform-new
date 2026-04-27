package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.sdkj.thermal.domain.vo.MdbVo;

public interface PrImportAuthorizationCodeMapper {

    @Insert("INSERT INTO pr_authorization_code (id, pro, code, name, address, heat_pay_code, house_id) " +
            "VALUES (REPLACE(UUID(),'-',''), #{vo.pro}, #{vo.code}, #{vo.name}, #{vo.address}, #{vo.heatPayCode}, #{vo.houseId})")
    void insertAuthorizationCode(@Param("vo") MdbVo vo);

    @Insert("INSERT INTO pr_number_segment_code (id, code) VALUES (REPLACE(UUID(),'-',''), #{code})")
    void insertNumberSegmentCode(@Param("code") String code);
}
