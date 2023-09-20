package com.grigoryev.cleverbankreactiveremaster.repository;

import com.grigoryev.cleverbankreactiveremaster.tables.pojos.NbRbCurrency;
import reactor.core.publisher.Mono;

public interface NbRbCurrencyRepository {

    Mono<NbRbCurrency> findByCurrencyIdForLocalDateNow(Integer currencyId);

    Mono<NbRbCurrency> save(NbRbCurrency bynCurrency);

}
