package com.grigoryev.cleverbankreactiveremaster.repository;

import com.grigoryev.cleverbankreactiveremaster.dto.PageRequest;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> findById(Long id);

    Flux<User> findAll(PageRequest request);

    Mono<User> save(User user);

    Mono<User> update(User user);

    Mono<User> delete(Long id);

}
