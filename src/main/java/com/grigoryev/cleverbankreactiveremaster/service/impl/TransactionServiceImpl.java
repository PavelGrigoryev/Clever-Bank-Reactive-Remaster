package com.grigoryev.cleverbankreactiveremaster.service.impl;

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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @Override
    public Mono<ChangeBalanceResponse> changeBalance(ChangeBalanceRequest request) {
        return null;
    }

    @Override
    public Mono<TransferBalanceResponse> transferBalance(TransferBalanceRequest request) {
        return null;
    }

    @Override
    public Mono<TransactionStatementResponse> findAllByPeriodOfDateAndAccountId(TransactionStatementRequest request) {
        return null;
    }

    @Override
    public Mono<AmountStatementResponse> findSumOfFundsByPeriodOfDateAndAccountId(TransactionStatementRequest request) {
        return null;
    }

    @Override
    public Mono<TransactionResponse> findById(Long id) {
        return null;
    }

    @Override
    public Flux<TransactionResponse> findAllBySendersAccountId(String id) {
        return null;
    }

    @Override
    public Flux<TransactionResponse> findAllByRecipientAccountId(String id) {
        return null;
    }

}
