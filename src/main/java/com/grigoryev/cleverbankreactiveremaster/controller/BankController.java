package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.bank.BankResponse;
import com.grigoryev.cleverbankreactiveremaster.service.BankService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
@RequestMapping("/banks")
public class BankController {

    private final BankService bankService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BankResponse>> findByIdResponse(@PathVariable @Positive Long id) {
        return bankService.findByIdResponse(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<BankResponse> findAll() {
        return bankService.findAll();
    }

    @PostMapping
    public Mono<ResponseEntity<BankResponse>> save(@RequestBody @Valid BankRequest request) {
        return bankService.save(request)
                .map(bankResponse -> ResponseEntity.status(HttpStatus.CREATED).body(bankResponse));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<BankResponse>> update(@PathVariable @Positive Long id, @RequestBody @Valid BankRequest request) {
        return bankService.update(id, request)
                .map(bankResponse -> ResponseEntity.status(HttpStatus.CREATED).body(bankResponse));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<DeleteResponse>> delete(@PathVariable @Positive Long id) {
        return bankService.delete(id)
                .map(ResponseEntity::ok);
    }

}
