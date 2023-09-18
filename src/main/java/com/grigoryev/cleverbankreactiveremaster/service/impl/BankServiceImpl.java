package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.UniquePhoneNumberException;
import com.grigoryev.cleverbankreactiveremaster.exception.notfound.BankNotFoundException;
import com.grigoryev.cleverbankreactiveremaster.mapper.BankMapper;
import com.grigoryev.cleverbankreactiveremaster.repository.BankRepository;
import com.grigoryev.cleverbankreactiveremaster.service.BankService;
import com.grigoryev.cleverbankreactiveremaster.tables.pojos.Bank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final BankMapper bankMapper;
    private final TransactionalOperator operator;

    @Override
    public Mono<Bank> findById(Long id) {
        return bankRepository.findById(id)
                .switchIfEmpty(Mono.error(new BankNotFoundException("Bank with ID " + id + " is not found!")));
    }

    @Override
    public Mono<BankResponse> findByIdResponse(Long id) {
        return findById(id)
                .map(bankMapper::toResponse);
    }

    @Override
    public Flux<BankResponse> findAll() {
        return bankRepository.findAll()
                .map(bankMapper::toResponse);
    }

    @Override
    public Mono<BankResponse> save(BankRequest request) {
        return Mono.fromSupplier(() -> bankMapper.fromRequest(request))
                .flatMap(bankRepository::save)
                .map(bankMapper::toResponse)
                .as(operator::transactional)
                .switchIfEmpty(Mono.error(new UniquePhoneNumberException("Bank with phone number "
                                                                         + request.phoneNumber() + " is already exist")));
    }

    @Override
    public Mono<BankResponse> update(Long id, BankRequest request) {
        return findById(id)
                .flatMap(bankById -> {
                    Bank bank = bankMapper.fromRequest(request);
                    bank.setId(bankById.getId());
                    return bankRepository.update(bank);
                })
                .map(bankMapper::toResponse)
                .as(operator::transactional)
                .switchIfEmpty(Mono.error(new UniquePhoneNumberException("Bank with phone number "
                                                                         + request.phoneNumber() + " is already exist")));
    }

    @Override
    public Mono<DeleteResponse> delete(Long id) {
        return bankRepository.delete(id)
                .map(bank -> new DeleteResponse("Bank with ID " + id + " was successfully deleted"))
                .as(operator::transactional)
                .switchIfEmpty(Mono.error(new BankNotFoundException("No Bank with ID " + id + " to delete")));
    }

}
