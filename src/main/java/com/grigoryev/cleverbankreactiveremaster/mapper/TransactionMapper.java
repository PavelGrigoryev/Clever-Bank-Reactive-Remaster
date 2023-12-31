package com.grigoryev.cleverbankreactiveremaster.mapper;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ExchangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatement;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.model.AccountData;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Transaction;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TransactionMapper {

    @Mapping(target = "accountSenderId", source = "request.accountSenderId")
    @Mapping(target = "accountRecipientId", source = "request.accountRecipientId")
    @Mapping(target = "sumSender", source = "request.sum")
    @Mapping(target = "sumRecipient", source = "request.sum")
    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toChangeTransaction(Long bankRecipientId,
                                    Long bankSenderId,
                                    ChangeBalanceRequest request);

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "sum", source = "transaction.sumSender")
    ChangeBalanceResponse toChangeResponse(Transaction transaction,
                                           String bankSenderName,
                                           String bankRecipientName,
                                           Currency currency,
                                           BigDecimal oldBalance,
                                           BigDecimal newBalance);

    @Mapping(target = "sumSender", source = "sum")
    @Mapping(target = "sumRecipient", source = "sum")
    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toTransferTransaction(Type type,
                                      Long bankSenderId,
                                      Long bankRecipientId,
                                      String accountSenderId,
                                      String accountRecipientId,
                                      BigDecimal sum);

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "sum", source = "transaction.sumSender")
    TransferBalanceResponse toTransferResponse(Transaction transaction,
                                               Currency currency,
                                               String bankSenderName,
                                               String bankRecipientName,
                                               BigDecimal senderOldBalance,
                                               BigDecimal senderNewBalance,
                                               BigDecimal recipientOldBalance,
                                               BigDecimal recipientNewBalance);

    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toExchangeTransaction(Type type,
                                      Long bankSenderId,
                                      Long bankRecipientId,
                                      String accountSenderId,
                                      String accountRecipientId,
                                      BigDecimal sumSender,
                                      BigDecimal sumRecipient);

    @Mapping(target = "transactionId", source = "transaction.id")
    ExchangeBalanceResponse toExchangeResponse(Transaction transaction,
                                               Currency currencySender,
                                               Currency currencyRecipient,
                                               String bankSenderName,
                                               String bankRecipientName,
                                               BigDecimal senderOldBalance,
                                               BigDecimal senderNewBalance,
                                               BigDecimal recipientOldBalance,
                                               BigDecimal recipientNewBalance);

    TransactionResponse toResponse(Transaction transaction);

    @Mapping(target = "formationDate", expression = "java(LocalDate.now())")
    @Mapping(target = "formationTime", expression = "java(LocalTime.now())")
    TransactionStatementResponse toStatementResponse(String bankName,
                                                     User user,
                                                     AccountData account,
                                                     TransactionStatementRequest request,
                                                     List<TransactionStatement> transactions);

    @Mapping(target = "formationDate", expression = "java(LocalDate.now())")
    @Mapping(target = "formationTime", expression = "java(LocalTime.now())")
    AmountStatementResponse toAmountResponse(String bankName,
                                             User user,
                                             AccountData account,
                                             TransactionStatementRequest request,
                                             BigDecimal spentFunds,
                                             BigDecimal receivedFunds);

}
