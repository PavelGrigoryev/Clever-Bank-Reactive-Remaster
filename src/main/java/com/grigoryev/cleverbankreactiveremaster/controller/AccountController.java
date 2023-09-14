package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountResponse;
import com.grigoryev.cleverbankreactiveremaster.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    public Mono<ServerResponse> findByIdResponse(ServerRequest request) {
        String id = request.pathVariable("id");
        return accountService.findByIdResponse(id)
                .flatMap(accountResponse -> ServerResponse.ok().bodyValue(accountResponse));
    }

    public Mono<ServerResponse> findAllResponses() {
        return ServerResponse.ok().body(accountService.findAllResponses(), AccountResponse.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<AccountRequest> accountRequest = request.bodyToMono(AccountRequest.class);
        return accountRequest.flatMap(accountService::save)
                .flatMap(accountResponse -> ServerResponse.status(HttpStatus.CREATED).bodyValue(accountResponse));
    }

    public Mono<ServerResponse> closeAccount(ServerRequest request) {
        String id = request.pathVariable("id");
        return accountService.closeAccount(id)
                .flatMap(accountResponse -> ServerResponse.status(HttpStatus.CREATED).bodyValue(accountResponse));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return accountService.delete(id)
                .flatMap(deleteResponse -> ServerResponse.ok().bodyValue(deleteResponse));
    }

}
