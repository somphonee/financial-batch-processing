package com.financialsystem.batchprocessing;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class FinancialBatchProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialBatchProcessingApplication.class, args);
	}

}
