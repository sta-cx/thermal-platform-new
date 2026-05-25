package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrUserHouse;
import org.sdkj.thermal.domain.vo.PrUserHouseVo;

import java.util.List;

public interface IPrUserHouseService extends IService<PrUserHouse> {

    List<PrUserHouseVo> selectChangeHistory(Long houseId, String startDate, String endDate);
}
