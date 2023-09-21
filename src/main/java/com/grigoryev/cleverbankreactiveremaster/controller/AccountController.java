package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.PageRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountResponse;
import com.grigoryev.cleverbankreactiveremaster.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountResponse>> findByIdResponse(@PathVariable String id) {
        return accountService.findByIdResponse(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<AccountResponse> findAllResponses(@Valid PageRequest request) {
        return accountService.findAllResponses(request);
    }

    @PostMapping
    public Mono<ResponseEntity<AccountResponse>> save(@RequestBody @Valid AccountRequest request) {
        return accountService.save(request)
                .map(userResponse -> ResponseEntity.status(HttpStatus.CREATED).body(userResponse));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AccountResponse>> closeAccount(@PathVariable String id) {
        return accountService.closeAccount(id)
                .map(accountResponse -> ResponseEntity.status(HttpStatus.CREATED).body(accountResponse));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<DeleteResponse>> delete(@PathVariable String id) {
        return accountService.delete(id)
                .map(ResponseEntity::ok);
    }

}
