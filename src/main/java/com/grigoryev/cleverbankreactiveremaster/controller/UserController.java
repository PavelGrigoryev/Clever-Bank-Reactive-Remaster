package com.grigoryev.cleverbankreactiveremaster.controller;

import com.grigoryev.cleverbankreactiveremaster.dto.user.UserRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public Mono<ServerResponse> findByIdResponse(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return userService.findByIdResponse(id)
                .flatMap(userResponse -> ServerResponse.ok().bodyValue(userResponse));
    }

    public Mono<ServerResponse> findAll() {
        return ServerResponse.ok().body(userService.findAll(), UserResponse.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<UserRequest> userRequest = request.bodyToMono(UserRequest.class);
        return userRequest.flatMap(userService::save)
                .flatMap(userResponse -> ServerResponse.status(HttpStatus.CREATED).bodyValue(userResponse));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        Mono<UserRequest> userRequest = request.bodyToMono(UserRequest.class);
        return userRequest.flatMap(req -> userService.update(id, req))
                .flatMap(userResponse -> ServerResponse.status(HttpStatus.CREATED).bodyValue(userResponse));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return userService.delete(id)
                .flatMap(deleteResponse -> ServerResponse.ok().bodyValue(deleteResponse));
    }

}
