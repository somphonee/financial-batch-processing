package com.financialsystem.batchprocessing.config;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BatchMonitoringConfig {
    private final MeterRegistry meterRegistry;

    private final JobRepository jobRepository;

    public BatchMonitoringConfig(MeterRegistry meterRegistry, JobRepository jobRepository) {
        this.meterRegistry = meterRegistry;
        this.jobRepository = jobRepository;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("BatchJob-");
        executor.initialize();
        return executor;
    }

    @Bean
    public JobLauncher asyncJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher(); // Use TaskExecutorJobLauncher
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(taskExecutor());
        jobLauncher.afterPropertiesSet(); // Initialize the launcher
        return jobLauncher;
    }

    @Bean
    public JobExecutionListener metricsListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                meterRegistry.counter("batch.job.start",
                        "jobName", jobExecution.getJobInstance().getJobName()).increment();
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                // Increment job finish counter
                meterRegistry.counter("batch.job.finish",
                        Tags.of(
                                "jobName", jobExecution.getJobInstance().getJobName(),
                                "status", jobExecution.getStatus().toString()
                        )
                ).increment();

                // Record execution time
                long executionTime = jobExecution.getEndTime().getMinute() - jobExecution.getStartTime().getMinute();
                meterRegistry.timer("batch.job.duration",
                        Tags.of("jobName", jobExecution.getJobInstance().getJobName())
                ).record(executionTime, java.util.concurrent.TimeUnit.MILLISECONDS);

                // Record read/write counts for each step
                jobExecution.getStepExecutions().forEach(stepExecution -> {
                    // Gauge for read count
                    meterRegistry.gauge("batch.step.readCount",
                            Tags.of("stepName", stepExecution.getStepName()),
                            stepExecution,
                            StepExecution::getReadCount
                    );

                    // Gauge for write count
                    meterRegistry.gauge("batch.step.writeCount",
                            Tags.of("stepName", stepExecution.getStepName()),
                            stepExecution,
                            StepExecution::getWriteCount
                    );
                });
            }
        };
    }
}