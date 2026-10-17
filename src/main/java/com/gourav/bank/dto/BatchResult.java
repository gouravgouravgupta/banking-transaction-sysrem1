package com.gourav.bank.dto;

import lombok.Getter;

@Getter
public class BatchResult {
    private final TransferRequest request;
    private final boolean success;
    private final String txnRef;
    private final String error;

    private BatchResult(TransferRequest request, boolean success, String txnRef, String error) {
        this.request = request;
        this.success = success;
        this.txnRef = txnRef;
        this.error = error;
    }

    public static BatchResult ok(TransferRequest r, TransactionResponse t) {
        return new BatchResult(r, true, t.getTxnRef(), null);
    }

    public static BatchResult failed(TransferRequest r, String error) {
        return new BatchResult(r, false, null, error);
    }
}
