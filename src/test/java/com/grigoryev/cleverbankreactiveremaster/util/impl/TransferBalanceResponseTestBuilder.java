package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
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
@NoArgsConstructor(staticName = "aTransferBalanceResponse")
@With
public class TransferBalanceResponseTestBuilder implements TestBuilder<TransferBalanceResponse> {

    private Long transactionId = 1L;
    private LocalDate date = LocalDate.now();
    private LocalTime time = LocalTime.now();
    private Currency currency = Currency.BYN;
    private Type type = Type.TRANSFER;
    private String bankSenderName = "Сбербанк";
    private String bankRecipientName = "ВТБ";
    private String accountSenderId = "6RE0 UZ6A 1I3X YK92 MEUR E5GX 13CW";
    private String accountRecipientId = "0UGT 45HU 37CW ZWMQ JZWK 7GLM ZBOT";
    private BigDecimal sum = new BigDecimal("20");
    private BigDecimal senderOldBalance = new BigDecimal("2555000.0");
    private BigDecimal senderNewBalance = senderOldBalance.subtract(sum);
    private BigDecimal recipientOldBalance = new BigDecimal("130000.0");
    private BigDecimal recipientNewBalance = recipientOldBalance.add(sum);

    @Override
    public TransferBalanceResponse build() {
        return new TransferBalanceResponse(transactionId, date, time, currency, type, bankSenderName, bankRecipientName,
                accountSenderId, accountRecipientId, sum, senderOldBalance, senderNewBalance, recipientOldBalance, recipientNewBalance);
    }

}
