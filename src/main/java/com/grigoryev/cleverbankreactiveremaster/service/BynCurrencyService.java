package com.grigoryev.cleverbankreactiveremaster.service;

import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface BynCurrencyService {

    Mono<BigDecimal> toByn(Currency currency, BigDecimal sum);

}
