package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aChangeBalanceResponse")
@With
public class ChangeBalanceResponseTestBuilder implements TestBuilder<ChangeBalanceResponse> {

    private Long transactionId = 1L;
    private LocalDate date = LocalDate.now();
    private LocalTime time = LocalTime.now();
    private Currency currency = Currency.EUR;
    private Type type = Type.REPLENISHMENT;
    private String bankSenderName = "Альфа-Банк";
    private String bankRecipientName = "Газпромбанк";
    private String accountRecipientId = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
    private BigDecimal sum = BigDecimal.valueOf(200.52);
    private BigDecimal oldBalance = new BigDecimal("2500.0");
    private BigDecimal newBalance = oldBalance.add(sum);

    @Override
    public ChangeBalanceResponse build() {
        return new ChangeBalanceResponse(transactionId, date, time, currency, type, bankSenderName, bankRecipientName,
                accountRecipientId, sum, oldBalance, newBalance);
    }

}
