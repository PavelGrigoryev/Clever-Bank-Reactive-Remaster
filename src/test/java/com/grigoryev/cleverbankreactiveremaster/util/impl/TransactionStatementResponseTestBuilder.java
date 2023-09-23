package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatement;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransactionStatementResponse")
@With
public class TransactionStatementResponseTestBuilder implements TestBuilder<TransactionStatementResponse> {

    private String bankName = "Альфа-Банк";
    private String lastname = "Орлов";
    private String firstname = "Олег";
    private String surname = "Олегович";
    private String accountId = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
    private Currency currency = Currency.EUR;
    private LocalDate openingDate = LocalDate.of(2018, Month.MARCH, 1);
    private LocalDate from = LocalDate.of(2023, Month.SEPTEMBER, 23);
    private LocalDate to = LocalDate.of(2023, Month.SEPTEMBER, 23);
    private LocalDate formationDate = LocalDate.now();
    private LocalTime formationTime = LocalTime.now();
    private BigDecimal balance = new BigDecimal("3500.0");
    private List<TransactionStatement> transactions = List.of(
            new TransactionStatement(LocalDate.of(2023, Month.SEPTEMBER, 23), Type.REPLENISHMENT, lastname,
                    BigDecimal.valueOf(255), BigDecimal.valueOf(255)),
            new TransactionStatement(LocalDate.of(2023, Month.SEPTEMBER, 23), Type.WITHDRAWAL, lastname,
                    BigDecimal.valueOf(255), BigDecimal.valueOf(255)),
            new TransactionStatement(LocalDate.of(2023, Month.SEPTEMBER, 23), Type.TRANSFER, lastname,
                    BigDecimal.valueOf(20), BigDecimal.valueOf(20)));

    @Override
    public TransactionStatementResponse build() {
        return new TransactionStatementResponse(bankName, lastname, firstname, surname, accountId, currency, openingDate,
                from, to, formationDate, formationTime, balance, transactions);
    }

}
