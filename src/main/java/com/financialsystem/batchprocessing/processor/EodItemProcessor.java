package com.financialsystem.batchprocessing.processor;

import com.financialsystem.batchprocessing.model.Transaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EodItemProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Logger logger = LoggerFactory.getLogger(EodItemProcessor.class);

    @Override
    public Transaction process(Transaction transaction) {
        logger.info("Processing EOD transaction: {}", transaction.getId());

        // Perform EOD validation and processing
        transaction.setProcessed(true);
        transaction.setStatus("PROCESSED");

        // Additional business logic for EOD processing
        // - Validate transaction integrity
        // - Apply daily fees or interests
        // - Check for overdrafts

        logger.info("EOD processing completed for transaction: {}", transaction.getId());
        return transaction;
    }
}
