package com.grigoryev.cleverbankreactiveremaster.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record TransactionStatementRequest(LocalDate from,
                                          LocalDate to,

                                          @JsonProperty("account_id")
                                          String accountId) {
}
