package com.gourav.bank.controller;

import com.gourav.bank.dto.AmountRequest;
import com.gourav.bank.dto.BatchResult;
import com.gourav.bank.dto.TransactionResponse;
import com.gourav.bank.dto.TransferRequest;
import com.gourav.bank.service.BatchTransferService;
import com.gourav.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final BatchTransferService batchService;

    @PostMapping("/deposit")
    public TransactionResponse deposit(@Valid @RequestBody AmountRequest r) {
        return transactionService.deposit(r.getAccountNumber(), r.getAmount());
    }

    @PostMapping("/withdraw")
    public TransactionResponse withdraw(@Valid @RequestBody AmountRequest r) {
        return transactionService.withdraw(r.getAccountNumber(), r.getAmount());
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest r) {
        return transactionService.transfer(r.getFromAccount(), r.getToAccount(), r.getAmount());
    }

    @PostMapping("/batch-transfer")
    public List<BatchResult> batch(@RequestBody List<TransferRequest> requests) {
        return batchService.process(requests);
    }
}
