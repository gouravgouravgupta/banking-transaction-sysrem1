package com.gourav.bank.controller;

import com.gourav.bank.dto.AccountResponse;
import com.gourav.bank.dto.CreateAccountRequest;
import com.gourav.bank.dto.TransactionResponse;
import com.gourav.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return service.create(request);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse get(@PathVariable String accountNumber) {
        return service.get(accountNumber);
    }

    @GetMapping("/{accountNumber}/transactions")
    public List<TransactionResponse> history(@PathVariable String accountNumber,
                                             @RequestParam(defaultValue = "20") int limit) {
        return service.history(accountNumber, Math.min(Math.max(limit, 1), 100));
    }
}
