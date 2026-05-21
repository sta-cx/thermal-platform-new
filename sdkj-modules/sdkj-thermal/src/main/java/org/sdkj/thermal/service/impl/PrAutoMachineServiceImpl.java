package org.sdkj.thermal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.domain.PrTransactionRecord;
import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;
import org.sdkj.thermal.mapper.PrOptionsHeatMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordMapper;
import org.sdkj.thermal.service.IPrAutoMachineService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 自助缴费机 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrAutoMachineServiceImpl implements IPrAutoMachineService {

    private final PrTransactionRecordMapper transactionRecordMapper;
    private final PrOptionsHeatMapper optionsHeatMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public String generateSerialNum() {
        // 查询公司级别的供热配置，获取流水号前缀
        PrOptionsHeat options = optionsHeatMapper.selectOne(
            new LambdaQueryWrapper<PrOptionsHeat>()
                .eq(PrOptionsHeat::getLevel, "company")
                .last("LIMIT 1")
        );

        String prefix = "";
        if (options != null && StrUtil.isNotBlank(options.getSerialPrefix())) {
            prefix = options.getSerialPrefix();
        }

        String today = LocalDate.now().format(DATE_FMT);
        String likePattern = prefix + today + "%";

        // 查询今日已有最大流水号
        List<PrTransactionRecord> records = transactionRecordMapper.selectList(
            new LambdaQueryWrapper<PrTransactionRecord>()
                .likeRight(PrTransactionRecord::getSerialNum, likePattern)
                .orderByDesc(PrTransactionRecord::getSerialNum)
                .last("LIMIT 1")
        );

        int nextSeq = 1;
        if (records != null && !records.isEmpty()) {
            String maxSerial = records.get(0).getSerialNum();
            // 从流水号尾部提取序号部分
            String seqPart = maxSerial.substring((prefix + today).length());
            if (StrUtil.isNotBlank(seqPart)) {
                try {
                    nextSeq = Integer.parseInt(seqPart) + 1;
                } catch (NumberFormatException e) {
                    log.warn("流水号格式解析失败: {}", maxSerial);
                    nextSeq = 1;
                }
            }
        }

        // 获取序号长度配置，默认4位
        int serialLength = 4;
        if (options != null && options.getSerialLength() != null && options.getSerialLength() > 0) {
            serialLength = options.getSerialLength();
        }

        return prefix + today + String.format("%0" + serialLength + "d", nextSeq);
    }

    @Override
    public String generateQrCode(String type, String serialNum) {
        if (StrUtil.isBlank(serialNum)) {
            throw new ServiceException("流水号不能为空");
        }
        if (StrUtil.isBlank(type)) {
            throw new ServiceException("支付类型不能为空");
        }

        // TODO: 第三方支付SDK集成后替换为真实支付链接
        // 目前返回模拟的二维码URL，供前端调试
        return switch (type) {
            case "alipay" -> "https://openapi.alipay.com/gateway.do?method=alipay.trade.precreate&out_trade_no=" + serialNum;
            default -> throw new ServiceException("不支持的支付类型: " + type);
        };
    }

    @Override
    public boolean checkPaymentStatus(String serialNum) {
        if (StrUtil.isBlank(serialNum)) {
            throw new ServiceException("流水号不能为空");
        }

        long count = transactionRecordMapper.selectCount(
            new LambdaQueryWrapper<PrTransactionRecord>()
                .eq(PrTransactionRecord::getSerialNum, serialNum)
                .eq(PrTransactionRecord::getStatus, 0) // status=0 表示正常（已完成的交易）
        );
        return count > 0;
    }

    @Override
    public PrTransactionRecordVo getRecordBySerialNum(String serialNum) {
        if (StrUtil.isBlank(serialNum)) {
            throw new ServiceException("流水号不能为空");
        }

        PrTransactionRecord record = transactionRecordMapper.selectOne(
            new LambdaQueryWrapper<PrTransactionRecord>()
                .eq(PrTransactionRecord::getSerialNum, serialNum)
                .last("LIMIT 1")
        );

        if (record == null) {
            throw new ServiceException("未找到流水号对应的交易记录: " + serialNum);
        }

        // 使用 BaseMapperPlus 的 VO 转换
        return transactionRecordMapper.selectVoById(record.getId());
    }
}
