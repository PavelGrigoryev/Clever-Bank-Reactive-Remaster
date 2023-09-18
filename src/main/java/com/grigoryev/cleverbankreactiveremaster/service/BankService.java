package com.grigoryev.cleverbankreactiveremaster.service;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Bank;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankService {

    Mono<Bank> findById(Long id);

    Mono<BankResponse> findByIdResponse(Long id);

    Flux<BankResponse> findAll();

    Mono<BankResponse> save(BankRequest request);

    Mono<BankResponse> update(Long id, BankRequest request);

    Mono<DeleteResponse> delete(Long id);

}
