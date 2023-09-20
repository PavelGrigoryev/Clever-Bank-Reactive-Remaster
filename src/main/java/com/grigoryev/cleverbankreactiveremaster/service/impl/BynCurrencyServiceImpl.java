package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.repository.BynCurrencyRepository;
import com.grigoryev.cleverbankreactiveremaster.service.BynCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class BynCurrencyServiceImpl implements BynCurrencyService {

    private final BynCurrencyRepository bynCurrencyRepository;
    private final TransactionalOperator operator;

    @Override
    public Mono<BigDecimal> toByn(Currency currency, BigDecimal sum) {
        return bynCurrencyRepository.findByCurrencyIdForLocalDateNow(currency.getCode())
                .map(bynCurrency -> sum.multiply(bynCurrency.getRate())
                        .divide(BigDecimal.valueOf(bynCurrency.getScale()), 2, RoundingMode.DOWN))
                .as(operator::transactional);
    }

}
