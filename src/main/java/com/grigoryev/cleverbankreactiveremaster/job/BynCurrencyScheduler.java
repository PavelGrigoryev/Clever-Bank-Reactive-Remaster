package com.grigoryev.cleverbankreactiveremaster.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grigoryev.cleverbankreactiveremaster.exception.internalservererror.JsonParseException;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.repository.BynCurrencyRepository;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.BynCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public final class BynCurrencyScheduler {

    private final BynCurrencyRepository bynCurrencyRepository;
    private final TransactionalOperator operator;
    private final ObjectMapper objectMapper;

    @Value("${BYNScheduler.url}")
    private String url;

    @Scheduled(cron = "${BYNScheduler.cron}")
    private void trackCurrency() {
        Currency[] currencies = {Currency.RUB, Currency.USD, Currency.EUR};
        WebClient webClient = WebClient.create();
        for (Currency currency : currencies) {
            webClient.get()
                    .uri(url + currency.getCode())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .<JsonNode>handle((s, sink) -> {
                        try {
                            sink.next(objectMapper.readTree(s));
                        } catch (JsonProcessingException e) {
                            sink.error(new JsonParseException(e.getMessage()));
                        }
                    })
                    .map(this::getBynCurrencyFromJsonNode)
                    .flatMap(bynCurrencyRepository::save)
                    .as(operator::transactional)
                    .log()
                    .subscribe();

        }
    }

    private BynCurrency getBynCurrencyFromJsonNode(JsonNode jsonNode) {
        int currencyId = jsonNode.at("/Cur_ID").asInt();
        String currencyName = jsonNode.at("/Cur_Abbreviation").asText();
        int scale = jsonNode.at("/Cur_Scale").asInt();
        BigDecimal rate = new BigDecimal(jsonNode.at("/Cur_OfficialRate").asText());
        BynCurrency bynCurrency = new BynCurrency();
        bynCurrency.setCurrencyId(currencyId);
        bynCurrency.setCurrency(currencyName);
        bynCurrency.setScale(scale);
        bynCurrency.setRate(rate);
        bynCurrency.setUpdateDate(LocalDate.now());
        return bynCurrency;
    }

}
