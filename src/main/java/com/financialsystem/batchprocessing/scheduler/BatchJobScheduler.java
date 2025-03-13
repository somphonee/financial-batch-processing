package com.financialsystem.batchprocessing.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

public class BatchJobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobScheduler.class);


    private final JobLauncher jobLauncher;

    @Qualifier("eodJob")
    private final Job eodJob;

    @Qualifier("eomJob")
    private final Job eomJob;

    @Qualifier("eoyJob")
    private final Job eoyJob;

    public BatchJobScheduler(JobLauncher jobLauncher, Job eodJob, Job eomJob, Job eoyJob) {
        this.jobLauncher = jobLauncher;
        this.eodJob = eodJob;
        this.eomJob = eomJob;
        this.eoyJob = eoyJob;
    }

    // Schedule EOD job to run every day at 11:59 PM
    @Scheduled(cron = "0 59 23 * * ?")
    public void runEodJob() {
        try {
            logger.info("Starting End of Day batch job at {}", LocalDateTime.now());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(eodJob, jobParameters);
            logger.info("End of Day batch job completed successfully");
        } catch (Exception e) {
            logger.error("Error running End of Day batch job", e);
        }
    }

    // Schedule EOM job to run on the last day of each month at 11:59 PM
    @Scheduled(cron = "0 59 23 L * ?")
    public void runEomJob() {
        try {
            logger.info("Starting End of Month batch job at {}", LocalDateTime.now());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(eomJob, jobParameters);
            logger.info("End of Month batch job completed successfully");
        } catch (Exception e) {
            logger.error("Error running End of Month batch job", e);
        }
    }

    // Schedule EOY job to run on December 31st at 11:59 PM
    @Scheduled(cron = "0 59 23 31 12 ?")
    public void runEoyJob() {
        try {
            logger.info("Starting End of Year batch job at {}", LocalDateTime.now());
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(eoyJob, jobParameters);
            logger.info("End of Year batch job completed successfully");
        } catch (Exception e) {
            logger.error("Error running End of Year batch job", e);
        }
    }

}

