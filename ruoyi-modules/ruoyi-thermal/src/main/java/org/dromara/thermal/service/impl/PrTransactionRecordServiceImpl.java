package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrTransactionRecord;
import org.dromara.thermal.domain.vo.PrTransactionRecordSubVo;
import org.dromara.thermal.domain.vo.PrTransactionRecordVo;
import org.dromara.thermal.mapper.PrTransactionRecordMapper;
import org.dromara.thermal.service.IPrTransactionRecordService;
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
        List<PrTransactionRecordVo> list = baseMapper.selectPageList(
            search, companyId, orgId, buildingId, unitCode, startTime, endTime, status);
        int total = list.size();
        int fromIndex = (int) ((pageQuery.getPageNum() - 1) * pageQuery.getPageSize());
        int toIndex = Math.min(fromIndex + (int) pageQuery.getPageSize(), total);
        List<PrTransactionRecordVo> pagedList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();
        Page<PrTransactionRecordVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        page.setRecords(pagedList);
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
            throw new RuntimeException("撤销失败：记录不存在、已撤销/作废，或已跨月");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean invalid(String recordId) {
        int rows = baseMapper.invalidRecord(recordId);
        if (rows == 0) {
            throw new RuntimeException("作废失败：记录不存在或已作废");
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
}
