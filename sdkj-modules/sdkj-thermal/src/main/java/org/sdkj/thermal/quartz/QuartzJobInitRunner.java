package org.sdkj.thermal.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.service.IHtTasksService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class QuartzJobInitRunner implements CommandLineRunner {

    private final IHtTasksService tasksService;
    private final ThermalJobManager thermalJobManager;

    @Override
    public void run(String... args) {
        log.info("Initializing Quartz jobs from database...");
        try {
            // Load all active thermal tasks that have a cron expression
            List<HtTasks> activeTasks = tasksService.lambdaQuery()
                .eq(HtTasks::getStatus, 1)
                .isNotNull(HtTasks::getCronExpression)
                .list();

            int successCount = 0;
            for (HtTasks task : activeTasks) {
                try {
                    thermalJobManager.addJob(task.getId());
                    successCount++;
                    log.info("Scheduled thermal task: id={}, name={}", task.getId(), task.getName());
                } catch (Exception e) {
                    log.error("Failed to schedule task id={}: {}", task.getId(), e.getMessage());
                }
            }
            log.info("Quartz job initialization complete. {}/{} tasks scheduled.", successCount, activeTasks.size());
        } catch (Exception e) {
            log.error("Quartz job initialization failed", e);
        }
    }
}
