package com.grigoryev.cleverbankreactiveremaster.service;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.PageRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountResponse;
import com.grigoryev.cleverbankreactiveremaster.model.AccountData;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountService {

    Mono<AccountData> findById(String id);

    Mono<AccountResponse> findByIdResponse(String id);

    Flux<Account> findAll();

    Flux<AccountResponse> findAllResponses(PageRequest request);

    Mono<AccountResponse> save(AccountRequest request);

    Mono<AccountData> updateBalance(Account account, BigDecimal balance);

    Mono<AccountResponse> closeAccount(String id);

    Mono<DeleteResponse> delete(String id);

}
