package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.aop.InputLoggable;
import com.grigoryev.cleverbankreactiveremaster.dto.PageRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ExchangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@InputLoggable
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final String className = this.getClass().getName();

    @PostMapping("/change")
    public Mono<ResponseEntity<ChangeBalanceResponse>> changeBalance(@RequestBody @Valid ChangeBalanceRequest request) {
        return transactionService.changeBalance(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .log(className);
    }

    @PostMapping("/transfer")
    public Mono<ResponseEntity<TransferBalanceResponse>> transferBalance(@RequestBody @Valid TransferBalanceRequest request) {
        return transactionService.transferBalance(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .log(className);
    }

    @PostMapping("/exchange")
    public Mono<ResponseEntity<ExchangeBalanceResponse>> exchangeBalance(@RequestBody @Valid TransferBalanceRequest request) {
        return transactionService.exchangeBalance(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .log(className);
    }

    @PostMapping("/statement")
    public Mono<ResponseEntity<TransactionStatementResponse>> findAllByPeriodOfDateAndAccountId(@RequestBody TransactionStatementRequest request) {
        return transactionService.findAllByPeriodOfDateAndAccountId(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .log(className);
    }

    @PostMapping("/amount")
    public Mono<ResponseEntity<AmountStatementResponse>> findSumOfFundsByPeriodOfDateAndAccountId(@RequestBody TransactionStatementRequest request) {
        return transactionService.findSumOfFundsByPeriodOfDateAndAccountId(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .log(className);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionResponse>> findById(@PathVariable @Positive Long id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok)
                .log(className);
    }

    @GetMapping("/senders/{id}")
    public Flux<TransactionResponse> findAllBySendersAccountId(@PathVariable String id, @Valid PageRequest request) {
        return transactionService.findAllBySendersAccountId(id, request)
                .log(className);
    }

    @GetMapping("/recipients/{id}")
    public Flux<TransactionResponse> findAllByRecipientAccountId(@PathVariable String id, @Valid PageRequest request) {
        return transactionService.findAllByRecipientAccountId(id, request)
                .log(className);
    }

}
