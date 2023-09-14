package com.grigoryev.cleverbankreactiveremaster.repository;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatement;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepository {

    Mono<Transaction> findById(Long id);

    Flux<Transaction> findAllBySendersAccountId(String id);

    Flux<Transaction> findAllByRecipientAccountId(String id);

    Mono<Transaction> save(Transaction transaction);

    Flux<TransactionStatement> findAllByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

    Mono<BigDecimal> findSumOfSpentFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

    Mono<BigDecimal> findSumOfReceivedFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

}
