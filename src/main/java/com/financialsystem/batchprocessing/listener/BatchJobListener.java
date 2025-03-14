package com.financialsystem.batchprocessing.listener;

import com.financialsystem.batchprocessing.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class BatchJobListener implements JobExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(BatchJobListener.class);
    private final  NotificationService notificationService;

    public BatchJobListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job {} starting at {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStartTime());
    }
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        logger.info("Job {} finished with status {}", jobName, jobExecution.getStatus());

        // Get statistics
        long readCount = jobExecution.getStepExecutions().stream()
                .mapToLong(stepExecution -> stepExecution.getReadCount())
                .sum();

        long writeCount = jobExecution.getStepExecutions().stream()
                .mapToLong(stepExecution -> stepExecution.getWriteCount())
                .sum();

        String statistics = "Read Count: " + readCount + "\n" +
                "Write Count: " + writeCount + "\n" +
                "Duration: " + (jobExecution.getEndTime().getMinute() - jobExecution.getStartTime().getMinute()) + " ms";

        // Send notification based on job status
        switch (jobExecution.getStatus()) {
            case COMPLETED:
                notificationService.sendJobCompletionNotification(jobName, statistics);
                break;
            case FAILED:
                String errorMsg = jobExecution.getExitStatus().getExitDescription();
                notificationService.sendErrorNotification(jobName, errorMsg);
                break;
            default:
                logger.warn("Job {} ended with unexpected status: {}", jobName, jobExecution.getStatus());
        }
    }
}
