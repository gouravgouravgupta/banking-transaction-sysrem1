package com.gourav.bank.dto;

import com.gourav.bank.model.Account;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AccountResponse {
    private final String accountNumber;
    private final String holderName;
    private final BigDecimal balance;

    public AccountResponse(Account a) {
        this.accountNumber = a.getAccountNumber();
        this.holderName = a.getHolderName();
        this.balance = a.getBalance();
    }
}
