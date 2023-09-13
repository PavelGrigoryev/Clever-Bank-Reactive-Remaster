package com.grigoryev.cleverbankreactiveremaster.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.BadRequestException;
import com.grigoryev.cleverbankreactiveremaster.exception.internalservererror.InternalServerErrorException;
import com.grigoryev.cleverbankreactiveremaster.exception.internalservererror.JsonParseException;
import com.grigoryev.cleverbankreactiveremaster.exception.notfound.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@RequiredArgsConstructor
public class CleverBankExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status;
        if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof InternalServerErrorException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return Mono.error(ex);
        }
        exchange.getResponse().setStatusCode(status);
        return getExceptionResponse(exchange, ex);
    }

    private Mono<Void> getExceptionResponse(ServerWebExchange exchange, Throwable ex) {
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(new ExceptionResponse(ex.getMessage()));
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e.getMessage());
        }
        return exchange.getResponse().writeWith(Mono.just(data)
                .map(bytes -> exchange.getResponse().bufferFactory().wrap(bytes)));
    }

}
