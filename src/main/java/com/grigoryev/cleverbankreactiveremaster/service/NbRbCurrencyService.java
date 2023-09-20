package com.grigoryev.cleverbankreactiveremaster.service;

import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface NbRbCurrencyService {

    Mono<BigDecimal> toByn(Currency currency, BigDecimal sum);

}
