package com.grigoryev.cleverbankreactiveremaster.repository;

import com.grigoryev.cleverbankreactiveremaster.dto.PageRequest;
import com.grigoryev.cleverbankreactiveremaster.model.AccountData;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository {

    Mono<AccountData> findById(String id);

    Flux<Account> findAll();

    Flux<AccountData> findAllDatas(PageRequest request);

    Mono<AccountData> save(Account account);

    Mono<AccountData> update(Account account);

    Mono<Account> delete(String id);

}
