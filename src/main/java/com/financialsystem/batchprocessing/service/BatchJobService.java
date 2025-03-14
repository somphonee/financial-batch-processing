package com.financialsystem.batchprocessing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BatchJobService {
    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    private final JobLauncher jobLauncher;

    @Qualifier("eodJob")
    private final Job eodJob;

    @Qualifier("eomJob")
    private final Job eomJob;

    @Qualifier("eoyJob")
    private final Job eoyJob;

    public BatchJobService(JobLauncher jobLauncher, Job eodJob, Job eomJob, Job eoyJob) {
        this.jobLauncher = jobLauncher;
        this.eodJob = eodJob;
        this.eomJob = eomJob;
        this.eoyJob = eoyJob;
    }

    public void runEodJobManually() {
        try {
            logger.info("Manually triggering EOD job");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("source", "manual")
                    .toJobParameters();

            jobLauncher.run(eodJob, jobParameters);
        } catch (Exception e) {
            logger.error("Error running manual EOD job", e);
            throw new RuntimeException("Failed to run EOD job", e);
        }
    }

    public void runEomJobManually() {
        try {
            logger.info("Manually triggering EOM job");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("source", "manual")
                    .toJobParameters();

            jobLauncher.run(eomJob, jobParameters);
        } catch (Exception e) {
            logger.error("Error running manual EOM job", e);
            throw new RuntimeException("Failed to run EOM job", e);
        }
    }

    public void runEoyJobManually() {
        try {
            logger.info("Manually triggering EOY job");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("source", "manual")
                    .toJobParameters();

            jobLauncher.run(eoyJob, jobParameters);
        } catch (Exception e) {
            logger.error("Error running manual EOY job", e);
            throw new RuntimeException("Failed to run EOY job", e);
        }
    }
}
