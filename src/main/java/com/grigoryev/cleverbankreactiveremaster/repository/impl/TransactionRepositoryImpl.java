package com.grigoryev.cleverbankreactiveremaster.repository.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatement;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.repository.TransactionRepository;
import com.grigoryev.cleverbankreactiveremaster.service.NbRbCurrencyService;
import com.grigoryev.cleverbankreactiveremaster.tables.Account;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Transaction;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.grigoryev.cleverbankreactiveremaster.Tables.ACCOUNT;
import static com.grigoryev.cleverbankreactiveremaster.Tables.TRANSACTION;
import static com.grigoryev.cleverbankreactiveremaster.Tables.USER;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sum;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final DSLContext dslContext;
    private final NbRbCurrencyService nbRbCurrencyService;

    @Override
    public Mono<Transaction> findById(Long id) {
        return Mono.from(dslContext.selectFrom(TRANSACTION)
                        .where(TRANSACTION.ID.eq(id)))
                .map(r -> r.into(Transaction.class));
    }

    @Override
    public Flux<Transaction> findAllBySendersAccountId(String id) {
        return Flux.from(dslContext.selectFrom(TRANSACTION)
                        .where(TRANSACTION.ACCOUNT_SENDER_ID.eq(id)))
                .map(r -> r.into(Transaction.class));
    }

    @Override
    public Flux<Transaction> findAllByRecipientAccountId(String id) {
        return Flux.from(dslContext.selectFrom(TRANSACTION)
                        .where(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id)))
                .map(r -> r.into(Transaction.class));
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return Mono.from(dslContext.insertInto(TRANSACTION)
                        .set(TRANSACTION.DATE, transaction.getDate())
                        .set(TRANSACTION.TIME, transaction.getTime())
                        .set(TRANSACTION.TYPE, transaction.getType())
                        .set(TRANSACTION.BANK_SENDER_ID, transaction.getBankSenderId())
                        .set(TRANSACTION.BANK_RECIPIENT_ID, transaction.getBankRecipientId())
                        .set(TRANSACTION.ACCOUNT_SENDER_ID, transaction.getAccountSenderId())
                        .set(TRANSACTION.ACCOUNT_RECIPIENT_ID, transaction.getAccountRecipientId())
                        .set(TRANSACTION.SUM, transaction.getSum())
                        .returning())
                .map(r -> r.into(Transaction.class));
    }

    @Override
    public Flux<TransactionStatement> findAllByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        Account a = ACCOUNT.as("a");
        Account b = ACCOUNT.as("b");
        return Flux.from(dslContext.select(TRANSACTION.DATE, TRANSACTION.TYPE, USER.LASTNAME, TRANSACTION.SUM)
                        .from(TRANSACTION)
                        .join(a).on(TRANSACTION.ACCOUNT_SENDER_ID.eq(a.ID))
                        .join(b).on(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(b.ID))
                        .join(USER).on(a.USER_ID.eq(USER.ID))
                        .where(TRANSACTION.DATE.between(from).and(to))
                        .and(TRANSACTION.ACCOUNT_SENDER_ID.eq(id).or(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id))))
                .map(r -> r.into(TransactionStatement.class));
    }

    @Override
    public Mono<BigDecimal> findSumOfSpentFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        return Mono.from(dslContext.select(sum(TRANSACTION.SUM).as("spent"))
                        .from(TRANSACTION)
                        .where(TRANSACTION.DATE.between(from).and(to))
                        .and(TRANSACTION.ACCOUNT_SENDER_ID.eq(id)
                                .and(TRANSACTION.TYPE.in(Type.TRANSFER.toString(), Type.EXCHANGE.toString())))
                        .or(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id).and(TRANSACTION.TYPE.eq(Type.WITHDRAWAL.toString()))))
                .map(r -> r.getValue("spent") == null
                        ? BigDecimal.ZERO
                        : r.getValue("spent", BigDecimal.class));
    }

    @Override
    public Mono<BigDecimal> findSumOfReceivedFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id) {
        return Mono.from(dslContext.select(
                        field(select(sum(TRANSACTION.SUM))
                                .from(TRANSACTION)
                                .where(TRANSACTION.DATE.between(from).and(to))
                                .and(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id)
                                        .and(TRANSACTION.TYPE.notIn(Type.WITHDRAWAL.toString(), Type.EXCHANGE.toString()))))
                                .as("sum_without_exchange"),
                        field(select(sum(TRANSACTION.SUM))
                                .from(TRANSACTION)
                                .where(TRANSACTION.DATE.between(from).and(to))
                                .and(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id).and(TRANSACTION.TYPE.eq(Type.EXCHANGE.toString()))))
                                .as("sum_with_exchange"),
                        field(select(ACCOUNT.CURRENCY)
                                .from(ACCOUNT)
                                .join(TRANSACTION).on(ACCOUNT.ID.eq(TRANSACTION.ACCOUNT_SENDER_ID))
                                .where(TRANSACTION.DATE.between(from).and(to))
                                .and(TRANSACTION.ACCOUNT_RECIPIENT_ID.eq(id).and(TRANSACTION.TYPE.eq(Type.EXCHANGE.toString())))
                                .limit(1))
                                .as("curr")))
                .flatMap(r -> {
                    BigDecimal sumWithoutExchange = r.getValue("sum_without_exchange") == null
                            ? BigDecimal.ZERO
                            : r.getValue("sum_without_exchange", BigDecimal.class);
                    BigDecimal sumWithExchange = r.getValue("sum_with_exchange") == null
                            ? BigDecimal.ZERO
                            : r.getValue("sum_with_exchange", BigDecimal.class);
                    return r.getValue("curr") == null
                            ? Mono.just(sumWithExchange.add(sumWithoutExchange))
                            : nbRbCurrencyService.toByn(r.getValue("curr", Currency.class), sumWithExchange)
                            .map(byn -> byn.add(sumWithoutExchange));
                });
    }

}
