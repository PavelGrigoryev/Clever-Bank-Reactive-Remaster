package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aAmountStatementResponse")
@With
public class AmountStatementResponseTestBuilder implements TestBuilder<AmountStatementResponse> {

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
    private BigDecimal spentFunds = new BigDecimal("20");
    private BigDecimal receivedFunds = BigDecimal.ZERO;

    @Override
    public AmountStatementResponse build() {
        return new AmountStatementResponse(bankName, lastname, firstname, surname, accountId, currency, openingDate,
                from, to, formationDate, formationTime, balance, spentFunds, receivedFunds);
    }

}
