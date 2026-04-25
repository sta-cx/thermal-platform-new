package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.vo.PrTransactionRecordSubVo;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;

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

    /** 退费记录查询 */
    Map<String, Object> refund(String companyId, String orgId, String buildingId,
        String startTime, String endTime, String search);

    /** 水费交易查询 */
    Map<String, Object> getWater(String companyId, String orgId, String buildingId,
        String startTime, String endTime, String search);

    /** 电费交易查询 */
    Map<String, Object> getEle(String companyId, String orgId, String buildingId,
        String startTime, String endTime, String search);

    /** 写卡日志查询 */
    Map<String, Object> cardLog(String companyId, String orgId, String buildingId,
        String startTime, String endTime, String search, String type, String createBy);

    /** 写卡操作人列表 */
    List<Map<String, Object>> cardLogCreateByName(String companyId, String orgId);

    /** 未收款查询 */
    Map<String, Object> uncoll(String companyId, String orgId, String buildingId,
        String startTime, String endTime, String search);

    /** 本月统计 */
    Map<String, Object> getThisMonth(String companyId, String userId);

    /** 本月分类统计 */
    Map<String, Object> getThisMonthVarious(String companyId, String userId);
}
