package com.gourav.bank.dao;

import com.gourav.bank.model.Account;

import java.util.Optional;

public interface AccountDao {
    Account save(Account account);
    boolean existsByNumber(String accountNumber);
    Optional<Account> findByNumber(String accountNumber);

    /** SELECT ... FOR UPDATE - blocks other writers until the current transaction ends. */
    Optional<Account> findByNumberForUpdate(String accountNumber);
}
