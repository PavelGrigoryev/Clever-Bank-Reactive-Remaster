package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransactionResponse")
@With
public class TransactionResponseTestBuilder implements TestBuilder<TransactionResponse> {

    private Long id = 1L;
    private LocalDate date = LocalDate.of(2023, Month.SEPTEMBER, 23);
    private LocalTime time = LocalTime.of(13, 34, 2);
    private Type type = Type.REPLENISHMENT;
    private Long bankSenderId = 3L;
    private Long bankRecipientId = 5L;
    private String accountSenderId = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
    private String accountRecipientId = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
    private BigDecimal sumSender = BigDecimal.valueOf(255);
    private BigDecimal sumRecipient = BigDecimal.valueOf(255);

    @Override
    public TransactionResponse build() {
        return new TransactionResponse(id, date, time, type, bankSenderId, bankRecipientId, accountSenderId,
                accountRecipientId, sumSender, sumRecipient);
    }

}
