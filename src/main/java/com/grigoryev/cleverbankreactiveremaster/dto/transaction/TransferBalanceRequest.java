package com.grigoryev.cleverbankreactiveremaster.dto.transaction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransferBalanceRequest(String accountSenderId,
                                     String accountRecipientId,

                                     @Positive
                                     BigDecimal sum) {
}
