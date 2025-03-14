package com.financialsystem.batchprocessing.config;

import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RetryConfig {

    @Bean
    public RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(Exception.class, false);
        retryableExceptions.put(RuntimeException.class, true);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3, retryableExceptions, true);
        return retryPolicy;
    }

    @Bean
    public SkipPolicy skipPolicy() {
        return (t, skipCount) -> {
            if (t instanceof FlatFileParseException && skipCount <= 10) {
                return true;
            }
            return false;
        };
    }

    @Bean
    public Classifier<Throwable, Boolean> exceptionClassifier() {
        return throwable -> {
            if (throwable instanceof RuntimeException) {
                return true;
            }
            return false;
        };
    }

}
