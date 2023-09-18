package com.grigoryev.cleverbankreactiveremaster.repository.impl;

import com.grigoryev.cleverbankreactiveremaster.repository.BynCurrencyRepository;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.BynCurrency;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.grigoryev.cleverbankreactiveremaster.Tables.BYN_CURRENCY;

@Repository
@RequiredArgsConstructor
public class BynCurrencyRepositoryImpl implements BynCurrencyRepository {

    private final DSLContext dslContext;

    @Override
    public Mono<BynCurrency> findByCurrencyIdForLocalDateNow(Integer currencyId) {
        return Mono.from(dslContext.select()
                        .from(BYN_CURRENCY)
                        .where(BYN_CURRENCY.CURRENCY_ID.eq(currencyId).and(BYN_CURRENCY.UPDATE_DATE.eq(LocalDate.now())))
                        .limit(1))
                .map(r -> r.into(BynCurrency.class));
    }

    @Override
    public Mono<BynCurrency> save(BynCurrency bynCurrency) {
        return Mono.from(dslContext.insertInto(BYN_CURRENCY)
                        .set(BYN_CURRENCY.CURRENCY_ID, bynCurrency.getCurrencyId())
                        .set(BYN_CURRENCY.CURRENCY, bynCurrency.getCurrency())
                        .set(BYN_CURRENCY.SCALE, bynCurrency.getScale())
                        .set(BYN_CURRENCY.RATE, bynCurrency.getRate())
                        .set(BYN_CURRENCY.UPDATE_DATE, bynCurrency.getUpdateDate())
                        .returning())
                .map(r -> r.into(BynCurrency.class));
    }

}
