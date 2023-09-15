package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.service.UserService;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> findByIdResponse(@PathVariable @Positive Long id) {
        return userService.findByIdResponse(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<UserResponse> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public Mono<ResponseEntity<UserResponse>> save(@RequestBody @Valid UserRequest request) {
        return userService.save(request)
                .map(userResponse -> ResponseEntity.status(HttpStatus.CREATED).body(userResponse));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> update(@PathVariable @Positive Long id, @RequestBody @Valid UserRequest request) {
        return userService.update(id, request)
                .map(userResponse -> ResponseEntity.status(HttpStatus.CREATED).body(userResponse));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<DeleteResponse>> delete(@PathVariable @Positive Long id) {
        return userService.delete(id)
                .map(ResponseEntity::ok);
    }

}
