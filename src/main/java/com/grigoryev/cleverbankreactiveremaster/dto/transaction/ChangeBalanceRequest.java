package com.grigoryev.cleverbankreactiveremaster.dto.transaction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChangeBalanceRequest(String accountSenderId,
                                   String accountRecipientId,

                                   @Positive
                                   @Digits(integer = 10, fraction = 2)
                                   BigDecimal sum,

                                   @Pattern(regexp = "REPLENISHMENT|WITHDRAWAL",
                                           message = "Available types are: REPLENISHMENT or WITHDRAWAL")
                                   String type) {
}
