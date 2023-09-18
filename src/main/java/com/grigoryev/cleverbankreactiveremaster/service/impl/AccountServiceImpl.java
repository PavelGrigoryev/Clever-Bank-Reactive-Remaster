package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.account.AccountResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.notfound.AccountNotFoundException;
import com.grigoryev.cleverbankreactiveremaster.mapper.AccountMapper;
import com.grigoryev.cleverbankreactiveremaster.model.AccountData;
import com.grigoryev.cleverbankreactiveremaster.repository.AccountRepository;
import com.grigoryev.cleverbankreactiveremaster.service.AccountService;
import com.grigoryev.cleverbankreactiveremaster.service.BankService;
import com.grigoryev.cleverbankreactiveremaster.service.UserService;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final BankService bankService;
    private final AccountMapper accountMapper;
    private final TransactionalOperator operator;

    @Override
    public Mono<AccountData> findById(String id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Account with ID " + id + " is not found!")));
    }

    @Override
    public Mono<AccountResponse> findByIdResponse(String id) {
        return findById(id)
                .map(accountMapper::toResponse);
    }

    @Override
    public Flux<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Flux<AccountResponse> findAllResponses() {
        return accountRepository.findAllDatas()
                .map(accountMapper::toResponse);
    }

    @Override
    public Mono<AccountResponse> save(AccountRequest request) {
        return Mono.zip(userService.findById(request.userId()), bankService.findById(request.bankId()), ((user, bank) -> {
                    Account account = accountMapper.fromRequest(request);
                    account.setOpeningDate(LocalDate.now());
                    account.setUserId(user.getId());
                    account.setBankId(bank.getId());
                    return account;
                }))
                .flatMap(accountRepository::save)
                .map(accountMapper::toResponse)
                .as(operator::transactional);
    }

    @Override
    public Mono<AccountData> updateBalance(Account account, BigDecimal balance) {
        return Mono.fromSupplier(() -> account.setBalance(balance))
                .flatMap(accountRepository::update)
                .as(operator::transactional);
    }

    @Override
    public Mono<AccountResponse> closeAccount(String id) {
        return findById(id)
                .map(accountData -> {
                    accountData.setClosingDate(LocalDate.now());
                    accountData.setBalance(BigDecimal.ZERO);
                    return accountData;
                })
                .map(accountMapper::fromAccountData)
                .flatMap(accountRepository::update)
                .map(accountMapper::toResponse)
                .as(operator::transactional);
    }

    @Override
    public Mono<DeleteResponse> delete(String id) {
        return accountRepository.delete(id)
                .map(account -> new DeleteResponse("Account with ID " + id + " was successfully deleted"))
                .as(operator::transactional)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("No Account with ID " + id + " to delete")));
    }

}
