package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    public Mono<ServerResponse> findByIdResponse(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return bankService.findByIdResponse(id)
                .flatMap(bankResponse -> ServerResponse.ok().bodyValue(bankResponse));
    }

    public Mono<ServerResponse> findAll() {
        return ServerResponse.ok().body(bankService.findAll(), BankResponse.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<BankRequest> bankRequest = request.bodyToMono(BankRequest.class);
        return bankRequest.flatMap(bankService::save)
                .flatMap(bankResponse -> ServerResponse.status(HttpStatus.CREATED).bodyValue(bankResponse));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<BankRequest> bankRequest = request.bodyToMono(BankRequest.class);
        return bankRequest.flatMap(req -> bankService.update(id, req))
                .flatMap(bankResponse -> ServerResponse.status(HttpStatus.CREATED).bodyValue(bankResponse));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return bankService.delete(id)
                .flatMap(deleteResponse -> ServerResponse.ok().bodyValue(deleteResponse));
    }

}
