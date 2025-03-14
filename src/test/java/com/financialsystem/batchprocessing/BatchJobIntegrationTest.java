package com.financialsystem.batchprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBatchTest

public class BatchJobIntegrationTest {

    private final JobLauncherTestUtils jobLauncherTestUtils;

    private final JobLauncher jobLauncher;

    @Qualifier("eodJob")
    private org.springframework.batch.core.Job eodJob;

    private final JdbcTemplate jdbcTemplate;

    public BatchJobIntegrationTest(JobLauncherTestUtils jobLauncherTestUtils, JobLauncher jobLauncher, JdbcTemplate jdbcTemplate) {
        this.jobLauncherTestUtils = jobLauncherTestUtils;
        this.jobLauncher = jobLauncher;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    @Sql({"/schema.sql", "/test-data.sql"})
    public void testEodJob() throws Exception {
        // Setup
        jobLauncherTestUtils.setJob(eodJob);

        // Execute
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Verify
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());

        // Verify database state
        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions WHERE processed = TRUE",
                Integer.class);

        assertEquals(10, count);  // assuming 10 test transactions
    }
}
