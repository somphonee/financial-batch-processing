package com.financialsystem.batchprocessing.processor;

import com.financialsystem.batchprocessing.model.Transaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EoyItemProcessor  implements ItemProcessor<Transaction, Transaction> {

    private static final Logger logger = LoggerFactory.getLogger(EoyItemProcessor.class);

    @Override
    public Transaction process(Transaction transaction) {
        logger.info("Processing EOY transaction: {}", transaction.getId());

        // EOY specific processing
        // - Generate yearly reports
        // - Calculate tax information
        // - Archive old transactions
        // - Reset yearly counters

        logger.info("EOY processing completed for transaction: {}", transaction.getId());
        return transaction;
    }
}