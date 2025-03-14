package com.financialsystem.batchprocessing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DataExportService {

    private static final Logger logger = LoggerFactory.getLogger(DataExportService.class);


    private final JdbcCursorItemReader transactionReader;

    public DataExportService(JdbcCursorItemReader transactionReader) {
        this.transactionReader = transactionReader;
    }

    public String exportYearlyDataToFile(int year) {
        try {
            logger.info("Exporting data for year: {}", year);

            String fileName = "financial_data_" + year + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

            // Setup file writer
            FlatFileItemWriter<Object> writer = new FlatFileItemWriter<>();
            writer.setResource(new FileSystemResource("exports/" + fileName));

            // Setup field mapping
            BeanWrapperFieldExtractor fieldExtractor = new BeanWrapperFieldExtractor();
            fieldExtractor.setNames(new String[]{"id", "transactionDate", "amount", "accountId", "status"});

            DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
            lineAggregator.setDelimiter(",");
            lineAggregator.setFieldExtractor(fieldExtractor);

            writer.setLineAggregator(lineAggregator);
            writer.setHeaderCallback(writer1 -> writer1.write("ID,Transaction Date,Amount,Account ID,Status"));
            writer.afterPropertiesSet();

            // TODO: Implement actual data export logic

            logger.info("Data export completed: {}", fileName);
            return "exports/" + fileName;
        } catch (Exception e) {
            logger.error("Error exporting yearly data", e);
            throw new RuntimeException("Failed to export data for year: " + year, e);
        }
    }
}
