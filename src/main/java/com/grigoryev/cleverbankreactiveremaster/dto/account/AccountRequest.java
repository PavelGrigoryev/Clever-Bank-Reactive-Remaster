package com.grigoryev.cleverbankreactiveremaster.dto.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AccountRequest(
        @Pattern(regexp = "BYN|RUB|USD|EUR", message = "Available currencies are: BYN, RUB, USD or EUR")
        String currency,

        @Positive
        BigDecimal balance,

        @Positive
        Long bankId,

        @Positive
        Long userId) {
}
