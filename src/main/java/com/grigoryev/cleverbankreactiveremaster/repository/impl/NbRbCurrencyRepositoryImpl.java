package com.grigoryev.cleverbankreactiveremaster.repository.impl;

import com.grigoryev.cleverbankreactiveremaster.repository.NbRbCurrencyRepository;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.NbRbCurrency;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.grigoryev.cleverbankreactiveremaster.Tables.NB_RB_CURRENCY;

@Repository
@RequiredArgsConstructor
public class NbRbCurrencyRepositoryImpl implements NbRbCurrencyRepository {

    private final DSLContext dslContext;

    @Override
    public Mono<NbRbCurrency> findByCurrencyIdForLocalDateNow(Integer currencyId) {
        return Mono.from(dslContext.select()
                        .from(NB_RB_CURRENCY)
                        .where(NB_RB_CURRENCY.CURRENCY_ID.eq(currencyId).and(NB_RB_CURRENCY.UPDATE_DATE.eq(LocalDate.now())))
                        .limit(1))
                .map(r -> r.into(NbRbCurrency.class));
    }

    @Override
    public Mono<NbRbCurrency> save(NbRbCurrency bynCurrency) {
        return Mono.from(dslContext.insertInto(NB_RB_CURRENCY)
                        .set(NB_RB_CURRENCY.CURRENCY_ID, bynCurrency.getCurrencyId())
                        .set(NB_RB_CURRENCY.CURRENCY, bynCurrency.getCurrency())
                        .set(NB_RB_CURRENCY.SCALE, bynCurrency.getScale())
                        .set(NB_RB_CURRENCY.RATE, bynCurrency.getRate())
                        .set(NB_RB_CURRENCY.UPDATE_DATE, bynCurrency.getUpdateDate())
                        .returning())
                .map(r -> r.into(NbRbCurrency.class));
    }

}
