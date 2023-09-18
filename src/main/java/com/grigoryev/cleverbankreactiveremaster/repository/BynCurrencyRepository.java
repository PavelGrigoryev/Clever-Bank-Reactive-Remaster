package com.grigoryev.cleverbankreactiveremaster.repository;

import com.grigoryev.cleverbankreactiveremaster.tables.pojos.BynCurrency;
import reactor.core.publisher.Mono;

public interface BynCurrencyRepository {

    Mono<BynCurrency> findByCurrencyIdForLocalDateNow(Integer currencyId);

    Mono<BynCurrency> save(BynCurrency bynCurrency);

}
