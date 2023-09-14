package com.grigoryev.cleverbankreactiveremaster.repository.impl;

import com.grigoryev.cleverbankreactiveremaster.model.AccountData;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.repository.AccountRepository;
import com.grigoryev.cleverbankreactiveremaster.util.RandomStringGenerator;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.Account;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.Bank;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.User;
import com.grigoryev.cleverbankrectiveremaster.tables.records.AccountRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.grigoryev.cleverbankrectiveremaster.tables.Account.ACCOUNT;
import static com.grigoryev.cleverbankrectiveremaster.tables.Bank.BANK;
import static com.grigoryev.cleverbankrectiveremaster.tables.User.USER;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final DSLContext dslContext;

    @Override
    public Mono<AccountData> findById(String id) {
        return Mono.from(dslContext.select()
                        .from(ACCOUNT)
                        .join(BANK).on(BANK.ID.eq(ACCOUNT.BANK_ID))
                        .join(USER).on(USER.ID.eq(ACCOUNT.USER_ID))
                        .where(ACCOUNT.ID.eq(id)))
                .map(this::getAccountWithBankAndUser);
    }

    @Override
    public Flux<Account> findAll() {
        return Flux.from(dslContext.selectFrom(ACCOUNT))
                .map(r -> r.into(Account.class));
    }

    @Override
    public Flux<AccountData> findAllDatas() {
        return Flux.from(dslContext.select()
                        .from(ACCOUNT)
                        .join(BANK).on(BANK.ID.eq(ACCOUNT.BANK_ID))
                        .join(USER).on(USER.ID.eq(ACCOUNT.USER_ID)))
                .map(this::getAccountWithBankAndUser);
    }

    @Override
    public Mono<AccountData> save(Account account) {
        return Mono.from(dslContext.insertInto(ACCOUNT)
                        .set(ACCOUNT.ID, RandomStringGenerator.generateRandomString())
                        .set(ACCOUNT.CURRENCY, account.getCurrency())
                        .set(ACCOUNT.BALANCE, account.getBalance())
                        .set(ACCOUNT.OPENING_DATE, account.getOpeningDate())
                        .set(ACCOUNT.CLOSING_DATE, account.getClosingDate())
                        .set(ACCOUNT.BANK_ID, account.getBankId())
                        .set(ACCOUNT.USER_ID, account.getUserId())
                        .onDuplicateKeyUpdate()
                        .set(ACCOUNT.ID, RandomStringGenerator.generateRandomString())
                        .returning())
                .map(AccountRecord::getId)
                .flatMap(this::findById);
    }

    @Override
    public Mono<AccountData> update(Account account) {
        return Mono.from(dslContext.update(ACCOUNT)
                        .set(ACCOUNT.CURRENCY, account.getCurrency())
                        .set(ACCOUNT.BALANCE, account.getBalance())
                        .set(ACCOUNT.OPENING_DATE, account.getOpeningDate())
                        .set(ACCOUNT.CLOSING_DATE, account.getClosingDate())
                        .set(ACCOUNT.BANK_ID, account.getBankId())
                        .set(ACCOUNT.USER_ID, account.getUserId())
                        .where(ACCOUNT.ID.eq(account.getId()))
                        .returning())
                .onErrorComplete()
                .map(AccountRecord::getId)
                .flatMap(this::findById);
    }

    @Override
    public Mono<Account> delete(String id) {
        return Mono.from(dslContext.deleteFrom(ACCOUNT)
                        .where(ACCOUNT.ID.eq(id))
                        .returning())
                .map(r -> r.into(Account.class));
    }

    private AccountData getAccountWithBankAndUser(org.jooq.Record r) {
        return AccountData.builder()
                .id(r.getValue(ACCOUNT.ID))
                .currency(Currency.valueOf(r.getValue(ACCOUNT.CURRENCY)))
                .balance(r.getValue(ACCOUNT.BALANCE))
                .openingDate(r.getValue(ACCOUNT.OPENING_DATE))
                .closingDate(r.getValue(ACCOUNT.CLOSING_DATE))
                .bank(new Bank(r.getValue(BANK.ID),
                        r.getValue(BANK.NAME),
                        r.getValue(BANK.ADDRESS),
                        r.getValue(BANK.PHONE_NUMBER)))
                .user(new User(r.getValue(USER.ID),
                        r.getValue(USER.LASTNAME),
                        r.getValue(USER.FIRSTNAME),
                        r.getValue(USER.SURNAME),
                        r.getValue(USER.REGISTER_DATE),
                        r.getValue(USER.MOBILE_NUMBER)))
                .build();
    }

}
