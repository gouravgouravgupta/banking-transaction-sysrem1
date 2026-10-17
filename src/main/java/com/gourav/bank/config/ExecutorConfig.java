package com.gourav.bank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ExecutorConfig {

    /** Fixed worker pool used for concurrent transaction processing. Spring calls shutdown() on close. */
    @Bean
    public ExecutorService transactionExecutor(@Value("${bank.batch.threads:8}") int threads) {
        AtomicInteger n = new AtomicInteger();
        return Executors.newFixedThreadPool(threads, r -> {
            Thread t = new Thread(r, "txn-worker-" + n.incrementAndGet());
            t.setDaemon(true);
            return t;
        });
    }
}
