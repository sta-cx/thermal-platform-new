package org.sdkj.ai.job;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiCallRecord;
import org.sdkj.ai.mapper.AiCallRecordMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiLogCleanupJob {

    private final AiCallRecordMapper callRecordMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    @DS("master")
    public void cleanupCallRecord() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -90);
        Date threshold = cal.getTime();

        LambdaQueryWrapper<AiCallRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(AiCallRecord::getCreatedAt, threshold);
        int deleted = callRecordMapper.delete(wrapper);
        log.info("AiLogCleanupJob: deleted {} ai_call_record rows older than {}", deleted, threshold);
    }
}
