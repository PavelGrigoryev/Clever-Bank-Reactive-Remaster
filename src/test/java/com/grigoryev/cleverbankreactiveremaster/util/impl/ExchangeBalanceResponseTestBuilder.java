package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ExchangeBalanceResponse;
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
@NoArgsConstructor(staticName = "aExchangeBalanceResponse")
@With
public class ExchangeBalanceResponseTestBuilder implements TestBuilder<ExchangeBalanceResponse> {

    private Long transactionId = 1L;
    private LocalDate date = LocalDate.now();
    private LocalTime time = LocalTime.now();
    private Currency currencySender = Currency.RUB;
    private Currency currencyRecipient = Currency.EUR;
    private Type type = Type.EXCHANGE;
    private String bankSenderName = "Сбербанк";
    private String bankRecipientName = "Газпромбанк";
    private String accountSenderId = "QR2Q PA57 LB3E LHT3 HCZ2 V4MV XL6M";
    private String accountRecipientId = "19CM 9B6S FFF7 0N1Y M8UY AXCE RMJV";
    private BigDecimal sumSender = new BigDecimal("3000");
    private BigDecimal sumRecipient = new BigDecimal("29.18");
    private BigDecimal senderOldBalance = new BigDecimal("100000.0");
    private BigDecimal senderNewBalance = senderOldBalance.subtract(sumSender);
    private BigDecimal recipientOldBalance = new BigDecimal("5500.0");
    private BigDecimal recipientNewBalance = recipientOldBalance.add(sumRecipient);

    @Override
    public ExchangeBalanceResponse build() {
        return new ExchangeBalanceResponse(transactionId, date, time, currencySender, currencyRecipient, type,
                bankSenderName, bankRecipientName, accountSenderId, accountRecipientId, sumSender,sumRecipient,
                senderOldBalance, senderNewBalance, recipientOldBalance, recipientNewBalance);
    }

}
