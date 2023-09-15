package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public Mono<ResponseEntity<ChangeBalanceResponse>> changeBalance(@RequestBody ChangeBalanceRequest request) {
        return transactionService.changeBalance(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

}
