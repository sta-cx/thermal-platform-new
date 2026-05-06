package org.sdkj.thermal.service.impl;

import org.sdkj.common.core.exception.ServiceException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.vo.PrTransactionRecordSubVo;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;
import org.sdkj.thermal.mapper.PrTransactionRecordMapper;
import org.sdkj.thermal.service.IPrTransactionRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrTransactionRecordServiceImpl extends ServiceImpl<PrTransactionRecordMapper, PrTransactionRecord>
        implements IPrTransactionRecordService {

    private final PrTransactionRecordMapper baseMapper;

    @Override
    public TableDataInfo<PrTransactionRecordVo> pageList(String search, String companyId, String orgId,
            String buildingId, String unitCode, String startTime, String endTime, String status, PageQuery pageQuery) {
        Page<PrTransactionRecordVo> page = pageQuery.build();
        baseMapper.selectPageList(page, search, companyId, orgId, buildingId, unitCode, startTime, endTime, status);
        return TableDataInfo.build(page);
    }

    @Override
    public List<PrTransactionRecordSubVo> getDetailByMainId(String mainId) {
        return baseMapper.selectDetailByMainId(mainId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revocation(String recordId) {
        int rows = baseMapper.revokeRecord(recordId);
        if (rows == 0) {
            throw new ServiceException("撤销失败：记录不存在、已撤销/作废，或已跨月");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean invalid(String recordId) {
        int rows = baseMapper.invalidRecord(recordId);
        if (rows == 0) {
            throw new ServiceException("作废失败：记录不存在或已作废");
        }
        return true;
    }

    @Override
    public Map<String, Object> comprehensive(String companyId, String orgId, String startTime, String endTime) {
        Map<String, Object> stats = baseMapper.selectComprehensiveStats(companyId, orgId, startTime, endTime);
        return stats != null ? stats : Map.of(
            "totalTransactions", 0, "chargeCount", 0, "refundCount", 0,
            "transferCount", 0, "chargeAmount", 0.0, "refundAmount", 0.0,
            "revokedCount", 0, "invalidCount", 0);
    }

    @Override
    public Map<String, Object> received(String companyId, String orgId, String startTime, String endTime) {
        Map<String, Object> stats = baseMapper.selectReceivedStats(companyId, orgId, startTime, endTime);
        return stats != null ? stats : Map.of(
            "receivedCount", 0, "totalReceived", 0.0,
            "cashAmount", 0.0, "wechatAmount", 0.0, "alipayAmount", 0.0, "cardAmount", 0.0);
    }

    @Override
    public Map<String, Object> arrears(String companyId, String orgId) {
        Map<String, Object> stats = baseMapper.selectArrearsStats(companyId, orgId);
        return stats != null ? stats : Map.of(
            "arrearsHouseCount", 0, "arrearsCount", 0, "totalArrears", 0.0);
    }

    @Override
    public List<PrTransactionRecordVo> daily(String companyId, String orgId, String date) {
        return baseMapper.selectDailyReport(companyId, orgId, date);
    }

    @Override
    public Map<String, Object> refund(String companyId, String orgId, String buildingId,
            String startTime, String endTime, String search) {
        List<Map<String, Object>> list = baseMapper.selectRefundList(companyId, orgId, buildingId, startTime, endTime, search);
        return Map.of("list", list, "total", list.size());
    }

    @Override
    public Map<String, Object> getWater(String companyId, String orgId, String buildingId,
            String startTime, String endTime, String search) {
        List<Map<String, Object>> list = baseMapper.selectWaterList(companyId, orgId, buildingId, startTime, endTime, search);
        return Map.of("list", list, "total", list.size());
    }

    @Override
    public Map<String, Object> getEle(String companyId, String orgId, String buildingId,
            String startTime, String endTime, String search) {
        List<Map<String, Object>> list = baseMapper.selectEleList(companyId, orgId, buildingId, startTime, endTime, search);
        return Map.of("list", list, "total", list.size());
    }

    @Override
    public Map<String, Object> cardLog(String companyId, String orgId, String buildingId,
            String startTime, String endTime, String search, String type, String createBy) {
        List<Map<String, Object>> list = baseMapper.selectCardLogList(companyId, orgId, buildingId, startTime, endTime, search, type, createBy);
        return Map.of("list", list, "total", list.size());
    }

    @Override
    public List<Map<String, Object>> cardLogCreateByName(String companyId, String orgId) {
        return baseMapper.selectCardLogOperators(companyId, orgId);
    }

    @Override
    public Map<String, Object> uncoll(String companyId, String orgId, String buildingId,
            String startTime, String endTime, String search) {
        List<Map<String, Object>> list = baseMapper.selectUncollList(companyId, orgId, buildingId, startTime, endTime, search);
        return Map.of("list", list, "total", list.size());
    }

    @Override
    public Map<String, Object> getThisMonth(String companyId, String userId) {
        Map<String, Object> record = baseMapper.selectThisMonthTotal(companyId, userId);
        List<Map<String, Object>> details = baseMapper.selectThisMonthDetails(companyId, userId);
        return Map.of("record", record != null ? record : Map.of("paidIn", 0), "details", details);
    }

    @Override
    public Map<String, Object> getThisMonthVarious(String companyId, String userId) {
        Map<String, Object> record = baseMapper.selectThisMonthTotal(companyId, userId);
        List<Map<String, Object>> details = baseMapper.selectThisMonthDetailsVarious(companyId, userId);
        return Map.of("record", record != null ? record : Map.of("paidIn", 0), "details", details);
    }
}
