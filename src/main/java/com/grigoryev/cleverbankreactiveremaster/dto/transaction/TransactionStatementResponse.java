package com.grigoryev.cleverbankreactiveremaster.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionStatementResponse(String bankName,
                                           String lastname,
                                           String firstname,
                                           String surname,
                                           String accountId,
                                           Currency currency,
                                           LocalDate openingDate,
                                           LocalDate from,
                                           LocalDate to,
                                           LocalDate formationDate,

                                           @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
                                           LocalTime formationTime,

                                           BigDecimal balance,
                                           List<TransactionStatement> transactions) {
}
