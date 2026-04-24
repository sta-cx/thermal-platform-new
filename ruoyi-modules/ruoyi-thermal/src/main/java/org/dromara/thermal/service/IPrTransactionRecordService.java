package org.dromara.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrTransactionRecord;
import org.dromara.thermal.domain.vo.PrTransactionRecordSubVo;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;

import java.util.List;
import java.util.Map;

public interface IPrTransactionRecordService extends IService<PrTransactionRecord> {

    TableDataInfo<PrTransactionRecordVo> pageList(String search, String companyId, String orgId,
        String buildingId, String unitCode, String startTime, String endTime, String status,
        PageQuery pageQuery);

    List<PrTransactionRecordSubVo> getDetailByMainId(String mainId);

    boolean revocation(String recordId);

    boolean invalid(String recordId);

    Map<String, Object> comprehensive(String companyId, String orgId, String startTime, String endTime);

    Map<String, Object> received(String companyId, String orgId, String startTime, String endTime);

    Map<String, Object> arrears(String companyId, String orgId);

    List<PrTransactionRecordVo> daily(String companyId, String orgId, String date);
}
