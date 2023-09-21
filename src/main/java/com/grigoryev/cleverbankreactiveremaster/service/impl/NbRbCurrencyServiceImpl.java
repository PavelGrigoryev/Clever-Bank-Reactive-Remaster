package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.repository.NbRbCurrencyRepository;
import com.grigoryev.cleverbankreactiveremaster.service.NbRbCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class NbRbCurrencyServiceImpl implements NbRbCurrencyService {

    private final NbRbCurrencyRepository nbRbCurrencyRepository;
    private final TransactionalOperator operator;

    @Override
    public Mono<BigDecimal> exchangeSumByCurrency(Currency currencySender, Currency currencyRecipient, BigDecimal sum) {
        if (currencySender.equals(currencyRecipient)) {
            return Mono.just(sum);
        } else if (currencyRecipient.equals(Currency.BYN)) {
            return nbRbCurrencyRepository.findByCurrencyIdForLocalDateNow(currencySender.getCode())
                    .map(nbRbCurrency -> sum.multiply(nbRbCurrency.getRate())
                            .divide(BigDecimal.valueOf(nbRbCurrency.getScale()), 2, RoundingMode.UP))
                    .as(operator::transactional);
        } else if (currencySender.equals(Currency.BYN)) {
            return nbRbCurrencyRepository.findByCurrencyIdForLocalDateNow(currencyRecipient.getCode())
                    .map(nbRbCurrency -> sum.divide(nbRbCurrency.getRate(), 2, RoundingMode.UP)
                            .multiply(BigDecimal.valueOf(nbRbCurrency.getScale())))
                    .as(operator::transactional);
        } else {
            return nbRbCurrencyRepository.findByCurrencyIdForLocalDateNow(currencySender.getCode())
                    .zipWith(nbRbCurrencyRepository.findByCurrencyIdForLocalDateNow(currencyRecipient.getCode()))
                    .map(tuple -> sum.multiply(tuple.getT1().getRate())
                            .divide(BigDecimal.valueOf(tuple.getT1().getScale()), 2, RoundingMode.UP)
                            .divide(tuple.getT2().getRate(), 2, RoundingMode.UP)
                            .multiply(BigDecimal.valueOf(tuple.getT2().getScale()))
                            .setScale(2, RoundingMode.UP))
                    .as(operator::transactional);
        }
    }

}
