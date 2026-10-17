package com.gourav.bank.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transactions", indexes = {
        @Index(name = "idx_txn_from", columnList = "from_account"),
        @Index(name = "idx_txn_to", columnList = "to_account")
})
@Getter @Setter @NoArgsConstructor
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "txn_ref", nullable = false, unique = true, length = 40)
    private String txnRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(name = "from_account", length = 20)
    private String fromAccount;

    @Column(name = "to_account", length = 20)
    private String toAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionStatus status;

    @Column(length = 255)
    private String message;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public BankTransaction(String txnRef, TransactionType type, String from, String to,
                           BigDecimal amount, TransactionStatus status, String message) {
        this.txnRef = txnRef;
        this.type = type;
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
        this.status = status;
        this.message = message;
    }
}
