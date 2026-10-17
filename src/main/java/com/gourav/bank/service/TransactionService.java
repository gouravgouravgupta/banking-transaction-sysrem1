package com.gourav.bank.service;

import com.gourav.bank.dao.AccountDao;
import com.gourav.bank.dao.TransactionDao;
import com.gourav.bank.dto.TransactionResponse;
import com.gourav.bank.exception.AccountNotFoundException;
import com.gourav.bank.exception.InsufficientFundsException;
import com.gourav.bank.exception.InvalidTransactionException;
import com.gourav.bank.model.Account;
import com.gourav.bank.model.BankTransaction;
import com.gourav.bank.model.TransactionStatus;
import com.gourav.bank.model.TransactionType;
import com.gourav.bank.util.TransactionReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final TransactionLogService logService;

    private final TransactionReferenceGenerator refs = TransactionReferenceGenerator.getInstance(); // Singleton

    @Transactional
    public TransactionResponse deposit(String accountNumber, BigDecimal amount) {
        String ref = refs.next();
        try {
            BigDecimal amt = validate(amount);
            Account acc = lock(accountNumber);
            acc.setBalance(acc.getBalance().add(amt));
            return success(ref, TransactionType.DEPOSIT, null, accountNumber, amt);
        } catch (RuntimeException ex) {
            logService.logFailure(ref, TransactionType.DEPOSIT, null, accountNumber, amount, ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, BigDecimal amount) {
        String ref = refs.next();
        try {
            BigDecimal amt = validate(amount);
            Account acc = lock(accountNumber);
            if (acc.getBalance().compareTo(amt) < 0) throw new InsufficientFundsException(accountNumber);
            acc.setBalance(acc.getBalance().subtract(amt));
            return success(ref, TransactionType.WITHDRAW, accountNumber, null, amt);
        } catch (RuntimeException ex) {
            logService.logFailure(ref, TransactionType.WITHDRAW, accountNumber, null, amount, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Atomic fund transfer. Both rows are locked in a fixed (alphabetical) order, so two concurrent
     * transfers A->B and B->A can never deadlock each other.
     */
    @Transactional
    public TransactionResponse transfer(String from, String to, BigDecimal amount) {
        String ref = refs.next();
        try {
            BigDecimal amt = validate(amount);
            if (from.equals(to)) throw new InvalidTransactionException("Cannot transfer to the same account");

            String firstKey = from.compareTo(to) < 0 ? from : to;
            String secondKey = firstKey.equals(from) ? to : from;
            Account first = lock(firstKey);
            Account second = lock(secondKey);

            Account src = first.getAccountNumber().equals(from) ? first : second;
            Account dst = (src == first) ? second : first;

            if (src.getBalance().compareTo(amt) < 0) throw new InsufficientFundsException(from);
            src.setBalance(src.getBalance().subtract(amt));
            dst.setBalance(dst.getBalance().add(amt));
            return success(ref, TransactionType.TRANSFER, from, to, amt);
        } catch (RuntimeException ex) {
            logService.logFailure(ref, TransactionType.TRANSFER, from, to, amount, ex.getMessage());
            throw ex;
        }
    }

    // ---- helpers ----
    private Account lock(String accountNumber) {
        return accountDao.findByNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private BigDecimal validate(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private TransactionResponse success(String ref, TransactionType type, String from, String to, BigDecimal amt) {
        BankTransaction t = transactionDao.save(
                new BankTransaction(ref, type, from, to, amt, TransactionStatus.SUCCESS, "OK"));
        return new TransactionResponse(t);
    }
}
