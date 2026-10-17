package com.gourav.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransferRequest {
    @NotBlank private String fromAccount;
    @NotBlank private String toAccount;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
}
