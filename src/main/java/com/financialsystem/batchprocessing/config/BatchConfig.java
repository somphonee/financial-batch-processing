package com.financialsystem.batchprocessing.config;

import com.financialsystem.batchprocessing.model.Transaction;
import com.financialsystem.batchprocessing.processor.EodItemProcessor;
import com.financialsystem.batchprocessing.processor.EomItemProcessor;
import com.financialsystem.batchprocessing.processor.EoyItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    // Reader for transaction data
    @Bean
    public JdbcCursorItemReader<Transaction> transactionReader() {
        return new JdbcCursorItemReaderBuilder<Transaction>()
                .name("transactionReader")
                .dataSource(dataSource)
                .sql("SELECT id, transaction_date, amount, account_id, status FROM transactions WHERE processed = false")
                .rowMapper(new BeanPropertyRowMapper<>(Transaction.class))
                .build();
    }

    // EOD Writer
    @Bean
    public JdbcBatchItemWriter<Transaction> eodWriter() {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .sql("UPDATE transactions SET processed = true, status = :status, processed_date = CURRENT_TIMESTAMP WHERE id = :id")
                .beanMapped()
                .build();
    }

    // EOM Writer
    @Bean
    public JdbcBatchItemWriter<Transaction> eomWriter() {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .sql("INSERT INTO monthly_summary (account_id, transaction_month, total_amount, entry_count) " +
                        "VALUES (:accountId, :transactionMonth, :totalAmount, :entryCount)")
                .beanMapped()
                .build();
    }

    // EOY Writer
    @Bean
    public JdbcBatchItemWriter<Transaction> eoyWriter() {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .sql("INSERT INTO yearly_summary (account_id, transaction_year, total_amount, total_fees) " +
                        "VALUES (:accountId, :transactionYear, :totalAmount, :totalFees)")
                .beanMapped()
                .build();
    }

    // EOD Job
    @Bean
    public Job eodJob(Step eodStep) {
        return new JobBuilder("eodJob", jobRepository)
                .start(eodStep)
                .build();
    }

    // EOM Job
    @Bean
    public Job eomJob(Step eomStep) {
        return new JobBuilder("eomJob", jobRepository)
                .start(eomStep)
                .build();
    }
    // EOY Job
    @Bean
    public Job eoyJob(Step eoyStep) {
        return new JobBuilder("eoyJob", jobRepository)
                .start(eoyStep)
                .build();
    }

    // EOD Step
    @Bean
    public Step eodStep(JdbcCursorItemReader<Transaction> transactionReader,
                        EodItemProcessor eodProcessor,
                        JdbcBatchItemWriter<Transaction> eodWriter) {
        return new StepBuilder("eodStep", jobRepository)
                .<Transaction, Transaction>chunk(100, transactionManager)
                .reader(transactionReader)
                .processor(eodProcessor)
                .writer(eodWriter)
                .build();
    }

    // EOM Step
    @Bean
    public Step eomStep(JdbcCursorItemReader<Transaction> transactionReader,
                        EomItemProcessor eomProcessor,
                        JdbcBatchItemWriter<Transaction> eomWriter) {
        return new StepBuilder("eomStep", jobRepository)
                .<Transaction, Transaction>chunk(100, transactionManager)
                .reader(transactionReader)
                .processor(eomProcessor)
                .writer(eomWriter)
                .build();
    }

    // EOY Step
    @Bean
    public Step eoyStep(JdbcCursorItemReader<Transaction> transactionReader,
                        EoyItemProcessor eoyProcessor,
                        JdbcBatchItemWriter<Transaction> eoyWriter) {
        return new StepBuilder("eoyStep", jobRepository)
                .<Transaction, Transaction>chunk(100, transactionManager)
                .reader(transactionReader)
                .processor(eoyProcessor)
                .writer(eoyWriter)
                .build();
    }



}
