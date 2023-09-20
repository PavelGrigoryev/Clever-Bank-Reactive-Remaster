package com.grigoryev.cleverbankreactiveremaster.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ExchangeBalanceResponse(Long transactionId,
                                      LocalDate date,

                                      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
                                      LocalTime time,

                                      Currency currencySender,
                                      Currency currencyRecipient,
                                      Type type,
                                      String bankSenderName,
                                      String bankRecipientName,
                                      String accountSenderId,
                                      String accountRecipientId,
                                      BigDecimal sumSender,
                                      BigDecimal sumRecipient,
                                      BigDecimal senderOldBalance,
                                      BigDecimal senderNewBalance,
                                      BigDecimal recipientOldBalance,
                                      BigDecimal recipientNewBalance) {
}
