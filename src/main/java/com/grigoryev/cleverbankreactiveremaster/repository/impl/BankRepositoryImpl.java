package com.grigoryev.cleverbankreactiveremaster.repository.impl;

import com.grigoryev.cleverbankreactiveremaster.repository.BankRepository;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Bank;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.grigoryev.cleverbankreactiveremaster.Tables.ACCOUNT;
import static com.grigoryev.cleverbankreactiveremaster.Tables.BANK;

@Repository
@RequiredArgsConstructor
public class BankRepositoryImpl implements BankRepository {

    private final DSLContext dslContext;

    @Override
    public Mono<Bank> findById(Long id) {
        return Mono.from(dslContext.selectFrom(BANK)
                        .where(BANK.ID.eq(id)))
                .map(r -> r.into(Bank.class));
    }

    @Override
    public Flux<Bank> findAll() {
        return Flux.from(dslContext.selectFrom(BANK))
                .map(r -> r.into(Bank.class));
    }

    @Override
    public Mono<Bank> save(Bank bank) {
        return Mono.from(dslContext.insertInto(BANK)
                        .set(BANK.NAME, bank.getName())
                        .set(BANK.ADDRESS, bank.getAddress())
                        .set(BANK.PHONE_NUMBER, bank.getPhoneNumber())
                        .onDuplicateKeyIgnore()
                        .returning())
                .map(r -> r.into(Bank.class));
    }

    @Override
    public Mono<Bank> update(Bank bank) {
        return Mono.from(dslContext.update(BANK)
                        .set(BANK.NAME, bank.getName())
                        .set(BANK.ADDRESS, bank.getAddress())
                        .set(BANK.PHONE_NUMBER, bank.getPhoneNumber())
                        .where(BANK.ID.eq(bank.getId()))
                        .returning())
                .onErrorComplete()
                .map(r -> r.into(Bank.class));
    }

    @Override
    public Mono<Bank> delete(Long id) {
        deleteAllBankAccounts(id);
        return Mono.from(dslContext.deleteFrom(BANK)
                        .where(BANK.ID.eq(id))
                        .returning())
                .map(r -> r.into(Bank.class));
    }

    private void deleteAllBankAccounts(Long bankId) {
        Mono.from(dslContext.deleteFrom(ACCOUNT)
                        .where(ACCOUNT.BANK_ID.eq(bankId)))
                .subscribe();
    }

}
