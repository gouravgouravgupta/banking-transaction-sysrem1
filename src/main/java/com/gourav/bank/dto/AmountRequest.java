package com.gourav.bank.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AmountRequest {
    @NotBlank private String accountNumber;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
}
