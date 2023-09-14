package com.grigoryev.cleverbankreactiveremaster.dto.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;

import java.math.BigDecimal;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AccountRequest(Currency currency,
                             BigDecimal balance,
                             Long bankId,
                             Long userId) {
}
