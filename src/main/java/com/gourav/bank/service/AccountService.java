package com.gourav.bank.service;

import com.gourav.bank.dao.AccountDao;
import com.gourav.bank.dto.AccountResponse;
import com.gourav.bank.dto.CreateAccountRequest;
import com.gourav.bank.dto.TransactionResponse;
import com.gourav.bank.dao.TransactionDao;
import com.gourav.bank.exception.AccountNotFoundException;
import com.gourav.bank.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    @Transactional
    public AccountResponse create(CreateAccountRequest req) {
        String number;
        do {
            number = "AC" + (1_000_000_000L + ThreadLocalRandom.current().nextLong(9_000_000_000L));
        } while (accountDao.existsByNumber(number));
        Account a = accountDao.save(new Account(number, req.getHolderName(),
                req.getOpeningBalance().setScale(2, java.math.RoundingMode.HALF_UP)));
        return new AccountResponse(a);
    }

    @Transactional(readOnly = true)
    public AccountResponse get(String accountNumber) {
        return accountDao.findByNumber(accountNumber)
                .map(AccountResponse::new)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> history(String accountNumber, int limit) {
        if (!accountDao.existsByNumber(accountNumber)) throw new AccountNotFoundException(accountNumber);
        return transactionDao.findByAccount(accountNumber, limit).stream()
                .map(TransactionResponse::new).collect(Collectors.toList());
    }
}
