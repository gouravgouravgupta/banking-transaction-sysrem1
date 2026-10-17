package com.gourav.bank.service;

import com.gourav.bank.dto.BatchResult;
import com.gourav.bank.dto.TransactionResponse;
import com.gourav.bank.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Multithreaded processing: each transfer runs on a worker thread, in its own DB transaction
 * (the call goes through the Spring proxy so @Transactional applies per thread).
 */
@Service
@RequiredArgsConstructor
public class BatchTransferService {

    private final TransactionService transactionService;
    private final ExecutorService transactionExecutor;

    public List<BatchResult> process(List<TransferRequest> requests) {
        List<CompletableFuture<BatchResult>> futures = requests.stream()
                .map(r -> CompletableFuture.supplyAsync(() -> {
                    try {
                        TransactionResponse t = transactionService.transfer(
                                r.getFromAccount(), r.getToAccount(), r.getAmount());
                        return BatchResult.ok(r, t);
                    } catch (Exception ex) {
                        return BatchResult.failed(r, ex.getMessage());
                    }
                }, transactionExecutor))
                .collect(Collectors.toList());

        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }
}
