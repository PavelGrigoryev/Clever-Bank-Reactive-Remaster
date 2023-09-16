package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/change")
    public Mono<ResponseEntity<ChangeBalanceResponse>> changeBalance(@RequestBody ChangeBalanceRequest request) {
        return transactionService.changeBalance(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PostMapping("/transfer")
    public Mono<ResponseEntity<TransferBalanceResponse>> transferBalance(@RequestBody TransferBalanceRequest request) {
        return transactionService.transferBalance(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PostMapping("/statement")
    public Mono<ResponseEntity<TransactionStatementResponse>> findAllByPeriodOfDateAndAccountId(@RequestBody TransactionStatementRequest request) {
        return transactionService.findAllByPeriodOfDateAndAccountId(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PostMapping("/amount")
    public Mono<ResponseEntity<AmountStatementResponse>> findSumOfFundsByPeriodOfDateAndAccountId(@RequestBody TransactionStatementRequest request) {
        return transactionService.findSumOfFundsByPeriodOfDateAndAccountId(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionResponse>> findById(@PathVariable Long id) {
        return transactionService.findById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/senders/{id}")
    public Flux<TransactionResponse> findAllBySendersAccountId(@PathVariable String id) {
        return transactionService.findAllBySendersAccountId(id);
    }

    @GetMapping("/recipients/{id}")
    public Flux<TransactionResponse> findAllByRecipientAccountId(@PathVariable String id) {
        return transactionService.findAllByRecipientAccountId(id);
    }

}
