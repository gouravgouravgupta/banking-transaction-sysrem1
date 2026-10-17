package com.gourav.bank.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotBlank private String holderName;
    @NotNull @PositiveOrZero private BigDecimal openingBalance;
}
