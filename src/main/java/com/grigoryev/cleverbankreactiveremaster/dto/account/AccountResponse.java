package com.grigoryev.cleverbankreactiveremaster.dto.account;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AccountResponse(String id,
                              Currency currency,
                              BigDecimal balance,
                              LocalDate openingDate,
                              LocalDate closingDate,
                              BankResponse bank,
                              UserResponse user) {
}
