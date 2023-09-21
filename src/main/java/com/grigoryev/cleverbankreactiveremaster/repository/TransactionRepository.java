package com.grigoryev.cleverbankreactiveremaster.repository;

import com.grigoryev.cleverbankreactiveremaster.dto.PageRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatement;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepository {

    Mono<Transaction> findById(Long id);

    Flux<Transaction> findAllBySendersAccountId(String id, PageRequest request);

    Flux<Transaction> findAllByRecipientAccountId(String id, PageRequest request);

    Mono<Transaction> save(Transaction transaction);

    Flux<TransactionStatement> findAllByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

    Mono<BigDecimal> findSumOfSpentFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

    Mono<BigDecimal> findSumOfReceivedFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

}
