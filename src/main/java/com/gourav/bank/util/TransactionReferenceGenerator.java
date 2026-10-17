package com.gourav.bank.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Singleton (initialization-on-demand holder idiom): lazy, thread-safe, no synchronization cost.
 * Generates unique transaction references across all threads.
 */
public final class TransactionReferenceGenerator {

    private final AtomicLong sequence = new AtomicLong();
    private final long startedAt = System.currentTimeMillis();

    private TransactionReferenceGenerator() { }

    private static final class Holder {
        private static final TransactionReferenceGenerator INSTANCE = new TransactionReferenceGenerator();
    }

    public static TransactionReferenceGenerator getInstance() {
        return Holder.INSTANCE;
    }

    public String next() {
        return "TXN-" + startedAt + "-" + sequence.incrementAndGet();
    }
}
