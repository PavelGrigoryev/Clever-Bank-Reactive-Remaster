package com.grigoryev.cleverbankreactiveremaster.dto.bank;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BankResponse(Long id,
                           String name,
                           String address,
                           String phoneNumber) {
}
