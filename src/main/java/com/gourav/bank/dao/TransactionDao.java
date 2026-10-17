package com.gourav.bank.dao;

import com.gourav.bank.model.BankTransaction;

import java.util.List;

public interface TransactionDao {
    BankTransaction save(BankTransaction txn);
    List<BankTransaction> findByAccount(String accountNumber, int limit);
}
