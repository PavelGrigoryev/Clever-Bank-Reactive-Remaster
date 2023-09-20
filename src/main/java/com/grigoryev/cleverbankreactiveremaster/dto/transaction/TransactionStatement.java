package com.grigoryev.cleverbankreactiveremaster.dto.transaction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.grigoryev.cleverbankreactiveremaster.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionStatement(LocalDate date,
                                   Type type,
                                   String userLastname,
                                   BigDecimal sumSender,
                                   BigDecimal sumRecipient) {
}
