package com.gourav.bank;

import com.gourav.bank.dto.AccountResponse;
import com.gourav.bank.dto.BatchResult;
import com.gourav.bank.dto.CreateAccountRequest;
import com.gourav.bank.dto.TransferRequest;
import com.gourav.bank.exception.InsufficientFundsException;
import com.gourav.bank.service.AccountService;
import com.gourav.bank.service.BatchTransferService;
import com.gourav.bank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BankingConcurrencyTest {

    @Autowired AccountService accounts;
    @Autowired TransactionService transactions;
    @Autowired BatchTransferService batch;

    private AccountResponse newAccount(String name, String balance) {
        CreateAccountRequest r = new CreateAccountRequest();
        r.setHolderName(name);
        r.setOpeningBalance(new BigDecimal(balance));
        return accounts.create(r);
    }

    @Test
    void concurrentOppositeTransfersKeepTotalBalanceAndDoNotDeadlock() {
        AccountResponse a = newAccount("Alice", "10000.00");
        AccountResponse b = newAccount("Bob", "10000.00");

        List<TransferRequest> reqs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            boolean forward = i % 2 == 0;
            reqs.add(new TransferRequest(
                    forward ? a.getAccountNumber() : b.getAccountNumber(),
                    forward ? b.getAccountNumber() : a.getAccountNumber(),
                    new BigDecimal("10.00")));
        }

        List<BatchResult> results = batch.process(reqs);

        assertTrue(results.stream().allMatch(BatchResult::isSuccess));
        BigDecimal total = accounts.get(a.getAccountNumber()).getBalance()
                .add(accounts.get(b.getAccountNumber()).getBalance());
        assertEquals(0, new BigDecimal("20000.00").compareTo(total));
    }

    @Test
    void insufficientFundsIsRejectedAndLoggedAsFailed() {
        AccountResponse a = newAccount("Carol", "50.00");
        AccountResponse b = newAccount("Dave", "0.00");

        assertThrows(InsufficientFundsException.class,
                () -> transactions.transfer(a.getAccountNumber(), b.getAccountNumber(), new BigDecimal("100.00")));

        assertEquals(0, new BigDecimal("50.00").compareTo(accounts.get(a.getAccountNumber()).getBalance()));
        assertEquals("FAILED", accounts.history(a.getAccountNumber(), 5).get(0).getStatus().name());
    }
}
