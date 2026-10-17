package com.gourav.bank.service;

import com.gourav.bank.dao.TransactionDao;
import com.gourav.bank.model.BankTransaction;
import com.gourav.bank.model.TransactionStatus;
import com.gourav.bank.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Writes FAILED transaction records in their own transaction (REQUIRES_NEW), so the audit row
 * survives even though the business transaction is rolled back.
 */
@Service
@RequiredArgsConstructor
public class TransactionLogService {

    private final TransactionDao transactionDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String ref, TransactionType type, String from, String to,
                           BigDecimal amount, String reason) {
        String msg = reason == null ? "Failed" : (reason.length() > 250 ? reason.substring(0, 250) : reason);
        transactionDao.save(new BankTransaction(ref, type, from, to,
                amount == null ? BigDecimal.ZERO : amount, TransactionStatus.FAILED, msg));
    }
}
