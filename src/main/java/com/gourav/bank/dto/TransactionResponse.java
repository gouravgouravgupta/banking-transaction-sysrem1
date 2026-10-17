package com.gourav.bank.dto;

import com.gourav.bank.model.BankTransaction;
import com.gourav.bank.model.TransactionStatus;
import com.gourav.bank.model.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {
    private final String txnRef;
    private final TransactionType type;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final TransactionStatus status;
    private final String message;
    private final LocalDateTime createdAt;

    public TransactionResponse(BankTransaction t) {
        this.txnRef = t.getTxnRef();
        this.type = t.getType();
        this.fromAccount = t.getFromAccount();
        this.toAccount = t.getToAccount();
        this.amount = t.getAmount();
        this.status = t.getStatus();
        this.message = t.getMessage();
        this.createdAt = t.getCreatedAt();
    }
}
