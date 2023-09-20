package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.transaction.AmountStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ExchangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.ChangeBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransactionStatementResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.transaction.TransferBalanceResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.AccountClosedException;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.BadCurrencyException;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.InsufficientFundsException;
import com.grigoryev.cleverbankreactiveremaster.exception.internalservererror.TransactionException;
import com.grigoryev.cleverbankreactiveremaster.exception.notfound.TransactionNotFoundException;
import com.grigoryev.cleverbankreactiveremaster.mapper.AccountMapper;
import com.grigoryev.cleverbankreactiveremaster.mapper.TransactionMapper;
import com.grigoryev.cleverbankreactiveremaster.model.AccountData;
import com.grigoryev.cleverbankreactiveremaster.model.Currency;
import com.grigoryev.cleverbankreactiveremaster.model.Type;
import com.grigoryev.cleverbankreactiveremaster.repository.TransactionRepository;
import com.grigoryev.cleverbankreactiveremaster.service.AccountService;
import com.grigoryev.cleverbankreactiveremaster.service.CheckService;
import com.grigoryev.cleverbankreactiveremaster.service.NbRbCurrencyService;
import com.grigoryev.cleverbankreactiveremaster.service.TransactionService;
import com.grigoryev.cleverbankreactiveremaster.service.UploadFileService;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final TransactionalOperator operator;
    private final CheckService checkService;
    private final UploadFileService uploadFileService;
    private final NbRbCurrencyService nbRbCurrencyService;

    @Override
    public Mono<ChangeBalanceResponse> changeBalance(ChangeBalanceRequest request) {
        return accountService.findById(request.accountRecipientId())
                .doOnNext(this::validateAccountForClosingDate)
                .doOnNext(accountData -> validateAccountForSufficientBalance(accountData, Type.valueOf(request.type()), request.sum()))
                .flatMap(accountData -> {
                    BigDecimal newBalance = Type.valueOf(request.type()) == Type.REPLENISHMENT
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
                                    Type.valueOf(request.type()) == Type.REPLENISHMENT
                                            ? tuple.getT1().getBalance().subtract(request.sum())
                                            : tuple.getT1().getBalance().add(request.sum()),
                                    tuple.getT1().getBalance()));
                })
                .doOnNext(response -> uploadFileService.uploadCheck(checkService.createChangeBalanceCheck(response)))
                .as(operator::transactional);
    }

    @Override
    public Mono<TransferBalanceResponse> transferBalance(TransferBalanceRequest request) {
        return accountService.findById(request.accountSenderId())
                .doOnNext(this::validateAccountForClosingDate)
                .doOnNext(accountData -> validateAccountForSufficientBalance(accountData, Type.TRANSFER, request.sum()))
                .zipWith(accountService.findById(request.accountRecipientId())
                        .doOnNext(this::validateAccountForClosingDate))
                .doOnNext(tuple -> validateAccountsForCurrencyEquality(tuple.getT1().getCurrency(), tuple.getT2().getCurrency()))
                .flatMap(tuple -> accountService.updateBalance(accountMapper
                                .fromAccountData(tuple.getT1()), tuple.getT1().getBalance().subtract(request.sum()))
                        .zipWith(accountService.updateBalance(accountMapper
                                .fromAccountData(tuple.getT2()), tuple.getT2().getBalance().add(request.sum()))))
                .flatMap(tuple -> {
                    Transaction transaction = transactionMapper.toTransferTransaction(
                            Type.TRANSFER,
                            tuple.getT1().getBank().getId(),
                            tuple.getT2().getBank().getId(),
                            tuple.getT1().getId(),
                            tuple.getT2().getId(),
                            request.sum());
                    return transactionRepository.save(transaction)
                            .map(savedTransaction -> transactionMapper.toTransferResponse(
                                    savedTransaction,
                                    tuple.getT1().getCurrency(),
                                    tuple.getT1().getBank().getName(),
                                    tuple.getT2().getBank().getName(),
                                    tuple.getT1().getBalance().add(request.sum()),
                                    tuple.getT1().getBalance(),
                                    tuple.getT2().getBalance().subtract(request.sum()),
                                    tuple.getT2().getBalance()));
                })
                .doOnNext(response -> uploadFileService.uploadCheck(checkService.createTransferBalanceCheck(response)))
                .as(operator::transactional);
    }

    @Override
    public Mono<ExchangeBalanceResponse> exchangeBalance(TransferBalanceRequest request) {
        AtomicReference<BigDecimal> exchangedSum = new AtomicReference<>(request.sum());
        return accountService.findById(request.accountSenderId())
                .doOnNext(this::validateAccountForClosingDate)
                .doOnNext(accountData -> validateAccountForSufficientBalance(accountData, Type.EXCHANGE, request.sum()))
                .zipWith(accountService.findById(request.accountRecipientId())
                        .doOnNext(this::validateAccountForClosingDate))
                .flatMap(tuple -> nbRbCurrencyService.exchangeSumByCurrency(tuple.getT1().getCurrency(),
                                tuple.getT2().getCurrency(), request.sum())
                        .doOnNext(exchangedSum::set)
                        .flatMap(byn -> accountService.updateBalance(accountMapper
                                        .fromAccountData(tuple.getT1()), tuple.getT1().getBalance().subtract(request.sum()))
                                .zipWith(accountService.updateBalance(accountMapper
                                        .fromAccountData(tuple.getT2()), tuple.getT2().getBalance().add(exchangedSum.get())))))
                .flatMap(tuple -> {
                    Transaction transaction = transactionMapper.toExchangeTransaction(
                            Type.EXCHANGE,
                            tuple.getT1().getBank().getId(),
                            tuple.getT2().getBank().getId(),
                            tuple.getT1().getId(),
                            tuple.getT2().getId(),
                            request.sum(),
                            exchangedSum.get());
                    return transactionRepository.save(transaction)
                            .map(savedTransaction -> transactionMapper.toExchangeResponse(
                                    savedTransaction,
                                    tuple.getT1().getCurrency(),
                                    tuple.getT2().getCurrency(),
                                    tuple.getT1().getBank().getName(),
                                    tuple.getT2().getBank().getName(),
                                    tuple.getT1().getBalance().add(request.sum()),
                                    tuple.getT1().getBalance(),
                                    tuple.getT2().getBalance().subtract(exchangedSum.get()),
                                    tuple.getT2().getBalance()
                            ));
                })
                .doOnNext(response -> uploadFileService.uploadCheck(checkService.createExchangeBalanceCheck(response)))
                .as(operator::transactional);
    }

    @Override
    public Mono<TransactionStatementResponse> findAllByPeriodOfDateAndAccountId(TransactionStatementRequest request) {
        return accountService.findById(request.accountId())
                .flatMap(accountData -> transactionRepository.findAllByPeriodOfDateAndAccountId(
                                request.from(), request.to(), accountData.getId())
                        .switchIfEmpty(Mono.error(new TransactionException(
                                "It is not possible to create a transaction statement because" +
                                " you do not have any transactions for this period of time : from "
                                + request.from() + " to " + request.to())))
                        .collectList()
                        .map(transactionStatements -> transactionMapper.toStatementResponse(
                                accountData.getBank().getName(),
                                accountData.getUser(),
                                accountData,
                                request,
                                transactionStatements)))
                .doOnNext(response -> uploadFileService.uploadStatement(checkService.createTransactionStatement(response)))
                .as(operator::transactional);
    }

    @Override
    public Mono<AmountStatementResponse> findSumOfFundsByPeriodOfDateAndAccountId(TransactionStatementRequest request) {
        return accountService.findById(request.accountId())
                .flatMap(accountData -> transactionRepository
                        .findSumOfSpentFundsByPeriodOfDateAndAccountId(request.from(), request.to(), accountData.getId())
                        .zipWith(transactionRepository
                                .findSumOfReceivedFundsByPeriodOfDateAndAccountId(
                                        request.from(), request.to(), accountData.getId()))
                        .map(tuple -> transactionMapper.toAmountResponse(
                                accountData.getBank().getName(),
                                accountData.getUser(),
                                accountData,
                                request,
                                tuple.getT1(),
                                tuple.getT2())))
                .doOnNext(response -> uploadFileService.uploadAmount(checkService.createAmountStatement(response)))
                .as(operator::transactional);
    }

    @Override
    public Mono<TransactionResponse> findById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse)
                .switchIfEmpty(Mono.error(new TransactionNotFoundException("Transaction with ID " + id + " is not found!")));
    }

    @Override
    public Flux<TransactionResponse> findAllBySendersAccountId(String id) {
        return transactionRepository.findAllBySendersAccountId(id)
                .map(transactionMapper::toResponse);
    }

    @Override
    public Flux<TransactionResponse> findAllByRecipientAccountId(String id) {
        return transactionRepository.findAllByRecipientAccountId(id)
                .map(transactionMapper::toResponse);
    }

    private void validateAccountForClosingDate(AccountData accountData) {
        if (accountData.getClosingDate() != null) {
            throw new AccountClosedException("Account with ID " + accountData.getId()
                                             + " is closed since " + accountData.getClosingDate());
        }
    }

    private void validateAccountForSufficientBalance(AccountData accountData, Type type, BigDecimal sum) {
        BigDecimal oldBalance = accountData.getBalance();
        if (type != Type.REPLENISHMENT && oldBalance.compareTo(sum) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the account! You want to withdrawal/transfer "
                                                 + sum + ", but you have only " + oldBalance);
        }
    }

    private void validateAccountsForCurrencyEquality(Currency senderCurrency, Currency recipientCurrency) {
        if (!senderCurrency.equals(recipientCurrency)) {
            throw new BadCurrencyException("Your currency is " + senderCurrency
                                           + ", but account currency is " + recipientCurrency);
        }
    }

}
