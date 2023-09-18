package com.grigoryev.cleverbankreactiveremaster.repository;

import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Bank;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankRepository {

    Mono<Bank> findById(Long id);

    Flux<Bank> findAll();

    Mono<Bank> save(Bank bank);

    Mono<Bank> update(Bank bank);

    Mono<Bank> delete(Long id);

}
