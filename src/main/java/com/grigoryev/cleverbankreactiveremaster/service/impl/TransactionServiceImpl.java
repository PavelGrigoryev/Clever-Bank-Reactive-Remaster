package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.AccountClosedException;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.InsufficientFundsException;
import com.grigoryev.cleverbankreactiveremaster.mapper.AccountMapper;
import com.grigoryev.cleverbankreactiveremaster.mapper.TransactionMapper;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.repository.TransactionRepository;
import com.grigoryev.cleverbankreactiveremaster.service.AccountService;
import com.grigoryev.cleverbankreactiveremaster.service.TransactionService;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final TransactionalOperator operator;

    @Override
    public Mono<ChangeBalanceResponse> changeBalance(ChangeBalanceRequest request) {
        return accountService.findById(request.accountRecipientId())
                .flatMap(accountData -> {
                    BigDecimal oldBalance = accountData.getBalance();
                    if (accountData.getClosingDate() != null) {
                        return Mono.error(
                                new AccountClosedException("Account with ID " + request.accountRecipientId()
                                                           + " is closed since " + accountData.getClosingDate()));
                    } else if (request.type() != Type.REPLENISHMENT && oldBalance.compareTo(request.sum()) < 0) {
                      return   Mono.error(
                                new InsufficientFundsException("Insufficient funds in the account! You want to withdrawal "
                                                               + request.sum() + ", but you have only " + oldBalance));
                    } else {
                        return Mono.just(accountData);
                    }
                })
                .flatMap(accountData -> {
                    BigDecimal newBalance = request.type() == Type.REPLENISHMENT
                            ? accountData.getBalance().add(request.sum())
                            : accountData.getBalance().subtract(request.sum());
                    return accountService.updateBalance(accountMapper.fromAccountData(accountData), newBalance);
                })
                .zipWith(accountService.findById(request.accountSenderId()))
                .flatMap(tuple -> {
                    Transaction transaction = transactionMapper.toChangeTransaction(
                            tuple.getT1().getBank().getId(),
                            tuple.getT2().getBank().getId(),
                            request);
                    return transactionRepository.save(transaction)
                            .map(savedTransaction -> transactionMapper.toChangeResponse(
                                    savedTransaction,
                                    tuple.getT2().getBank().getName(),
                                    tuple.getT1().getBank().getName(),
                                    tuple.getT1().getCurrency(),
                                    request.type() == Type.REPLENISHMENT ? tuple.getT1().getBalance().subtract(request.sum())
                                            : tuple.getT1().getBalance().add(request.sum()),
                                    tuple.getT1().getBalance()));
                })
                .as(operator::transactional);
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
