package org.sdkj.thermal.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * pr_data_grant 墓碑清理 Job 配置项。
 * 对应 application-thermal.yml 中 sdkj.data-grant-cleanup.* 配置块。
 */
@Data
@ConfigurationProperties(prefix = "sdkj.data-grant-cleanup")
public class DataGrantCleanupProperties {

    /** 总开关,false 则完全不跑(应急止血用) */
    private boolean enabled = true;

    /** 多少天前的墓碑算可清理 */
    private int retentionDays = 30;

    /** 单批 DELETE 行数上限 */
    private int batchSize = 500;

    /** 全租户合计待删上限,超过整个 Job 跳过 + WARN(防止配错阈值或异常涌入) */
    private long maxDeletePerRun = 5000;

    /** cron 表达式(给 @Scheduled 用 ${} 占位符引用,非作用本字段) */
    private String cron = "0 30 3 * * ?";
}
