package com.financialsystem.batchprocessing;

import com.financialsystem.batchprocessing.processor.EodItemProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import com.financialsystem.batchprocessing.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EodItemProcessorTest {
    private final EodItemProcessor processor;

    public EodItemProcessorTest(EodItemProcessor processor) {
        this.processor = processor;
    }

    @Test
    public void testProcessItem() {
        // Setup
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setAccountId(1001L);
        transaction.setStatus("PENDING");
        transaction.setProcessed(false);

        // Execute
        Transaction processedTransaction = processor.process(transaction);

        // Verify
        assertNotNull(processedTransaction);
        assertTrue(processedTransaction.isProcessed());
        assertEquals("PROCESSED", processedTransaction.getStatus());
    }
}
