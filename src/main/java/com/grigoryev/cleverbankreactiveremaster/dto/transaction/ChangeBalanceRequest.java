package com.grigoryev.cleverbankreactiveremaster.dto.transaction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.grigoryev.cleverbankreactiveremaster.model.Type;

import java.math.BigDecimal;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChangeBalanceRequest(String accountSenderId,
                                   String accountRecipientId,
                                   BigDecimal sum,
                                   Type type) {
}
