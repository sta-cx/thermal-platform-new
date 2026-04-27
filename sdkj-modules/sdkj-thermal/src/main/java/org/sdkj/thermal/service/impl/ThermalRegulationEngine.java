package org.sdkj.thermal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.HtStrategy;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.mapper.HtStrategyMapper;
import org.sdkj.thermal.mapper.HtTasksMapper;
import org.sdkj.thermal.service.IHtTasksService;
import org.sdkj.thermal.quartz.ThermalJobManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Thermal Regulation Engine
 *
 * Encapsulates the complete thermal regulation logic ported from the old ControlJob.
 * Handles all adjustBasis branches (0=single strategy, 1=return water temp,
 * 3=DTU broadcast/avg temp, 4=return water with coefficient) and all scopeType
 * branches (1=house valve, 2=unit valve, 3=DTU broadcast, 4=mixed).
 *
 * Regulation flow for single valve (scopeType 1/2/4):
 *   1. deleteHtTasksPerformLs   - clear temp table
 *   2. insertHtTasksPerformLs   - populate temp table with scope data
 *   3. instructionGeneration     - evaluate alert types (temperature judgment)
 *   4. updateInstructionGeneration - determine next instruction to execute
 *   5. insertHtTasksPerform     - move finalized records to execution table
 *
 * Regulation flow for DTU broadcast (scopeType 3):
 *   Delegated to IHtTasksService.insertHtTasksPerformDtu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThermalRegulationEngine {

    private final IHtTasksService tasksService;
    private final HtTasksMapper tasksMapper;
    private final HtStrategyMapper strategyMapper;
    private final ThermalJobManager jobManager;

    /**
     * Execute the full regulation cycle for a given task.
     *
     * @param taskId    the task ID (ht_tasks.id)
     * @param scopeType the scope type (1=house valve, 2=unit valve, 3=DTU, 4=mixed)
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeRegulation(Integer taskId, Integer scopeType) {
        String tasksIdStr = String.valueOf(taskId);
        log.info("Thermal regulation engine started for task {} scopeType {}", taskId, scopeType);

        // 1. Load task
        HtTasks task = tasksService.getById(taskId);
        if (task == null) {
            log.warn("Task {} not found, skipping regulation", taskId);
            return;
        }

        // Refresh scopeType from the task itself if not provided
        if (scopeType == null && task.getScopeType() != null) {
            scopeType = task.getScopeType();
        }
        if (scopeType == null) {
            log.warn("Task {} has no scopeType, defaulting to 1", taskId);
            scopeType = 1;
        }

        // 2. Load strategy
        HtStrategy strategy = null;
        if (task.getStrategyId() != null && !task.getStrategyId().isEmpty()) {
            strategy = strategyMapper.selectById(task.getStrategyId());
        }

        // 3. Calculate elapsed days and execution duration
        long elapsedDays = 0;
        long executionTimeSeconds = 0;
        if (task.getLastTime() != null) {
            LocalDate lastTimeDate = task.getLastTime().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
            elapsedDays = ChronoUnit.DAYS.between(lastTimeDate, LocalDate.now());
            // Execution time in hours (ported from old DateUtils.getDistanceOfTwoHour)
            long millisDiff = System.currentTimeMillis() - task.getLastTime().getTime();
            executionTimeSeconds = millisDiff / 1000;
        }
        int executionTime = (int) executionTimeSeconds;

        // 4. Get average return water temperature based on adjustBasis and scopeType
        double average = 0.0;
        if (task.getAdjustBasis() != null) {
            int adjustBasis = task.getAdjustBasis();
            if (adjustBasis == 0 || adjustBasis == 1 || adjustBasis == 3 || adjustBasis == 4) {
                if (scopeType == 1 || scopeType == 3) {
                    // House valve average return water temperature
                    average = tasksMapper.queryHtTasksPJHS(tasksIdStr);
                } else if (scopeType == 2) {
                    // Unit valve average return water temperature
                    average = tasksMapper.queryHtTasksPJHSD(tasksIdStr);
                }
            }
        }

        // 5. Update execution stats on task (execution_time, out_temp_pj)
        tasksMapper.updateExecutionTime(tasksIdStr, executionTime, average);

        // 6. Check termination: elapsed days exceeded
        if (task.getDays() != null && task.getDays() > 0 && elapsedDays >= task.getDays()) {
            stopTask(taskId, "Days exceeded: elapsed " + elapsedDays + ", limit " + task.getDays());
            return;
        }

        // 7. Check balance rate
        int currentBalanceRate = 0;
        if (scopeType == 1 || scopeType == 2) {
            currentBalanceRate = tasksMapper.queryStandard(tasksIdStr);
        } else if (scopeType == 3) {
            tasksMapper.updateDtuScopeStatus(tasksIdStr);
            currentBalanceRate = tasksMapper.queryDtuStandard(tasksIdStr);
        }

        // Determine if regulation should proceed
        boolean shouldRegulate = false;
        if (task.getStandard() != null && task.getStandard() > 0) {
            // Balance rate not reached, or adjustBasis is 0 (single strategy), or first control
            boolean isUseFirstControl = task.getIsUseFirstControl() != null && task.getIsUseFirstControl() == 1;
            boolean isFirstRun = task.getNumber() == null || task.getNumber() == 0;
            if (task.getStandard() >= currentBalanceRate || task.getAdjustBasis() == null || task.getAdjustBasis() == 0
                || (isUseFirstControl && isFirstRun)) {
                shouldRegulate = true;
            }
        } else {
            // No balance rate target set, always regulate
            shouldRegulate = true;
        }

        if (!shouldRegulate) {
            stopTask(taskId, "Balance rate reached: current " + currentBalanceRate + "%, target " + task.getStandard() + "%");
            return;
        }

        // 8. Check execution count limit
        int currentNumber = task.getNumber() != null ? task.getNumber() : 0;
        int maxNumbers = task.getNums() != null ? task.getNums() : Integer.MAX_VALUE;

        // 9. Execute regulation based on scopeType
        if (scopeType == 1 || scopeType == 2 || scopeType == 4) {
            // Single valve regulation: 5-step process
            if (currentNumber <= maxNumbers) {
                executeSingleValveRegulation(tasksIdStr, task, strategy, average);
            } else {
                log.info("Task {} regulation count reached limit ({}/{}), skipping instruction generation",
                    taskId, currentNumber, maxNumbers);
            }
        } else if (scopeType == 3) {
            // DTU broadcast regulation
            if (currentNumber < maxNumbers) {
                executeDtuRegulation(task);
            } else {
                log.info("Task {} (DTU) regulation count reached limit ({}/{}), skipping",
                    taskId, currentNumber, maxNumbers);
            }
        }
    }

    /**
     * Execute the 5-step single valve regulation process.
     *
     * Step 1: deleteHtTasksPerformLs - Clear temp table
     * Step 2: insertHtTasksPerformLs - Populate temp table with scope meter data
     * Step 3: instructionGeneration - Evaluate temperatures and set alert types
     * Step 4: updateInstructionGeneration - Determine next instruction sequence
     * Step 5: insertHtTasksPerform - Finalize and move to execution tables
     */
    private void executeSingleValveRegulation(String tasksId, HtTasks task, HtStrategy strategy, double average) {
        log.info("Single valve regulation started for task {}", tasksId);

        // Step 1: Clear temp table
        tasksMapper.deleteHtTasksPerformLs(tasksId);
        log.debug("Step 1 complete: cleared temp table for task {}", tasksId);

        // Step 2: Populate temp table (branch by adjustBasis)
        int adjustBasis = task.getAdjustBasis() != null ? task.getAdjustBasis() : 0;
        int scopeType = task.getScopeType() != null ? task.getScopeType() : 1;

        if (adjustBasis == 3 || adjustBasis == 4) {
            // Average return water temperature regulation
            if (scopeType == 1 || scopeType == 4) {
                tasksMapper.insertHtTasksPerformLsHfPj(tasksId);
            } else if (scopeType == 2) {
                tasksMapper.insertHtTasksPerformLsDyPj(tasksId);
            }
        } else {
            // adjustBasis 0 (single strategy) or 1 (return water temp) or others
            if (scopeType == 1 || scopeType == 4) {
                tasksMapper.insertHtTasksPerformLsHfPj(tasksId);
            } else if (scopeType == 2) {
                tasksMapper.insertHtTasksPerformLsDyPj(tasksId);
            }
        }
        log.debug("Step 2 complete: populated temp table for task {} adjustBasis={}", tasksId, adjustBasis);

        // Step 3: Instruction generation (temperature judgment / alert type evaluation)
        executeInstructionGeneration(tasksId, task, strategy, average);
        log.debug("Step 3 complete: instruction generation for task {}", tasksId);

        // Step 4: Update instruction generation (determine next instruction)
        executeUpdateInstructionGeneration(tasksId, task, strategy, average);
        log.debug("Step 4 complete: updated instruction generation for task {}", tasksId);

        // Step 5: Insert to perform tables and cleanup
        executeInsertHtTasksPerform(tasksId, task, strategy, average);
        log.info("Single valve regulation completed for task {}", tasksId);
    }

    /**
     * Step 3: Evaluate temperatures and set alert types on the temp table.
     *
     * Alert types:
     *   0 = pending execution
     *   1 = execution failed (retry)
     *   2-3 = data anomalies
     *   4 = valve angle alarm (cannot adjust further)
     *   5 = temperature alarm (exceeded regulation count)
     *   9 = regulation complete (temperature in range)
     */
    private void executeInstructionGeneration(String tasksId, HtTasks task, HtStrategy strategy, double average) {
        int adjustBasis = task.getAdjustBasis() != null ? task.getAdjustBasis() : 0;

        switch (adjustBasis) {
            case 0:
                // Single strategy: mark as complete when count exceeded
                tasksMapper.setNumber(tasksId);
                break;

            case 1:
                // Return water temperature regulation
                tasksMapper.setOutTempNumberX(tasksId);
                tasksMapper.setOutTempNumberD(tasksId);
                tasksMapper.setOutTempW(tasksId);
                break;

            case 2:
                // Room temperature regulation (legacy, no average)
                tasksMapper.setRoomTempNumberX(tasksId);
                tasksMapper.setRoomTempNumberD(tasksId);
                tasksMapper.setRoomTempW(tasksId);
                break;

            case 3:
            case 4:
                // Average return water temperature regulation
                tasksMapper.setRoomTempNumberXPJ(tasksId, average);
                tasksMapper.setRoomTempNumberDPJ(tasksId, average);
                tasksMapper.setRoomTempWPJ(tasksId, average);
                break;

            default:
                // Default: treat as single strategy
                tasksMapper.setNumber(tasksId);
                break;
        }
    }

    /**
     * Step 4: Determine the next instruction to execute based on alert type and strategy.
     *
     * For adjustBasis 0 (single strategy): cycles through strategy_sub records.
     * For adjustBasis 3/4 (avg return water temp): determines direction (increase/decrease valve angle).
     * For no strategy (strategyId null): uses default up/down logic.
     */
    private void executeUpdateInstructionGeneration(String tasksId, HtTasks task, HtStrategy strategy, double average) {
        // Update exception records (retry failed executions)
        tasksMapper.updateInstructionGenerationyC(tasksId);

        int adjustBasis = task.getAdjustBasis() != null ? task.getAdjustBasis() : 0;
        int currentNumber = task.getNumber() != null ? task.getNumber() : 0;

        if (task.getStrategyId() == null || task.getStrategyId().isEmpty()) {
            // No strategy: default up/down adjustment
            tasksMapper.updateInstructionGenerationD(tasksId);
            tasksMapper.updateInstructionGenerationX(tasksId);
            return;
        }

        switch (adjustBasis) {
            case 0:
                // Single strategy: cycle through strategy_sub
                if (currentNumber > 0) {
                    tasksMapper.updateInstructionGeneration(tasksId);
                } else {
                    tasksMapper.updateInstructionGenerationOne(tasksId);
                }
                break;

            case 3:
                // Average return water temperature regulation
                if (currentNumber > 0) {
                    tasksMapper.updateInstructionGenerationDPJ(tasksId, average);
                    tasksMapper.updateInstructionGenerationXPJ(tasksId, average);
                    tasksMapper.updateInstructionGenerationWPJ(tasksId, average);
                } else {
                    tasksMapper.updateInstructionGenerationOnePJ(tasksId, average);
                }
                break;

            case 4:
                // Return water temp with heating coefficient
                if (currentNumber > 0) {
                    tasksMapper.updateInstructionGenerationDPJ(tasksId, average);
                    tasksMapper.updateInstructionGenerationXPJ(tasksId, average);
                    tasksMapper.updateInstructionGenerationWPJ(tasksId, average);
                } else {
                    tasksMapper.updateInstructionGenerationOnePJ(tasksId, average);
                }
                break;

            default:
                // Default: use D/X pattern
                tasksMapper.updateInstructionGenerationD(tasksId);
                tasksMapper.updateInstructionGenerationX(tasksId);
                break;
        }
    }

    /**
     * Step 5: Finalize regulation by moving records from temp table to execution tables.
     *
     * This also handles:
     * - Boundary checks (max/min valve angle limits)
     * - Inserting into the perform table
     * - Inserting alerts for anomalous records
     * - Updating execution count on the task
     * - Updating scope status
     * - Backing up to perform_last table
     * - Cleaning up temp table
     */
    private void executeInsertHtTasksPerform(String tasksId, HtTasks task, HtStrategy strategy, double average) {
        int adjustBasis = task.getAdjustBasis() != null ? task.getAdjustBasis() : 0;

        // Boundary checks for valve angle limits
        if (task.getStrategyId() == null || task.getStrategyId().isEmpty()) {
            tasksMapper.updateInstructionGenerationDMax(tasksId);
            tasksMapper.updateInstructionGenerationDMin(tasksId);
            tasksMapper.updateInstructionGenerationHPEQUJ(tasksId);
        } else {
            switch (adjustBasis) {
                case 0:
                case 3:
                    tasksMapper.updateInstructionGenerationDPJMax(tasksId, average);
                    tasksMapper.updateInstructionGenerationHPEQUJ(tasksId);
                    break;

                case 4:
                    tasksMapper.updateInstructionGenerationDPJMax(tasksId, average);
                    tasksMapper.updateInstructionGenerationHPEQUJ(tasksId);
                    break;

                default:
                    tasksMapper.updateInstructionGenerationDKzMax(tasksId);
                    tasksMapper.updateInstructionGenerationXKzMin(tasksId);
                    tasksMapper.updateInstructionGenerationHPEQUJ(tasksId);
                    break;
            }
        }

        // Delete status=0 (pending) records from perform table
        tasksMapper.deleteHtTasksPerform0(tasksId);

        // Insert finalized records into perform table
        if (adjustBasis == 3 || adjustBasis == 0 || adjustBasis == 4) {
            tasksMapper.inserHtTasksPerformPj(tasksId);
        } else {
            tasksMapper.inserHtTasksPerform(tasksId);
            tasksMapper.inserHtTasksPerformW(tasksId);
        }

        // Insert alert records for anomalous entries
        tasksMapper.insertHtAlert(tasksId);

        // Update execution count on task
        tasksMapper.updateHtasks(tasksId);

        // Update scope status from temp table alert_type
        tasksMapper.updateHtScope(tasksId);

        // Backup to last table and clean up
        tasksMapper.deleteHtTasksPerformLast(tasksId);
        tasksMapper.inserHtTasksPerformLast(tasksId);
        tasksMapper.deleteTtTasksPerformTemp(tasksId);
    }

    /**
     * Execute DTU broadcast regulation.
     *
     * Delegates to the existing insertHtTasksPerformDtu implementation
     * in HtTasksServiceImpl which handles:
     * - Report rate checking
     * - Strategy perform lookup
     * - Group data creation with temperature averages
     * - Instruction generation for DTU broadcast
     */
    private void executeDtuRegulation(HtTasks task) {
        boolean success = tasksService.insertHtTasksPerformDtu(task);
        if (!success) {
            log.error("DTU regulation failed for task {}, terminating task", task.getId());
            try {
                tasksService.changeStatus(task.getId(), 0);
            } catch (Exception e) {
                log.error("Failed to stop task {} after DTU regulation failure", task.getId(), e);
            }
        }
    }

    /**
     * Stop a task: update status to stopped, remove Quartz job, set end time.
     */
    private void stopTask(Integer taskId, String reason) {
        log.info("Stopping task {}: {}", taskId, reason);

        // Update status and end time
        tasksService.lambdaUpdate()
            .eq(HtTasks::getId, taskId)
            .set(HtTasks::getStatus, 0)
            .set(HtTasks::getEndTime, new java.util.Date())
            .update();

        // Remove Quartz job
        try {
            jobManager.deleteJob(taskId);
        } catch (Exception e) {
            log.warn("Failed to delete Quartz job for task {}: {}", taskId, e.getMessage());
        }

        log.info("Task {} stopped: {}", taskId, reason);
    }
}
