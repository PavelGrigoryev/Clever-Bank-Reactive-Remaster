package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ExchangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.service.CheckService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class CheckServiceImpl implements CheckService {

    @Override
    public String createChangeBalanceCheck(ChangeBalanceResponse response) {
        String repeat = "-".repeat(61);
        return """
                %s%s
                | %36s%23s
                | Чек: %52s |
                | %s %46s |
                | Тип транзакции: %41s |
                | Банк отправителя: %39s |
                | Банк получателя: %40s |
                | Счет получателя: %40s |
                | Сумма: %46s %s |
                %s
                """.formatted("\n",
                repeat,
                "Банковский чек", "|",
                response.transactionId(),
                response.date(), response.time().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                response.type().getName(),
                response.bankSenderName(),
                response.bankRecipientName(),
                response.accountRecipientId(),
                response.type() == Type.WITHDRAWAL ? "-" + response.sum() : "" + response.sum(), response.currency(),
                repeat);
    }

    @Override
    public String createTransferBalanceCheck(TransferBalanceResponse response) {
        String repeat = "-".repeat(61);
        return """
                %s%s
                | %36s%23s
                | Чек: %52s |
                | %s %46s |
                | Тип транзакции: %41s |
                | Банк отправителя: %39s |
                | Банк получателя: %40s |
                | Счет отправителя: %39s |
                | Счет получателя: %40s |
                | Сумма: %46s %s |
                %s
                """.formatted("\n",
                repeat,
                "Банковский чек", "|",
                response.transactionId(),
                response.date(), response.time().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                response.type().getName(),
                response.bankSenderName(),
                response.bankRecipientName(),
                response.accountSenderId(),
                response.accountRecipientId(),
                response.sum(), response.currency(),
                repeat);
    }

    @Override
    public String createExchangeBalanceCheck(ExchangeBalanceResponse response) {
        String repeat = "-".repeat(61);
        return """
                %s%s
                | %36s%23s
                | Чек: %52s |
                | %s %46s |
                | Тип транзакции: %41s |
                | Банк отправителя: %39s |
                | Банк получателя: %40s |
                | Счет отправителя: %39s |
                | Счет получателя: %40s |
                | Сумма отправителя: %34s %s |
                | Сумма получателя: %35s %s |
                %s
                """.formatted("\n",
                repeat,
                "Банковский чек", "|",
                response.transactionId(),
                response.date(), response.time().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                response.type().getName(),
                response.bankSenderName(),
                response.bankRecipientName(),
                response.accountSenderId(),
                response.accountRecipientId(),
                response.sumSender(), response.currencySender(),
                response.sumRecipient(), response.currencyRecipient(),
                repeat);
    }

    @Override
    public String createTransactionStatement(TransactionStatementResponse response) {
        String line = "|";
        StringBuilder result = new StringBuilder();
        response.transactions()
                .forEach(transaction -> {
                    String sum = transaction.sumRecipient().toString();
                    if (transaction.type().equals(Type.WITHDRAWAL)) {
                        sum = "-" + transaction.sumRecipient();
                    } else if ((transaction.type().equals(Type.TRANSFER) || transaction.type().equals(Type.EXCHANGE))
                               && transaction.userLastname().equals(response.lastname())) {
                        sum = "-" + transaction.sumSender();
                    }
                    result.append("%s %4s %-15s от %-10s %9s %s %s%s"
                            .formatted(transaction.date(), line, transaction.type().getName(), transaction.userLastname(),
                                    line, sum, response.currency(), "\n"));
                });
        result.deleteCharAt(result.length() - 1);
        return """
                %s%36s
                %38s
                Клиент %25s %s %s %s
                Счет %27s %s
                Валюта %25s %s
                Дата открытия %18s %s
                Период %25s %s - %s
                Дата и время формирования %6s %s,  %s
                Остаток %24s %s %s
                %8s %6s %20s %18s %8s
                %s
                %s
                """.formatted("\n",
                "Выписка",
                response.bankName(),
                line, response.lastname(), response.firstname(), response.surname(),
                line, response.accountId(),
                line, response.currency(),
                line, response.openingDate(),
                line, response.from(), response.to(),
                line, response.formationDate(), response.formationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                line, response.balance(), response.currency(),
                "Дата", line, "Примечание", line, "Сумма",
                "-".repeat(70),
                result);
    }

    @Override
    public String createAmountStatement(AmountStatementResponse response) {
        String line = "|";
        return """
                %s%36s
                %32s
                Клиент %25s %s %s %s
                Счет %27s %s
                Валюта %25s %s
                Дата открытия %18s %s
                Период %25s %s - %s
                Дата и время формирования %6s %s,  %s
                Остаток %24s %s %s
                %20s %6s %8s
                %42s
                %20s %6s %12s
                """.formatted("\n",
                "Выписка по деньгам",
                response.bankName(),
                line, response.lastname(), response.firstname(), response.surname(),
                line, response.accountId(),
                line, response.currency(),
                line, response.openingDate(),
                line, response.from(), response.to(),
                line, response.formationDate(), response.formationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                line, response.balance(), response.currency(),
                "Приход", line, "Уход",
                "-".repeat(32),
                response.receivedFunds(), line,
                response.spentFunds());
    }

}
