package com.grigoryev.cleverbankreactiveremaster.service;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    Mono<ChangeBalanceResponse> changeBalance(ChangeBalanceRequest request);

    Mono<TransferBalanceResponse> transferBalance(TransferBalanceRequest request);

    Mono<TransactionStatementResponse> findAllByPeriodOfDateAndAccountId(TransactionStatementRequest request);

    Mono<AmountStatementResponse> findSumOfFundsByPeriodOfDateAndAccountId(TransactionStatementRequest request);

    Mono<TransactionResponse> findById(Long id);

    Flux<TransactionResponse> findAllBySendersAccountId(String id);

    Flux<TransactionResponse> findAllByRecipientAccountId(String id);

}
