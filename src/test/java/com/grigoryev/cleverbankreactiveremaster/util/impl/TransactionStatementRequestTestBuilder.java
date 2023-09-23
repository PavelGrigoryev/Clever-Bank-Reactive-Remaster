package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransactionStatementRequest")
@With
public class TransactionStatementRequestTestBuilder implements TestBuilder<TransactionStatementRequest> {

    private LocalDate from = LocalDate.of(2023, Month.SEPTEMBER, 23);
    private LocalDate to = LocalDate.of(2023, Month.SEPTEMBER, 23);
    private String accountId = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";

    @Override
    public TransactionStatementRequest build() {
        return new TransactionStatementRequest(from, to, accountId);
    }

}
