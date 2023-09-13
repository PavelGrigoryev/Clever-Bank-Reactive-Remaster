package com.grigoryev.cleverbankreactiveremaster.service;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> findById(Long id);

    Mono<UserResponse> findByIdResponse(Long id);

    Flux<UserResponse> findAll();

    Mono<UserResponse> save(UserRequest request);

    Mono<UserResponse> update(Long id, UserRequest request);

    Mono<DeleteResponse> delete(Long id);

}
